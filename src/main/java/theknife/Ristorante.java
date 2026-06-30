/*
 * TheKnife - Ristorante
 * Autori:
 *   Gharsellaoui Omema - 761146 - VA
 *   Kufura Razak - 760614 - VA
 */

package theknife;

import java.util.ArrayList;

/**
 * La classe <strong>Ristorante</strong> rappresenta un ristorante registrato
 * nella piattaforma <em>TheKnife</em>.
 *
 * <p>Contiene informazioni anagrafiche (nome, indirizzo, città, nazione),
 * caratteristiche (tipo di cucina, fascia di prezzo, coordinate geografiche)
 * e servizi disponibili (delivery e prenotazione online).</p>
 *
 * <p>Nel Lab B tutti i dati vengono letti e scritti tramite
 * {@link RistoranteDAO} e {@link RecensioneDAO} su PostgreSQL.</p>
 *
 * @author Gharsellaoui Omema
 * @author Kufura Razak
 * @version 1.0
 */
public class Ristorante {

    // ==================== ATTRIBUTI ====================
    
    /** Nome del ristorante. */
    private String name;
    
    /** Indirizzo del ristorante. */
    private String address;
    
    /** Città del ristorante. */
    private String city;
    
    /** Nazione del ristorante. */
    private String nation;
    
    /** Fascia di prezzo (simboli Michelin o valore numerico). */
    private String price;
    
    /** Tipo di cucina del ristorante. */
    private String cuisine;
    
    /** Longitudine del ristorante. */
    private double longitude;
    
    /** Latitudine del ristorante. */
    private double latitude;
    
    /** Disponibilità del servizio di delivery. */
    private boolean delivery;
    
    /** Disponibilità della prenotazione online. */
    private boolean reservation;
    
    /** Lista delle recensioni del ristorante. */
    private ArrayList<Recensione> recensioni;

    /**
     * Distanza in chilometri dal punto di ricerca.
     * Vale -1 quando il ristorante non proviene da una ricerca per vicinanza.
     */
    private double distanzaKm = -1;

    // ==================== COSTRUTTORE ====================
    
    /**
     * Costruisce un nuovo ristorante con tutti i dati richiesti.
     *
     * @param name        il nome del ristorante
     * @param address     l'indirizzo del ristorante
     * @param city        la città del ristorante
     * @param price       la fascia di prezzo (simboli o valore numerico)
     * @param nation      la nazione del ristorante
     * @param cuisine     il tipo di cucina
     * @param longitude   la longitudine del ristorante
     * @param latitude    la latitudine del ristorante
     * @param delivery    true se il delivery è disponibile
     * @param reservation true se la prenotazione online è disponibile
     * @param recensioni  la lista delle recensioni (può essere null)
     */
    public Ristorante(String name, String address, String city, String price, String nation,
                      String cuisine, double longitude, double latitude,
                      boolean delivery, boolean reservation, ArrayList<Recensione> recensioni) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.price = price;
        this.cuisine = cuisine;
        this.longitude = longitude;
        this.latitude = latitude;
        this.delivery = delivery;
        this.reservation = reservation;
        this.nation = nation;
        this.recensioni = recensioni;
    }

    // ==================== GETTER E SETTER ====================
    
    /**
     * Restituisce il nome del ristorante.
     *
     * @return il nome del ristorante
     */
    public String getName() {
        return name;
    }
    
    /**
     * Imposta il nome del ristorante.
     *
     * @param name il nuovo nome del ristorante
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Restituisce l'indirizzo del ristorante.
     *
     * @return l'indirizzo del ristorante
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * Imposta l'indirizzo del ristorante.
     *
     * @param address il nuovo indirizzo del ristorante
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Restituisce la città del ristorante.
     *
     * @return la città del ristorante
     */
    public String getCity() {
        return city;
    }
    
    /**
     * Imposta la città del ristorante.
     *
     * @param location la nuova città del ristorante
     */
    public void setLocation(String location) {
        this.city = location;
    }

    /**
     * Restituisce la nazione del ristorante.
     *
     * @return la nazione del ristorante
     */
    public String getNation() {
        return nation;
    }
    
    /**
     * Imposta la nazione del ristorante.
     *
     * @param nation la nuova nazione del ristorante
     */
    public void setNation(String nation) {
        this.nation = nation;
    }

    /**
     * Restituisce la fascia di prezzo del ristorante.
     *
     * @return la fascia di prezzo
     */
    public String getPrice() {
        return price;
    }
    
    /**
     * Imposta la fascia di prezzo del ristorante.
     *
     * @param price la nuova fascia di prezzo
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     * Restituisce il tipo di cucina del ristorante.
     *
     * @return il tipo di cucina
     */
    public String getCuisine() {
        return cuisine;
    }
    
    /**
     * Imposta il tipo di cucina del ristorante.
     *
     * @param cuisine il nuovo tipo di cucina
     */
    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    /**
     * Restituisce la longitudine del ristorante.
     *
     * @return la longitudine
     */
    public double getLongitude() {
        return longitude;
    }
    
    /**
     * Imposta la longitudine del ristorante.
     *
     * @param longitude la nuova longitudine
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Restituisce la latitudine del ristorante.
     *
     * @return la latitudine
     */
    public double getLatitude() {
        return latitude;
    }
    
    /**
     * Imposta la latitudine del ristorante.
     *
     * @param latitude la nuova latitudine
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Verifica se il delivery è disponibile.
     *
     * @return true se il delivery è disponibile
     */
    public boolean getDelivery() {
        return delivery;
    }
    
    /**
     * Imposta la disponibilità del delivery.
     *
     * @param delivery true se il delivery è disponibile
     */
    public void setDelivery(boolean delivery) {
        this.delivery = delivery;
    }

    /**
     * Verifica se la prenotazione online è disponibile.
     *
     * @return true se la prenotazione online è disponibile
     */
    public boolean getReservation() {
        return reservation;
    }
    
    /**
     * Imposta la disponibilità della prenotazione online.
     *
     * @param reservation true se la prenotazione online è disponibile
     */
    public void setReservation(boolean reservation) {
        this.reservation = reservation;
    }

    /**
     * Restituisce la lista delle recensioni del ristorante.
     *
     * @return la lista delle recensioni
     */
    public ArrayList<Recensione> getRecensioni() {
        return recensioni;
    }
    
    /**
     * Imposta la lista delle recensioni del ristorante.
     *
     * @param recensioni la nuova lista delle recensioni
     */
    public void setRecensioni(ArrayList<Recensione> recensioni) {
        this.recensioni = recensioni;
    }

    /**
     * Restituisce la distanza in km dal punto di ricerca.
     *
     * @return la distanza in km, oppure -1 se non calcolata
     */
    public double getDistanzaKm() {
        return distanzaKm;
    }

    /**
     * Imposta la distanza in km dal punto di ricerca.
     * Utilizzato durante la ricerca per vicinanza.
     *
     * @param distanzaKm la distanza in chilometri
     */
    public void setDistanzaKm(double distanzaKm) {
        this.distanzaKm = distanzaKm;
    }
}