package se.status4u.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representerar en träningsaktivitet i systemet.
 * Innehåller information om aktiviteten samt alla mätpunkter.
 * Implementerar Serializable för att kunna sparas till fil.
 * @author Victor Vilches
 */
public class Activity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Aktivitetsinformation
    private final String name;
    private final String date;
    private final User user;
    private final List<TrackPoint> trackPoints;

    /**
     * Skapar en ny aktivitet med angivna mätpunkter och information.
     * Aktiviteten läggs automatiskt till i användarens aktivitetslista.
     *
     * @param trackPoints lista med aktivitetens mätpunkter
     * @param name aktivitetens namn
     * @param user användaren som aktiviteten tillhör
     * @throws IllegalArgumentException om någon parameter är ogiltig
     */
    public Activity(List<TrackPoint> trackPoints, String name, User user) {
        // Validera indata
        validateTrackPoints(trackPoints);
        validateName(name);
        Objects.requireNonNull(user, "Användare får inte vara null");

        // Kopiera mätpunkter till en ny lista för att undvika extern modifiering
        this.trackPoints = new ArrayList<>(trackPoints);
        this.name = name.trim();
        this.date = extractDate(trackPoints);
        this.user = user;

        // Lägg till aktiviteten i användarens lista
        user.addActivity(this);
    }

    /**
     * Validerar listan med mätpunkter.
     * @param points listan som ska valideras
     * @throws IllegalArgumentException om listan är ogiltig
     */
    private void validateTrackPoints(List<TrackPoint> points) {
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException("Mätpunktslistan får inte vara tom");
        }
        if (points.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Mätpunktslistan får inte innehålla null-värden");
        }
    }

    /**
     * Validerar aktivitetsnamnet.
     * @param name namnet som ska valideras
     * @throws IllegalArgumentException om namnet är ogiltigt
     */
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Aktivitetsnamn får inte vara tomt");
        }
    }

    /**
     * Extraherar datum från första mätpunkten.
     * @param points lista med mätpunkter
     * @return datum från första mätpunkten
     */
    private String extractDate(List<TrackPoint> points) {
        return points.get(0).getDate();
    }

    /**
     * Hämtar alla mätpunkter för aktiviteten.
     * @return oföränderlig lista med mätpunkter
     */
    public List<TrackPoint> getTrackPoints() {
        return Collections.unmodifiableList(trackPoints);
    }

    /**
     * @deprecated Använd getTrackPoints() istället
     */
    @Deprecated
    public List<TrackPoint> getActivity() {
        return getTrackPoints();
    }

    /**
     * Hämtar användarens namn.
     * @return användarnamn
     */
    public String getUserName() {
        return user.getName();
    }

    /**
     * Hämtar användaren som aktiviteten tillhör.
     * @return User-objekt
     */
    public User getUser() {
        return user;
    }

    /**
     * Hämtar aktivitetens namn.
     * @return aktivitetsnamn
     */
    public String getName() {
        return name;
    }

    /**
     * Hämtar aktivitetens datum.
     * @return datum i format från TrackPoint
     */
    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return String.format("Aktivitet: %s (%s) - %d mätpunkter",
                name, date, trackPoints.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return Objects.equals(name, activity.name) &&
                Objects.equals(date, activity.date) &&
                Objects.equals(user.getName(), activity.user.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date, user.getName());
    }
}