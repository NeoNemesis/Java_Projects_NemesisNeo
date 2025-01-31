package se.status4u.util;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import se.status4u.controller.Controller;
import se.status4u.gui.MainFrame;

/**
 * Huvudklass som startar Gizmo2020-applikationen.
 * Ansvarar för initialisering av controller och användargränssnitt.
 * @author Victor Vilches
 */
public class App {

    /**
     * Applikationens startpunkt.
     * Initialiserar systemet och startar användargränssnittet.
     *
     * @param args kommandoradsargument (används ej)
     */
    public static void main(String[] args) {
        try {
            // Försök sätta systemets utseende
            setSystemLookAndFeel();

            // Skapa controller
            Controller controller = new Controller();

            // Starta GUI på EDT (Event Dispatch Thread)
            SwingUtilities.invokeLater(() -> {
                try {
                    MainFrame mainFrame = new MainFrame(controller);
                    mainFrame.setVisible(true);
                } catch (Exception e) {
                    visaFelmeddelande("Kunde inte starta applikationen", e);
                }
            });

        } catch (Exception e) {
            visaFelmeddelande("Ett oväntat fel uppstod vid start av applikationen", e);
            System.exit(1);
        }
    }

    /**
     * Försöker sätta systemets standardutseende.
     */
    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            // Om det misslyckas, fortsätt med standardutseendet
            System.err.println("Kunde inte sätta systemets utseende: " + e.getMessage());
        }
    }

    /**
     * Visar ett felmeddelande för användaren.
     *
     * @param meddelande huvudmeddelandet som ska visas
     * @param e exception som innehåller mer detaljer
     */
    private static void visaFelmeddelande(String meddelande, Exception e) {
        String detaljmeddelande = String.format("%s%n%nTekniska detaljer:%n%s",
                meddelande, e.getMessage());

        JOptionPane.showMessageDialog(null,
                detaljmeddelande,
                "Fel",
                JOptionPane.ERROR_MESSAGE);

        // Logga felet
        e.printStackTrace();
    }
}