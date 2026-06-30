/*
 * TheKnife - ClientTK
 * Autori:
 *   Gharsellaoui Omema - 761146 - VA
 *   Kufura Razak - 760614 - VA
 */

package theknife;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * ClientTK - Client dell'applicazione TheKnife con interfaccia grafica Swing.
 * Gestisce tutte le interazioni dell'utente con il sistema, inclusa la
 * navigazione tra le schermate (login, registrazione, guest, cliente,
 * ristoratore, ricerca avanzata), le operazioni di ricerca, la gestione
 * dei preferiti e delle recensioni.
 *
 * <p>L'interfaccia utilizza un CardLayout per gestire le diverse schermate.
 * Tutte le chiamate di rete sono eseguite in background tramite SwingWorker
 * per non bloccare l'Event Dispatch Thread (EDT).</p>
 *
 * <p>La palette di colori è stata definita per garantire uniformità grafica
 * e un'esperienza utente coerente in tutta l'applicazione.</p>
 *
 * @author Gharsellaoui Omema
 * @author Kufura Razak
 * @version 1.0
 */
public class ClientTK extends JFrame implements ActionListener {
    
    // ==================== PALETTE COLORI ====================
    
    /** Colore rosa primario per pulsanti principali, titoli ed elementi di rilievo. */
    private static final Color ROSA_PRIMARIO   = new Color(200, 80, 110);
    
    /** Colore rosa secondario per pulsanti secondari e componenti decorativi. */
    private static final Color ROSA_SECONDARIO = new Color(220, 150, 175);
    
    /** Colore rosa chiaro per lo sfondo principale dell'applicazione. */
    private static final Color ROSA_CHIARO     = new Color(252, 235, 240);
    
    /** Colore rosa per il gradiente utilizzato nelle schermate principali. */
    private static final Color ROSA_GRADIENTE  = new Color(255, 220, 230);
    
    /** Colore grigio scuro per i testi principali. */
    private static final Color GRIGIO_SCURO    = new Color(60, 55, 65);
    
    /** Colore grigio medio per i testi secondari. */
    private static final Color GRIGIO_MEDIO    = new Color(140, 135, 145);
    
    /** Colore grigio chiaro per i bordi e separatori. */
    private static final Color GRIGIO_CHIARO   = new Color(220, 215, 225);
    
    /** Colore bianco per card, pannelli e campi di input. */
    private static final Color BIANCO          = new Color(255, 255, 255);
    
    /** Colore ombra per gli elementi dell'interfaccia. */
    private static final Color OMBRA           = new Color(0, 0, 0, 20);
    
    /** Colore ombra per le card centrali. */
    private static final Color OMBRA_CARD      = new Color(0, 0, 0, 12);
    
    // ==================== FONT ====================
    
    /** Font per il logo dell'applicazione. */
    private static final Font FONT_LOGO = new Font("Segoe UI", Font.BOLD, 32);
    
    /** Font per i titoli delle sezioni. */
    private static final Font FONT_TITOLO = new Font("Segoe UI", Font.BOLD, 20);
    
    /** Font per i sottotitoli. */
    private static final Font FONT_SOTTOTITOLO = new Font("Segoe UI", Font.PLAIN, 14);
    
    /** Font per i pulsanti. */
    private static final Font FONT_BOTTONE = new Font("Segoe UI", Font.BOLD, 14);
    
    /** Font per i campi di testo. */
    private static final Font FONT_CAMPO = new Font("Segoe UI", Font.PLAIN, 13);
    
    // ==================== COMPONENTI DI RETE ====================
    
    /** Socket per la connessione al server. */
    private Socket socket;
    
    /** Stream di output per l'invio di oggetti serializzati al server. */
    private ObjectOutputStream out;
    
    /** Stream di input per la ricezione di oggetti serializzati dal server. */
    private ObjectInputStream in;
    
    // ==================== DATI UTENTE LOGGATO ====================
    
    /** Username dell'utente attualmente loggato. */
    private String loggedUserUsername = null;
    
    /** Nome dell'utente attualmente loggato. */
    private String loggedUserNome = null;
    
    /** Ruolo dell'utente attualmente loggato ("cliente" o "ristoratore"). */
    private String loggedUserRuolo = null;
    
    /** Domicilio dell'utente attualmente loggato. */
    private String loggedUserDomicilio = null;
    
    // ==================== COMPONENTI GRAFICI PRINCIPALI ====================
    
    /** Pannello principale con CardLayout per la navigazione tra le schermate. */
    private JPanel mainPanel;
    
    /** Layout a schede per la gestione delle diverse schermate. */
    private CardLayout cardLayout;
    
    /** Area di testo per i risultati nella modalità guest. */
    private JTextArea resultArea;
    
    /** Area di testo per i risultati nella dashboard del cliente. */
    private JTextArea resultAreaCliente;
    
    /** Area di testo per i risultati nella dashboard del ristoratore. */
    private JTextArea resultAreaRistoratore;
    
    /** Etichetta del titolo nella dashboard del cliente. */
    private JLabel titleLabelCliente;
    
    /** Etichetta del titolo nella dashboard del ristoratore. */
    private JLabel titleLabelRistoratore;
    
    // ==================== COMPONENTI LOGIN ====================
    
    /** Campo di testo per l'inserimento dello username nel login. */
    private JTextField userField;
    
    /** Campo password per l'inserimento della password nel login. */
    private JPasswordField passField;
    
    // ==================== COMPONENTI REGISTRAZIONE ====================
    
    /** Campo di testo per il nome nella registrazione. */
    private JTextField nomeField;
    
    /** Campo di testo per il cognome nella registrazione. */
    private JTextField cognomeField;
    
    /** Campo di testo per la data di nascita nella registrazione (opzionale). */
    private JTextField dataNascitaField;
    
    /** Campo di testo per lo username nella registrazione. */
    private JTextField userRegField;
    
    /** Campo di testo per il luogo di domicilio nella registrazione. */
    private JTextField luogoField;
    
    /** Campo password per la registrazione. */
    private JPasswordField passRegField;
    
    /** Combo box per la selezione del ruolo (cliente/ristoratore). */
    private JComboBox<String> ruoloCombo;
    
    // ==================== COMPONENTI RICERCA ====================
    
    /** Campo di testo per la città nella ricerca guest. */
    private JTextField cityField;

    /** Origine della ricerca ("guest" o "cliente") per il ritorno alla schermata corretta. */
    private String searchOrigin = "guest";

    /** Campo di testo per il luogo nella ricerca avanzata. */
    private JTextField luogoSearchField;
    
    /** Campo di testo per la cucina nella ricerca avanzata. */
    private JTextField cucinaSearchField;
    
    /** Campo di testo per il prezzo minimo nella ricerca avanzata. */
    private JTextField prezzoMinField;
    
    /** Campo di testo per il prezzo massimo nella ricerca avanzata. */
    private JTextField prezzoMaxField;
    
    /** Combo box per il filtro del servizio di delivery. */
    private JComboBox<String> deliveryCombo;
    
    /** Combo box per il filtro della prenotazione online. */
    private JComboBox<String> prenotazioneCombo;
    
    /** Combo box per il filtro della valutazione minima. */
    private JComboBox<String> stelleCombo;
    
    /** Campo di testo per il raggio di ricerca dei ristoranti vicini. */
    private JTextField raggioField;
    
    /** Modello per la lista dei risultati della ricerca. */
    private DefaultListModel<RistoranteDTO> risultatiModel;
    
    /** Lista per la visualizzazione dei risultati della ricerca. */
    private JList<RistoranteDTO> risultatiList;
    
    /** Etichetta per lo stato della ricerca. */
    private JLabel searchStatusLabel;
    
    // ==================== PANNELLI ====================
    
    /** Pannello della schermata di login. */
    private JPanel loginPanel;
    
    /** Pannello della schermata di registrazione. */
    private JPanel registerPanel;
    
    /** Pannello della modalità guest. */
    private JPanel guestPanel;
    
    /** Pannello della dashboard del cliente. */
    private JPanel clientePanel;
    
    /** Pannello della dashboard del ristoratore. */
    private JPanel ristoratorePanel;
    
    /** Pannello della ricerca avanzata. */
    private JPanel searchPanel;
    
    // ==================== COSTRUTTORE ====================
    
    /**
     * Costruttore principale del client.
     * Inizializza l'interfaccia grafica, configura il CardLayout per la
     * navigazione tra le schermate, crea tutti i pannelli e stabilisce
     * la connessione al server sulla porta 12345.
     */
    public ClientTK() {
        setTitle("TheKnife");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 780);
        setMinimumSize(new Dimension(850, 650));
        setLocationRelativeTo(null);
        
        getContentPane().setBackground(ROSA_CHIARO);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setOpaque(false);
        
        loginPanel = createLoginPanel();
        registerPanel = createRegisterPanel();
        guestPanel = createGuestPanel();
        clientePanel = createClientePanel();
        ristoratorePanel = createRistoratorePanel();
        searchPanel = createSearchPanel();
        
        mainPanel.add(loginPanel, "login");
        mainPanel.add(registerPanel, "register");
        mainPanel.add(guestPanel, "guest");
        mainPanel.add(clientePanel, "cliente");
        mainPanel.add(ristoratorePanel, "ristoratore");
        mainPanel.add(searchPanel, "search");
        
        add(mainPanel);
        connectToServer();
    }
    
    // ==================== CONNESSIONE AL SERVER ====================
    
    /**
     * Stabilisce la connessione al server sulla porta 12345.
     * Imposta un timeout di connessione di 10 secondi e un timeout
     * di lettura di 30 secondi.
     * In caso di errore, mostra un messaggio di dialogo e termina
     * l'applicazione con codice di uscita 1.
     */
    private void connectToServer() {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", 12345), 10000);
            socket.setSoTimeout(30000);
            
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connesso al server!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Impossibile connettersi al server.\nAssicurati che il server sia avviato.",
                "Errore di connessione", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    /**
     * Invia un comando al server con i parametri specificati.
     * Il comando e i parametri vengono serializzati e inviati tramite
     * ObjectOutputStream. La risposta del server viene deserializzata
     * e restituita come oggetto.
     *
     * @param command il comando da inviare (es. "login", "cercaMultiCriterio")
     * @param params  i parametri del comando in ordine di invio
     * @return la risposta del server (può essere una String o un ArrayList)
     */
    private Object sendCommand(String command, Object... params) {
        try {
            out.writeObject(command);
            for (Object p : params) out.writeObject(p);
            out.flush();
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return "ERRORE|" + e.getMessage();
        }
    }
    
    // ==================== SHADOW BORDER PER CARD ====================
    
    /**
     * Bordo personalizzato con effetto ombra per le card dell'interfaccia.
     * Utilizzato per dare profondità agli elementi principali come la
     * card di login e registrazione.
     */
    private class ShadowBorder extends AbstractBorder {
        
        /** Colore dell'ombra. */
        private Color shadowColor;
        
        /** Dimensione dell'ombra in pixel. */
        private int shadowSize;
        
        /** Offset dell'ombra rispetto al bordo. */
        private int offset;
        
        /**
         * Costruisce un bordo con effetto ombra.
         *
         * @param shadowColor il colore dell'ombra
         * @param shadowSize  la dimensione dell'ombra in pixel
         * @param offset      l'offset dell'ombra rispetto al bordo
         */
        public ShadowBorder(Color shadowColor, int shadowSize, int offset) {
            this.shadowColor = shadowColor;
            this.shadowSize = shadowSize;
            this.offset = offset;
        }
        
        /**
         * Disegna il bordo con l'effetto ombra.
         *
         * @param c      il componente su cui disegnare il bordo
         * @param g      il contesto grafico
         * @param x      la coordinata x di partenza
         * @param y      la coordinata y di partenza
         * @param width  la larghezza del componente
         * @param height l'altezza del componente
         */
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setColor(shadowColor);
            for (int i = 0; i < shadowSize; i++) {
                g2d.drawRoundRect(x + offset + i, y + offset + i, 
                                 width - offset - i*2 - 1, 
                                 height - offset - i*2 - 1, 15, 15);
            }
        }
        
        /**
         * Restituisce gli spazi interni del bordo.
         *
         * @param c il componente
         * @return gli spazi interni del bordo
         */
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(shadowSize + offset, shadowSize + offset, 
                             shadowSize + offset, shadowSize + offset);
        }
    }
    
    // ==================== PANEL CON GRADIENTE ====================
    
    /**
     * Pannello personalizzato con sfondo a gradiente lineare.
     * Il gradiente va dal colore iniziale a quello finale, da sinistra a destra.
     */
    private class GradientPanel extends JPanel {
        
        /** Colore iniziale del gradiente. */
        private Color colore1;
        
        /** Colore finale del gradiente. */
        private Color colore2;
        
        /**
         * Costruisce un pannello con sfondo a gradiente.
         *
         * @param c1 il colore iniziale del gradiente
         * @param c2 il colore finale del gradiente
         */
        public GradientPanel(Color c1, Color c2) {
            this.colore1 = c1;
            this.colore2 = c2;
            setOpaque(false);
        }
        
        /**
         * Disegna lo sfondo del pannello con il gradiente.
         *
         * @param g il contesto grafico
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, colore1, getWidth(), getHeight(), colore2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    
    // ==================== CREAZIONE PANNELLI ====================
    
    /**
     * Crea il pannello di login con logo, campi username/password
     * e bottoni per accedere, registrarsi o continuare come ospite.
     * Utilizza una card centrale con effetto ombra.
     *
     * @return il pannello di login
     */
    private JPanel createLoginPanel() {
        GradientPanel panel = new GradientPanel(ROSA_CHIARO, ROSA_GRADIENTE);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BIANCO);
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(OMBRA_CARD, 8, 3),
            BorderFactory.createEmptyBorder(35, 45, 35, 45)
        ));
        card.setPreferredSize(new Dimension(420, 480));
        
        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.insets = new Insets(8, 8, 8, 8);
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel logoLabel = new JLabel("🍽️  TheKnife");
        logoLabel.setFont(FONT_LOGO);
        logoLabel.setForeground(ROSA_PRIMARIO);
        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        cardGbc.gridwidth = 2;
        cardGbc.anchor = GridBagConstraints.CENTER;
        card.add(logoLabel, cardGbc);
        
        JLabel subtitleLabel = new JLabel("Trova il tuo ristorante ideale");
        subtitleLabel.setFont(FONT_SOTTOTITOLO);
        subtitleLabel.setForeground(GRIGIO_MEDIO);
        cardGbc.gridy = 1;
        card.add(subtitleLabel, cardGbc);
        
        cardGbc.gridy = 2;
        JLabel separator = new JLabel("────────────────────");
        separator.setForeground(GRIGIO_CHIARO);
        card.add(separator, cardGbc);
        
        cardGbc.gridwidth = 1;
        cardGbc.gridy = 3;
        cardGbc.gridx = 0;
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        userIcon.setForeground(GRIGIO_MEDIO);
        card.add(userIcon, cardGbc);
        cardGbc.gridx = 1;
        userField = createStyledTextField();
        userField.setPreferredSize(new Dimension(240, 42));
        card.add(userField, cardGbc);
        
        cardGbc.gridy = 4;
        cardGbc.gridx = 0;
        JLabel passIcon = new JLabel("🔒");
        passIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        passIcon.setForeground(GRIGIO_MEDIO);
        card.add(passIcon, cardGbc);
        cardGbc.gridx = 1;
        passField = createStyledPasswordField();
        passField.setPreferredSize(new Dimension(240, 42));
        card.add(passField, cardGbc);
        
        cardGbc.gridy = 5;
        cardGbc.gridx = 0;
        cardGbc.gridwidth = 2;
        JButton loginBtn = createPremiumButton("ACCEDI", ROSA_PRIMARIO, BIANCO);
        loginBtn.addActionListener(this);
        loginBtn.setActionCommand("login");
        loginBtn.setPreferredSize(new Dimension(240, 48));
        card.add(loginBtn, cardGbc);
        
        cardGbc.gridy = 6;
        JButton registerBtn = createPremiumButton("REGISTRATI", GRIGIO_MEDIO, BIANCO);
        registerBtn.addActionListener(this);
        registerBtn.setActionCommand("showRegister");
        registerBtn.setPreferredSize(new Dimension(240, 45));
        card.add(registerBtn, cardGbc);
        
        cardGbc.gridy = 7;
        JButton guestBtn = createPremiumButton("CONTINUA COME OSPITE", new Color(240, 235, 240), GRIGIO_SCURO);
        guestBtn.addActionListener(this);
        guestBtn.setActionCommand("showGuest");
        guestBtn.setPreferredSize(new Dimension(240, 42));
        card.add(guestBtn, cardGbc);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(card, gbc);
        
        return panel;
    }
    
    /**
     * Crea il pannello di registrazione con tutti i campi richiesti.
     * Include campi per nome, cognome, data di nascita (opzionale),
     * username, password, luogo di domicilio e selezione del ruolo.
     *
     * @return il pannello di registrazione
     */
    private JPanel createRegisterPanel() {
        GradientPanel panel = new GradientPanel(ROSA_CHIARO, ROSA_GRADIENTE);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BIANCO);
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(OMBRA_CARD, 8, 3),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        card.setPreferredSize(new Dimension(480, 600));
        
        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.insets = new Insets(6, 8, 6, 8);
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("📝  Registrazione");
        titleLabel.setFont(FONT_TITOLO);
        titleLabel.setForeground(ROSA_PRIMARIO);
        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        cardGbc.gridwidth = 2;
        card.add(titleLabel, cardGbc);
        
        JLabel subTitle = new JLabel("Crea il tuo account");
        subTitle.setFont(FONT_SOTTOTITOLO);
        subTitle.setForeground(GRIGIO_MEDIO);
        cardGbc.gridy = 1;
        card.add(subTitle, cardGbc);
        
        cardGbc.gridwidth = 1;
        int row = 2;
        
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        JLabel nomeLbl = new JLabel("✏️ Nome");
        nomeLbl.setFont(FONT_CAMPO);
        nomeLbl.setForeground(GRIGIO_SCURO);
        card.add(nomeLbl, cardGbc);
        cardGbc.gridx = 1;
        nomeField = createStyledTextField();
        nomeField.setPreferredSize(new Dimension(220, 38));
        card.add(nomeField, cardGbc);
        row++;
        
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        JLabel cognomeLbl = new JLabel("✏️ Cognome");
        cognomeLbl.setFont(FONT_CAMPO);
        cognomeLbl.setForeground(GRIGIO_SCURO);
        card.add(cognomeLbl, cardGbc);
        cardGbc.gridx = 1;
        cognomeField = createStyledTextField();
        cognomeField.setPreferredSize(new Dimension(220, 38));
        card.add(cognomeField, cardGbc);
        row++;
        
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        JLabel dataLbl = new JLabel("📅 Data Nascita (opz.)");
        dataLbl.setFont(FONT_CAMPO);
        dataLbl.setForeground(GRIGIO_SCURO);
        card.add(dataLbl, cardGbc);
        cardGbc.gridx = 1;
        dataNascitaField = createStyledTextField();
        dataNascitaField.setPreferredSize(new Dimension(220, 38));
        card.add(dataNascitaField, cardGbc);
        row++;
        
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        JLabel userLbl = new JLabel("👤 Username");
        userLbl.setFont(FONT_CAMPO);
        userLbl.setForeground(GRIGIO_SCURO);
        card.add(userLbl, cardGbc);
        cardGbc.gridx = 1;
        userRegField = createStyledTextField();
        userRegField.setPreferredSize(new Dimension(220, 38));
        card.add(userRegField, cardGbc);
        row++;
        
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        JLabel passLbl = new JLabel("🔒 Password (min 6)");
        passLbl.setFont(FONT_CAMPO);
        passLbl.setForeground(GRIGIO_SCURO);
        card.add(passLbl, cardGbc);
        cardGbc.gridx = 1;
        passRegField = createStyledPasswordField();
        passRegField.setPreferredSize(new Dimension(220, 38));
        card.add(passRegField, cardGbc);
        row++;
        
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        JLabel luogoLbl = new JLabel("🏠 Luogo Domicilio");
        luogoLbl.setFont(FONT_CAMPO);
        luogoLbl.setForeground(GRIGIO_SCURO);
        card.add(luogoLbl, cardGbc);
        cardGbc.gridx = 1;
        luogoField = createStyledTextField();
        luogoField.setPreferredSize(new Dimension(220, 38));
        card.add(luogoField, cardGbc);
        row++;
        
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        JLabel ruoloLbl = new JLabel("👔 Ruolo");
        ruoloLbl.setFont(FONT_CAMPO);
        ruoloLbl.setForeground(GRIGIO_SCURO);
        card.add(ruoloLbl, cardGbc);
        cardGbc.gridx = 1;
        ruoloCombo = new JComboBox<>(new String[]{"cliente", "ristoratore"});
        ruoloCombo.setFont(FONT_CAMPO);
        ruoloCombo.setBackground(BIANCO);
        ruoloCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIGIO_CHIARO, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        ruoloCombo.setPreferredSize(new Dimension(220, 38));
        card.add(ruoloCombo, cardGbc);
        row++;
        
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        cardGbc.gridwidth = 2;
        JButton registerBtn = createPremiumButton("REGISTRATI", ROSA_PRIMARIO, BIANCO);
        registerBtn.addActionListener(this);
        registerBtn.setActionCommand("doRegister");
        registerBtn.setPreferredSize(new Dimension(240, 45));
        card.add(registerBtn, cardGbc);
        row++;
        
        cardGbc.gridy = row;
        JButton backBtn = createPremiumButton("← INDIETRO", GRIGIO_CHIARO, GRIGIO_SCURO);
        backBtn.addActionListener(this);
        backBtn.setActionCommand("backToLogin");
        backBtn.setPreferredSize(new Dimension(240, 40));
        card.add(backBtn, cardGbc);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(card, gbc);
        
        return panel;
    }
    
    /**
     * Crea il pannello per la modalità guest.
     * Permette agli utenti non autenticati di effettuare ricerche
     * di base per città e di accedere alla ricerca avanzata.
     *
     * @return il pannello guest
     */
    private JPanel createGuestPanel() {
        GradientPanel panel = new GradientPanel(ROSA_CHIARO, ROSA_GRADIENTE);
        panel.setLayout(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("🔍  Modalita Ospite");
        titleLabel.setFont(FONT_TITOLO);
        titleLabel.setForeground(ROSA_PRIMARIO);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton backBtn = createPremiumButton("← INDIETRO", GRIGIO_CHIARO, GRIGIO_SCURO);
        backBtn.addActionListener(this);
        backBtn.setActionCommand("backToLogin");
        backBtn.setPreferredSize(new Dimension(140, 38));
        headerPanel.add(backBtn, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        searchBarPanel.setOpaque(false);
        
        cityField = createStyledTextField();
        cityField.setPreferredSize(new Dimension(250, 42));
        cityField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton searchBtn = createPremiumButton("🔍 CERCA", ROSA_PRIMARIO, BIANCO);
        searchBtn.addActionListener(this);
        searchBtn.setActionCommand("guestSearch");
        searchBtn.setPreferredSize(new Dimension(140, 42));
        
        JButton advancedBtn = createPremiumButton("⚙️ AVANZATA", GRIGIO_MEDIO, BIANCO);
        advancedBtn.addActionListener(this);
        advancedBtn.setActionCommand("guestAdvancedSearch");
        advancedBtn.setPreferredSize(new Dimension(160, 42));
        
        searchBarPanel.add(new JLabel("📍 Citta:"));
        searchBarPanel.add(cityField);
        searchBarPanel.add(searchBtn);
        searchBarPanel.add(advancedBtn);
        
        panel.add(searchBarPanel, BorderLayout.CENTER);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resultArea.setBackground(BIANCO);
        resultArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIGIO_CHIARO, 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(GRIGIO_CHIARO, 1, true),
            "📋  Risultati",
            TitledBorder.LEFT, TitledBorder.TOP,
            FONT_BOTTONE, ROSA_PRIMARIO
        ));
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        panel.add(scrollPane, BorderLayout.SOUTH);

        return panel;
    }
    
    /**
     * Crea la dashboard del cliente con tutti i bottoni di gestione.
     * Include le funzionalità per la ricerca avanzata, la gestione dei
     * preferiti (aggiungi/rimuovi/visualizza), la gestione delle recensioni
     * (aggiungi/modifica/elimina/visualizza) e il pulsante "VICINI"
     * per la ricerca dei ristoranti vicini al domicilio.
     *
     * @return il pannello del cliente
     */
    private JPanel createClientePanel() {
        GradientPanel panel = new GradientPanel(ROSA_CHIARO, ROSA_GRADIENTE);
        panel.setLayout(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        titleLabelCliente = new JLabel("👤  Benvenuto, Cliente!");
        titleLabelCliente.setFont(FONT_TITOLO);
        titleLabelCliente.setForeground(ROSA_PRIMARIO);
        headerPanel.add(titleLabelCliente, BorderLayout.WEST);
        
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRight.setOpaque(false);
        
        JButton viciniBtn = createPremiumButton("📍 VICINI", ROSA_PRIMARIO, BIANCO);
        viciniBtn.addActionListener(this);
        viciniBtn.setActionCommand("cliente_Vicini");
        viciniBtn.setPreferredSize(new Dimension(160, 38));
        headerRight.add(viciniBtn);
        
        JButton logoutBtn = createPremiumButton("🚪 LOGOUT", GRIGIO_CHIARO, GRIGIO_SCURO);
        logoutBtn.addActionListener(this);
        logoutBtn.setActionCommand("cliente_Logout");
        logoutBtn.setPreferredSize(new Dimension(140, 38));
        headerRight.add(logoutBtn);
        
        headerPanel.add(headerRight, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        
        String[][] bottoniCliente = {
            {"🔍 Ricerca Avanzata", "cliente_RicercaAvanzata"},
            {"❤️ I miei Preferiti", "cliente_ImieiPreferiti"},
            {"➕ Aggiungi Preferito", "cliente_AggiungiaiPreferiti"},
            {"➖ Rimuovi Preferito", "cliente_RimuovidaiPreferiti"},
            {"📝 Le mie Recensioni", "cliente_LemieRecensioni"},
            {"✏️ Aggiungi Recensione", "cliente_AggiungiRecensione"},
            {"✏️ Modifica Recensione", "cliente_ModificaRecensione"},
            {"🗑️ Elimina Recensione", "cliente_EliminaRecensione"},
        };
        
        Color[] colori = {
            ROSA_PRIMARIO, ROSA_SECONDARIO, GRIGIO_MEDIO, new Color(180, 120, 140),
            ROSA_PRIMARIO, ROSA_SECONDARIO, GRIGIO_MEDIO, new Color(180, 120, 140)
        };
        
        for (int i = 0; i < bottoniCliente.length; i++) {
            JButton btn = createPremiumButton(bottoniCliente[i][0], colori[i], BIANCO);
            btn.addActionListener(this);
            btn.setActionCommand(bottoniCliente[i][1]);
            buttonPanel.add(btn);
        }
        
        panel.add(buttonPanel, BorderLayout.CENTER);
        
        resultAreaCliente = new JTextArea();
        resultAreaCliente.setEditable(false);
        resultAreaCliente.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resultAreaCliente.setBackground(BIANCO);
        resultAreaCliente.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIGIO_CHIARO, 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JScrollPane scrollPane = new JScrollPane(resultAreaCliente);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(GRIGIO_CHIARO, 1, true),
            "📋  Area Cliente",
            TitledBorder.LEFT, TitledBorder.TOP,
            FONT_BOTTONE, ROSA_PRIMARIO
        ));
        scrollPane.setPreferredSize(new Dimension(900, 280));
        
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Crea la dashboard del ristoratore con le funzionalità di gestione.
     * Include bottoni per visualizzare i propri ristoranti, aggiungerne
     * di nuovi, visualizzare il riepilogo delle recensioni, visualizzare
     * le recensioni dettagliate e rispondere alle recensioni.
     *
     * @return il pannello del ristoratore
     */
    private JPanel createRistoratorePanel() {
        GradientPanel panel = new GradientPanel(ROSA_CHIARO, ROSA_GRADIENTE);
        panel.setLayout(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        titleLabelRistoratore = new JLabel("🍽️  Benvenuto, Ristoratore!");
        titleLabelRistoratore.setFont(FONT_TITOLO);
        titleLabelRistoratore.setForeground(ROSA_PRIMARIO);
        headerPanel.add(titleLabelRistoratore, BorderLayout.WEST);
        
        JButton logoutBtn = createPremiumButton("🚪 LOGOUT", GRIGIO_CHIARO, GRIGIO_SCURO);
        logoutBtn.addActionListener(this);
        logoutBtn.setActionCommand("ristoratore_Logout");
        logoutBtn.setPreferredSize(new Dimension(140, 38));
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        
        String[][] bottoniRistoratore = {
            {"🏠 I miei Ristoranti", "ristoratore_ImieiRistoranti"},
            {"➕ Aggiungi Ristorante", "ristoratore_AggiungiRistorante"},
            {"📊 Riepilogo Recensioni", "ristoratore_RiepilogoRecensioni"},
            {"📋 Visualizza Recensioni", "ristoratore_VisualizzaRecensioni"},
            {"💬 Rispondi a Recensione", "ristoratore_RispondiaRecensione"},
        };
        
        Color[] coloriRist = {
            GRIGIO_MEDIO, ROSA_PRIMARIO, ROSA_SECONDARIO, new Color(180, 120, 140), ROSA_PRIMARIO
        };
        
        for (int i = 0; i < bottoniRistoratore.length; i++) {
            JButton btn = createPremiumButton(bottoniRistoratore[i][0], coloriRist[i], BIANCO);
            btn.addActionListener(this);
            btn.setActionCommand(bottoniRistoratore[i][1]);
            buttonPanel.add(btn);
        }
        
        panel.add(buttonPanel, BorderLayout.CENTER);
        
        resultAreaRistoratore = new JTextArea();
        resultAreaRistoratore.setEditable(false);
        resultAreaRistoratore.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resultAreaRistoratore.setBackground(BIANCO);
        resultAreaRistoratore.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIGIO_CHIARO, 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JScrollPane scrollPane = new JScrollPane(resultAreaRistoratore);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(GRIGIO_CHIARO, 1, true),
            "📋  Area Ristoratore",
            TitledBorder.LEFT, TitledBorder.TOP,
            FONT_BOTTONE, ROSA_PRIMARIO
        ));
        scrollPane.setPreferredSize(new Dimension(900, 280));
        
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Crea il pannello di ricerca avanzata con tutti i filtri disponibili.
     * Include filtri per luogo (obbligatorio), tipo di cucina, fascia di
     * prezzo, delivery, prenotazione online, valutazione minima e raggio
     * per la ricerca per vicinanza.
     *
     * @return il pannello di ricerca avanzata
     */
    private JPanel createSearchPanel() {
        GradientPanel panel = new GradientPanel(ROSA_CHIARO, ROSA_GRADIENTE);
        panel.setLayout(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("🔍  Ricerca Ristoranti");
        titleLabel.setFont(FONT_TITOLO);
        titleLabel.setForeground(ROSA_PRIMARIO);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton backBtn = createPremiumButton("← INDIETRO", GRIGIO_CHIARO, GRIGIO_SCURO);
        backBtn.addActionListener(this);
        backBtn.setActionCommand("backFromSearch");
        backBtn.setPreferredSize(new Dimension(140, 38));
        headerPanel.add(backBtn, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        luogoSearchField = createStyledTextField();
        luogoSearchField.setPreferredSize(new Dimension(200, 38));
        cucinaSearchField = createStyledTextField();
        cucinaSearchField.setPreferredSize(new Dimension(200, 38));
        prezzoMinField = createStyledTextField();
        prezzoMinField.setPreferredSize(new Dimension(60, 38));
        prezzoMaxField = createStyledTextField();
        prezzoMaxField.setPreferredSize(new Dimension(60, 38));
        
        deliveryCombo = createStyledCombo(new String[]{"Qualsiasi", "Si", "No"});
        prenotazioneCombo = createStyledCombo(new String[]{"Qualsiasi", "Si", "No"});
        stelleCombo = createStyledCombo(new String[]{"Qualsiasi", "1+", "2+", "3+", "4+", "5"});
        raggioField = createStyledTextField();
        raggioField.setPreferredSize(new Dimension(60, 38));
        raggioField.setText("10");

        int row = 0;
        
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel luogoLbl = new JLabel("📍 Luogo (citta)");
        luogoLbl.setFont(FONT_CAMPO);
        luogoLbl.setForeground(GRIGIO_SCURO);
        form.add(luogoLbl, gbc);
        gbc.gridx = 1;
        form.add(luogoSearchField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel cucinaLbl = new JLabel("🍝 Tipo di cucina");
        cucinaLbl.setFont(FONT_CAMPO);
        cucinaLbl.setForeground(GRIGIO_SCURO);
        form.add(cucinaLbl, gbc);
        gbc.gridx = 1;
        form.add(cucinaSearchField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel prezzoLbl = new JLabel("💰 Prezzo medio (Euro)");
        prezzoLbl.setFont(FONT_CAMPO);
        prezzoLbl.setForeground(GRIGIO_SCURO);
        form.add(prezzoLbl, gbc);
        
        JPanel prezzoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        prezzoPanel.setOpaque(false);
        prezzoPanel.add(prezzoMinField);
        prezzoPanel.add(new JLabel("-"));
        prezzoPanel.add(prezzoMaxField);
        gbc.gridx = 1;
        form.add(prezzoPanel, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel deliveryLbl = new JLabel("🚚 Delivery");
        deliveryLbl.setFont(FONT_CAMPO);
        deliveryLbl.setForeground(GRIGIO_SCURO);
        form.add(deliveryLbl, gbc);
        gbc.gridx = 1;
        form.add(deliveryCombo, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel prenotazioneLbl = new JLabel("📅 Prenotazione online");
        prenotazioneLbl.setFont(FONT_CAMPO);
        prenotazioneLbl.setForeground(GRIGIO_SCURO);
        form.add(prenotazioneLbl, gbc);
        gbc.gridx = 1;
        form.add(prenotazioneCombo, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel stelleLbl = new JLabel("⭐ Valutazione minima");
        stelleLbl.setFont(FONT_CAMPO);
        stelleLbl.setForeground(GRIGIO_SCURO);
        form.add(stelleLbl, gbc);
        gbc.gridx = 1;
        form.add(stelleCombo, gbc);
        row++;

        JButton cercaBtn = createPremiumButton("🔍 CERCA", ROSA_PRIMARIO, BIANCO);
        cercaBtn.addActionListener(this);
        cercaBtn.setActionCommand("doCercaMultiCriterio");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        form.add(cercaBtn, gbc);
        row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JSeparator(), gbc);
        gbc.gridx = 1;
        form.add(new JSeparator(), gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel raggioLbl = new JLabel("📏 Raggio di ricerca (km)");
        raggioLbl.setFont(FONT_CAMPO);
        raggioLbl.setForeground(GRIGIO_SCURO);
        form.add(raggioLbl, gbc);
        gbc.gridx = 1;
        form.add(raggioField, gbc);
        row++;

        JButton viciniBtn = createPremiumButton("📍 RISTORANTI VICINI", GRIGIO_MEDIO, BIANCO);
        viciniBtn.addActionListener(this);
        viciniBtn.setActionCommand("doCercaVicini");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        form.add(viciniBtn, gbc);

        risultatiModel = new DefaultListModel<>();
        risultatiList = new JList<>(risultatiModel);
        risultatiList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        risultatiList.setFixedCellHeight(40);
        risultatiList.setBackground(BIANCO);
        risultatiList.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        risultatiList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    RistoranteDTO sel = risultatiList.getSelectedValue();
                    if (sel != null) apriDettaglioDaRicerca(sel);
                }
            }
        });
        
        JScrollPane listScroll = new JScrollPane(risultatiList);
        listScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(GRIGIO_CHIARO, 1, true),
            "📋  Risultati (doppio click per dettaglio)",
            TitledBorder.LEFT, TitledBorder.TOP,
            FONT_BOTTONE, ROSA_PRIMARIO
        ));
        listScroll.setPreferredSize(new Dimension(800, 250));

        searchStatusLabel = new JLabel(" ");
        searchStatusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        searchStatusLabel.setForeground(GRIGIO_MEDIO);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(form, BorderLayout.NORTH);
        centerPanel.add(listScroll, BorderLayout.CENTER);
        centerPanel.add(searchStatusLabel, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }
    
    // ==================== METODI DI UTILITA PER COMPONENTI STILIZZATI ====================
    
    /**
     * Crea un campo di testo con stile personalizzato.
     * Include bordi arrotondati, padding e font predefinito.
     *
     * @return il campo di testo stilizzato
     */
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(15);
        field.setFont(FONT_CAMPO);
        field.setBackground(BIANCO);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIGIO_CHIARO, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    /**
     * Crea un campo password con stile personalizzato.
     * Include bordi arrotondati, padding e font predefinito.
     *
     * @return il campo password stilizzato
     */
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(15);
        field.setFont(FONT_CAMPO);
        field.setBackground(BIANCO);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIGIO_CHIARO, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    /**
     * Crea una combo box con stile personalizzato.
     *
     * @param items gli elementi da inserire nella combo box
     * @return la combo box stilizzata
     */
    private JComboBox<String> createStyledCombo(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(FONT_CAMPO);
        combo.setBackground(BIANCO);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIGIO_CHIARO, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return combo;
    }
    
    /**
     * Crea un pulsante con stile premium.
     * Include angoli arrotondati, ombreggiatura, effetto hover
     * e padding personalizzato.
     *
     * @param text    il testo del pulsante
     * @param bgColor il colore di sfondo del pulsante
     * @param fgColor il colore del testo del pulsante
     * @return il pulsante stilizzato
     */
    private JButton createPremiumButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(OMBRA);
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 12, 12);
                
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 12, 12);
                
                g2d.setColor(getForeground());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2 - 3;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };
        
        button.setFont(FONT_BOTTONE);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(200, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
                button.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
                button.repaint();
            }
        });
        
        return button;
    }
    
    // ==================== GESTIONE EVENTI ====================
    
    /**
     * Gestisce tutti gli eventi dei pulsanti dell'interfaccia.
     * In base al comando ricevuto dall'ActionEvent, richiama il
     * metodo corrispondente per eseguire l'operazione richiesta.
     *
     * @param e l'evento generato dal pulsante
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        
        if (cmd.equals("login")) eseguiLogin();
        else if (cmd.equals("showRegister")) cardLayout.show(mainPanel, "register");
        else if (cmd.equals("showGuest")) cardLayout.show(mainPanel, "guest");
        else if (cmd.equals("doRegister")) eseguiRegistrazione();
        else if (cmd.equals("backToLogin")) cardLayout.show(mainPanel, "login");
        else if (cmd.equals("guestSearch")) guestCerca();
        else if (cmd.equals("guestAdvancedSearch")) apriRicercaAvanzataGuest();
        else if (cmd.equals("backFromSearch")) cardLayout.show(mainPanel, searchOrigin);
        else if (cmd.equals("doCercaMultiCriterio")) eseguiCercaMultiCriterio();
        else if (cmd.equals("doCercaVicini")) eseguiCercaVicini();
        else if (cmd.equals("cliente_Vicini")) clienteVicini();
        else if (cmd.equals("cliente_RicercaAvanzata")) apriRicercaAvanzataCliente();
        else if (cmd.equals("cliente_ImieiPreferiti")) clienteMostraPreferiti();
        else if (cmd.equals("cliente_AggiungiaiPreferiti")) clienteAggiungiPreferito();
        else if (cmd.equals("cliente_RimuovidaiPreferiti")) clienteRimuoviPreferito();
        else if (cmd.equals("cliente_LemieRecensioni")) clienteMostraRecensioni();
        else if (cmd.equals("cliente_AggiungiRecensione")) clienteAggiungiRecensione();
        else if (cmd.equals("cliente_ModificaRecensione")) clienteModificaRecensione();
        else if (cmd.equals("cliente_EliminaRecensione")) clienteEliminaRecensione();
        else if (cmd.equals("ristoratore_ImieiRistoranti")) ristoratoreMostraRistoranti();
        else if (cmd.equals("ristoratore_AggiungiRistorante")) ristoratoreAggiungi();
        else if (cmd.equals("ristoratore_RiepilogoRecensioni")) ristoratoreRiepilogo();
        else if (cmd.equals("ristoratore_VisualizzaRecensioni")) ristoratoreVisualizzaRecensioni();
        else if (cmd.equals("ristoratore_RispondiaRecensione")) ristoratoreRispondi();
        else if (cmd.equals("cliente_Logout") || cmd.equals("ristoratore_Logout")) logout();
    }

    // ==================== LOGIN ====================
    
    /**
     * Esegue la procedura di login inviando le credenziali al server.
     * Se il login ha successo, carica il domicilio dell'utente e
     * mostra la dashboard corrispondente al ruolo.
     * Per i clienti, avvia il caricamento automatico dei ristoranti
     * vicini al domicilio in background.
     */
    private void eseguiLogin() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        Object response = sendCommand("login", username, password);
        String respStr = response.toString();

        if (respStr.startsWith("OK")) {
            String[] parts = respStr.split("\\|");
            loggedUserUsername    = parts[1];
            loggedUserNome  = parts[2];
            loggedUserRuolo = parts[3];
            
            Object domicilioResp = sendCommand("getDomicilio", loggedUserUsername);
            if (domicilioResp.toString().startsWith("OK")) {
                String[] domParts = domicilioResp.toString().split("\\|");
                if (domParts.length > 1) {
                    loggedUserDomicilio = domParts[1];
                }
            }

            userField.setText("");
            passField.setText("");

            if (loggedUserRuolo.equals("cliente")) {
                titleLabelCliente.setText("👤  Benvenuto, " + loggedUserNome + "!");
                cardLayout.show(mainPanel, "cliente");
                if (loggedUserDomicilio != null && !loggedUserDomicilio.isEmpty()) {
                    caricaViciniInBackground();
                }
            } else {
                titleLabelRistoratore.setText("🍽️  Benvenuto, Ristoratore " + loggedUserNome + "!");
                cardLayout.show(mainPanel, "ristoratore");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Credenziali errate!");
        }
    }
    
    /**
     * Carica in background i primi 5 ristoranti vicini al domicilio
     * dell'utente e li mostra nella dashboard cliente.
     * Il raggio di ricerca è di default 10 km.
     * Questa funzione viene eseguita automaticamente dopo il login
     * del cliente come da specifiche di progetto.
     */
    private void caricaViciniInBackground() {
        final String luogo = loggedUserDomicilio;
        final double raggio = 10.0;
        
        new SwingWorker<Object, Void>() {
            @Override
            protected Object doInBackground() throws Exception {
                return sendCommand("cercaVicini", luogo, raggio);
            }
            
            @Override
            protected void done() {
                try {
                    Object response = get();
                    if (response instanceof ArrayList) {
                        @SuppressWarnings("unchecked")
                        ArrayList<RistoranteDTO> lista = (ArrayList<RistoranteDTO>) response;
                        if (!lista.isEmpty()) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("🍽️ RISTORANTI VICINI A ").append(luogo).append(":\n\n");
                            int maxMostra = Math.min(lista.size(), 5);
                            for (int i = 0; i < maxMostra; i++) {
                                RistoranteDTO r = lista.get(i);
                                sb.append("📍 ").append(r.getNome());
                                if (r.getDistanzaKm() >= 0) {
                                    sb.append("  (").append(String.format("%.1f km", r.getDistanzaKm())).append(")");
                                }
                                sb.append("\n");
                            }
                            if (lista.size() > 5) {
                                sb.append("\n... e altri ").append(lista.size() - 5).append(" ristoranti.");
                            }
                            sb.append("\n\n💡 Usa il bottone 'VICINI' per una ricerca completa.");
                            resultAreaCliente.setText(sb.toString());
                        }
                    }
                } catch (Exception e) {
                    // Silenzioso - non bloccare il login
                }
            }
        }.execute();
    }

    // ==================== REGISTRAZIONE ====================
    
    /**
     * Esegue la procedura di registrazione di un nuovo utente.
     * Verifica che tutti i campi obbligatori siano compilati e che
     * la password abbia almeno 6 caratteri.
     * Controlla che lo username non sia già in uso tramite il server.
     * In caso di successo, torna alla schermata di login.
     */
    private void eseguiRegistrazione() {
        String nome     = nomeField.getText().trim();
        String cognome  = cognomeField.getText().trim();
        String username = userRegField.getText().trim();
        String password = new String(passRegField.getPassword()).trim();
        String dataNasc = dataNascitaField.getText().trim();
        String luogo    = luogoField.getText().trim();
        String ruolo    = (String) ruoloCombo.getSelectedItem();

        if (nome.isEmpty() || cognome.isEmpty() || username.isEmpty() || password.isEmpty() || luogo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Compila tutti i campi obbligatori!");
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "La password deve essere di almeno 6 caratteri!");
            return;
        }

        Object userCheck = sendCommand("userEsiste", username);
        if (userCheck.toString().equals("OK|true")) {
            JOptionPane.showMessageDialog(this, "Username già in uso, scegline un altro!");
            return;
        }

        String dataDaInviare = dataNasc.isEmpty() ? "N/A" : dataNasc;
        Object response = sendCommand("registra", nome, cognome, username, password, dataDaInviare, luogo, ruolo);
        String respStr = response.toString();

        if (respStr.startsWith("OK")) {
            JOptionPane.showMessageDialog(this, "Registrazione completata! Ora puoi fare il login.");
            cardLayout.show(mainPanel, "login");
            nomeField.setText("");
            cognomeField.setText("");
            dataNascitaField.setText("");
            userRegField.setText("");
            passRegField.setText("");
            luogoField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Errore nella registrazione: " + respStr);
        }
    }
    
    // ==================== RICERCA AVANZATA ====================
    
    /**
     * Apre la ricerca avanzata dalla modalità guest.
     * Prefill automaticamente il campo luogo con la città inserita
     * nella barra di ricerca della modalità guest.
     */
    private void apriRicercaAvanzataGuest() {
        searchOrigin = "guest";
        String prefillLuogo = cityField.getText().trim();
        luogoSearchField.setText(prefillLuogo);
        cucinaSearchField.setText("");
        prezzoMinField.setText("");
        prezzoMaxField.setText("");
        deliveryCombo.setSelectedIndex(0);
        prenotazioneCombo.setSelectedIndex(0);
        stelleCombo.setSelectedIndex(0);
        raggioField.setText("10");
        risultatiModel.clear();
        searchStatusLabel.setText(" ");
        cardLayout.show(mainPanel, "search");
    }
    
    /**
     * Apre la ricerca avanzata dalla dashboard cliente.
     * Prefill automaticamente il campo luogo con il domicilio
     * dell'utente loggato, se disponibile.
     */
    private void apriRicercaAvanzataCliente() {
        searchOrigin = "cliente";
        String prefillLuogo = (loggedUserDomicilio != null && !loggedUserDomicilio.isEmpty()) 
                       ? loggedUserDomicilio : "";
        luogoSearchField.setText(prefillLuogo);
        cucinaSearchField.setText("");
        prezzoMinField.setText("");
        prezzoMaxField.setText("");
        deliveryCombo.setSelectedIndex(0);
        prenotazioneCombo.setSelectedIndex(0);
        stelleCombo.setSelectedIndex(0);
        raggioField.setText("10");
        risultatiModel.clear();
        searchStatusLabel.setText(" ");
        
        if (prefillLuogo.isEmpty()) {
            searchStatusLabel.setText("Attenzione: nessun domicilio registrato. Inserisci una citta manualmente.");
        }
        
        cardLayout.show(mainPanel, "search");
    }

    /**
     * Legge un valore Double da un campo di testo in modo opzionale.
     * Se il campo è vuoto, restituisce null.
     * Se il campo contiene un valore non numerico, mostra un messaggio
     * di errore e restituisce null.
     *
     * @param field     il campo di testo da leggere
     * @param nomeCampo il nome del campo per i messaggi di errore
     * @return il valore Double, o null se il campo è vuoto o non valido
     */
    private Double leggiDoubleOpzionale(JTextField field, String nomeCampo) {
        String testo = field.getText().trim();
        if (testo.isEmpty()) return null;
        try {
            return Double.parseDouble(testo.replace(",", "."));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Il campo \"" + nomeCampo + "\" deve essere un numero.");
            return null;
        }
    }

    /**
     * Legge un valore Boolean da una combo box per i filtri Si/No/Qualsiasi.
     *
     * @param combo la combo box da leggere
     * @return true se selezionato "Si", false se selezionato "No",
     *         null se selezionato "Qualsiasi"
     */
    private Boolean leggiBooleanCombo(JComboBox<String> combo) {
        String sel = (String) combo.getSelectedItem();
        if (sel == null || sel.equals("Qualsiasi")) return null;
        return sel.equals("Si");
    }

    /**
     * Esegue la ricerca multi-criterio con tutti i filtri selezionati.
     * Il campo luogo è obbligatorio.
     * I risultati vengono visualizzati nella lista della ricerca avanzata.
     */
    private void eseguiCercaMultiCriterio() {
        String luogo = luogoSearchField.getText().trim();
        if (luogo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Il campo \"Luogo (citta)\" e obbligatorio.");
            return;
        }

        String cucina = cucinaSearchField.getText().trim();
        if (cucina.isEmpty()) cucina = null;

        Double prezzoMin = leggiDoubleOpzionale(prezzoMinField, "Prezzo da");
        if (prezzoMinField.getText().trim().length() > 0 && prezzoMin == null) return;

        Double prezzoMax = leggiDoubleOpzionale(prezzoMaxField, "Prezzo a");
        if (prezzoMaxField.getText().trim().length() > 0 && prezzoMax == null) return;

        if (prezzoMin != null && prezzoMax != null && prezzoMin > prezzoMax) {
            JOptionPane.showMessageDialog(this, "Il prezzo minimo non puo essere maggiore del prezzo massimo.");
            return;
        }

        Boolean delivery = leggiBooleanCombo(deliveryCombo);
        Boolean prenotazione = leggiBooleanCombo(prenotazioneCombo);

        String stelleSel = (String) stelleCombo.getSelectedItem();
        Double mediaMin = null;
        if (stelleSel != null && !stelleSel.equals("Qualsiasi")) {
            mediaMin = Double.parseDouble(stelleSel.replace("+", ""));
        }

        searchStatusLabel.setText("Ricerca in corso...");
        
        final String luogoFinale = luogo;
        final String cucinaFinale = cucina;
        final Double prezzoMinFinale = prezzoMin;
        final Double prezzoMaxFinale = prezzoMax;
        final Boolean deliveryFinale = delivery;
        final Boolean prenotazioneFinale = prenotazione;
        final Double mediaMinFinale = mediaMin;
        
        new SwingWorker<Object, Void>() {
            @Override
            protected Object doInBackground() throws Exception {
                return sendCommand("cercaMultiCriterio", luogoFinale, cucinaFinale,
                        prezzoMinFinale, prezzoMaxFinale, deliveryFinale, prenotazioneFinale, mediaMinFinale);
            }
            
            @Override
            protected void done() {
                try {
                    Object response = get();
                    mostraRisultatiRicerca(response, "Nessun ristorante trovato con questi criteri a \"" + luogoFinale + "\".");
                } catch (Exception e) {
                    searchStatusLabel.setText("Errore: " + e.getMessage());
                }
            }
        }.execute();
    }

    /**
     * Esegue la ricerca dei ristoranti vicini a un luogo specificato.
     * Il campo luogo è obbligatorio e il raggio deve essere un numero positivo.
     * Utilizza la formula di Haversine per il calcolo della distanza.
     */
    private void eseguiCercaVicini() {
        String luogo = luogoSearchField.getText().trim();
        if (luogo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Il campo \"Luogo (citta)\" e obbligatorio.");
            return;
        }

        double raggio;
        try {
            raggio = Double.parseDouble(raggioField.getText().trim().replace(",", "."));
            if (raggio <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Il raggio deve essere un numero positivo.");
            return;
        }

        searchStatusLabel.setText("Ricerca in corso...");
        
        final String luogoFinale = luogo;
        final double raggioFinale = raggio;
        
        new SwingWorker<Object, Void>() {
            @Override
            protected Object doInBackground() throws Exception {
                return sendCommand("cercaVicini", luogoFinale, raggioFinale);
            }
            
            @Override
            protected void done() {
                try {
                    Object response = get();
                    mostraRisultatiRicerca(response, "Nessun ristorante trovato entro " + raggioFinale +
                            " km da \"" + luogoFinale + "\".");
                } catch (Exception e) {
                    searchStatusLabel.setText("Errore: " + e.getMessage());
                }
            }
        }.execute();
    }

    /**
     * Mostra i risultati della ricerca nella lista della ricerca avanzata.
     *
     * @param response       la risposta del server (ArrayList di RistoranteDTO)
     * @param messaggioVuoto il messaggio da mostrare se non ci sono risultati
     */
    @SuppressWarnings("unchecked")
    private void mostraRisultatiRicerca(Object response, String messaggioVuoto) {
        risultatiModel.clear();

        if (response instanceof String) {
            searchStatusLabel.setText("Errore: " + response);
            return;
        }

        if (!(response instanceof ArrayList)) {
            searchStatusLabel.setText("Risposta inattesa dal server.");
            return;
        }

        ArrayList<RistoranteDTO> lista = (ArrayList<RistoranteDTO>) response;
        if (lista.isEmpty()) {
            searchStatusLabel.setText(messaggioVuoto);
            return;
        }

        for (RistoranteDTO r : lista) risultatiModel.addElement(r);
        searchStatusLabel.setText("Trovati " + lista.size() + " ristoranti.");
    }

    /**
     * Apre il dettaglio di un ristorante dalla lista dei risultati.
     * Il doppio click su un elemento della lista richiama questo metodo.
     *
     * @param r il ristorante selezionato dalla lista
     */
    private void apriDettaglioDaRicerca(RistoranteDTO r) {
        if (searchOrigin.equals("cliente")) {
            mostraDettaglioRistorante(r.getNome());
            cardLayout.show(mainPanel, "cliente");
        } else {
            mostraDettaglioRistoranteGuest(r.getNome());
            cardLayout.show(mainPanel, "guest");
        }
    }
    
    // ==================== CLIENTE - RISTORANTI VICINI ====================
    
    /**
     * Cerca i ristoranti vicini al domicilio del cliente loggato.
     * Se l'utente non ha un domicilio registrato, mostra un avviso.
     * Richiede all'utente di inserire il raggio di ricerca (default 10 km).
     */
    private void clienteVicini() {
        if (loggedUserDomicilio == null || loggedUserDomicilio.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Nessun domicilio registrato.\nImposta il domicilio nel tuo profilo per usare questa funzione.",
                "Domicilio mancante", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        resultAreaCliente.setText("🔍 Ricerca ristoranti vicini a " + loggedUserDomicilio + "...");
        
        String raggioStr = JOptionPane.showInputDialog(this, 
            "Inserisci il raggio di ricerca in km (default: 10):",
            "Ristoranti vicini", JOptionPane.QUESTION_MESSAGE);
        
        double raggio;
        try {
            raggio = (raggioStr == null || raggioStr.isEmpty()) ? 10.0 : Double.parseDouble(raggioStr.replace(",", "."));
            if (raggio <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            resultAreaCliente.setText("Raggio non valido. Inserisci un numero positivo.");
            return;
        }
        
        final String luogo = loggedUserDomicilio;
        final double raggioFinale = raggio;
        
        new SwingWorker<Object, Void>() {
            @Override
            protected Object doInBackground() throws Exception {
                return sendCommand("cercaVicini", luogo, raggioFinale);
            }
            
            @Override
            protected void done() {
                try {
                    Object response = get();
                    mostraRisultatiCliente(response, "Nessun ristorante trovato entro " + raggioFinale + 
                            " km da \"" + luogo + "\".");
                } catch (Exception e) {
                    resultAreaCliente.setText("Errore: " + e.getMessage());
                }
            }
        }.execute();
    }
    
    /**
     * Mostra i risultati della ricerca dei ristoranti vicini nella dashboard cliente.
     * Visualizza per ogni ristorante: nome, cucina, prezzo, distanza,
     * media stelle e numero di recensioni.
     *
     * @param response       la risposta del server (ArrayList di RistoranteDTO)
     * @param messaggioVuoto il messaggio da mostrare se non ci sono risultati
     */
    @SuppressWarnings("unchecked")
    private void mostraRisultatiCliente(Object response, String messaggioVuoto) {
        if (response instanceof String) {
            resultAreaCliente.setText("Errore: " + response);
            return;
        }
        
        if (!(response instanceof ArrayList)) {
            resultAreaCliente.setText("Risposta inattesa dal server.");
            return;
        }
        
        ArrayList<RistoranteDTO> lista = (ArrayList<RistoranteDTO>) response;
        if (lista.isEmpty()) {
            resultAreaCliente.setText(messaggioVuoto);
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("🍽️ RISTORANTI VICINI A ").append(loggedUserDomicilio).append(":\n\n");
        
        for (RistoranteDTO r : lista) {
            sb.append("📍 ").append(r.getNome()).append("\n");
            sb.append("   Cucina: ").append(r.getCucina()).append("\n");
            sb.append("   Prezzo: ").append(r.getPrezzo()).append("€\n");
            if (r.getDistanzaKm() >= 0) {
                sb.append(String.format("   Distanza: %.1f km\n", r.getDistanzaKm()));
            }
            if (r.getNumeroRecensioni() > 0) {
                sb.append(String.format("   ⭐ %.1f/5 (%d recensioni)\n", r.getMediaStelle(), r.getNumeroRecensioni()));
            } else {
                sb.append("   ⭐ Nessuna recensione\n");
            }
            sb.append("\n");
        }
        
        sb.append("---\nTrovati ").append(lista.size()).append(" ristoranti.");
        resultAreaCliente.setText(sb.toString());
    }
    
    // ==================== GUEST ====================
    
    /**
     * Esegue una ricerca di base per città nella modalità guest.
     * Mostra la lista dei ristoranti trovati e permette di selezionarne
     * uno per visualizzare i dettagli tramite una finestra di dialogo.
     */
    private void guestCerca() {
        String city = cityField.getText().trim();
        if (city.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inserisci una citta");
            return;
        }
        
        resultArea.setText("Ricerca in corso...");
        Object response = sendCommand("cercaPerCitta", city);
        String respStr = response.toString();
        
        if (respStr.startsWith("OK")) {
            String[] parts = respStr.split("\\|");
            int count = Integer.parseInt(parts[1]);
            if (count == 0) {
                resultArea.setText("Nessun ristorante trovato a " + city);
                return;
            }

            StringBuilder sb = new StringBuilder("Ristoranti a " + city + ":\n\n");
            String[] nomi = new String[Math.min(count, 100)];
            for (int i = 0; i < count && i < 100; i++) {
                nomi[i] = parts[2 + i*3];
                sb.append((i+1) + ". " + nomi[i] + "\n");
                sb.append("   Cucina: " + parts[4 + i*3] + "\n\n");
            }
            resultArea.setText(sb.toString());

            String scelta = (String) JOptionPane.showInputDialog(
                this,
                "Seleziona un ristorante per vedere i dettagli:",
                "Seleziona Ristorante",
                JOptionPane.QUESTION_MESSAGE,
                null,
                nomi,
                nomi[0]
            );
            if (scelta != null) {
                mostraDettaglioRistoranteGuest(scelta);
            }
        } else {
            resultArea.setText("Errore: " + respStr);
        }
    }

    /**
     * Mostra il dettaglio di un ristorante nella modalità guest.
     * Visualizza informazioni anagrafiche, servizi, media stelle
     * e tutte le recensioni del ristorante in forma anonima.
     *
     * @param nomeRistorante il nome del ristorante da visualizzare
     */
    private void mostraDettaglioRistoranteGuest(String nomeRistorante) {
        Object detResp = sendCommand("dettaglioRistorante", nomeRistorante);
        String detStr = detResp.toString();

        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(nomeRistorante).append(" ===\n\n");

        if (detStr.startsWith("OK")) {
            String[] d = detStr.split("\\|");
            sb.append("Indirizzo:    ").append(d.length > 2 ? d[2] : "N/A").append("\n");
            sb.append("Citta:        ").append(d.length > 3 ? d[3] : "N/A").append("\n");
            sb.append("Nazione:      ").append(d.length > 4 ? d[4] : "N/A").append("\n");
            sb.append("Prezzo medio: ").append(d.length > 5 ? d[5] + "Euro" : "N/A").append("\n");
            sb.append("Cucina:       ").append(d.length > 6 ? d[6] : "N/A").append("\n");
            sb.append("Delivery:     ").append(d.length > 9  ? (d[9].equals("true")  ? "Si" : "No") : "N/A").append("\n");
            sb.append("Prenotazione: ").append(d.length > 10 ? (d[10].equals("true") ? "Si" : "No") : "N/A").append("\n");
        }

        Object mediaResp = sendCommand("mediaRistorante", nomeRistorante);
        String mediaStr = mediaResp.toString();
        if (mediaStr.startsWith("OK")) {
            String[] m = mediaStr.split("\\|");
            sb.append("\nMedia stelle: ").append(m.length > 1 ? m[1] : "0").append("/5");
            sb.append("  (").append(m.length > 2 ? m[2] : "0").append(" recensioni)\n");
        }

        Object recResp = sendCommand("recensioniRistorante", nomeRistorante);
        String recStr = recResp.toString();
        if (recStr.startsWith("OK")) {
            String[] rp = recStr.split("\\|");
            int recCount = Integer.parseInt(rp[1]);
            if (recCount == 0) {
                sb.append("\nNessuna recensione ancora.\n");
            } else {
                sb.append("\nRECENSIONI:\n\n");
                for (int i = 0; i < recCount; i++) {
                    int base = 2 + i * 6;
                    String testo  = (rp.length > base+1) ? rp[base+1] : "";
                    String stelle = (rp.length > base+2) ? rp[base+2] : "0";
                    String data   = (rp.length > base+3) ? rp[base+3] : "";
                    String risp   = (rp.length > base+4) ? rp[base+4] : "";
                    sb.append("Voto: ").append(stelle).append(" stelle\n");
                    sb.append("Data: ").append(data).append("\n");
                    if (!testo.isEmpty()) sb.append("Commento: \"").append(testo).append("\"\n");
                    if (!risp.isEmpty())  sb.append("Risposta: \"").append(risp).append("\"\n");
                    sb.append("\n");
                }
            }
        }

        resultArea.setText(sb.toString());
    }
    
    // ==================== CLIENTE ====================
    
    /**
     * Mostra il dettaglio di un ristorante nella dashboard cliente.
     * Visualizza informazioni anagrafiche, servizi, media stelle
     * e tutte le recensioni del ristorante.
     *
     * @param nomeRistorante il nome del ristorante da visualizzare
     */
    private void mostraDettaglioRistorante(String nomeRistorante) {
        Object detResp = sendCommand("dettaglioRistorante", nomeRistorante);
        String detStr = detResp.toString();

        StringBuilder sb = new StringBuilder();
        sb.append("=== " + nomeRistorante + " ===\n\n");

        if (detStr.startsWith("OK")) {
            String[] d = detStr.split("\\|");
            sb.append("Indirizzo: ").append(d.length > 2 ? d[2] : "N/A").append("\n");
            sb.append("Citta: ").append(d.length > 3 ? d[3] : "N/A").append("\n");
            sb.append("Nazione: ").append(d.length > 4 ? d[4] : "N/A").append("\n");
            sb.append("Prezzo: ").append(d.length > 5 ? d[5] : "N/A").append("\n");
            sb.append("Cucina: ").append(d.length > 6 ? d[6] : "N/A").append("\n");
            sb.append("Delivery: ").append(d.length > 9 ? (d[9].equals("true") ? "Si" : "No") : "N/A").append("\n");
            sb.append("Prenotazione: ").append(d.length > 10 ? (d[10].equals("true") ? "Si" : "No") : "N/A").append("\n");
        }

        Object mediaResp = sendCommand("mediaRistorante", nomeRistorante);
        String mediaStr = mediaResp.toString();
        if (mediaStr.startsWith("OK")) {
            String[] m = mediaStr.split("\\|");
            sb.append("\nMedia stelle: ").append(m.length > 1 ? m[1] : "0").append("/5");
            sb.append("  (").append(m.length > 2 ? m[2] : "0").append(" recensioni)\n");
        }

        Object recResp = sendCommand("recensioniRistorante", nomeRistorante);
        String recStr = recResp.toString();
        if (recStr.startsWith("OK")) {
            String[] rp = recStr.split("\\|");
            int recCount = Integer.parseInt(rp[1]);
            if (recCount == 0) {
                sb.append("\nNessuna recensione ancora.\n");
            } else {
                sb.append("\nRECENSIONI:\n\n");
                for (int i = 0; i < recCount; i++) {
                    int base = 2 + i * 6;
                    String cliente = (rp.length > base)   ? rp[base]   : "";
                    String testo   = (rp.length > base+1) ? rp[base+1] : "";
                    String stelle  = (rp.length > base+2) ? rp[base+2] : "0";
                    String data    = (rp.length > base+3) ? rp[base+3] : "";
                    String risp    = (rp.length > base+4) ? rp[base+4] : "";
                    sb.append("Utente: ").append(cliente).append("\n");
                    sb.append("Voto: ").append(stelle).append(" stelle\n");
                    sb.append("Data: ").append(data).append("\n");
                    if (!testo.isEmpty()) sb.append("Commento: \"").append(testo).append("\"\n");
                    if (!risp.isEmpty())  sb.append("Risposta: \"").append(risp).append("\"\n");
                    sb.append("\n");
                }
            }
        }

        resultAreaCliente.setText(sb.toString());
    }
    
    /**
     * Mostra la lista dei ristoranti preferiti del cliente.
     * Recupera la lista dal server e la visualizza nell'area risultati.
     */
    private void clienteMostraPreferiti() {
        resultAreaCliente.setText("Caricamento preferiti...");
        Object response = sendCommand("getPreferiti", loggedUserUsername);
        String respStr = response.toString();
        
        if (respStr.startsWith("OK")) {
            String[] parts = respStr.split("\\|");
            int count = Integer.parseInt(parts[1]);
            if (count == 0) {
                resultAreaCliente.setText("Nessun ristorante nei preferiti.");
            } else {
                StringBuilder sb = new StringBuilder("I tuoi ristoranti preferiti:\n\n");
                for (int i = 0; i < count; i++) {
                    sb.append((i+1) + ". " + parts[2 + i] + "\n");
                }
                resultAreaCliente.setText(sb.toString());
            }
        } else {
            resultAreaCliente.setText("Errore: " + respStr);
        }
    }
    
    /**
     * Aggiunge un ristorante ai preferiti del cliente.
     * Prima chiede la città, poi mostra la lista dei ristoranti
     * disponibili e permette di selezionarne uno da aggiungere.
     */
    private void clienteAggiungiPreferito() {
        String city = JOptionPane.showInputDialog(this, "Inserisci citta per cercare:");
        if (city == null || city.isEmpty()) return;
        
        Object response = sendCommand("cercaPerCitta", city);
        String respStr = response.toString();
        
        if (respStr.startsWith("OK")) {
            String[] parts = respStr.split("\\|");
            int count = Integer.parseInt(parts[1]);
            if (count == 0) {
                resultAreaCliente.setText("Nessun ristorante a " + city);
                return;
            }
            
            String[] options = new String[count];
            for (int i = 0; i < count; i++) options[i] = parts[2 + i*3];
            
            String scelta = (String) JOptionPane.showInputDialog(this, "Seleziona ristorante:", "Aggiungi ai Preferiti",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (scelta != null) {
                Object addResp = sendCommand("aggiungiPreferito", loggedUserUsername, scelta);
                resultAreaCliente.setText(addResp.toString().startsWith("OK") ? scelta + " aggiunto ai preferiti!" : "Errore: " + addResp);
            }
        }
    }
    
    /**
     * Rimuove un ristorante dalla lista dei preferiti del cliente.
     * Mostra la lista dei preferiti e permette di selezionarne uno
     * da rimuovere.
     */
    private void clienteRimuoviPreferito() {
        Object response = sendCommand("getPreferiti", loggedUserUsername);
        String respStr = response.toString();
        
        if (respStr.startsWith("OK")) {
            String[] parts = respStr.split("\\|");
            int count = Integer.parseInt(parts[1]);
            if (count == 0) {
                resultAreaCliente.setText("Nessun preferito da rimuovere.");
                return;
            }
            
            String[] options = new String[count];
            for (int i = 0; i < count; i++) options[i] = parts[2 + i];
            
            String scelta = (String) JOptionPane.showInputDialog(this, "Seleziona ristorante da rimuovere:", "Rimuovi dai Preferiti",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (scelta != null) {
                Object removeResp = sendCommand("rimuoviPreferito", loggedUserUsername, scelta);
                resultAreaCliente.setText(removeResp.toString().startsWith("OK") ? scelta + " rimosso dai preferiti!" : "Errore: " + removeResp);
            }
        }
    }
    
    /**
     * Mostra tutte le recensioni scritte dal cliente.
     * Per ogni recensione visualizza: ristorante, voto, data,
     * commento e eventuale risposta del ristoratore.
     */
    private void clienteMostraRecensioni() {
        resultAreaCliente.setText("Caricamento recensioni...");
        Object response = sendCommand("recensioniCliente", loggedUserUsername);
        String respStr = response.toString();
        
        if (respStr.startsWith("OK")) {
            String[] parts = respStr.split("\\|");
            int count = Integer.parseInt(parts[1]);
            if (count == 0) {
                resultAreaCliente.setText("Non hai ancora scritto nessuna recensione.");
            } else {
                StringBuilder sb = new StringBuilder("LE TUE RECENSIONI:\n\n");
                for (int i = 0; i < count; i++) {
                    int base = 2 + i * 5;
                    String ristorante = parts[base];
                    String testo = (parts.length > base+1) ? parts[base+1] : "";
                    String stelle = (parts.length > base+2) ? parts[base+2] : "0";
                    String data = (parts.length > base+3) ? parts[base+3] : "";
                    String risposta = (parts.length > base+4) ? parts[base+4] : "";
                    
                    sb.append("Ristorante: ").append(ristorante).append("\n");
                    sb.append("Voto: ").append(stelle).append(" stelle\n");
                    sb.append("Data: ").append(data).append("\n");
                    if (!testo.isEmpty()) sb.append("Commento: \"").append(testo).append("\"\n");
                    if (!risposta.isEmpty()) sb.append("Risposta ristoratore: \"").append(risposta).append("\"\n");
                    if (i < count-1) sb.append("\n----------------------------------------\n\n");
                }
                resultAreaCliente.setText(sb.toString());
            }
        } else {
            resultAreaCliente.setText("Errore: " + respStr);
        }
    }
    
    /**
     * Aggiunge una nuova recensione per un ristorante selezionato.
     * Richiede all'utente di selezionare un ristorante, inserire il
     * testo (opzionale) e il voto da 1 a 5 stelle.
     */
    private void clienteAggiungiRecensione() {
        String city = JOptionPane.showInputDialog(this, "Inserisci citta del ristorante:");
        if (city == null || city.isEmpty()) return;
        
        Object response = sendCommand("cercaPerCitta", city);
        String respStr = response.toString();
        
        if (respStr.startsWith("OK")) {
            String[] parts = respStr.split("\\|");
            int count = Integer.parseInt(parts[1]);
            if (count == 0) {
                resultAreaCliente.setText("Nessun ristorante a " + city);
                return;
            }
            
            String[] options = new String[count];
            for (int i = 0; i < count; i++) options[i] = parts[2 + i*3];
            
            String ristorante = (String) JOptionPane.showInputDialog(this, "Seleziona ristorante:", "Aggiungi Recensione",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (ristorante == null) return;
            
            String testo = JOptionPane.showInputDialog(this, "Testo recensione (opzionale):");
            String stelleStr = JOptionPane.showInputDialog(this, "Voto (1-5 stelle):");
            if (stelleStr == null) return;
            
            try {
                int stelle = Integer.parseInt(stelleStr);
                if (stelle < 1 || stelle > 5) throw new NumberFormatException();
                Object addResp = sendCommand("aggiungiRecensione", ristorante, loggedUserUsername, testo == null ? "" : testo, stelle);
                resultAreaCliente.setText(addResp.toString().startsWith("OK") ? "Recensione aggiunta!" : "Errore: " + addResp);
            } catch (NumberFormatException ex) {
                resultAreaCliente.setText("Voto non valido (deve essere 1-5)");
            }
        }
    }
    
    /**
     * Modifica una recensione esistente del cliente.
     * Mostra la lista delle recensioni del cliente e permette di
     * selezionarne una da modificare, cambiando testo e voto.
     */
    private void clienteModificaRecensione() {
        Object response = sendCommand("recensioniCliente", loggedUserUsername);
        String respStr = response.toString();
        
        if (respStr.startsWith("OK")) {
            String[] parts = respStr.split("\\|");
            int count = Integer.parseInt(parts[1]);
            if (count == 0) {
                resultAreaCliente.setText("Non hai recensioni da modificare.");
                return;
            }
            
            String[] options = new String[count];
            for (int i = 0; i < count; i++) options[i] = parts[2 + i*5];
            
            String ristorante = (String) JOptionPane.showInputDialog(this, "Seleziona recensione da modificare:", "Modifica Recensione",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (ristorante == null) return;
            
            String nuovoTesto = JOptionPane.showInputDialog(this, "Nuovo testo (lascia vuoto per mantenere):");
            String nuoveStelleStr = JOptionPane.showInputDialog(this, "Nuovo voto (1-5):");
            if (nuoveStelleStr == null) return;
            
            try {
                int nuoveStelle = Integer.parseInt(nuoveStelleStr);
                if (nuoveStelle < 1 || nuoveStelle > 5) throw new NumberFormatException();
                Object modResp = sendCommand("modificaRecensione", ristorante, loggedUserUsername, nuovoTesto == null ? "" : nuovoTesto, nuoveStelle);
                resultAreaCliente.setText(modResp.toString().startsWith("OK") ? "Recensione modificata!" : "Errore: " + modResp);
            } catch (NumberFormatException ex) {
                resultAreaCliente.setText("Voto non valido (deve essere 1-5)");
            }
        }
    }
    
    /**
     * Elimina una recensione esistente del cliente.
     * Mostra la lista delle recensioni del cliente e permette di
     * selezionarne una da eliminare, richiedendo conferma prima
     * di procedere con l'eliminazione.
     */
    private void clienteEliminaRecensione() {
        Object response = sendCommand("recensioniCliente", loggedUserUsername);
        String respStr = response.toString();
        
        if (respStr.startsWith("OK")) {
            String[] parts = respStr.split("\\|");
            int count = Integer.parseInt(parts[1]);
            if (count == 0) {
                resultAreaCliente.setText("Non hai recensioni da eliminare.");
                return;
            }
            
            String[] options = new String[count];
            for (int i = 0; i < count; i++) options[i] = parts[2 + i*5];
            
            String ristorante = (String) JOptionPane.showInputDialog(this, "Seleziona recensione da eliminare:", "Elimina Recensione",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (ristorante == null) return;
            
            int conferma = JOptionPane.showConfirmDialog(this, "Eliminare la recensione per " + ristorante + "?", "Conferma", JOptionPane.YES_NO_OPTION);
            if (conferma == JOptionPane.YES_OPTION) {
                Object delResp = sendCommand("eliminaRecensione", ristorante, loggedUserUsername);
                resultAreaCliente.setText(delResp.toString().startsWith("OK") ? "Recensione eliminata!" : "Errore: " + delResp);
            }
        }
    }
    
    // ==================== RISTORATORE ====================
    
    /**
     * Mostra la lista dei ristoranti posseduti dal ristoratore.
     * Recupera la lista dal server e la visualizza nell'area risultati.
     */
    private void ristoratoreMostraRistoranti() {
        Object response = sendCommand("getMieiRistoranti", loggedUserUsername);
        String respStr = response.toString();
        
        if (respStr.startsWith("OK")) {
            String[] parts = respStr.split("\\|");
            int count = Integer.parseInt(parts[1]);
            if (count == 0) {
                resultAreaRistoratore.setText("Non hai ancora inserito nessun ristorante.\n\nUsa 'Aggiungi Ristorante' per crearne uno.");
            } else {
                StringBuilder sb = new StringBuilder("I TUOI RISTORANTI:\n\n");
                for (int i = 0; i < count; i++) {
                    sb.append((i+1) + ". " + parts[2 + i] + "\n");
                }
                resultAreaRistoratore.setText(sb.toString());
            }
        } else {
            resultAreaRistoratore.setText("Errore: " + respStr);
        }
    }
    
    /**
     * Aggiunge un nuovo ristorante al database per il ristoratore.
     * Mostra un form con tutti i campi richiesti (nome, indirizzo,
     * città, nazione, prezzo, cucina, coordinate, delivery, prenotazione).
     * Verifica che tutti i dati siano validi prima dell'invio.
     */
    private void ristoratoreAggiungi() {
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField nome = new JTextField(), indirizzo = new JTextField(), citta = new JTextField();
        JTextField nazione = new JTextField(), prezzo = new JTextField(), cucina = new JTextField();
        JTextField longitudine = new JTextField(), latitudine = new JTextField();
        JCheckBox delivery = new JCheckBox(), prenotazione = new JCheckBox();
        
        form.add(new JLabel("Nome:")); form.add(nome);
        form.add(new JLabel("Indirizzo:")); form.add(indirizzo);
        form.add(new JLabel("Citta:")); form.add(citta);
        form.add(new JLabel("Nazione:")); form.add(nazione);
        form.add(new JLabel("Prezzo (Euro):")); form.add(prezzo);
        form.add(new JLabel("Cucina:")); form.add(cucina);
        form.add(new JLabel("Longitudine:")); form.add(longitudine);
        form.add(new JLabel("Latitudine:")); form.add(latitudine);
        form.add(new JLabel("Delivery:")); form.add(delivery);
        form.add(new JLabel("Prenotazione:")); form.add(prenotazione);
        
        int ok = JOptionPane.showConfirmDialog(this, form, "Nuovo Ristorante", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                if (nome.getText().trim().isEmpty()) {
                    resultAreaRistoratore.setText("Il nome del ristorante e obbligatorio!");
                    return;
                }

                String prezzoTesto = prezzo.getText().trim().replace(",", ".");
                double prezzoValore;
                try {
                    prezzoValore = Double.parseDouble(prezzoTesto);
                } catch (NumberFormatException ex) {
                    resultAreaRistoratore.setText("Prezzo non valido! Inserisci un numero (es. 25 o 25.50), senza simbolo Euro.");
                    return;
                }
                if (prezzoValore <= 0) {
                    resultAreaRistoratore.setText("Il prezzo medio deve essere maggiore di zero.");
                    return;
                }

                Object check = sendCommand("verificaEsistenzaRistorante", nome.getText().trim());
                if (check.toString().equals("OK|true")) {
                    resultAreaRistoratore.setText("Ristorante gia esistente!");
                    return;
                }
                
                Object resp = sendCommand("aggiungiRistoranteCustom", 
                    nome.getText().trim(), indirizzo.getText().trim(), citta.getText().trim(),
                    nazione.getText().trim(), prezzoTesto, cucina.getText().trim(),
                    Double.parseDouble(longitudine.getText().trim()), 
                    Double.parseDouble(latitudine.getText().trim()),
                    delivery.isSelected(), prenotazione.isSelected(), loggedUserUsername);
                    
                resultAreaRistoratore.setText(resp.toString().startsWith("OK") ? "Ristorante aggiunto con successo!" : "Errore: " + resp);
            } catch (NumberFormatException ex) {
                resultAreaRistoratore.setText("Coordinate non valide! Usa numeri con il punto decimale (es. 12.345)");
            } catch (Exception ex) {
                resultAreaRistoratore.setText("Errore: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Mostra il riepilogo delle recensioni per un ristorante selezionato.
     * Visualizza la media delle stelle e il numero totale di recensioni.
     */
    private void ristoratoreRiepilogo() {
        Object response = sendCommand("getMieiRistoranti", loggedUserUsername);
        String respStr = response.toString();
        
        if (respStr.startsWith("OK")) {
            String[] parts = respStr.split("\\|");
            int count = Integer.parseInt(parts[1]);
            if (count == 0) {
                resultAreaRistoratore.setText("Non hai ancora ristoranti.");
                return;
            }
            
            String[] options = new String[count];
            for (int i = 0; i < count; i++) options[i] = parts[2 + i];
            
            String ristorante = (String) JOptionPane.showInputDialog(this, "Seleziona ristorante:", "Riepilogo Recensioni",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (ristorante != null) {
                Object mediaResp = sendCommand("mediaRistorante", ristorante);
                String mediaStr = mediaResp.toString();
                if (mediaStr.startsWith("OK")) {
                    String[] mediaParts = mediaStr.split("\\|");
                    resultAreaRistoratore.setText("RIEPILOGO per " + ristorante + ":\n\n" +
                        "Numero recensioni: " + (mediaParts.length > 2 ? mediaParts[2] : "0") + "\n" +
                        "Media stelle: " + (mediaParts.length > 1 ? mediaParts[1] : "0.0"));
                } else {
                    resultAreaRistoratore.setText("Errore: " + mediaStr);
                }
            }
        }
    }
    
    /**
     * Visualizza tutte le recensioni di un ristorante selezionato.
     * Mostra per ogni recensione: cliente, voto, data, commento
     * e l'eventuale risposta del ristoratore.
     */
    private void ristoratoreVisualizzaRecensioni() {
        Object response = sendCommand("getMieiRistoranti", loggedUserUsername);
        String respStr = response.toString();
        
        if (respStr.startsWith("OK")) {
            String[] parts = respStr.split("\\|");
            int count = Integer.parseInt(parts[1]);
            if (count == 0) {
                resultAreaRistoratore.setText("Non hai ancora ristoranti.");
                return;
            }
            
            String[] options = new String[count];
            for (int i = 0; i < count; i++) options[i] = parts[2 + i];
            
            String ristorante = (String) JOptionPane.showInputDialog(this, "Seleziona ristorante:", "Visualizza Recensioni",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (ristorante != null) {
                Object recResp = sendCommand("recensioniRistorante", ristorante);
                String recStr = recResp.toString();
                if (recStr.startsWith("OK")) {
                    String[] recParts = recStr.split("\\|");
                    int recCount = Integer.parseInt(recParts[1]);
                    if (recCount == 0) {
                        resultAreaRistoratore.setText("Nessuna recensione per " + ristorante);
                    } else {
                        StringBuilder sb = new StringBuilder("RECENSIONI per " + ristorante + ":\n\n");
                        for (int i = 0; i < recCount; i++) {
                            int base = 2 + i * 6;
                            String clienteNome = (recParts.length > base)   ? recParts[base]   : "Sconosciuto";
                            String commento    = (recParts.length > base+1) ? recParts[base+1] : "";
                            String stelle      = (recParts.length > base+2) ? recParts[base+2] : "0";
                            String data        = (recParts.length > base+3) ? recParts[base+3] : "";
                            String risposta    = (recParts.length > base+4) ? recParts[base+4] : "";

                            sb.append("Cliente: ").append(clienteNome).append("\n");
                            sb.append("Voto: ").append(stelle).append(" stelle\n");
                            sb.append("Data: ").append(data).append("\n");
                            if (!commento.isEmpty()) sb.append("Commento: \"").append(commento).append("\"\n");
                            if (!risposta.isEmpty()) sb.append("Tua risposta: \"").append(risposta).append("\"\n");
                            if (i < recCount-1) sb.append("\n---\n");
                        }
                        resultAreaRistoratore.setText(sb.toString());
                    }
                } else {
                    resultAreaRistoratore.setText("Errore: " + recStr);
                }
            }
        }
    }
    
    /**
     * Permette al ristoratore di rispondere a una recensione.
     * Mostra solo le recensioni che non hanno ancora ricevuto risposta.
     * Dopo la selezione, permette di inserire il testo della risposta.
     */
    private void ristoratoreRispondi() {
        Object response = sendCommand("getMieiRistoranti", loggedUserUsername);
        String respStr = response.toString();
        
        if (respStr.startsWith("OK")) {
            String[] parts = respStr.split("\\|");
            int count = Integer.parseInt(parts[1]);
            if (count == 0) {
                resultAreaRistoratore.setText("Non hai ancora ristoranti.");
                return;
            }
            
            String[] options = new String[count];
            for (int i = 0; i < count; i++) options[i] = parts[2 + i];
            
            String ristorante = (String) JOptionPane.showInputDialog(this, "Seleziona ristorante:", "Rispondi a Recensione",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (ristorante == null) return;
            
            Object recResp = sendCommand("recensioniRistorante", ristorante);
            String recStr = recResp.toString();
            
            if (recStr.startsWith("OK")) {
                String[] recParts = recStr.split("\\|");
                int recCount = Integer.parseInt(recParts[1]);
                if (recCount == 0) {
                    resultAreaRistoratore.setText("Nessuna recensione per " + ristorante);
                    return;
                }
                
                ArrayList<String> senzaRisposta = new ArrayList<>();
                ArrayList<String> usernameClienti = new ArrayList<>();
                for (int i = 0; i < recCount; i++) {
                    int base = 2 + i * 6;
                    String clienteNome = (recParts.length > base)   ? recParts[base]   : "Sconosciuto";
                    String commento    = (recParts.length > base+1) ? recParts[base+1] : "";
                    String stelle      = (recParts.length > base+2) ? recParts[base+2] : "0";
                    String risposta    = (recParts.length > base+4 && recParts[base+4] != null && !recParts[base+4].isEmpty()) ? recParts[base+4] : "";
                    String userCliente  = (recParts.length > base+5) ? recParts[base+5] : "";

                    if (risposta.isEmpty()) {
                        senzaRisposta.add(clienteNome + " - Voto: " + stelle + " stelle\n   Commento: " + commento);
                        usernameClienti.add(userCliente);
                    }
                }
                
                if (senzaRisposta.isEmpty()) {
                    resultAreaRistoratore.setText("Tutte le recensioni hanno gia una risposta.");
                    return;
                }
                
                String scelta = (String) JOptionPane.showInputDialog(this, "Seleziona recensione a cui rispondere:", "Rispondi",
                    JOptionPane.QUESTION_MESSAGE, null, senzaRisposta.toArray(), senzaRisposta.get(0));
                if (scelta != null) {
                    int idx = senzaRisposta.indexOf(scelta);
                    String risposta = JOptionPane.showInputDialog(this, "Inserisci la tua risposta:");
                    if (risposta != null && !risposta.isEmpty()) {
                        Object rispResp = sendCommand("rispondiARecensione", ristorante, usernameClienti.get(idx), risposta);
                        resultAreaRistoratore.setText(rispResp.toString().startsWith("OK") ? "Risposta inviata con successo!" : "Errore: " + rispResp);
                    }
                }
            } else {
                resultAreaRistoratore.setText("Errore nel caricamento recensioni: " + recStr);
            }
        }
    }
    
    // ==================== LOGOUT ====================
    
    /**
     * Esegue il logout dell'utente, resettando tutte le variabili
     * di sessione (username, nome, ruolo, domicilio) e tornando
     * alla schermata di login con le aree di testo pulite.
     */
    private void logout() {
        loggedUserUsername = null;
        loggedUserNome = null;
        loggedUserRuolo = null;
        loggedUserDomicilio = null;
        cardLayout.show(mainPanel, "login");
        resultAreaCliente.setText("");
        resultAreaRistoratore.setText("");
        System.out.println("Logout eseguito");
    }
    
    // ==================== MAIN ====================
    
    /**
     * Punto di ingresso dell'applicazione client.
     * Configura il look and feel FlatLaf per un aspetto moderno
     * e avvia la finestra principale dell'applicazione.
     *
     * @param args argomenti della riga di comando (non utilizzati)
     */
    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("Button.hoverBackground", new Color(240, 240, 240));
        } catch (Exception e) {
            System.err.println("Errore caricamento FlatLaf: " + e.getMessage());
        }
        
        new ClientTK().setVisible(true);
    }
}