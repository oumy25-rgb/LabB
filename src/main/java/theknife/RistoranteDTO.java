/*
 * TheKnife - RistoranteDTO
 * Autori:
 *   Gharsellaoui Omema - 761146 - VA
 *   Kufura Razak - 760614 - VA
 */

package theknife;

import java.io.Serializable;

/**
 * La classe <strong>RistoranteDTO</strong> (Data Transfer Object) rappresenta
 * un ristorante nello scambio di dati tra il server e il client.
 *
 * <p>A differenza del protocollo "a stringhe separate da '|'" usato dai comandi
 * più vecchi, i nuovi comandi (ricerca multi-criterio e ricerca per vicinanza)
 * scambiano direttamente oggetti {@code RistoranteDTO} serializzati, che
 * includono anche la media delle stelle, il numero di recensioni e la
 * distanza in km dal punto di ricerca.</p>
 *
 * <p>Essendo serializzabile, può essere trasmesso tramite
 * {@code ObjectOutputStream} e {@code ObjectInputStream}.</p>
 *
 * @author Gharsellaoui Omema
 * @author Kufura Razak
 * @version 1.0
 */
public class RistoranteDTO implements Serializable {

    // ==================== ATTRIBUTI ====================
    
    /** Versione della serializzazione per la compatibilità. */
    private static final long serialVersionUID = 1L;

    /** Nome del ristorante. */
    private String nome;
    
    /** Indirizzo del ristorante. */
    private String indirizzo;
    
    /** Città del ristorante. */
    private String citta;
    
    /** Nazione del ristorante. */
    private String nazione;
    
    /** Fascia di prezzo del ristorante. */
    private String prezzo;
    
    /** Tipo di cucina del ristorante. */
    private String cucina;
    
    /** Longitudine del ristorante. */
    private double longitudine;
    
    /** Latitudine del ristorante. */
    private double latitudine;
    
    /** Disponibilità del servizio di delivery. */
    private boolean delivery;
    
    /** Disponibilità della prenotazione online. */
    private boolean prenotazione;

    /** Media delle stelle delle recensioni (0 se nessuna recensione). */
    private double mediaStelle;

    /** Numero di recensioni presenti per il ristorante. */
    private int numeroRecensioni;

    /** Distanza in km dal punto di ricerca, oppure -1 se non applicabile. */
    private double distanzaKm;

    // ==================== COSTRUTTORE ====================
    
    /**
     * Costruisce un DTO a partire da un oggetto {@link Ristorante} e dalle
     * statistiche di valutazione associate.
     *
     * @param r                il ristorante di origine
     * @param mediaStelle      la media delle stelle delle recensioni
     * @param numeroRecensioni il numero di recensioni del ristorante
     */
    public RistoranteDTO(Ristorante r, double mediaStelle, int numeroRecensioni) {
        this.nome = r.getName();
        this.indirizzo = r.getAddress();
        this.citta = r.getCity();
        this.nazione = r.getNation();
        this.prezzo = r.getPrice();
        this.cucina = r.getCuisine();
        this.longitudine = r.getLongitude();
        this.latitudine = r.getLatitude();
        this.delivery = r.getDelivery();
        this.prenotazione = r.getReservation();
        this.mediaStelle = mediaStelle;
        this.numeroRecensioni = numeroRecensioni;
        this.distanzaKm = r.getDistanzaKm();
    }

    // ==================== GETTER ====================
    
    /**
     * Restituisce il nome del ristorante.
     *
     * @return il nome del ristorante
     */
    public String getNome() {
        return nome;
    }
    
    /**
     * Restituisce l'indirizzo del ristorante.
     *
     * @return l'indirizzo del ristorante
     */
    public String getIndirizzo() {
        return indirizzo;
    }
    
    /**
     * Restituisce la città del ristorante.
     *
     * @return la città del ristorante
     */
    public String getCitta() {
        return citta;
    }
    
    /**
     * Restituisce la nazione del ristorante.
     *
     * @return la nazione del ristorante
     */
    public String getNazione() {
        return nazione;
    }
    
    /**
     * Restituisce la fascia di prezzo del ristorante.
     *
     * @return la fascia di prezzo
     */
    public String getPrezzo() {
        return prezzo;
    }
    
    /**
     * Restituisce il tipo di cucina del ristorante.
     *
     * @return il tipo di cucina
     */
    public String getCucina() {
        return cucina;
    }
    
    /**
     * Restituisce la longitudine del ristorante.
     *
     * @return la longitudine
     */
    public double getLongitudine() {
        return longitudine;
    }
    
    /**
     * Restituisce la latitudine del ristorante.
     *
     * @return la latitudine
     */
    public double getLatitudine() {
        return latitudine;
    }
    
    /**
     * Verifica se il delivery è disponibile.
     *
     * @return true se il delivery è disponibile
     */
    public boolean isDelivery() {
        return delivery;
    }
    
    /**
     * Verifica se la prenotazione online è disponibile.
     *
     * @return true se la prenotazione online è disponibile
     */
    public boolean isPrenotazione() {
        return prenotazione;
    }
    
    /**
     * Restituisce la media delle stelle delle recensioni.
     *
     * @return la media delle stelle
     */
    public double getMediaStelle() {
        return mediaStelle;
    }
    
    /**
     * Restituisce il numero di recensioni del ristorante.
     *
     * @return il numero di recensioni
     */
    public int getNumeroRecensioni() {
        return numeroRecensioni;
    }

    /**
     * Restituisce la distanza in km dal punto di ricerca.
     *
     * @return la distanza in km, oppure -1 se non applicabile
     */
    public double getDistanzaKm() {
        return distanzaKm;
    }

    // ==================== METODI ====================
    
    /**
     * Restituisce una rappresentazione sintetica del ristorante, utile
     * per la visualizzazione in liste e combo box nella GUI.
     * Include nome, città, nazione, cucina, prezzo, media stelle,
     * numero di recensioni e distanza (se disponibile).
     *
     * @return la stringa riassuntiva del ristorante
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(nome)
            .append("  (").append(citta).append(", ").append(nazione).append(")")
            .append(" - ").append(cucina)
            .append(" - ").append(prezzo).append("€");
        if (numeroRecensioni > 0) {
            sb.append(String.format(" - %.1f★ (%d recensioni)", mediaStelle, numeroRecensioni));
        } else {
            sb.append(" - nessuna recensione");
        }
        if (distanzaKm >= 0) {
            sb.append(String.format(" - %.1f km", distanzaKm));
        }
        return sb.toString();
    }
}