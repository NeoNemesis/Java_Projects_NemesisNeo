package se.status4u.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.status4u.data.CSVReader;
import se.status4u.model.Activity;
import se.status4u.model.Statistics;
import se.status4u.model.TrackPoint;
import se.status4u.model.User;

/**
 * Kontrollerklass som hanterar kommunikationen mellan användargränssnittet och modellen.
 * Denna klass är ansvarig för:
 * - Användarhantering (inloggning/utloggning)
 * - Aktivitetshantering (import/skapande)
 * - Statistikberäkningar
 * - Datalagring via CSVReader
 * @author Victor Vilches
 */
public class Controller {
    // Huvudkomponenter för datahantering
    private final CSVReader csvReader;
    private Statistics stats;
    private User currentUser;
    private Activity currentActivity;

    /**
     * Skapar en ny Controller med tillhörande CSVReader
     */
    public Controller() {
        this.csvReader = new CSVReader();
    }

    // Användarhantering och Persistens

    /**
     * Försöker ladda en användare från sparad data
     * @param userName användarens namn
     * @return User-objekt om det finns sparat, annars null
     */
    public User loadUser(String userName) {
        try {
            return csvReader.loadUserData(userName);
        } catch (Exception e) {
            System.err.println("Fel vid laddning av användardata: " + e.getMessage());
            return null;
        }
    }

    /**
     * Sparar den aktuella användarens data till fil
     */
    public void save() {
        if (currentUser != null) {
            try {
                csvReader.saveUserData(currentUser);
            } catch (IOException e) {
                System.err.println("Fel vid sparande av användardata: " + e.getMessage());
            }
        }
    }

    /**
     * Loggar ut nuvarande användare och sparar data
     */
    public void logout() {
        if (currentUser != null) {
            save();
        }
        this.currentUser = null;
        this.currentActivity = null;
        this.stats = null;
    }

    /**
     * Sätter aktuell användare från ett befintligt User-objekt
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Skapar och sätter en ny användare med angivna värden
     */
    public void setCurrentUser(String name, int age, double weight, double maxHeartRate) {
        try {
            this.currentUser = new User(name, age, weight, maxHeartRate);
        } catch (Exception e) {
            throw new RuntimeException("Kunde inte skapa användare: " + e.getMessage(), e);
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    //Aktivitetshantering
    /**
     * Tar bort en aktivitet från den aktuella användaren
     * @param activityName namnet på aktiviteten som ska tas bort
     * @return true om aktiviteten togs bort, false annars
     */
    public boolean removeActivity(String activityName) {
        if (currentUser != null) {
            // Om aktiviteten som tas bort är den aktiva aktiviteten
            if (currentActivity != null && currentActivity.getName().equals(activityName)) {
                currentActivity = null;
                stats = null;
            }

            // Försök ta bort aktiviteten från användaren
            return currentUser.removeActivity(activityName);
        }
        return false;
    }

    /**
     * Importerar aktivitetsdata från CSV-fil och skapar en ny aktivitet
     */
    public void importActivity(String filePath, String activityName) throws FileNotFoundException {
        List<String> activityData = csvReader.readActivityData(filePath);
        createTrackPoints(activityData, activityName);
    }

    /**
     * Skapar TrackPoints från CSV-data
     */
    private void createTrackPoints(List<String> lines, String name) {
        List<TrackPoint> trackPoints = new ArrayList<>();

        try {
            for (String line : lines) {
                try {
                    trackPoints.add(new TrackPoint(line));
                } catch (Exception e) {
                    System.err.println("Fel vid parsning av rad: " + e.getMessage());
                }
            }
            createActivity(trackPoints, name);
        } catch (Exception e) {
            System.err.println("Fel vid skapande av TrackPoints: " + e.getMessage());
        }
    }

    /**
     * Skapar en ny aktivitet och kopplar den till nuvarande användare
     */
    private void createActivity(List<TrackPoint> trackPoints, String name) {
        if (currentUser != null && !trackPoints.isEmpty()) {
            try {
                currentActivity = new Activity(trackPoints, name, currentUser);
                calculateNewStatistics();
            } catch (Exception e) {
                System.err.println("Fel vid skapande av aktivitet: " + e.getMessage());
            }
        }
    }

    /**
     * Sätter en aktivitet som aktiv baserat på dess namn
     */
    public void setCurrentActivity(String activityName) {
        if (currentUser != null) {
            currentActivity = currentUser.getMap().values().stream()
                    .filter(activity -> activity.getName().equals(activityName))
                    .findFirst()
                    .orElse(null);

            if (currentActivity != null) {
                calculateNewStatistics();
            }
        }
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public Collection<Activity> getActivities() {
        return currentUser != null ? currentUser.getActivities() : new ArrayList<>();
    }

    public List<TrackPoint> getTrackPoints() {
        return currentActivity != null ? currentActivity.getTrackPoints() : new ArrayList<>();
    }

    //Statistikhantering

    /**
     * Beräknar statistik för den aktuella aktiviteten
     */
    private void calculateNewStatistics() {
        if (currentActivity != null) {
            try {
                stats = new Statistics(currentActivity.getTrackPoints());
            } catch (Exception e) {
                System.err.println("Fel vid beräkning av statistik: " + e.getMessage());
                stats = null;
            }
        }
    }

    /**
     * Hjälpmetod för att formatera statistikvärden
     */
    private String getFormattedStat(StatFunction statFunction, String defaultValue) {
        if (currentActivity == null || stats == null) {
            return defaultValue;
        }
        try {
            double value = statFunction.apply(stats);
            return String.format("%,.2f", value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @FunctionalInterface
    private interface StatFunction {
        double apply(Statistics stats);
    }

    //Statistikmetoder

    public String getTotalDistance() {
        return getFormattedStat(Statistics::totalDistance, "0.0");
    }

    public String getStartTime() {
        return currentActivity != null && stats != null ? stats.startTime() : "00:00:00";
    }

    public String getEndTime() {
        return currentActivity != null && stats != null ? stats.endTime() : "00:00:00";
    }

    public String getAveragePulse() {
        return getFormattedStat(Statistics::averagePulse, "0.0");
    }

    public String getMinPulse() {
        return getFormattedStat(Statistics::minPulse, "0.0");
    }

    public String getMaxPulse() {
        return getFormattedStat(Statistics::maxPulse, "0.0");
    }

    public String getAverageSpeed() {
        return getFormattedStat(Statistics::averageSpeed, "0.0");
    }

    public String getMinSpeed() {
        return getFormattedStat(Statistics::minSpeed, "0.0");
    }

    public String getMaxSpeed() {
        return getFormattedStat(Statistics::maxSpeed, "0.0");
    }

    public String getAverageCadence() {
        return getFormattedStat(Statistics::averageCadence, "0.0");
    }

    public String getMinCadence() {
        return getFormattedStat(Statistics::minCadence, "0.0");
    }

    public String getMaxCadence() {
        return getFormattedStat(Statistics::maxCadence, "0.0");
    }
}