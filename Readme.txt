===============================================================================
THEKNIFE - PROGETTO DI LABORATORIO INTERDISCIPLINARE B
===============================================================================

Università degli Studi dell'Insubria
Dipartimento di Scienze Teoriche e Applicate

Autori:
- Gharsellaoui Omema - 761146 - VA
- Kufura Razak - 760614 - VA

Anno Accademico: 2024/2025
Data: Giugno 2026
Versione: 1.0

===============================================================================
INDICE
===============================================================================

1. REQUISITI DI SISTEMA
2. INSTALLAZIONE E CONFIGURAZIONE
   2.1 Installazione di Java
   2.2 Installazione di PostgreSQL
   2.3 Creazione del Database
3. COMPILAZIONE DEL PROGETTO
   3.1 Con Maven (Consigliata)
   3.2 Con NetBeans / IntelliJ IDEA
4. GENERAZIONE DELLA JAVADOC
5. AVVIO DELL'APPLICAZIONE
   5.1 Avvio del Server
   5.2 Avvio del Client
6. NOTE PER L'ESECUZIONE
7. STRUTTURA DEL PROGETTO
8. CONTATTI

===============================================================================
1. REQUISITI DI SISTEMA
===============================================================================

Il software è progettato per essere multipiattaforma.
È stato testato e funziona correttamente su Windows 11.

Per eseguire correttamente TheKnife è necessario disporre dei seguenti
requisiti:

  Componente        Requisito
  ----------------- --------------------------------------------------------
  Sistema           Windows 10/11, Linux oppure macOS
  Operativo         
  Java              JDK 11 o versione superiore
  Database          PostgreSQL 14 o superiore
  Maven             Apache Maven 3.6 o superiore (solo per compilazione)
  RAM consigliata   Almeno 4 GB
  Spazio libero     Circa 300 MB
  Connessione       Connessione locale (localhost) per Client e Server

Per verificare la corretta installazione di Java, aprire il Prompt dei
Comandi (o terminale su Linux/macOS) ed eseguire:

  java -version

Il comando dovrà mostrare la versione di Java installata sul computer.

===============================================================================
2. INSTALLAZIONE E CONFIGURAZIONE
===============================================================================

2.1 Installazione di Java

  - Scaricare JDK 11 o superiore dal sito Oracle o da OpenJDK.
  - Installare seguendo le istruzioni del setup.
  - Configurare la variabile d'ambiente JAVA_HOME.
  - Verificare con il comando: java -version

2.2 Installazione di PostgreSQL

  - Scaricare PostgreSQL 14 o superiore dal sito ufficiale:
    https://www.postgresql.org/download/
  - Installare seguendo le istruzioni del setup.
  - Durante l'installazione, ricordare la password impostata per l'utente
    postgres (verrà richiesta all'avvio del server).
  - Verificare che il servizio PostgreSQL sia in esecuzione.

2.3 Creazione del Database

  Dal terminale o da pgAdmin, eseguire i seguenti passaggi:

  1. Creare il database:

     psql -U postgres -c "CREATE DATABASE theknife_db;"

  2. Connettersi al database appena creato:

     psql -U postgres -d theknife_db

  3. Eseguire lo script di creazione delle tabelle fornito con il progetto:

     \i schema.sql

  In alternativa, aprire pgAdmin, creare il database "theknife_db" ed
  eseguire il file "schema.sql" presente nella root del progetto.

  Il database theknife_db deve essere già creato prima di avviare il
  server TheKnife.

===============================================================================
3. COMPILAZIONE DEL PROGETTO
===============================================================================

3.1 Con Maven (Consigliata)

  Nella cartella root del progetto, eseguire il seguente comando:

  Windows:
    mvn clean package

  Linux / macOS:
    mvn clean package

  Questo comando:
  - Pulisce la cartella target/ (se esiste)
  - Compila il codice sorgente
  - Esegue i test (se presenti)
  - Genera i file JAR nella cartella target/

  I file JAR generati saranno:
  - target/serverTK.jar
  - target/clientTK.jar

  Dopo la generazione, i JAR vanno spostati nella cartella bin/:

  Windows:
    copy target\serverTK.jar bin\
    copy target\clientTK.jar bin\

  Linux / macOS:
    cp target/serverTK.jar bin/
    cp target/clientTK.jar bin/

3.2 Con NetBeans / IntelliJ IDEA

  Aprire il progetto nella propria IDE preferita (NetBeans, IntelliJ IDEA
  o Eclipse) e utilizzare il comando di build integrato.

  Le IDE moderne riconoscono automaticamente il file pom.xml e importano
  tutte le dipendenze necessarie.

===============================================================================
4. GENERAZIONE DELLA JAVADOC
===============================================================================

Per generare la documentazione JavaDoc dell'intero progetto, eseguire il
seguente comando Maven:

  mvn javadoc:javadoc

La documentazione viene generata nella cartella:

  target/site/apidocs/

Per consultarla, aprire il file index.html all'interno di questa cartella
con un browser web.

La JavaDoc generata deve poi essere copiata nella cartella doc/javadoc/
per essere inclusa nella consegna finale.

===============================================================================
5. AVVIO DELL'APPLICAZIONE
===============================================================================

L'applicazione è composta da due componenti che devono essere eseguiti
nell'ordine corretto: PRIMA il Server, POI il Client.

5.1 Avvio del Server

  Assicurarsi che PostgreSQL sia in esecuzione e che il database
  theknife_db sia stato creato.

  Aprire il terminale (Prompt dei Comandi su Windows) nella cartella
  root del progetto ed eseguire:

  Windows:
    java -jar bin\serverTK.jar

  Linux / macOS:
    java -jar bin/serverTK.jar

  All'avvio, il server richiederà i parametri di connessione al database:

    Host del database [localhost]
    Porta del database [5432]
    Nome del database [theknife_db]
    Utente PostgreSQL [postgres]
    Password PostgreSQL [inserire la password configurata]

  Premere Invio per accettare i valori di default (consigliato se
  PostgreSQL è installato localmente).

  Se la connessione al database ha successo, il server si avvierà e
  resterà in attesa di connessioni sulla porta 12345.

  Il server importerà automaticamente il dataset Michelin se non è già
  presente nel database. Questa operazione potrebbe richiedere alcuni
  secondi.

5.2 Avvio del Client

  Dopo aver avviato il server, aprire un nuovo terminale (o un secondo
  prompt) nella cartella root del progetto ed eseguire:

  Windows:
    java -jar bin\clientTK.jar

  Linux / macOS:
    java -jar bin/clientTK.jar

  Il client si connetterà automaticamente al server in esecuzione su
  localhost:12345.

  Se il server non è in esecuzione o non è raggiungibile, il client
  mostrerà un messaggio di errore.

===============================================================================
6. NOTE PER L'ESECUZIONE
===============================================================================

  - Il progetto è stato testato e funziona correttamente su Windows 11.
  - Su Linux e macOS, l'esecuzione dei JAR potrebbe richiedere l'aggiunta
    dei permessi di esecuzione:
      chmod +x bin/*.jar
  - Su macOS, potrebbe apparire un avviso di sicurezza per "sviluppatore
    non identificato". Per aggirarlo:
      - Aprire le Impostazioni di Sistema -> Sicurezza
      - Cliccare su "Apri lo stesso"
      - Oppure eseguire da terminale con: java -jar bin/clientTK.jar
  - Assicurarsi che il server sia in esecuzione PRIMA di avviare il client.
  - Il database PostgreSQL deve essere in esecuzione PRIMA di avviare il
    server TheKnife.
  - Il dataset Michelin viene importato automaticamente al primo avvio.
  - Per testare l'applicazione, è possibile utilizzare le credenziali
    riportate nel Manuale Utente (doc/manuale_utente.pdf).

===============================================================================
7. STRUTTURA DEL PROGETTO
===============================================================================

  /
  ├── autori.txt                 # Autori del progetto
  ├── README.txt                 # Questo file
  ├── pom.xml                    # File di configurazione Maven
  ├── schema.sql                 # Script di creazione database
  │
  ├── bin/
  │   ├── serverTK.jar          # Eseguibile del server
  │   └── clientTK.jar          # Eseguibile del client
  │
  ├── doc/
  │   ├── manuale_utente.pdf    # Manuale Utente
  │   ├── manuale_tecnico.pdf   # Manuale Tecnico
  │   ├── javadoc/              # JavaDoc generata
  │   ├── Schema ER non ristrutturato.png
  │   └── Schema ER ristrutturato.png
  │
  ├── src/
  │   └── theknife/             # Codice sorgente
  │       ├── ClientTK.java
  │       ├── ServerTK.java
  │       ├── SlaveThread.java
  │       ├── Utente.java
  │       ├── UtenteDAO.java
  │       ├── UtenteFactory.java
  │       ├── Cliente.java
  │       ├── Ristoratore.java
  │       ├── Ristorante.java
  │       ├── RistoranteDAO.java
  │       ├── RistoranteDTO.java
  │       ├── Recensione.java
  │       ├── RecensioneDAO.java
  │       ├── GestioneUtenti.java
  │       ├── DataBaseManager.java
  │       └── GeoUtils.java
  │
  └── target/                   # Generato da Maven (non incluso nel repository)
      ├── site/apidocs/         # JavaDoc generata
      ├── serverTK.jar
      └── clientTK.jar

===============================================================================
8. CONTATTI
===============================================================================

Per eventuali domande, chiarimenti o segnalazioni di bug:

  Gharsellaoui Omema - 761146 - VA
  Kufura Razak - 760614 - VA

===============================================================================
FINE DEL DOCUMENTO
===============================================================================