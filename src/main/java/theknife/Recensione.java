/*
 * TheKnife - Recensione
 * Autori:
 *   Gharsellaoui Omema - 761146 - VA
 *   Kufura Razak - 760614 - VA
 */

package theknife;

/**
 * La classe <strong>Recensione</strong> rappresenta una recensione scritta da un cliente
 * su un ristorante all'interno della piattaforma <em>TheKnife</em>.
 *
 * <p>Nel Lab B le recensioni vengono salvate e lette dal database PostgreSQL
 * tramite {@link RecensioneDAO}, non più da file CSV.</p>
 *
 * <p>Ogni recensione contiene:
 * <ul>
 *   <li>Il nome del ristorante recensito</li>
 *   <li>Lo username del cliente autore</li>
 *   <li>Un testo opzionale</li>
 *   <li>Un voto in stelle (1–5)</li>
 *   <li>La data della recensione</li>
 *   <li>Un'eventuale risposta del ristoratore</li>
 * </ul>
 * </p>
 *
 * @author Gharsellaoui Omema
 * @author Kufura Razak
 * @version 1.0
 */
public class Recensione {

    // ==================== ATTRIBUTI ====================
    
    /** Nome del ristorante recensito. */
    private String nomeRistorante;
    
    /** Username del cliente autore della recensione. */
    private String usernameCliente;
    
    /** Voto in stelle (1.0 - 5.0). */
    private double stelle;
    
    /** Testo della recensione (opzionale). */
    private String testoRecensione;
    
    /** Data della recensione. */
    private String data;
    
    /** Risposta del ristoratore (null se non presente). */
    private String risposta;

    // ==================== COSTRUTTORE ====================
    
    /**
     * Costruisce una nuova recensione con tutti i dati.
     *
     * @param nomeRistorante  il nome del ristorante recensito
     * @param usernameCliente lo username del cliente autore
     * @param testoRecensione il testo della recensione (opzionale)
     * @param stelle          il voto in stelle (1-5)
     * @param data            la data della recensione
     * @param risposta        l'eventuale risposta del ristoratore
     */
    public Recensione(String nomeRistorante, String usernameCliente,
                      String testoRecensione, double stelle,
                      String data, String risposta) {
        this.nomeRistorante  = nomeRistorante;
        this.usernameCliente = usernameCliente;
        this.testoRecensione = testoRecensione;
        this.stelle          = stelle;
        this.data            = data;
        this.risposta        = risposta;
    }

    // ==================== GETTER E SETTER ====================
    
    /**
     * Restituisce il nome del ristorante recensito.
     *
     * @return il nome del ristorante
     */
    public String getNomeRistorante() {
        return nomeRistorante;
    }
    
    /**
     * Imposta il nome del ristorante recensito.
     *
     * @param nomeRistorante il nome del ristorante
     */
    public void setNomeRistorante(String nomeRistorante) {
        this.nomeRistorante = nomeRistorante;
    }

    /**
     * Restituisce lo username del cliente autore della recensione.
     *
     * @return lo username del cliente
     */
    public String getUsernameCliente() {
        return usernameCliente;
    }

    /**
     * Imposta lo username del cliente autore della recensione.
     *
     * @param usernameCliente lo username del cliente
     */
    public void setUsernameCliente(String usernameCliente) {
        this.usernameCliente = usernameCliente;
    }
    
    /**
     * Restituisce il voto in stelle della recensione.
     *
     * @return il voto in stelle (1.0-5.0)
     */
    public double getStelle() {
        return stelle;
    }
    
    /**
     * Imposta il voto in stelle della recensione.
     *
     * @param stelle il voto in stelle (1-5)
     */
    public void setStelle(double stelle) {
        this.stelle = stelle;
    }

    /**
     * Restituisce il testo della recensione.
     *
     * @return il testo della recensione
     */
    public String getTestoRecensione() {
        return testoRecensione;
    }
    
    /**
     * Imposta il testo della recensione.
     *
     * @param testoRecensione il nuovo testo
     */
    public void setTestoRecensione(String testoRecensione) {
        this.testoRecensione = testoRecensione;
    }

    /**
     * Restituisce la data della recensione.
     *
     * @return la data della recensione
     */
    public String getData() {
        return data;
    }
    
    /**
     * Imposta la data della recensione.
     *
     * @param data la nuova data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Restituisce la risposta del ristoratore alla recensione.
     *
     * @return la risposta del ristoratore, o null se non presente
     */
    public String getRisposta() {
        return risposta;
    }
    
    /**
     * Imposta la risposta del ristoratore alla recensione.
     *
     * @param risposta la risposta del ristoratore
     */
    public void setRisposta(String risposta) {
        this.risposta = risposta;
    }
    
    // ==================== METODI ====================

    /**
     * Restituisce una rappresentazione leggibile della recensione
     * per la visualizzazione lato cliente, con stelle in simboli.
     *
     * @return stringa con dettagli della recensione
     */
    private String visualizzaPerCliente() {
        String stelleStr = "";
        for (int i = 0; i < (int) stelle; i++) stelleStr += "*";

        String base = "Ristorante: " + nomeRistorante +
                      "\nVoto: " + stelleStr + " (" + stelle + "/5)" +
                      "\nData: " + data +
                      "\nRecensione: " + testoRecensione;
        if (risposta != null && !risposta.isEmpty()) {
            base += "\nRisposta del ristoratore: \"" + risposta + "\"";
        }
        return base;
    }

    /**
     * Override di toString() per la visualizzazione lato cliente.
     *
     * @return stringa rappresentativa della recensione
     */
    @Override
    public String toString() {
        return visualizzaPerCliente();
    }
}