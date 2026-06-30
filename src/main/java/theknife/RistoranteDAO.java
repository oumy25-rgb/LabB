/*
 * TheKnife - RistoranteDAO
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

/**
 * Data Access Object per la gestione dei ristoranti su PostgreSQL.
 * Fornisce metodi per la ricerca, aggiunta e statistiche sui ristoranti.
 *
 * @author Gharsellaoui Omema
 * @author Kufura Razak
 * @version 1.0
 */
public class RistoranteDAO {

    /**
     * Costruisce un oggetto Ristorante da un ResultSet.
     *
     * @param rs il ResultSet posizionato sulla riga corrente
     * @return l'oggetto Ristorante costruito
     * @throws SQLException se la lettura del ResultSet fallisce
     */
    private static Ristorante buildRistorante(ResultSet rs) throws SQLException {
        return new Ristorante(
            rs.getString("nome"),
            rs.getString("indirizzo"),
            rs.getString("citta"),
            rs.getString("prezzo"),
            rs.getString("nazione"),
            rs.getString("tipo_cucina"),
            rs.getDouble("longitudine"),
            rs.getDouble("latitudine"),
            rs.getBoolean("delivery"),
            rs.getBoolean("prenotazione"),
            null
        );
    }

    /**
     * Cerca ristoranti per città (case-insensitive).
     *
     * @param citta la città in cui cercare
     * @return lista di ristoranti trovati
     */
    public static ArrayList<Ristorante> cercaPerCitta(String citta) {
        ArrayList<Ristorante> lista = new ArrayList<>();
        String sql = "SELECT * FROM RistorantiTheKnife WHERE citta ILIKE ? ORDER BY nome";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, citta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(buildRistorante(rs));
        } catch (SQLException e) {
            System.err.println("Errore ricerca per città: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Cerca ristoranti per città e tipo di cucina (case-insensitive).
     *
     * @param citta  la città in cui cercare
     * @param cucina il tipo di cucina (ricerca parziale)
     * @return lista di ristoranti trovati
     */
    public static ArrayList<Ristorante> cercaPerCittaECucina(String citta, String cucina) {
        ArrayList<Ristorante> lista = new ArrayList<>();
        String sql = "SELECT * FROM RistorantiTheKnife " +
                     "WHERE citta ILIKE ? AND tipo_cucina ILIKE ? ORDER BY nome";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, citta);
            ps.setString(2, "%" + cucina + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(buildRistorante(rs));
        } catch (SQLException e) {
            System.err.println("Errore ricerca per cucina: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Cerca ristoranti per città e fascia di prezzo.
     * I simboli Michelin (€€€€) vengono convertiti in valori numerici.
     *
     * @param citta    la città in cui cercare
     * @param prezzoMin il prezzo minimo in euro
     * @param prezzoMax il prezzo massimo in euro
     * @return lista di ristoranti trovati
     */
    public static ArrayList<Ristorante> cercaPerCittaEPrezzo(String citta,
            double prezzoMin, double prezzoMax) {
        ArrayList<Ristorante> lista = new ArrayList<>();
        String sql = "SELECT * FROM RistorantiTheKnife " +
                     "WHERE citta ILIKE ? AND " +
                     "CASE " +
                     "  WHEN prezzo ~ '^[0-9]+(\\.[0-9]+)?$' THEN prezzo::DOUBLE PRECISION " +
                     "  ELSE LENGTH(TRIM(prezzo)) * 37.5 " +
                     "END BETWEEN ? AND ? " +
                     "ORDER BY nome";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, citta);
            ps.setDouble(2, prezzoMin);
            ps.setDouble(3, prezzoMax);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(buildRistorante(rs));
        } catch (SQLException e) {
            System.err.println("Errore ricerca per prezzo: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Cerca ristoranti con tutti i criteri specificati.
     * Costruisce dinamicamente la query in base ai filtri non nulli.
     *
     * @param citta        la città (obbligatoria)
     * @param cucina       il tipo di cucina (null = qualsiasi)
     * @param prezzoMin    il prezzo minimo (-1 = nessun filtro)
     * @param prezzoMax    il prezzo massimo (-1 = nessun filtro)
     * @param delivery     true/false per filtro delivery (null = qualsiasi)
     * @param prenotazione true/false per filtro prenotazione (null = qualsiasi)
     * @param mediaMin     la media stelle minima (-1 = nessun filtro)
     * @return lista di ristoranti trovati
     */
    public static ArrayList<Ristorante> cercaTuttiCriteri(String citta, String cucina,
            double prezzoMin, double prezzoMax,
            Boolean delivery, Boolean prenotazione, double mediaMin) {
        ArrayList<Ristorante> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT r.* FROM RistorantiTheKnife r " +
            "LEFT JOIN Recensioni rec ON r.id = rec.ristorante_id " +
            "WHERE r.citta ILIKE ? ");

        if (cucina != null && !cucina.isEmpty())
            sql.append("AND r.tipo_cucina ILIKE ? ");
        if (prezzoMin >= 0 && prezzoMax >= 0)
            sql.append("AND CASE WHEN r.prezzo ~ '^[0-9]+(\\.[0-9]+)?$' " +
                       "THEN r.prezzo::DOUBLE PRECISION ELSE LENGTH(TRIM(r.prezzo)) * 37.5 " +
                       "END BETWEEN ? AND ? ");
        if (delivery != null)
            sql.append("AND r.delivery = ? ");
        if (prenotazione != null)
            sql.append("AND r.prenotazione = ? ");

        sql.append("GROUP BY r.id ");

        if (mediaMin >= 0)
            sql.append("HAVING COALESCE(AVG(rec.stelle), 0) >= ? ");

        sql.append("ORDER BY r.nome");

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            ps.setString(idx++, citta);
            if (cucina != null && !cucina.isEmpty())
                ps.setString(idx++, "%" + cucina + "%");
            if (prezzoMin >= 0 && prezzoMax >= 0) {
                ps.setDouble(idx++, prezzoMin);
                ps.setDouble(idx++, prezzoMax);
            }
            if (delivery != null)    ps.setBoolean(idx++, delivery);
            if (prenotazione != null) ps.setBoolean(idx++, prenotazione);
            if (mediaMin >= 0)       ps.setDouble(idx++, mediaMin);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(buildRistorante(rs));

        } catch (SQLException e) {
            System.err.println("Errore ricerca tutti criteri: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Aggiunge un nuovo ristorante custom inserito da un ristoratore.
     *
     * @param r          il ristorante da aggiungere
     * @param utente_id  l'ID del ristoratore proprietario
     * @return true se l'inserimento ha avuto successo
     */
    public static boolean aggiungiRistorante(Ristorante r, int utente_id) {
        String sqlInsert = "INSERT INTO RistorantiTheKnife " +
            "(nome, indirizzo, citta, nazione, prezzo, tipo_cucina, " +
            "longitudine, latitudine, delivery, prenotazione, fonte) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'custom') RETURNING id";

        String sqlProprietario = "INSERT INTO Proprietari (utente_id, ristorante_id) VALUES (?, ?)";

        try (Connection conn = DataBaseManager.getConnection()) {
            conn.setAutoCommit(false);

            int ristoranteId = -1;
            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setString(1,  r.getName());
                ps.setString(2,  r.getAddress());
                ps.setString(3,  r.getCity());
                ps.setString(4,  r.getNation());
                ps.setString(5,  r.getPrice());
                ps.setString(6,  r.getCuisine());
                ps.setDouble(7,  r.getLongitude());
                ps.setDouble(8,  r.getLatitude());
                ps.setBoolean(9, r.getDelivery());
                ps.setBoolean(10, r.getReservation());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) ristoranteId = rs.getInt(1);
            }

            if (ristoranteId == -1) { conn.rollback(); return false; }

            try (PreparedStatement ps = conn.prepareStatement(sqlProprietario)) {
                ps.setInt(1, utente_id);
                ps.setInt(2, ristoranteId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Errore aggiunta ristorante: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cerca un ristorante per nome (case-insensitive).
     *
     * @param nome il nome del ristorante
     * @return il ristorante trovato, o null se non esiste
     */
    public static Ristorante cercaPerNome(String nome) {
        String sql = "SELECT * FROM RistorantiTheKnife WHERE nome ILIKE ? LIMIT 1";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return buildRistorante(rs);
        } catch (SQLException e) {
            System.err.println("Errore ricerca per nome: " + e.getMessage());
        }
        return null;
    }

    /**
     * Restituisce l'ID di un ristorante dato il suo nome.
     *
     * @param nome il nome del ristorante
     * @return l'ID del ristorante, o -1 se non trovato
     */
    public static int getIdPerNome(String nome) {
        String sql = "SELECT id FROM RistorantiTheKnife WHERE nome ILIKE ? LIMIT 1";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            System.err.println("Errore get id ristorante: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Verifica se un ristorante con quel nome esiste già nel database.
     *
     * @param nome il nome del ristorante
     * @return true se esiste, false altrimenti
     */
    public static boolean esisteRistorante(String nome) {
        return getIdPerNome(nome) != -1;
    }

    /**
     * Calcola la media stelle e il numero di recensioni per un ristorante.
     *
     * @param nomeRistorante il nome del ristorante
     * @return array [mediaStelle, numeroRecensioni]
     */
    public static double[] getMediaECount(String nomeRistorante) {
        String sql = "SELECT COALESCE(AVG(rec.stelle), 0) AS media, COUNT(rec.id) AS count " +
                     "FROM RistorantiTheKnife r " +
                     "LEFT JOIN Recensioni rec ON r.id = rec.ristorante_id " +
                     "WHERE r.nome ILIKE ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomeRistorante);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new double[]{ rs.getDouble("media"), rs.getDouble("count") };
            }
        } catch (SQLException e) {
            System.err.println("Errore calcolo media: " + e.getMessage());
        }
        return new double[]{0.0, 0.0};
    }

    /**
     * Restituisce la lista dei nomi dei ristoranti posseduti da un ristoratore.
     *
     * @param utenteId l'ID del ristoratore
     * @return lista di nomi dei ristoranti
     */
    public static ArrayList<String> getRistorantiDelRistoratore(int utenteId) {
        ArrayList<String> lista = new ArrayList<>();
        String sql = "SELECT r.nome FROM RistorantiTheKnife r " +
                     "JOIN Proprietari p ON r.id = p.ristorante_id " +
                     "WHERE p.utente_id = ? ORDER BY r.nome";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, utenteId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(rs.getString("nome"));
        } catch (SQLException e) {
            System.err.println("Errore get ristoranti ristoratore: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Calcola le coordinate medie di una città come media delle coordinate
     * di tutti i ristoranti presenti in quella città.
     *
     * @param luogo il nome della città
     * @return array [latitudineMedia, longitudineMedia], o null se non trovato
     */
    public static double[] getCoordinateCitta(String luogo) {
        String sql = "SELECT AVG(latitudine) AS lat_media, AVG(longitudine) AS lon_media, COUNT(*) AS n " +
                     "FROM RistorantiTheKnife WHERE citta ILIKE ?";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, luogo);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt("n") > 0) {
                return new double[]{ rs.getDouble("lat_media"), rs.getDouble("lon_media") };
            }
        } catch (SQLException e) {
            System.err.println("Errore calcolo coordinate città: " + e.getMessage());
        }
        return null;
    }

    /**
     * Cerca ristoranti entro un raggio da un punto geografico specificato.
     * Utilizza la formula di Haversine per il calcolo della distanza.
     *
     * @param lat      latitudine del punto di partenza
     * @param lon      longitudine del punto di partenza
     * @param raggioKm il raggio di ricerca in km
     * @return lista di ristoranti entro il raggio, ordinati per distanza
     */
    public static ArrayList<Ristorante> cercaRistorantiVicini(double lat, double lon, double raggioKm) {
        ArrayList<Ristorante> lista = new ArrayList<>();
        String sql = "SELECT * FROM RistorantiTheKnife";
        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                double ristLat = rs.getDouble("latitudine");
                double ristLon = rs.getDouble("longitudine");
                double distanza = GeoUtils.distanzaKm(lat, lon, ristLat, ristLon);
                if (distanza <= raggioKm) {
                    Ristorante r = buildRistorante(rs);
                    r.setDistanzaKm(distanza);
                    lista.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore ricerca ristoranti vicini: " + e.getMessage());
        }
        lista.sort((a, b) -> Double.compare(a.getDistanzaKm(), b.getDistanzaKm()));
        return lista;
    }

    /**
     * Cerca ristoranti vicini a un luogo specificato per nome.
     *
     * @param luogo    il nome del luogo (città)
     * @param raggioKm il raggio di ricerca in km
     * @return lista di ristoranti entro il raggio, ordinati per distanza
     */
    public static ArrayList<Ristorante> cercaRistorantiViciniPerLuogo(String luogo, double raggioKm) {
        double[] coords = getCoordinateCitta(luogo);
        if (coords == null) {
            return new ArrayList<>();
        }
        return cercaRistorantiVicini(coords[0], coords[1], raggioKm);
    }
}