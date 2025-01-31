package se.status4u.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Representerar en användare i systemet.
 * Innehåller användarinformation och användarens träningsaktiviteter.
 * Implementerar Serializable för att kunna sparas till fil.
 * @author Victor Vilches
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // Konstanter för validering
    private static final int MIN_AGE = 5;
    private static final int MAX_AGE = 120;
    private static final double MIN_WEIGHT = 20.0;
    private static final double MAX_WEIGHT = 300.0;
    private static final double MIN_MAX_PULSE = 100.0;
    private static final double MAX_MAX_PULSE = 250.0;

    // Användarinformation
    private final String userName;
    private final int age;
    private final double weight;
    private final double maxPulse;

    // Användarens aktiviteter
    private final Map<String, Activity> activities;

    /**
     * Skapar en ny användare med specificerad information.
     *
     * @param userName användarens namn
     * @param age användarens ålder (5-120 år)
     * @param weight användarens vikt i kg (20-300 kg)
     * @param maxPulse användarens maximala puls (100-250 slag/min)
     * @throws IllegalArgumentException om någon parameter är ogiltig
     */
    public User(String userName, int age, double weight, double maxPulse) {
        // Validera användarnamn
        this.userName = validateUserName(userName);

        // Validera ålder
        if (age < MIN_AGE || age > MAX_AGE) {
            throw new IllegalArgumentException(
                    String.format("Ålder måste vara mellan %d och %d år", MIN_AGE, MAX_AGE)
            );
        }
        this.age = age;

        // Validera vikt
        if (weight < MIN_WEIGHT || weight > MAX_WEIGHT) {
            throw new IllegalArgumentException(
                    String.format("Vikt måste vara mellan %.1f och %.1f kg", MIN_WEIGHT, MAX_WEIGHT)
            );
        }
        this.weight = weight;

        // Validera maxpuls
        if (maxPulse < MIN_MAX_PULSE || maxPulse > MAX_MAX_PULSE) {
            throw new IllegalArgumentException(
                    String.format("Maxpuls måste vara mellan %.1f och %.1f slag/min",
                            MIN_MAX_PULSE, MAX_MAX_PULSE)
            );
        }
        this.maxPulse = maxPulse;

        // Initialisera aktivitetslista
        this.activities = new HashMap<>();
    }

    /**
     * Validerar användarnamn
     * @param userName namnet som ska valideras
     * @return validerat namn
     * @throws IllegalArgumentException om namnet är ogiltigt
     */
    private String validateUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("Användarnamn får inte vara tomt");
        }
        return userName.trim();
    }

    /**
     * Lägger till en ny aktivitet för användaren.
     * Aktiviteten lagras med en nyckel som består av aktivitetens namn och datum.
     *
     * @param activity aktiviteten som ska läggas till
     * @throws IllegalArgumentException om aktiviteten är null
     */
    public void addActivity(Activity activity) {
        Objects.requireNonNull(activity, "Aktivitet får inte vara null");
        String activityKey = createActivityKey(activity);
        activities.put(activityKey, activity);
    }

    /**
     * Tar bort en aktivitet baserat på dess namn.
     * Söker efter aktiviteten i aktivitetslistan och tar bort den om den hittas.
     *
     * @param activityName namnet på aktiviteten som ska tas bort
     * @return true om aktiviteten togs bort, false om den inte hittades
     */
    public boolean removeActivity(String activityName) {
        // Hitta aktivitetsnyckeln som matchar namnet
        String keyToRemove = activities.keySet().stream()
                .filter(key -> key.startsWith(activityName))
                .findFirst()
                .orElse(null);

        // Ta bort aktiviteten om den hittades
        if (keyToRemove != null) {
            activities.remove(keyToRemove);
            return true;
        }
        return false;
    }

    /**
     * Skapar en nyckel för aktivitetskartan baserat på aktivitetens namn och datum.
     *
     * @param activity aktiviteten att skapa nyckel för
     * @return unik nyckel för aktiviteten
     */
    private String createActivityKey(Activity activity) {
        return activity.getName() + " " + activity.getDate();
    }

    /**
     * Hämtar alla användarens aktiviteter.
     * @return oföränderlig samling av aktiviteter
     */
    public Collection<Activity> getActivities() {
        return Collections.unmodifiableCollection(activities.values());
    }

    /**
     * Hämtar aktivitetskartan.
     * @return oföränderlig karta över aktiviteter
     */
    public Map<String, Activity> getMap() {
        return Collections.unmodifiableMap(activities);
    }

    // Getters för användarinformation
    public String getName() {
        return userName;
    }

    public int getAge() {
        return age;
    }

    public double getWeight() {
        return weight;
    }

    public double getMaxPulse() {
        return maxPulse;
    }

    @Override
    public String toString() {
        return String.format("Användare: %s (Ålder: %d, Vikt: %.1f kg, MaxPuls: %.1f)",
                userName, age, weight, maxPulse);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userName, user.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName);
    }
}