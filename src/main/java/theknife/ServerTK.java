/*
 * TheKnife - ServerTK
 * Autori:
 *   Gharsellaoui Omema - 761146 - VA
 *   Kufura Razak - 760614 - VA
 */

package theknife;

import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * ServerTK - Server dell'applicazione TheKnife.
 * Rimane in attesa di connessioni dai client sulla porta 12345.
 * Utilizza un thread pool (ExecutorService) per gestire multipli client
 * in concorrenza, migliorando le prestazioni e la scalabilità.
 *
 * <p>All'avvio, il server richiede all'operatore le credenziali per la
 * connessione al database PostgreSQL. Se la connessione ha successo,
 * il server si mette in attesa di connessioni dai client.</p>
 *
 * <p>Il server importa automaticamente il dataset Michelin se non è
 * già presente nel database.</p>
 *
 * @author Gharsellaoui Omema
 * @author Kufura Razak
 * @version 1.0
 */
public class ServerTK {
    
    // ==================== ATTRIBUTI ====================
    
    /** Porta su cui il server resta in ascolto. */
    private static final int PORT = 12345;
    
    /** Thread pool per la gestione dei client connessi. */
    private static ExecutorService threadPool = Executors.newCachedThreadPool();
    
    /** Flag che indica se il server è in esecuzione. */
    private static volatile boolean running = true;
    
    // ==================== COSTRUTTORE ====================
    
    /**
     * Costruttore vuoto del server.
     * L'inizializzazione avviene tramite il metodo {@link #exec()}.
     */
    public ServerTK() {}

    // ==================== METODI PRIVATI ====================
    
    /**
     * Richiede all'operatore i parametri di connessione al database PostgreSQL.
     * I valori di default sono mostrati tra parentesi quadre e possono essere
     * accettati premendo semplicemente Invio.
     *
     * @param scanner lo Scanner per la lettura dell'input da console
     */
    private static void chiediConfigurazioneDB(Scanner scanner) {
        System.out.println("=== CONFIGURAZIONE DATABASE dbTK ===");
        System.out.println("Premi Invio per usare il valore di default tra parentesi quadre.\n");

        System.out.print("Host del database [localhost]: ");
        String host = scanner.nextLine().trim();
        if (host.isEmpty()) host = "localhost";

        String porta;
        do {
            System.out.print("Porta del database [5432]: ");
            porta = scanner.nextLine().trim();
            if (porta.isEmpty()) { porta = "5432"; break; }
            if (!porta.matches("\\d+")) {
                System.out.println("La porta deve essere un numero, riprova.");
                porta = null;
            }
        } while (porta == null);

        System.out.print("Nome del database [theknife_db]: ");
        String nomeDb = scanner.nextLine().trim();
        if (nomeDb.isEmpty()) nomeDb = "theknife_db";

        System.out.print("Utente PostgreSQL [postgres]: ");
        String user = scanner.nextLine().trim();
        if (user.isEmpty()) user = "postgres";

        System.out.print("Password PostgreSQL: ");
        String password = scanner.nextLine();

        DataBaseManager.configura(host, porta, nomeDb, user, password);
        System.out.println();
    }
    
    // ==================== METODO PRINCIPALE ====================
    
    /**
     * Avvia il server eseguendo la configurazione del database,
     * l'importazione del dataset Michelin e la messa in ascolto
     * sulla porta specificata per le connessioni dei client.
     *
     * @throws IOException se la creazione del ServerSocket fallisce
     */
    public void exec() throws IOException {
        System.out.println("=== SERVER THEKNIFE ===");

        Scanner scanner = new Scanner(System.in);

        boolean connesso = false;
        do {
            chiediConfigurazioneDB(scanner);
            System.out.println("Verifica della connessione al database in corso...");

            if (DataBaseManager.testConnessione()) {
                connesso = true;
            } else {
                System.out.println("Impossibile connettersi al database con i parametri forniti.");
                System.out.print("Vuoi riprovare? (s/n): ");
                String risposta = scanner.nextLine().trim();
                if (!risposta.equalsIgnoreCase("s")) {
                    System.out.println("Avvio del server interrotto.");
                    return;
                }
            }
        } while (!connesso);

        System.out.println("Avvio del server in corso...");
        
        // Importa il dataset Michelin se non è già presente
        DataBaseManager.importaMichelin();
        
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server avviato sulla porta " + PORT);
        System.out.println("In attesa di connessioni...");
        
        // Aggiunge uno ShutdownHook per lo spegnimento pulito del server
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Spegnimento del server in corso...");
            running = false;
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
            }
            DataBaseManager.closePool();
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Errore chiusura server socket: " + e.getMessage());
            }
        }));
        
        try {
            while (running) {
                Socket socket = serverSocket.accept();
                System.out.println("Connessione accettata da " + socket.getInetAddress());
                threadPool.execute(new SlaveThread(socket));
            }
        } finally {
            serverSocket.close();
        }
    }
    
    /**
     * Punto di ingresso dell'applicazione server.
     *
     * @param args argomenti della riga di comando (non utilizzati)
     * @throws IOException se l'avvio del server fallisce
     */
    public static void main(String[] args) throws IOException {
        new ServerTK().exec();
    }
}