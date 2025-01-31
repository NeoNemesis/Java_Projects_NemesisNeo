package se.status4u.model;

import java.io.Serializable;

/**
 * TrackPoint-klassen representerar en enskild mätpunkt från en träningsaktivitet.
 * Varje TrackPoint innehåller information som tid, position, puls etc.
 * Klassen implementerar Serializable för att kunna sparas till fil.
 * @author Victor Vilches
 */
public class TrackPoint implements Serializable {

    // Versions-ID för serialisering
    private static final long serialVersionUID = 1L;

    // Tidsinformation
    private final String date;        // Datum för mätpunkten
    private final String time;        // Klockslag för mätpunkten
    private final int elapsedTime;    // Förfluten tid i sekunder

    // Positionsdata
    private final double longitude;   // Longitud (X-koordinat)
    private final double latitude;    // Latitud (Y-koordinat)
    private final double altitude;    // Höjd över havet i meter

    // Träningsdata
    private final double distance;    // Tillryggalagd sträcka i meter
    private final double heartRate;   // Hjärtfrekvens i slag per minut
    private final double speed;       // Hastighet i meter per sekund
    private final double cadence;     // Kadens (steg per minut)

    /**
     * Skapar en ny TrackPoint från en semikolon-separerad sträng.
     *
     * @param attribute En sträng med alla värden separerade med semikolon
     * @throws NumberFormatException om någon konvertering till tal misslyckas
     * @throws ArrayIndexOutOfBoundsException om strängen inte innehåller rätt antal värden
     */
    public TrackPoint(String attribute) {
        // Byt ut kommatecken mot punkter för decimal-tal
        String formattedData = attribute.replace(",", ".");

        // Dela upp strängen vid semikolon
        String[] activityAttributes = formattedData.split(";");

        // Kontrollera att vi har rätt antal värden
        if (activityAttributes.length != 10) {
            throw new IllegalArgumentException(
                    "Felaktigt format på indata. Förväntade 10 värden, fick " +
                            activityAttributes.length
            );
        }

        try {
            // Tilldela värden till instansvariabler
            this.date = activityAttributes[0];
            this.time = activityAttributes[1];
            this.elapsedTime = Integer.parseInt(activityAttributes[2]);
            this.longitude = Double.parseDouble(activityAttributes[3]);
            this.latitude = Double.parseDouble(activityAttributes[4]);
            this.altitude = Double.parseDouble(activityAttributes[5]);
            this.distance = Double.parseDouble(activityAttributes[6]);
            this.heartRate = Double.parseDouble(activityAttributes[7]);
            this.speed = Double.parseDouble(activityAttributes[8]);
            this.cadence = Double.parseDouble(activityAttributes[9]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Fel vid konvertering av värden: " + e.getMessage());
        }
    }

    // Getters för alla fält
    /**
     * @return Datumet för mätpunkten
     */
    public String getDate() {
        return date;
    }

    /**
     * @return Klockslaget för mätpunkten
     */
    public String getTime() {
        return time;
    }

    /**
     * @return Förfluten tid i sekunder
     */
    public int getElapsedTime() {
        return elapsedTime;
    }

    /**
     * @return Longituden (X-koordinat)
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @return Latituden (Y-koordinat)
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return Höjden över havet i meter
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * @return Tillryggalagd sträcka i meter
     */
    public double getDistance() {
        return distance;
    }

    /**
     * @return Hjärtfrekvensen i slag per minut
     */
    public double getHeartRate() {
        return heartRate;
    }

    /**
     * @return Hastigheten i meter per sekund
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * @return Kadensen (steg per minut)
     */
    public double getCadence() {
        return cadence;
    }

    /**
     * Skapar en strängrepresentation av TrackPoint-objektet.
     *
     * @return En sträng med alla värden formaterade
     */
    @Override
    public String toString() {
        return String.format(
                "TrackPoint[tid=%s, distans=%.2f m, puls=%.0f bpm, hastighet=%.2f m/s]",
                time, distance, heartRate, speed
        );
    }
}