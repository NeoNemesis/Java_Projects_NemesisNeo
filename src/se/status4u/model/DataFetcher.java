package se.status4u.model;

/**
 * Funktionellt interface för att hämta numeriska värden från TrackPoint-objekt.
 * Används främst av plottningskomponenter för att extrahera specifika mätvärden
 * som ska visualiseras, till exempel puls, hastighet eller höjd.
 * @author Victor Vilches
 */
@FunctionalInterface
public interface DataFetcher {

    /**
     * Hämtar ett specifikt numeriskt värde från en mätpunkt.
     *
     * @param trackPoint mätpunkten som värdet ska hämtas från
     * @return det numeriska värdet som extraherats från mätpunkten
     * @throws NullPointerException om trackPoint är null
     *
     * Exempel på användning:
     * DataFetcher pulsFetcher = tp -> tp.getHeartRate();
     * DataFetcher hastighetsFetcher = tp -> tp.getSpeed();
     * DataFetcher höjdFetcher = tp -> tp.getAltitude();
     */
    double fetch(TrackPoint trackPoint);
}