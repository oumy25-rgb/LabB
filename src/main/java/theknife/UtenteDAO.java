/*
 * TheKnife - UtenteDAO
 * Autori:
 *   Gharsellaoui Omema - 761146 - VA
 *   Kufura Razak - 760614 - VA
 */

package theknife;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object per la gestione degli utenti su PostgreSQL.
 *
 * <p><strong>Concorrenza:</strong> {@link #registraUtente(Utente)} è
 * {@code synchronized} per eliminare la race condition tra il controllo
 * "username esiste?" e l'INSERT.</p>
 *
 * @author Gharsellaoui Omema
 * @author Kufura Razak
 * @version 1.0
 */
public class UtenteDAO {

    /**
     * Registra un nuovo utente nel database.
     * Il metodo è synchronized per evitare race condition sullo username.
     *
     * @param u l'utente da registrare (password già cifrata)
     * @return true se la registrazione ha avuto successo, false se username già in uso
     */
    public static synchronized boolean registraUtente(Utente u) {
        if (usernameEsiste(u.getUsername())) {
            return false;
        }

        String sql = "INSERT INTO Utenti " +
                     "(nome, cognome, username, password, " +
                     "data_nascita, luogo_domicilio, ruolo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getCognome());
            ps.setString(3, u.getUsername());
            ps.setString(4, u.getPassword());
            ps.setString(5, u.getDataNascita());
            ps.setString(6, u.getLuogoDomicilio());
            ps.setString(7, u.getRuolo());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Errore SQL registrazione: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica le credenziali di un utente e lo restituisce se valido.
     *
     * @param username lo username dell'utente
     * @param password la password cifrata con SHA-256
     * @return l'oggetto Utente se le credenziali sono corrette, null altrimenti
     */
    public static Utente login(String username, String password) {
        String sql = "SELECT * FROM Utenti WHERE username = ? AND password = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return buildUtente(rs);
            }
        } catch (SQLException e) {
            System.err.println("Errore login: " + e.getMessage());
        }
        return null;
    }

    /**
     * Verifica se uno username è già registrato nel database.
     *
     * @param username lo username da verificare
     * @return true se lo username esiste già, false altrimenti
     */
    public static boolean usernameEsiste(String username) {
        String sql = "SELECT COUNT(*) FROM Utenti WHERE username = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Errore check username: " + e.getMessage());
        }
        return false;
    }

    /**
     * Restituisce l'ID interno di un utente dato il suo username.
     *
     * @param username lo username dell'utente
     * @return l'ID dell'utente, o -1 se non trovato
     */
    public static int getIdPerUsername(String username) {
        String sql = "SELECT id FROM Utenti WHERE username = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            System.err.println("Errore get id utente: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Restituisce il nome di un utente dato il suo username.
     *
     * @param username lo username dell'utente
     * @return il nome dell'utente, o lo username stesso se non trovato
     */
    public static String getNomePerUsername(String username) {
        String sql = "SELECT nome FROM Utenti WHERE username = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("nome");
        } catch (SQLException e) {
            System.err.println("Errore get nome utente: " + e.getMessage());
        }
        return username;
    }
    
    /**
     * Restituisce il domicilio di un utente dato il suo username.
     *
     * @param username lo username dell'utente
     * @return il domicilio dell'utente, o null se non trovato
     */
    public static String getDomicilio(String username) {
        String sql = "SELECT luogo_domicilio FROM Utenti WHERE username = ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("luogo_domicilio");
            }
        } catch (SQLException e) {
            System.err.println("Errore get domicilio: " + e.getMessage());
        }
        return null;
    }

    /**
     * Costruisce un oggetto Cliente o Ristoratore da un ResultSet.
     *
     * @param rs il ResultSet posizionato sulla riga corrente
     * @return l'oggetto Utente costruito
     * @throws SQLException se la lettura del ResultSet fallisce
     */
    private static Utente buildUtente(ResultSet rs) throws SQLException {
        String ruolo         = rs.getString("ruolo");
        String nome          = rs.getString("nome");
        String cognome       = rs.getString("cognome");
        String username      = rs.getString("username");
        String password      = rs.getString("password");
        String dataNascita   = rs.getString("data_nascita");
        String luogoDomicilio = rs.getString("luogo_domicilio");

        return UtenteFactory.createUtente(ruolo, nome, cognome, username, password,
                dataNascita, luogoDomicilio);
    }
}