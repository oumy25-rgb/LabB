/*
 * TheKnife - SlaveThread
 * Autori:
 *   Gharsellaoui Omema - 761146 - VA
 *   Kufura Razak - 760614 - VA
 */

package theknife;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * SlaveThread - Gestisce la comunicazione con un singolo client connesso al server.
 * Ogni connessione client ha il proprio thread dedicato che rimane attivo
 * per tutto il tempo della sessione.
 *
 * <p>Il thread riceve comandi dal client tramite ObjectInputStream, li esegue
 * interagendo con i DAO e il database, e restituisce le risposte al client
 * tramite ObjectOutputStream.</p>
 *
 * <p><strong>Comandi supportati:</strong> login, registra, userEsiste, getDomicilio,
 * cercaPerCitta, cercaPerCucina, cercaPerPrezzo, cercaMultiCriterio, cercaVicini,
 * dettaglioRistorante, aggiungiRecensione, modificaRecensione, eliminaRecensione,
 * recensioniRistorante, recensioniCliente, getPreferiti, aggiungiPreferito,
 * rimuoviPreferito, getMieiRistoranti, aggiungiRistoranteCustom,
 * rispondiARecensione, mediaRistorante, verificaEsistenzaRistorante, end.</p>
 *
 * @author Gharsellaoui Omema
 * @author Kufura Razak
 * @version 1.0
 */
public class SlaveThread extends Thread {
    
    // ==================== ATTRIBUTI ====================
    
    /** Socket per la comunicazione con il client. */
    private Socket socket;
    
    /** Stream di input per ricevere oggetti serializzati dal client. */
    private ObjectInputStream in;
    
    /** Stream di output per inviare oggetti serializzati al client. */
    private ObjectOutputStream out;
    
    /** Set dei comandi riconosciuti dal server per la validazione. */
    private static final java.util.Set<String> COMANDI_NOTI = java.util.Set.of(
        "end", "login", "registra", "userEsiste", "getDomicilio",
        "cercaPerCitta", "cercaPerCucina", "cercaPerPrezzo",
        "cercaMultiCriterio", "cercaVicini", "dettaglioRistorante",
        "aggiungiRecensione", "modificaRecensione", "eliminaRecensione",
        "recensioniRistorante", "recensioniCliente",
        "getPreferiti", "aggiungiPreferito", "rimuoviPreferito",
        "getMieiRistoranti", "aggiungiRistoranteCustom",
        "rispondiARecensione", "mediaRistorante", "verificaEsistenzaRistorante"
    );

    // ==================== COSTRUTTORE ====================
    
    /**
     * Costruisce un nuovo SlaveThread per un client connesso.
     * Inizializza gli stream di input e output per la comunicazione
     * con il client specificato.
     *
     * @param s il socket del client connesso
     * @throws IOException se la creazione degli stream di input/output fallisce
     */
    public SlaveThread(Socket s) throws IOException {
        socket = s;
        out = new ObjectOutputStream(s.getOutputStream());
        out.flush();
        in = new ObjectInputStream(s.getInputStream());
    }

    // ==================== METODI PRIVATI ====================
    
    /**
     * Invia una risposta al client attraverso l'ObjectOutputStream.
     * La risposta può essere una String o un oggetto serializzabile.
     * Il metodo si occupa di flushare lo stream dopo la scrittura.
     *
     * @param obj l'oggetto da inviare al client
     * @throws IOException se la scrittura sullo stream fallisce
     */
    private void sendResponse(Object obj) throws IOException {
        out.writeObject(obj);
        out.flush();
    }

    // ==================== METODO PRINCIPALE ====================
    
    /**
     * Esegue il thread di gestione del client.
     * Rimane in attesa di comandi dal client finché non riceve il comando "end"
     * o fino a quando la connessione non viene interrotta.
     *
     * <p>Per ogni comando ricevuto:
     * <ul>
     *   <li>Verifica che il comando sia riconosciuto</li>
     *   <li>Esegue il comando tramite {@link #eseguiComando(String)}</li>
     *   <li>Gestisce eventuali eccezioni e invia un messaggio di errore al client</li>
     * </ul>
     * </p>
     */
    @Override
    public void run() {
        String command;
        try {
            while (true) {
                try {
                    command = (String) in.readObject();
                } catch (ClassNotFoundException e) {
                    System.err.println("Errore lettura comando: " + e.getMessage());
                    sendResponse("ERRORE|Comando non valido");
                    continue;
                }
                
                if (command.equals("end")) {
                    System.out.println("Client " + socket.getInetAddress() + " disconnesso");
                    break;
                }

                if (!COMANDI_NOTI.contains(command)) {
                    System.err.println("Comando non riconosciuto: " + command);
                    sendResponse("ERRORE|Comando non riconosciuto");
                    continue;
                }
                
                try {
                    eseguiComando(command);
                } catch (ClassNotFoundException | IOException e) {
                    System.err.println("Errore durante l'esecuzione del comando " + command + ": " + e.getMessage());
                    sendResponse("ERRORE|Errore interno del server");
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nella comunicazione con il client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Errore chiusura socket: " + e.getMessage());
            }
        }
    }
    
    // ==================== ESECUZIONE COMANDI ====================
    
    /**
     * Esegue il comando ricevuto dal client.
     * Legge i parametri dal flusso di input, esegue l'operazione
     * richiesta e invia la risposta al client.
     *
     * @param command il comando da eseguire
     * @throws IOException se la lettura/scrittura degli stream fallisce
     * @throws ClassNotFoundException se la deserializzazione di un oggetto fallisce
     */
    private void eseguiComando(String command) throws IOException, ClassNotFoundException {
        
        // ============================================================
        // COMANDI: UTENTI
        // ============================================================
        
        /**
         * Comando "login" - Autentica un utente.
         * Riceve username e password dal client, cifra la password con SHA-256,
         * verifica le credenziali tramite GestioneUtenti e restituisce
         * username, nome e ruolo dell'utente autenticato.
         *
         * Parametri attesi: (String username, String password)
         * Risposta: "OK|username|nome|ruolo" oppure "ERRORE|Credenziali errate"
         */
        if (command.equals("login")) {
            String username = (String) in.readObject();
            String password = (String) in.readObject();
            System.out.println("Login richiesto per: " + username);

            password = GestioneUtenti.cifraPassword(password);
            Utente user = GestioneUtenti.getInstance().login(username, password);

            if (user == null) {
                sendResponse("ERRORE|Credenziali errate");
            } else {
                sendResponse("OK|" + user.getUsername() + "|" + user.getNome() + "|" + user.getRuolo());
            }
        }
        
        /**
         * Comando "getDomicilio" - Recupera il domicilio di un utente.
         *
         * Parametri attesi: (String username)
         * Risposta: "OK|domicilio" oppure "ERRORE|Domicilio non trovato"
         */
        if (command.equals("getDomicilio")) {
            String username = (String) in.readObject();
            String domicilio = UtenteDAO.getDomicilio(username);
            if (domicilio != null && !domicilio.isEmpty()) {
                sendResponse("OK|" + domicilio);
            } else {
                sendResponse("ERRORE|Domicilio non trovato");
            }
        }
        
        /**
         * Comando "registra" - Registra un nuovo utente.
         * Riceve tutti i dati del nuovo utente, cifra la password,
         * crea l'oggetto Utente tramite UtenteFactory e lo registra.
         *
         * Parametri attesi: (String nome, String cognome, String username,
         *                   String password, String dataNascita,
         *                   String luogoDomicilio, String ruolo)
         * Risposta: "OK|Registrazione completata" oppure "ERRORE|Username già in uso"
         */
        if (command.equals("registra")) {
            try {
                String nome          = (String) in.readObject();
                String cognome       = (String) in.readObject();
                String username      = (String) in.readObject();
                String password      = (String) in.readObject();
                String dataNascita   = (String) in.readObject();
                String luogoDomicilio = (String) in.readObject();
                String ruolo         = (String) in.readObject();

                password = GestioneUtenti.cifraPassword(password);

                Utente nuovo = UtenteFactory.createUtente(ruolo, nome, cognome, username,
                        password, dataNascita, luogoDomicilio);

                boolean ok = GestioneUtenti.getInstance().registraUtente(nuovo);
                sendResponse(ok ? "OK|Registrazione completata" : "ERRORE|Username già in uso");
            } catch (Exception e) {
                System.err.println("Errore registrazione: " + e.getMessage());
                sendResponse("ERRORE|" + e.getMessage());
            }
        }
        
        /**
         * Comando "userEsiste" - Verifica se uno username è già registrato.
         *
         * Parametri attesi: (String username)
         * Risposta: "OK|true" oppure "OK|false"
         */
        if (command.equals("userEsiste")) {
            String user = (String) in.readObject();
            boolean esiste = GestioneUtenti.userEsiste(user);
            sendResponse(esiste ? "OK|true" : "OK|false");
        }
        
        // ============================================================
        // COMANDI: RISTORANTI
        // ============================================================
        
        /**
         * Comando "cercaPerCitta" - Cerca ristoranti per città.
         *
         * Parametri attesi: (String citta)
         * Risposta: "OK|count|nome1|citta1|cucina1|nome2|citta2|cucina2|..."
         */
        if (command.equals("cercaPerCitta")) {
            String citta = (String) in.readObject();
            ArrayList<Ristorante> ristoranti = RistoranteDAO.cercaPerCitta(citta);
            sendResponse(ristorantiToObject(ristoranti));
        }
        
        /**
         * Comando "cercaPerCucina" - Cerca ristoranti per città e tipo di cucina.
         *
         * Parametri attesi: (String citta, String cucina)
         * Risposta: "OK|count|nome1|citta1|cucina1|nome2|citta2|cucina2|..."
         */
        if (command.equals("cercaPerCucina")) {
            String citta = (String) in.readObject();
            String cucina = (String) in.readObject();
            ArrayList<Ristorante> ristoranti = RistoranteDAO.cercaPerCittaECucina(citta, cucina);
            sendResponse(ristorantiToObject(ristoranti));
        }
        
        /**
         * Comando "cercaPerPrezzo" - Cerca ristoranti per città e fascia di prezzo.
         *
         * Parametri attesi: (String citta, double prezzoMin, double prezzoMax)
         * Risposta: "OK|count|nome1|citta1|cucina1|nome2|citta2|cucina2|..."
         */
        if (command.equals("cercaPerPrezzo")) {
            String citta = (String) in.readObject();
            double min = (double) in.readObject();
            double max = (double) in.readObject();
            ArrayList<Ristorante> ristoranti = RistoranteDAO.cercaPerCittaEPrezzo(citta, min, max);
            sendResponse(ristorantiToObject(ristoranti));
        }

        // ============================================================
        // COMANDI: RICERCA MULTI-CRITERIO E VICINANZA
        // ============================================================
        
        /**
         * Comando "cercaMultiCriterio" - Ricerca multi-criterio con tutti i filtri.
         *
         * <p>I filtri disponibili sono: luogo (obbligatorio), cucina,
         * prezzoMin, prezzoMax, delivery, prenotazione, mediaMin.</p>
         *
         * @param citta        la città (obbligatoria)
         * @param cucina       il tipo di cucina (null = qualsiasi)
         * @param prezzoMin    il prezzo minimo (null = nessun filtro)
         * @param prezzoMax    il prezzo massimo (null = nessun filtro)
         * @param delivery     true/false per filtro delivery (null = qualsiasi)
         * @param prenotazione true/false per filtro prenotazione (null = qualsiasi)
         * @param mediaMin     la media stelle minima (null = nessun filtro)
         * @return lista di RistoranteDTO
         */
        if (command.equals("cercaMultiCriterio")) {
            String citta        = (String) in.readObject();
            String cucina       = (String) in.readObject();
            Double prezzoMin    = (Double) in.readObject();
            Double prezzoMax    = (Double) in.readObject();
            Boolean delivery    = (Boolean) in.readObject();
            Boolean prenotazione = (Boolean) in.readObject();
            Double mediaMin     = (Double) in.readObject();

            ArrayList<Ristorante> ristoranti = RistoranteDAO.cercaTuttiCriteri(
                citta,
                (cucina == null || cucina.isEmpty()) ? null : cucina,
                prezzoMin == null ? -1 : prezzoMin,
                prezzoMax == null ? -1 : prezzoMax,
                delivery,
                prenotazione,
                mediaMin == null ? -1 : mediaMin
            );

            sendResponse(toDTOList(ristoranti));
        }

        /**
         * Comando "cercaVicini" - Cerca ristoranti vicini a un luogo.
         * Utilizza la formula di Haversine per il calcolo della distanza.
         *
         * @param luogo    il nome del luogo (città)
         * @param raggioKm il raggio di ricerca in km
         * @return lista di RistoranteDTO ordinati per distanza crescente
         */
        if (command.equals("cercaVicini")) {
            String luogo = (String) in.readObject();
            double raggioKm = (double) in.readObject();

            ArrayList<Ristorante> ristoranti = RistoranteDAO.cercaRistorantiViciniPerLuogo(luogo, raggioKm);
            sendResponse(toDTOList(ristoranti));
        }
        
        /**
         * Comando "dettaglioRistorante" - Recupera i dettagli di un ristorante per nome.
         *
         * @param nome il nome del ristorante
         * @return "OK|nome|indirizzo|citta|nazione|prezzo|cucina|longitudine|latitudine|delivery|prenotazione"
         *         oppure "ERRORE|Ristorante non trovato"
         */
        if (command.equals("dettaglioRistorante")) {
            String nome = (String) in.readObject();
            Ristorante r = RistoranteDAO.cercaPerNome(nome);
            if (r == null) {
                sendResponse("ERRORE|Ristorante non trovato");
            } else {
                sendResponse("OK|" + r.getName() + "|" + r.getAddress() + "|" + r.getCity() + 
                               "|" + r.getNation() + "|" + r.getPrice() + "|" + r.getCuisine() +
                               "|" + r.getLongitude() + "|" + r.getLatitude() +
                               "|" + r.getDelivery() + "|" + r.getReservation());
            }
        }
        
        // ============================================================
        // COMANDI: RECENSIONI
        // ============================================================
        
        /**
         * Comando "aggiungiRecensione" - Aggiunge una nuova recensione.
         * Un cliente può recensire un ristorante una sola volta.
         *
         * @param nomeRistorante il nome del ristorante
         * @param username       lo username del cliente
         * @param testo          il testo della recensione
         * @param stelle         il voto in stelle (1-5)
         * @return "OK|Recensione aggiunta" oppure "ERRORE|Recensione non aggiunta"
         */
        if (command.equals("aggiungiRecensione")) {
            String nomeRistorante = (String) in.readObject();
            String username = (String) in.readObject();
            String testo = (String) in.readObject();
            int stelle = (int) in.readObject();
            boolean ok = RecensioneDAO.aggiungiRecensione(nomeRistorante, username, testo, stelle);
            sendResponse(ok ? "OK|Recensione aggiunta" : "ERRORE|Recensione non aggiunta");
        }
        
        /**
         * Comando "modificaRecensione" - Modifica una recensione esistente.
         *
         * @param nomeRistorante il nome del ristorante
         * @param username       lo username del cliente
         * @param nuovoTesto     il nuovo testo della recensione
         * @param nuoveStelle    il nuovo voto (1-5)
         * @return "OK|Recensione modificata" oppure "ERRORE|Modifica fallita"
         */
        if (command.equals("modificaRecensione")) {
            String nomeRistorante = (String) in.readObject();
            String username = (String) in.readObject();
            String nuovoTesto = (String) in.readObject();
            int nuoveStelle = (int) in.readObject();
            boolean ok = RecensioneDAO.modificaRecensione(nomeRistorante, username, nuovoTesto, nuoveStelle);
            sendResponse(ok ? "OK|Recensione modificata" : "ERRORE|Modifica fallita");
        }
        
        /**
         * Comando "eliminaRecensione" - Elimina una recensione.
         * La risposta associata viene eliminata in cascata (CASCADE).
         *
         * @param nomeRistorante il nome del ristorante
         * @param username       lo username del cliente
         * @return "OK|Recensione eliminata" oppure "ERRORE|Eliminazione fallita"
         */
        if (command.equals("eliminaRecensione")) {
            String nomeRistorante = (String) in.readObject();
            String username = (String) in.readObject();
            boolean ok = RecensioneDAO.eliminaRecensione(nomeRistorante, username);
            sendResponse(ok ? "OK|Recensione eliminata" : "ERRORE|Eliminazione fallita");
        }
        
        /**
         * Comando "recensioniRistorante" - Recupera tutte le recensioni di un ristorante.
         *
         * @param nomeRistorante il nome del ristorante
         * @return lista di Recensione serializzata
         */
        if (command.equals("recensioniRistorante")) {
            String nomeRistorante = (String) in.readObject();
            ArrayList<Recensione> recensioni = RecensioneDAO.getRecensioniRistorante(nomeRistorante);
            sendResponse(recensioniToObject(recensioni));
        }
        
        /**
         * Comando "recensioniCliente" - Recupera tutte le recensioni di un cliente.
         *
         * @param username lo username del cliente
         * @return lista di Recensione serializzata in formato stringa
         */
        if (command.equals("recensioniCliente")) {
            String username = (String) in.readObject();
            ArrayList<Recensione> recensioni = RecensioneDAO.getRecensioniCliente(username);

            if (recensioni.isEmpty()) {
                sendResponse("OK|0|");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("OK|").append(recensioni.size());

                for (Recensione r : recensioni) {
                    String nomeRistorante = r.getNomeRistorante();
                    String testo = r.getTestoRecensione();
                    if (testo == null) testo = "";
                    double stelle = r.getStelle();
                    String data = r.getData();
                    String risposta = r.getRisposta();
                    if (risposta == null) risposta = "";

                    sb.append("|").append(nomeRistorante)
                      .append("|").append(testo)
                      .append("|").append(stelle)
                      .append("|").append(data)
                      .append("|").append(risposta);
                }
                sendResponse(sb.toString());
            }
        }
        
        // ============================================================
        // COMANDI: PREFERITI
        // ============================================================
        
        /**
         * Comando "getPreferiti" - Recupera la lista dei ristoranti preferiti di un cliente.
         *
         * @param username lo username del cliente
         * @return "OK|count|nome1|nome2|..." oppure "OK|0|"
         */
        if (command.equals("getPreferiti")) {
            String username = (String) in.readObject();
            ArrayList<String> preferiti = RecensioneDAO.getPreferiti(username);
            if (preferiti.isEmpty()) {
                sendResponse("OK|0|");
            } else {
                StringBuilder sb = new StringBuilder("OK|" + preferiti.size());
                for (String s : preferiti) {
                    sb.append("|").append(s);
                }
                sendResponse(sb.toString());
            }
        }
        
        /**
         * Comando "aggiungiPreferito" - Aggiunge un ristorante ai preferiti.
         *
         * @param username       lo username del cliente
         * @param nomeRistorante il nome del ristorante
         * @return "OK|Preferito aggiunto" oppure "ERRORE|Già nei preferiti"
         */
        if (command.equals("aggiungiPreferito")) {
            String username = (String) in.readObject();
            String nomeRistorante = (String) in.readObject();
            boolean ok = RecensioneDAO.aggiungiPreferito(username, nomeRistorante);
            sendResponse(ok ? "OK|Preferito aggiunto" : "ERRORE|Già nei preferiti");
        }
        
        /**
         * Comando "rimuoviPreferito" - Rimuove un ristorante dai preferiti.
         *
         * @param username       lo username del cliente
         * @param nomeRistorante il nome del ristorante
         * @return "OK|Preferito rimosso" oppure "ERRORE|Rimozione fallita"
         */
        if (command.equals("rimuoviPreferito")) {
            String username = (String) in.readObject();
            String nomeRistorante = (String) in.readObject();
            boolean ok = RecensioneDAO.rimuoviPreferito(username, nomeRistorante);
            sendResponse(ok ? "OK|Preferito rimosso" : "ERRORE|Rimozione fallita");
        }
        
        // ============================================================
        // COMANDI: RISTORATORI
        // ============================================================
        
        /**
         * Comando "getMieiRistoranti" - Recupera la lista dei ristoranti posseduti.
         *
         * @param username lo username del ristoratore
         * @return "OK|count|nome1|nome2|..." oppure "OK|0|"
         */
        if (command.equals("getMieiRistoranti")) {
            String username = (String) in.readObject();
            int utenteId = UtenteDAO.getIdPerUsername(username);
            ArrayList<String> mieiRistoranti = RistoranteDAO.getRistorantiDelRistoratore(utenteId);
            sendResponse(preferitiToObject(mieiRistoranti));
        }
        
        /**
         * Comando "aggiungiRistoranteCustom" - Aggiunge un nuovo ristorante custom.
         * Il ristorante viene inserito nel database e associato al ristoratore.
         *
         * @param nome         il nome del ristorante
         * @param indirizzo    l'indirizzo del ristorante
         * @param citta        la città del ristorante
         * @param nazione      la nazione del ristorante
         * @param prezzo       il prezzo medio in Euro
         * @param cucina       il tipo di cucina
         * @param longitudine  la longitudine del ristorante
         * @param latitudine   la latitudine del ristorante
         * @param delivery     true se disponibile il delivery
         * @param prenotazione true se disponibile la prenotazione online
         * @param username     lo username del ristoratore
         * @return "OK|Ristorante aggiunto" oppure "ERRORE|Aggiunta fallita"
         */
        if (command.equals("aggiungiRistoranteCustom")) {
            String nome = (String) in.readObject();
            String indirizzo = (String) in.readObject();
            String citta = (String) in.readObject();
            String nazione = (String) in.readObject();
            String prezzo = (String) in.readObject();
            String cucina = (String) in.readObject();
            double longitudine = (double) in.readObject();
            double latitudine = (double) in.readObject();
            boolean delivery = (boolean) in.readObject();
            boolean prenotazione = (boolean) in.readObject();
            String username = (String) in.readObject();

            Ristorante nuovo = new Ristorante(nome, indirizzo, citta, prezzo, nazione,
                                              cucina, longitudine, latitudine,
                                              delivery, prenotazione, null);
            int utenteId = UtenteDAO.getIdPerUsername(username);
            boolean ok = RistoranteDAO.aggiungiRistorante(nuovo, utenteId);
            sendResponse(ok ? "OK|Ristorante aggiunto" : "ERRORE|Aggiunta fallita");
        }
        
        /**
         * Comando "rispondiARecensione" - Scrive la risposta del ristoratore a una recensione.
         *
         * @param nomeRistorante  il nome del ristorante
         * @param usernameCliente lo username del cliente
         * @param risposta        il testo della risposta
         * @return "OK|Risposta inviata" oppure "ERRORE|Risposta fallita"
         */
        if (command.equals("rispondiARecensione")) {
            String nomeRistorante = (String) in.readObject();
            String usernameCliente = (String) in.readObject();
            String risposta = (String) in.readObject();
            boolean ok = RecensioneDAO.scriviRisposta(nomeRistorante, usernameCliente, risposta);
            sendResponse(ok ? "OK|Risposta inviata" : "ERRORE|Risposta fallita");
        }
        
        /**
         * Comando "mediaRistorante" - Calcola la media stelle e il numero di recensioni.
         *
         * @param nomeRistorante il nome del ristorante
         * @return "OK|mediaStelle|numeroRecensioni"
         */
        if (command.equals("mediaRistorante")) {
            String nomeRistorante = (String) in.readObject();
            double[] stats = RistoranteDAO.getMediaECount(nomeRistorante);
            sendResponse("OK|" + stats[0] + "|" + (int) stats[1]);
        }
        
        /**
         * Comando "verificaEsistenzaRistorante" - Verifica se un ristorante esiste nel DB.
         *
         * @param nome il nome del ristorante
         * @return "OK|true" oppure "OK|false"
         */
        if (command.equals("verificaEsistenzaRistorante")) {
            String nome = (String) in.readObject();
            boolean esiste = RistoranteDAO.esisteRistorante(nome);
            sendResponse(esiste ? "OK|true" : "OK|false");
        }
    }
    
    // ============================================================
    // METODI DI SUPPORTO PER LA SERIALIZZAZIONE
    // ============================================================
    
    /**
     * Converte una lista di oggetti Ristorante in una lista di RistoranteDTO.
     * Per ogni ristorante vengono calcolate media stelle e numero di recensioni.
     *
     * @param lista la lista di oggetti Ristorante da convertire
     * @return la lista di oggetti RistoranteDTO
     */
    private ArrayList<RistoranteDTO> toDTOList(ArrayList<Ristorante> lista) {
        ArrayList<RistoranteDTO> dto = new ArrayList<>();
        for (Ristorante r : lista) {
            double[] stats = RistoranteDAO.getMediaECount(r.getName());
            dto.add(new RistoranteDTO(r, stats[0], (int) stats[1]));
        }
        return dto;
    }

    /**
     * Converte una lista di oggetti Ristorante in una stringa formattata
     * per l'invio al client.
     * Il formato della risposta è: "OK|count|nome1|citta1|cucina1|..."
     *
     * @param lista la lista di oggetti Ristorante da serializzare
     * @return la stringa di risposta formattata, o "OK|0|" se la lista è vuota
     */
    private Object ristorantiToObject(ArrayList<Ristorante> lista) {
        if (lista.isEmpty()) return "OK|0|";
        StringBuilder sb = new StringBuilder("OK|" + lista.size());
        for (Ristorante r : lista) {
            sb.append("|").append(r.getName())
              .append("|").append(r.getCity())
              .append("|").append(r.getCuisine());
        }
        return sb.toString();
    }
    
    /**
     * Converte una lista di oggetti Recensione in una stringa formattata
     * per l'invio al client.
     * Il formato della risposta è:
     * "OK|count|cliente1|testo1|stelle1|data1|risposta1|username1|..."
     *
     * @param lista la lista di oggetti Recensione da serializzare
     * @return la stringa di risposta formattata, o "OK|0|" se la lista è vuota
     */
    private Object recensioniToObject(ArrayList<Recensione> lista) {
        if (lista.isEmpty()) return "OK|0|";
        StringBuilder sb = new StringBuilder("OK|" + lista.size());
        for (Recensione r : lista) {
            String nomeCliente = UtenteDAO.getNomePerUsername(r.getUsernameCliente());
            String username = r.getUsernameCliente();
            String testo = r.getTestoRecensione() != null ? r.getTestoRecensione() : "";
            String risposta = r.getRisposta() != null ? r.getRisposta() : "";
            sb.append("|").append(nomeCliente)
              .append("|").append(testo)
              .append("|").append(r.getStelle())
              .append("|").append(r.getData())
              .append("|").append(risposta)
              .append("|").append(username);
        }
        return sb.toString();
    }
    
    /**
     * Converte una lista di stringhe (nomi di ristoranti) in una stringa
     * formattata per l'invio al client.
     * Il formato della risposta è: "OK|count|nome1|nome2|..."
     *
     * @param lista la lista di stringhe da serializzare
     * @return la stringa di risposta formattata, o "OK|0|" se la lista è vuota
     */
    private Object preferitiToObject(ArrayList<String> lista) {
        if (lista.isEmpty()) return "OK|0|";
        StringBuilder sb = new StringBuilder("OK|" + lista.size());
        for (String s : lista) {
            sb.append("|").append(s);
        }
        return sb.toString();
    }
}