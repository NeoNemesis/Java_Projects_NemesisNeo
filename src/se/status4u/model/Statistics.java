package se.status4u.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.OptionalDouble;

/**
 * Statistik-klass som beräknar olika mätvärden från en träningsaktivitet.
 * Hanterar beräkningar för distans, tid, puls, hastighet och kadens.
 * @author Victor Vilches
 */
public class Statistics {
    // Lista med alla mätpunkter från aktiviteten
    private final List<TrackPoint> trackPoints;

    // Cache för första och sista mätpunkten för snabbare åtkomst
    private final TrackPoint firstPoint;
    private final TrackPoint lastPoint;

    /**
     * Skapar ett nytt Statistics-objekt med givna mätpunkter
     * @param points Lista med TrackPoint-objekt som ska analyseras
     * @throws IllegalArgumentException om listan är tom
     */
    public Statistics(List<TrackPoint> points) {
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException("Mätpunktslistan får inte vara tom");
        }

        this.trackPoints = new ArrayList<>(points); // Skapar en defensiv kopia
        this.firstPoint = trackPoints.get(0);
        this.lastPoint = trackPoints.get(trackPoints.size() - 1);
    }

    /**
     * Hämtar alla longitud-värden från aktiviteten
     * @return Array med longitud-värden
     */
    public double[] longitude() {
        return trackPoints.stream()
                .mapToDouble(TrackPoint::getLongitude)
                .toArray();
    }

    /**
     * Hämtar alla latitud-värden från aktiviteten
     * @return Array med latitud-värden
     */
    public double[] latitude() {
        return trackPoints.stream()
                .mapToDouble(TrackPoint::getLatitude)
                .toArray();
    }

    /**
     * Hämtar alla höjd-värden från aktiviteten
     * @return Array med höjdvärden
     */
    public double[] altitude() {
        return trackPoints.stream()
                .mapToDouble(TrackPoint::getAltitude)
                .toArray();
    }

    /**
     * Beräknar aktivitetens totala tid i formatet "HH:mm:ss"
     * @return Formaterad sträng med total tid
     */
    public String elapsedTime() {
        int totalSeconds = lastPoint.getElapsedTime();
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Beräknar total tillryggalagd sträcka i meter
     * @return Total distans i meter
     */
    public double totalDistance() {
        return lastPoint.getDistance() - firstPoint.getDistance();
    }

    /**
     * Hämtar aktivitetens starttid
     * @return Starttid i formatet som finns i TrackPoint
     */
    public String startTime() {
        return firstPoint.getTime();
    }

    /**
     * Hämtar aktivitetens sluttid
     * @return Sluttid i formatet som finns i TrackPoint
     */
    public String endTime() {
        return lastPoint.getTime();
    }

    /**
     * Generisk metod för att beräkna genomsnitt av valfritt mätvärde
     */
    private double calculateAverage(TrackPointValueExtractor extractor) {
        return trackPoints.stream()
                .mapToDouble(extractor::getValue)
                .average()
                .orElse(0.0);
    }

    /**
     * Generisk metod för att hitta minimum av valfritt mätvärde
     */
    private double calculateMin(TrackPointValueExtractor extractor) {
        return trackPoints.stream()
                .mapToDouble(extractor::getValue)
                .min()
                .orElse(0.0);
    }

    /**
     * Generisk metod för att hitta maximum av valfritt mätvärde
     */
    private double calculateMax(TrackPointValueExtractor extractor) {
        return trackPoints.stream()
                .mapToDouble(extractor::getValue)
                .max()
                .orElse(0.0);
    }

    // Funktionellt interface för att extrahera värden från TrackPoint
    @FunctionalInterface
    private interface TrackPointValueExtractor {
        double getValue(TrackPoint point);
    }

    // Puls-relaterade metoder
    public double averagePulse() {
        return calculateAverage(TrackPoint::getHeartRate);
    }

    public double minPulse() {
        return calculateMin(TrackPoint::getHeartRate);
    }

    public double maxPulse() {
        return calculateMax(TrackPoint::getHeartRate);
    }

    // Hastighets-relaterade metoder
    public double averageSpeed() {
        return calculateAverage(TrackPoint::getSpeed);
    }

    public double minSpeed() {
        return calculateMin(TrackPoint::getSpeed);
    }

    public double maxSpeed() {
        return calculateMax(TrackPoint::getSpeed);
    }

    // Kadens-relaterade metoder
    public double averageCadence() {
        return calculateAverage(TrackPoint::getCadence);
    }

    public double minCadence() {
        return calculateMin(TrackPoint::getCadence);
    }

    public double maxCadence() {
        return calculateMax(TrackPoint::getCadence);
    }
}