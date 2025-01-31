package se.status4u.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import se.status4u.model.User;

/**
 * Klass för att hantera läsning och skrivning av CSV-filer och användardata.
 * Ansvarar för all filhantering i applikationen.
 * @author Victor Vilches
 */
public class CSVReader {
    private static final String USER_DATA_DIR = "userData";
    private static final String FILE_EXTENSION = ".dat";

    /**
     * Sparar användardata till fil.
     * @param user användaren som ska sparas
     * @throws IOException om det uppstår fel vid filskrivning
     */
    public void saveUserData(User user) throws IOException {
        Path userDir = Paths.get(USER_DATA_DIR);
        if (!userDir.toFile().exists()) {
            userDir.toFile().mkdirs();
        }

        String fileName = getUserFileName(user.getName());
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(fileName))) {
            out.writeObject(user);
        }
    }

    /**
     * Läser användardata från fil.
     * @param userName användarens namn
     * @return User-objekt om filen finns, null annars
     */
    public User loadUserData(String userName) {
        String fileName = getUserFileName(userName);
        File userFile = new File(fileName);

        if (!userFile.exists()) {
            return null;
        }

        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(userFile))) {
            return (User) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Fel vid läsning av användardata: " + e.getMessage());
            return null;
        }
    }

    /**
     * Läser aktivitetsdata från CSV-fil.
     * @param filePath sökväg till CSV-filen
     * @return Lista med rader från CSV-filen
     * @throws FileNotFoundException om filen inte hittas
     */
    public List<String> readActivityData(String filePath) throws FileNotFoundException {
        List<String> activityData = new ArrayList<>();
        System.out.println("Läser fil: " + filePath);

        try (Scanner scanner = new Scanner(new File(filePath))) {
            // Skippa första raden (rubriker)
            if (scanner.hasNextLine()) {
                String headers = scanner.nextLine();
                System.out.println("Headers: " + headers);
            }

            // Läs resterande rader
            int lineCount = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    activityData.add(line);
                    lineCount++;
                    if (lineCount <= 3) { // Visa bara de första 3 raderna
                        System.out.println("Läst rad " + lineCount + ": " + line);
                    }
                }
            }
            System.out.println("Totalt antal rader lästa: " + lineCount);
        }

        return activityData;
    }

    /**
     * Genererar filnamn för användardata.
     * @param userName användarens namn
     * @return komplett sökväg till användarens datafil
     */
    private String getUserFileName(String userName) {
        return Paths.get(USER_DATA_DIR, userName + FILE_EXTENSION)
                .toString();
    }


}