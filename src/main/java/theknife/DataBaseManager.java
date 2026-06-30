/*
 * TheKnife - DataBaseManager
 * Autori:
 *   Gharsellaoui Omema - 761146 - VA
 *   Kufura Razak - 760614 - VA
 */

package theknife;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * La classe <strong>DatabaseManager</strong> gestisce la connessione al database
 * PostgreSQL utilizzando un pool di connessioni HikariCP per alte prestazioni.
 *
 * <p>Il pool di connessioni offre:</p>
 * <ul>
 *   <li>Riuso delle connessioni (riduce l'overhead di apertura/chiusura)</li>
 *   <li>Timeout configurabili</li>
 *   <li>Rilevamento automatico delle connessioni leak</li>
 *   <li>Gestione thread-safe per l'ambiente multi-client</li>
 * </ul>
 *
 * <p>Il dataset Michelin viene importato automaticamente al primo avvio
 * del server se non è già presente nel database.</p>
 *
 * @author Gharsellaoui Omema
 * @author Kufura Razak
 * @version 1.0
 */
public class DataBaseManager {

    // ==================== ATTRIBUTI ====================
    
    /** DataSource HikariCP per il pool di connessioni. */
    private static HikariDataSource dataSource;

    /** Indica se {@link #configura} è già stato chiamato. */
    private static boolean configurato = false;

    /**
     * Path del file CSV Michelin da importare al primo avvio.
     * Configurabile tramite property di sistema.
     */
    private static final String MICHELIN_CSV_PATH = 
        System.getProperty("michelin.csv.path", "src/dati/michelin_my_maps.csv");

    // ==================== INIZIALIZZAZIONE STATICA ====================
    
    /**
     * Carica esplicitamente il driver JDBC di PostgreSQL.
     */
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver PostgreSQL non trovato nel classpath: " + e.getMessage());
        }
    }

    /**
     * Costruttore privato: la classe è puramente statica/di utilità
     * e non deve essere istanziata.
     */
    private DataBaseManager() {}

    // ==================== CONFIGURAZIONE DEL POOL ====================
    
    /**
     * Configura il pool di connessioni HikariCP per PostgreSQL.
     *
     * @param host     l'host del server PostgreSQL
     * @param porta    la porta del server PostgreSQL
     * @param nomeDb   il nome del database
     * @param user     lo username per l'accesso al database
     * @param password la password per l'accesso al database
     */
    public static void configura(String host, String porta, String nomeDb, 
                                  String user, String password) {
        HikariConfig config = new HikariConfig();
        
        config.setJdbcUrl("jdbc:postgresql://" + host + ":" + porta + "/" + nomeDb);
        config.setUsername(user);
        config.setPassword(password);
        
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(60000);
        config.setPoolName("TheKnifePool");
        config.setConnectionTestQuery("SELECT 1");
        
        dataSource = new HikariDataSource(config);
        configurato = true;
        
        System.out.println("✅ Pool di connessioni configurato: " + 
                          "max=" + config.getMaximumPoolSize() + 
                          ", min=" + config.getMinimumIdle());
    }

    /**
     * Verifica se la connessione al database è già stata configurata.
     *
     * @return true se {@code configura} è già stato chiamato, false altrimenti
     */
    public static boolean isConfigurato() {
        return configurato;
    }

    // ==================== OTTENIMENTO CONNESSIONI ====================
    
    /**
     * Restituisce una connessione dal pool HikariCP.
     * Ogni chiamata prende una connessione già aperta dal pool.
     * La connessione deve essere chiusa con try-with-resources per
     * essere restituita al pool.
     *
     * @return un oggetto {@link Connection} dal pool
     * @throws SQLException se la connessione fallisce o se la configurazione
     *                       non è ancora stata effettuata
     */
    public static Connection getConnection() throws SQLException {
        if (!configurato || dataSource == null) {
            throw new SQLException("DataBaseManager non configurato: chiamare configura(...) prima.");
        }
        return dataSource.getConnection();
    }

    /**
     * Chiude il pool di connessioni (da chiamare allo spegnimento del server).
     */
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("✅ Pool di connessioni chiuso.");
        }
    }

    // ==================== TEST CONNESSIONE ====================
    
    /**
     * Testa la connessione al database usando il pool.
     *
     * @return true se la connessione funziona correttamente, false altrimenti
     */
    public static boolean testConnessione() {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Connessione al DB riuscita!");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Errore connessione DB: " + e.getMessage());
            System.err.println("Verifica che PostgreSQL sia avviato e che le credenziali siano corrette.");
            return false;
        }
    }

    // ==================== IMPORT DATASET MICHELIN ====================
    
    /**
     * Controlla se il dataset Michelin è già stato importato nel database.
     *
     * @return true se il dataset è già presente, false altrimenti
     */
    public static boolean isMichelinImportato() {
        String sql = "SELECT COUNT(*) FROM RistorantiTheKnife WHERE fonte = 'michelin'";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Errore controllo import Michelin: " + e.getMessage());
        }
        return false;
    }

    /**
     * Verifica se il file CSV del dataset Michelin esiste.
     *
     * @return true se il file esiste, false altrimenti
     */
    private static boolean fileCsvEsiste() {
        java.io.File file = new java.io.File(MICHELIN_CSV_PATH);
        if (!file.exists()) {
            System.err.println("⚠️ File CSV non trovato: " + MICHELIN_CSV_PATH);
            System.err.println("💡 Per specificare il percorso: -Dmichelin.csv.path=/percorso/completo");
            return false;
        }
        return true;
    }

    /**
     * Importa il dataset Michelin dal file CSV nel database.
     * Viene chiamato automaticamente all'avvio del server se il DB è vuoto.
     * Utilizza batch per ottimizzare le prestazioni.
     */
    public static void importaMichelin() {
        if (isMichelinImportato()) {
            System.out.println("Dataset Michelin già presente nel DB.");
            return;
        }

        if (!fileCsvEsiste()) {
            System.err.println("❌ Impossibile importare il dataset Michelin.");
            return;
        }

        System.out.println("📂 Importazione da: " + MICHELIN_CSV_PATH);
        System.out.println("Importazione dataset Michelin in corso...");

        String sql = "INSERT INTO RistorantiTheKnife " +
                     "(nome, indirizzo, citta, nazione, prezzo, tipo_cucina, " +
                     "longitudine, latitudine, delivery, prenotazione, fonte) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'michelin') " +
                     "ON CONFLICT (nome, citta, indirizzo) DO NOTHING";

        int importati = 0;
        int errori = 0;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             BufferedReader br = new BufferedReader(new FileReader(MICHELIN_CSV_PATH))) {

            conn.setAutoCommit(false);

            String line;
            boolean primaRiga = true;

            while ((line = br.readLine()) != null) {
                if (primaRiga) { primaRiga = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] fields = parseCsvLine(line);
                if (fields.length < 7) continue;

                try {
                    String name      = fields[0].trim();
                    String address   = fields[1].trim();
                    String location  = fields[2].trim();
                    String price     = fields[3].trim();
                    String cuisine   = fields[4].trim();
                    double longitude = Double.parseDouble(fields[5].trim());
                    double latitude  = Double.parseDouble(fields[6].trim());
                    boolean reservation = fields.length > 12 &&
                                         fields[12].toLowerCase().contains("booking");
                    String city   = estraiCitta(location);
                    String nation = estraiNazione(location);

                    if (name.isEmpty() || city.isEmpty()) continue;

                    ps.setString(1, name);
                    ps.setString(2, address);
                    ps.setString(3, city);
                    ps.setString(4, nation);
                    ps.setString(5, price);
                    ps.setString(6, cuisine);
                    ps.setDouble(7, longitude);
                    ps.setDouble(8, latitude);
                    ps.setBoolean(9, false);
                    ps.setBoolean(10, reservation);
                    ps.addBatch();

                    importati++;

                    if (importati % 500 == 0) {
                        ps.executeBatch();
                        conn.commit();
                        System.out.println("Importati " + importati + " ristoranti...");
                    }

                } catch (NumberFormatException e) {
                    errori++;
                }
            }

            ps.executeBatch();
            conn.commit();

            System.out.println("Importazione completata! " + importati + 
                              " ristoranti importati, " + errori + " righe saltate.");

        } catch (IOException e) {
            System.err.println("❌ Errore lettura CSV Michelin: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Errore import nel DB: " + e.getMessage());
        }
    }

    // ==================== METODI DI SUPPORTO PER L'IMPORT CSV ====================
    
    /**
     * Parsing di una riga CSV gestendo i campi tra virgolette.
     *
     * @param line la riga CSV da parsare
     * @return un array di stringhe contenente i campi
     */
    private static String[] parseCsvLine(String line) {
        java.util.ArrayList<String> fields = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString().trim());
        return fields.toArray(new String[0]);
    }

    /**
     * Estrae la città da una stringa di localizzazione.
     * La città è la parte prima dell'ultima virgola.
     *
     * @param location la stringa di localizzazione (es. "Milano, Italia")
     * @return la città estratta
     */
    private static String estraiCitta(String location) {
        int last = location.lastIndexOf(',');
        return last > 0 ? location.substring(0, last).trim() : location.trim();
    }

    /**
     * Estrae la nazione da una stringa di localizzazione.
     * La nazione è la parte dopo l'ultima virgola.
     *
     * @param location la stringa di localizzazione (es. "Milano, Italia")
     * @return la nazione estratta
     */
    private static String estraiNazione(String location) {
        int last = location.lastIndexOf(',');
        return (last > 0 && last < location.length() - 1)
               ? location.substring(last + 1).trim()
               : location.trim();
    }
}