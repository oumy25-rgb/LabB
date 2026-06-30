/*
 * TheKnife - UtenteFactory
 * Autori:
 *   Gharsellaoui Omema - 761146 - VA
 *   Kufura Razak - 760614 - VA
 */

package theknife;

/**
 * Factory per la creazione di oggetti {@link Utente} (Cliente o Ristoratore).
 * Centralizza la logica di istanziazione in base al ruolo.
 *
 * @author Gharsellaoui Omema
 * @author Kufura Razak
 * @version 1.0
 */
public class UtenteFactory {

    /**
     * Costruttore privato: la classe espone solo il metodo statico.
     */
    private UtenteFactory() {
    }

    /**
     * Crea l'istanza concreta di Utente (Cliente o Ristoratore) in base al ruolo.
     *
     * @param ruolo          "cliente" oppure "ristoratore"
     * @param nome           il nome dell'utente
     * @param cognome        il cognome dell'utente
     * @param username       lo username univoco
     * @param password       la password (già cifrata)
     * @param dataNascita    la data di nascita (opzionale, può essere "N/A")
     * @param luogoDomicilio il luogo di domicilio
     * @return una nuova istanza di Cliente o Ristoratore
     * @throws IllegalArgumentException se il ruolo non è riconosciuto
     */
    public static Utente createUtente(String ruolo, String nome, String cognome,
                                       String username, String password,
                                       String dataNascita, String luogoDomicilio) {
        if (ruolo == null) {
            throw new IllegalArgumentException("Ruolo non specificato");
        }
        switch (ruolo) {
            case "cliente":
                return new Cliente(nome, cognome, username, password, dataNascita, luogoDomicilio);
            case "ristoratore":
                return new Ristoratore(nome, cognome, username, password, dataNascita, luogoDomicilio);
            default:
                throw new IllegalArgumentException("Ruolo sconosciuto: " + ruolo);
        }
    }
}