/*
 * TheKnife - Ristoratore
 * Autori:
 *   Gharsellaoui Omema - 761146 - VA
 *   Kufura Razak - 760614 - VA
 */

package theknife;

/**
 * Rappresenta un ristoratore registrato nella piattaforma TheKnife.
 * Estende {@link Utente} con ruolo "ristoratore".
 *
 * <p>Il ristoratore può:
 * <ul>
 *   <li>Aggiungere nuovi ristoranti</li>
 *   <li>Visualizzare i propri ristoranti</li>
 *   <li>Visualizzare le recensioni ricevute</li>
 *   <li>Rispondere alle recensioni dei clienti</li>
 *   <li>Visualizzare il riepilogo delle recensioni (media e numero)</li>
 * </ul>
 * </p>
 *
 * @author Gharsellaoui Omema
 * @author Kufura Razak
 * @version 1.0
 */
public class Ristoratore extends Utente {

    /**
     * Costruisce un nuovo ristoratore con tutti i dati anagrafici e di accesso.
     * Il ruolo viene impostato automaticamente a "ristoratore".
     *
     * @param nome           il nome del ristoratore
     * @param cognome        il cognome del ristoratore
     * @param username       lo username univoco del ristoratore
     * @param password       la password cifrata con SHA-256
     * @param dataNascita    la data di nascita (opzionale)
     * @param luogoDomicilio il luogo di domicilio del ristoratore
     */
    public Ristoratore(String nome, String cognome, String username,
                       String password, String dataNascita, String luogoDomicilio) {
        super(nome, cognome, username, password, dataNascita, luogoDomicilio, "ristoratore");
    }
}