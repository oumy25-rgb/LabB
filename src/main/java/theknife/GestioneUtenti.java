/*
 * TheKnife - GestioneUtenti
 * Autori:
 *   Gharsellaoui Omema - 761146 - VA
 *   Kufura Razak - 760614 - VA
 */

package theknife;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * La classe <strong>GestioneUtenti</strong> fornisce i metodi
 * per gestire gli utenti della piattaforma <em>TheKnife</em>.
 *
 * <p>Nel Lab B tutti i dati vengono letti e scritti nel database
 * PostgreSQL tramite {@link UtenteDAO}, non più su file CSV.</p>
 *
 * <p><strong>Design pattern:</strong> questa classe implementa il pattern
 * <em>Singleton</em> — esiste una sola istanza condivisa nell'applicazione,
 * accessibile tramite {@link #getInstance()}.</p>
 *
 * <p>Le principali funzionalità includono:
 * <ul>
 *   <li>Registrazione di un nuovo utente</li>
 *   <li>Login con credenziali (username e password)</li>
 *   <li>Verifica della validità di campi e formati inseriti</li>
 *   <li>Cifratura sicura della password con SHA-256</li>
 *   <li>Controllo dell'esistenza di username già registrati</li>
 * </ul>
 * </p>
 *
 * @author Gharsellaoui Omema
 * @author Kufura Razak
 * @version 1.0
 */
public class GestioneUtenti {

    // ==================== SINGLETON ====================
    
    /** Unica istanza della classe (Singleton). */
    private static GestioneUtenti instance;

    /**
     * Costruttore privato: impedisce la creazione diretta di istanze
     * dall'esterno (pattern Singleton).
     */
    private GestioneUtenti() {}

    /**
     * Restituisce l'unica istanza di {@link GestioneUtenti}.
     * Se non esiste ancora, la crea (lazy initialization).
     * Il blocco {@code synchronized} garantisce la thread-safety
     * nel contesto multi-client del server.
     *
     * @return l'istanza Singleton di GestioneUtenti
     */
    public static synchronized GestioneUtenti getInstance() {
        if (instance == null) {
            instance = new GestioneUtenti();
        }
        return instance;
    }

    // ==================== LOGIN ====================
    
    /**
     * Verifica le credenziali di un utente e lo restituisce se valide.
     * Punto di accesso unico (Singleton) per il login, usato da {@link SlaveThread}.
     *
     * @param username        lo username inserito
     * @param passwordCifrata la password già cifrata con SHA-256
     * @return l'oggetto {@link Utente} se le credenziali sono corrette,
     *         {@code null} altrimenti
     */
    public Utente login(String username, String passwordCifrata) {
        return UtenteDAO.login(username, passwordCifrata);
    }

    // ==================== REGISTRAZIONE ====================
    
    /**
     * Registra un nuovo utente nel database tramite {@link UtenteDAO}.
     *
     * <p>Questo è il punto di accesso unico (Singleton) per la registrazione:
     * tutti i chiamanti (es. {@link SlaveThread}) devono passare da
     * {@link #getInstance()} invece di invocare {@link UtenteDAO} direttamente.</p>
     *
     * @param utente l'utente da registrare
     * @return {@code true} se la registrazione è andata a buon fine,
     *         {@code false} altrimenti (es. username già esistente)
     */
    public boolean registraUtente(Utente utente) {
        boolean ok = UtenteDAO.registraUtente(utente);
        if (ok) {
            System.out.println("Registrazione avvenuta con successo!");
        } else {
            System.out.println("Errore durante la registrazione. Riprova.");
        }
        return ok;
    }

    // ==================== CIFRATURA PASSWORD (SHA-256) ====================
    
    /**
     * Cifra una password usando l'algoritmo SHA-256.
     *
     * <p>SHA-256 è una funzione hash crittografica a senso unico: la password
     * originale non può essere ricavata dall'hash. Al login si confronta
     * l'hash della password inserita con quello salvato nel DB.</p>
     *
     * @param input la password in chiaro
     * @return l'hash SHA-256 in formato esadecimale (64 caratteri),
     *         oppure la stringa originale in caso di errore imprevisto
     */
    public static String cifraPassword(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Errore cifratura password: " + e.getMessage());
            return input;
        }
    }

    // ==================== VALIDAZIONE ====================
    
    /**
     * Verifica se un nominativo è valido:
     * non vuoto e composto solo da lettere o spazi.
     *
     * @param nominativo la stringa da verificare
     * @return true se valido, false altrimenti
     */
    public static boolean nominativoValido(String nominativo) {
        boolean controllo = true;
        if (GestioneUtenti.campoNonVuoto(nominativo)) {
            for (int i = 0; i < nominativo.length(); i++) {
                if (!Character.isLetter(nominativo.charAt(i)) && nominativo.charAt(i) != ' ') {
                    System.out.println("Non puoi inserire numeri o simboli, riprova.");
                    controllo = false;
                    break;
                }
            }
        } else {
            System.out.println("Non puoi lasciare il campo vuoto, riprova.");
            controllo = false;
        }
        return controllo;
    }

    /**
     * Verifica se una stringa rispetta un determinato formato (regex).
     *
     * @param s     la stringa da verificare
     * @param regex l'espressione regolare da rispettare
     * @return true se valido, false altrimenti
     */
    public static boolean formatoValido(String s, String regex) {
        boolean controllo = true;
        s = s.toUpperCase().trim();
        if (!s.matches(regex)) {
            System.out.println("Formato non valido, riprova.");
            controllo = false;
        }
        return controllo;
    }

    /**
     * Verifica che una stringa non sia vuota o nulla.
     *
     * @param s la stringa da controllare
     * @return true se non vuota, false altrimenti
     */
    public static boolean campoNonVuoto(String s) {
        if (s == null || s.isEmpty()) {
            System.out.println("Non puoi lasciare il campo vuoto, riprova.");
            return false;
        }
        return true;
    }

    /**
     * Controlla se una stringa rappresenta una longitudine valida (-180 a 180).
     *
     * @param longi la stringa con il valore di longitudine
     * @return true se valida, false altrimenti
     */
    public static boolean isLongitudineValida(String longi) {
        try {
            double val = Double.parseDouble(longi.replace(",", ".").trim());
            return val >= -180 && val <= 180;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Controlla se una stringa rappresenta una latitudine valida (-90 a 90).
     *
     * @param lati la stringa con il valore di latitudine
     * @return true se valida, false altrimenti
     */
    public static boolean isLatitudineValida(String lati) {
        try {
            double val = Double.parseDouble(lati.replace(",", ".").trim());
            return val >= -90 && val <= 90;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Verifica se uno username è già registrato nel database.
     *
     * @param user lo username da controllare
     * @return true se esiste già, false altrimenti
     */
    public static boolean userEsiste(String user) {
        boolean esiste = UtenteDAO.usernameEsiste(user);
        if (esiste) System.out.println("Questo username è già presente! Riprova.");
        return esiste;
    }
}