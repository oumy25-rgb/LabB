/*
 * TheKnife - GeoUtils
 * Autori:
 *   Gharsellaoui Omema - 761146 - VA
 *   Kufura Razak - 760614 - VA
 */

package theknife;

/**
 * La classe <strong>GeoUtils</strong> fornisce metodi di utilità per il
 * calcolo di distanze geografiche tra coordinate, utilizzati dalla
 * piattaforma <em>TheKnife</em> per individuare i ristoranti più vicini
 * a un dato luogo (utente guest o domicilio di un utente registrato).
 *
 * <p>La formula utilizzata è la formula di Haversine, che calcola la
 * distanza tra due punti sulla superficie terrestre considerando la
 * curvatura della Terra.</p>
 *
 * @author Gharsellaoui Omema
 * @author Kufura Razak
 * @version 1.0
 */
public class GeoUtils {

    /** Raggio medio della Terra in chilometri, usato dalla formula di Haversine. */
    private static final double RAGGIO_TERRA_KM = 6371.0;

    /**
     * Costruttore privato: la classe è puramente statica/di utilità
     * e non deve essere istanziata.
     */
    private GeoUtils() {}

    /**
     * Calcola la distanza in chilometri tra due punti geografici,
     * dati in latitudine/longitudine, usando la formula di Haversine.
     *
     * <p>La formula tiene conto della curvatura terrestre ed è
     * sufficientemente precisa per ordinare ristoranti per vicinanza
     * su scala cittadina/regionale.</p>
     *
     * <p><strong>Complessità:</strong> O(1)</p>
     *
     * @param lat1 latitudine del primo punto (in gradi)
     * @param lon1 longitudine del primo punto (in gradi)
     * @param lat2 latitudine del secondo punto (in gradi)
     * @param lon2 longitudine del secondo punto (in gradi)
     * @return distanza in chilometri tra i due punti
     */
    public static double distanzaKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RAGGIO_TERRA_KM * c;
    }
}