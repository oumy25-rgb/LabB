/*
 * TheKnife - RecensioneDAO
 * Autori:
 *   Gharsellaoui Omema - 761146 - VA
 *   Kufura Razak - 760614 - VA
 */

package theknife;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Data Access Object per la gestione di recensioni, risposte e preferiti.
 * Utilizza un ReentrantReadWriteLock per la gestione della concorrenza.
 *
 * @author Gharsellaoui Omema
 * @author Kufura Razak
 * @version 1.0
 */
public class RecensioneDAO {

    /**
     * Lock per la gestione della concorrenza su recensioni.
     * readLock() per operazioni di lettura (concorrenti).
     * writeLock() per operazioni di scrittura (esclusive).
     */
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Aggiunge una nuova recensione al database.
     * Un cliente può recensire un ristorante una sola volta.
     *
     * @param nomeRistorante il nome del ristorante
     * @param username       lo username del cliente
     * @param testo          il testo della recensione (può essere vuoto)
     * @param stelle         il voto in stelle (1-5)
     * @return true se l'inserimento ha avuto successo
     */
    public static boolean aggiungiRecensione(String nomeRistorante, String username,
                                              String testo, int stelle) {
        lock.writeLock().lock();
        try {
            int ristoranteId = RistoranteDAO.getIdPerNome(nomeRistorante);
            int utenteId = UtenteDAO.getIdPerUsername(username);
            if (ristoranteId == -1 || utenteId == -1) return false;

            if (haGiaRecensito(nomeRistorante, username)) {
                return false;
            }

            String sql = "INSERT INTO Recensioni (ristorante_id, utente_id, testo, stelle) " +
                         "VALUES (?, ?, ?, ?)";
            try (Connection conn = DataBaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, ristoranteId);
                ps.setInt(2, utenteId);
                ps.setString(3, testo);
                ps.setInt(4, stelle);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Errore aggiunta recensione: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Modifica una recensione esistente di un cliente.
     *
     * @param nomeRistorante il nome del ristorante
     * @param username       lo username del cliente
     * @param nuovoTesto     il nuovo testo della recensione
     * @param nuoveStelle    il nuovo voto (1-5)
     * @return true se la modifica ha avuto successo
     */
    public static boolean modificaRecensione(String nomeRistorante, String username,
                                              String nuovoTesto, int nuoveStelle) {
        lock.writeLock().lock();
        try {
            int ristoranteId = RistoranteDAO.getIdPerNome(nomeRistorante);
            int utenteId = UtenteDAO.getIdPerUsername(username);
            if (ristoranteId == -1 || utenteId == -1) return false;

            if (!haGiaRecensito(nomeRistorante, username)) {
                return false;
            }

            String sql = "UPDATE Recensioni SET testo = ?, stelle = ? " +
                         "WHERE ristorante_id = ? AND utente_id = ?";
            try (Connection conn = DataBaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nuovoTesto);
                ps.setInt(2, nuoveStelle);
                ps.setInt(3, ristoranteId);
                ps.setInt(4, utenteId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Errore modifica recensione: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Elimina una recensione di un cliente.
     * La risposta associata viene eliminata in cascata.
     *
     * @param nomeRistorante il nome del ristorante
     * @param username       lo username del cliente
     * @return true se l'eliminazione ha avuto successo
     */
    public static boolean eliminaRecensione(String nomeRistorante, String username) {
        lock.writeLock().lock();
        try {
            int ristoranteId = RistoranteDAO.getIdPerNome(nomeRistorante);
            int utenteId = UtenteDAO.getIdPerUsername(username);
            if (ristoranteId == -1 || utenteId == -1) return false;

            String sql = "DELETE FROM Recensioni WHERE ristorante_id = ? AND utente_id = ?";
            try (Connection conn = DataBaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, ristoranteId);
                ps.setInt(2, utenteId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Errore eliminazione recensione: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Verifica se un cliente ha già recensito un ristorante.
     *
     * @param nomeRistorante il nome del ristorante
     * @param username       lo username del cliente
     * @return true se il cliente ha già recensito il ristorante
     */
    public static boolean haGiaRecensito(String nomeRistorante, String username) {
        lock.readLock().lock();
        try {
            int ristoranteId = RistoranteDAO.getIdPerNome(nomeRistorante);
            int utenteId = UtenteDAO.getIdPerUsername(username);
            if (ristoranteId == -1 || utenteId == -1) return false;

            String sql = "SELECT COUNT(*) FROM Recensioni " +
                         "WHERE ristorante_id = ? AND utente_id = ?";
            try (Connection conn = DataBaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, ristoranteId);
                ps.setInt(2, utenteId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Errore check recensione: " + e.getMessage());
        } finally {
            lock.readLock().unlock();
        }
        return false;
    }

    /**
     * Restituisce tutte le recensioni di un ristorante.
     *
     * @param nomeRistorante il nome del ristorante
     * @return lista di recensioni del ristorante
     */
    public static ArrayList<Recensione> getRecensioniRistorante(String nomeRistorante) {
        lock.readLock().lock();
        try {
            ArrayList<Recensione> lista = new ArrayList<>();
            String sql = "SELECT u.username, rec.testo, rec.stelle, " +
                         "CAST(rec.data_rec AS VARCHAR) AS data_rec, " +
                         "risp.testo AS risposta " +
                         "FROM Recensioni rec " +
                         "JOIN Utenti u ON rec.utente_id = u.id " +
                         "JOIN RistorantiTheKnife r ON rec.ristorante_id = r.id " +
                         "LEFT JOIN Risposte risp ON risp.recensione_id = rec.id " +
                         "WHERE r.nome ILIKE ? " +
                         "ORDER BY rec.data_rec DESC";
            try (Connection conn = DataBaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nomeRistorante);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    lista.add(new Recensione(
                        nomeRistorante,
                        rs.getString("username"),
                        rs.getString("testo"),
                        rs.getDouble("stelle"),
                        rs.getString("data_rec"),
                        rs.getString("risposta")
                    ));
                }
            } catch (SQLException e) {
                System.err.println("Errore get recensioni ristorante: " + e.getMessage());
            }
            return lista;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Restituisce tutte le recensioni scritte da un cliente.
     *
     * @param username lo username del cliente
     * @return lista di recensioni del cliente
     */
    public static ArrayList<Recensione> getRecensioniCliente(String username) {
        lock.readLock().lock();
        try {
            ArrayList<Recensione> lista = new ArrayList<>();
            String sql = "SELECT r.nome AS ristorante, rec.testo, rec.stelle, " +
                         "CAST(rec.data_rec AS VARCHAR) AS data_rec, " +
                         "risp.testo AS risposta " +
                         "FROM Recensioni rec " +
                         "JOIN Utenti u ON rec.utente_id = u.id " +
                         "JOIN RistorantiTheKnife r ON rec.ristorante_id = r.id " +
                         "LEFT JOIN Risposte risp ON risp.recensione_id = rec.id " +
                         "WHERE u.username = ? " +
                         "ORDER BY rec.data_rec DESC";
            try (Connection conn = DataBaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    lista.add(new Recensione(
                        rs.getString("ristorante"),
                        username,
                        rs.getString("testo"),
                        rs.getDouble("stelle"),
                        rs.getString("data_rec"),
                        rs.getString("risposta")
                    ));
                }
            } catch (SQLException e) {
                System.err.println("Errore get recensioni cliente: " + e.getMessage());
            }
            return lista;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Scrive la risposta del ristoratore a una recensione.
     *
     * @param nomeRistorante  il nome del ristorante
     * @param usernameCliente lo username del cliente
     * @param testo           il testo della risposta
     * @return true se la risposta è stata scritta con successo
     */
    public static boolean scriviRisposta(String nomeRistorante,
                                          String usernameCliente, String testo) {
        lock.writeLock().lock();
        try {
            int ristoranteId = RistoranteDAO.getIdPerNome(nomeRistorante);
            int clienteId = UtenteDAO.getIdPerUsername(usernameCliente);
            if (ristoranteId == -1 || clienteId == -1) return false;

            String sqlGetIds =
                "SELECT rec.id AS rec_id, p.utente_id AS rist_id " +
                "FROM Recensioni rec " +
                "JOIN Proprietari p ON p.ristorante_id = rec.ristorante_id " +
                "WHERE rec.ristorante_id = ? AND rec.utente_id = ? LIMIT 1";

            String sqlInsert =
                "INSERT INTO Risposte (recensione_id, utente_id, testo) VALUES (?, ?, ?) " +
                "ON CONFLICT (recensione_id) DO UPDATE SET testo = EXCLUDED.testo";

            try (Connection conn = DataBaseManager.getConnection()) {
                int recId = -1, ristId = -1;
                try (PreparedStatement ps = conn.prepareStatement(sqlGetIds)) {
                    ps.setInt(1, ristoranteId);
                    ps.setInt(2, clienteId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        recId = rs.getInt("rec_id");
                        ristId = rs.getInt("rist_id");
                    }
                }
                if (recId == -1) return false;
                try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                    ps.setInt(1, recId);
                    ps.setInt(2, ristId);
                    ps.setString(3, testo);
                    return ps.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore scriviRisposta: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Aggiunge un ristorante alla lista dei preferiti del cliente.
     *
     * @param username       lo username del cliente
     * @param nomeRistorante il nome del ristorante
     * @return true se l'aggiunta ha avuto successo
     */
    public static boolean aggiungiPreferito(String username, String nomeRistorante) {
        lock.writeLock().lock();
        try {
            int utenteId = UtenteDAO.getIdPerUsername(username);
            int ristoranteId = RistoranteDAO.getIdPerNome(nomeRistorante);
            if (utenteId == -1 || ristoranteId == -1) return false;

            if (isPreferito(username, nomeRistorante)) {
                return false;
            }

            String sql = "INSERT INTO Preferiti (utente_id, ristorante_id) VALUES (?, ?)";
            try (Connection conn = DataBaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, utenteId);
                ps.setInt(2, ristoranteId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Errore aggiunta preferito: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Rimuove un ristorante dalla lista dei preferiti del cliente.
     *
     * @param username       lo username del cliente
     * @param nomeRistorante il nome del ristorante
     * @return true se la rimozione ha avuto successo
     */
    public static boolean rimuoviPreferito(String username, String nomeRistorante) {
        lock.writeLock().lock();
        try {
            int utenteId = UtenteDAO.getIdPerUsername(username);
            int ristoranteId = RistoranteDAO.getIdPerNome(nomeRistorante);
            if (utenteId == -1 || ristoranteId == -1) return false;

            String sql = "DELETE FROM Preferiti WHERE utente_id = ? AND ristorante_id = ?";
            try (Connection conn = DataBaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, utenteId);
                ps.setInt(2, ristoranteId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Errore rimozione preferito: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Restituisce la lista dei ristoranti preferiti di un cliente.
     *
     * @param username lo username del cliente
     * @return lista di nomi dei ristoranti preferiti
     */
    public static ArrayList<String> getPreferiti(String username) {
        lock.readLock().lock();
        try {
            ArrayList<String> lista = new ArrayList<>();
            String sql = "SELECT r.nome FROM Preferiti p " +
                         "JOIN RistorantiTheKnife r ON p.ristorante_id = r.id " +
                         "JOIN Utenti u ON p.utente_id = u.id " +
                         "WHERE u.username = ? ORDER BY r.nome";
            try (Connection conn = DataBaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) lista.add(rs.getString("nome"));
            } catch (SQLException e) {
                System.err.println("Errore get preferiti: " + e.getMessage());
            }
            return lista;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Verifica se un ristorante è già nei preferiti di un cliente.
     *
     * @param username       lo username del cliente
     * @param nomeRistorante il nome del ristorante
     * @return true se il ristorante è già nei preferiti
     */
    public static boolean isPreferito(String username, String nomeRistorante) {
        lock.readLock().lock();
        try {
            int utenteId = UtenteDAO.getIdPerUsername(username);
            int ristoranteId = RistoranteDAO.getIdPerNome(nomeRistorante);
            if (utenteId == -1 || ristoranteId == -1) return false;

            String sql = "SELECT COUNT(*) FROM Preferiti " +
                         "WHERE utente_id = ? AND ristorante_id = ?";
            try (Connection conn = DataBaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, utenteId);
                ps.setInt(2, ristoranteId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Errore check preferito: " + e.getMessage());
        } finally {
            lock.readLock().unlock();
        }
        return false;
    }
}