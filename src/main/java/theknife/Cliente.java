/*
 * TheKnife - Cliente
 * Autori:
 *   Gharsellaoui Omema - 761146 - VA
 *   Kufura Razak - 760614 - VA
 */

package theknife;

/**
 * Rappresenta un cliente registrato nella piattaforma TheKnife.
 * Estende {@link Utente} con ruolo "cliente".
 *
 * <p>Il cliente può:
 * <ul>
 *   <li>Cercare ristoranti</li>
 *   <li>Gestire una lista di ristoranti preferiti</li>
 *   <li>Scrivere, modificare ed eliminare recensioni</li>
 *   <li>Visualizzare le proprie recensioni</li>
 * </ul>
 * </p>
 *
 * @author Gharsellaoui Omema
 * @author Kufura Razak
 * @version 1.0
 */
public class Cliente extends Utente {

    /**
     * Costruisce un nuovo cliente con tutti i dati anagrafici e di accesso.
     * Il ruolo viene impostato automaticamente a "cliente".
     *
     * @param nome           il nome del cliente
     * @param cognome        il cognome del cliente
     * @param username       lo username univoco del cliente
     * @param password       la password cifrata con SHA-256
     * @param dataNascita    la data di nascita (opzionale)
     * @param luogoDomicilio il luogo di domicilio del cliente
     */
    public Cliente(String nome, String cognome, String username,
                   String password, String dataNascita, String luogoDomicilio) {
        super(nome, cognome, username, password, dataNascita, luogoDomicilio, "cliente");
    }
}