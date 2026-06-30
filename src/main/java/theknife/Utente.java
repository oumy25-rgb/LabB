/*
 * TheKnife - Utente
 * Autori:
 *   Gharsellaoui Omema - 761146 - VA
 *   Kufura Razak - 760614 - VA
 */

package theknife;

/**
 * Classe astratta che rappresenta un utente della piattaforma TheKnife.
 * <p>
 * Identificativo principale: {@code username} (univoco nel DB).
 * Il codice fiscale non è richiesto dalle specifiche del progetto
 * ed è stato rimosso per supportare utenti internazionali.
 * </p>
 * <p>
 * Questa classe è estesa da {@link Cliente} e {@link Ristoratore}
 * che specializzano il comportamento in base al ruolo.
 * </p>
 *
 * @author Gharsellaoui Omema
 * @author Kufura Razak
 * @version 1.0
 */
public abstract class Utente {

    // ==================== ATTRIBUTI ====================
    
    /** Nome dell'utente. */
    private String nome;
    
    /** Cognome dell'utente. */
    private String cognome;
    
    /** Username univoco dell'utente (usato come identificativo per il login). */
    private String username;
    
    /** Password cifrata con SHA-256. */
    private String password;
    
    /** Data di nascita dell'utente (opzionale, "N/A" se non fornita). */
    private String dataNascita;
    
    /** Luogo di domicilio dell'utente. */
    private String luogoDomicilio;
    
    /** Ruolo dell'utente ("cliente" o "ristoratore"). */
    private String ruolo;

    // ==================== COSTRUTTORE ====================
    
    /**
     * Costruisce un nuovo utente con tutti i dati anagrafici e di accesso.
     *
     * @param nome           il nome dell'utente
     * @param cognome        il cognome dell'utente
     * @param username       lo username univoco (usato come identificativo)
     * @param password       la password cifrata con SHA-256
     * @param dataNascita    la data di nascita (opzionale, "N/A" se non fornita)
     * @param luogoDomicilio il luogo di domicilio dell'utente
     * @param ruolo          il ruolo dell'utente ("cliente" o "ristoratore")
     */
    public Utente(String nome, String cognome, String username, String password,
                  String dataNascita, String luogoDomicilio, String ruolo) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
        this.dataNascita = (dataNascita != null && !dataNascita.trim().isEmpty())
                           ? dataNascita : "N/A";
        this.luogoDomicilio = luogoDomicilio;
        this.ruolo = ruolo;
    }

    // ==================== GETTER ====================
    
    /**
     * Restituisce il nome dell'utente.
     *
     * @return il nome dell'utente
     */
    public String getNome() {
        return nome;
    }
    
    /**
     * Restituisce il cognome dell'utente.
     *
     * @return il cognome dell'utente
     */
    public String getCognome() {
        return cognome;
    }
    
    /**
     * Restituisce lo username dell'utente.
     *
     * @return lo username dell'utente
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Restituisce la password cifrata dell'utente.
     *
     * @return la password cifrata con SHA-256
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Restituisce la data di nascita dell'utente.
     *
     * @return la data di nascita, o "N/A" se non fornita
     */
    public String getDataNascita() {
        return dataNascita;
    }
    
    /**
     * Restituisce il luogo di domicilio dell'utente.
     *
     * @return il luogo di domicilio
     */
    public String getLuogoDomicilio() {
        return luogoDomicilio;
    }
    
    /**
     * Restituisce il ruolo dell'utente.
     *
     * @return il ruolo ("cliente" o "ristoratore")
     */
    public String getRuolo() {
        return ruolo;
    }
}