package se.status4u.gui.mainPanel;

import java.awt.*;
import javax.swing.*;
import java.io.Serial;
import se.status4u.controller.Controller;

/**
 * Panel som visar statistisk data för en vald aktivitet.
 * Denna panel innehåller sex underpaneler som visar:
 * - Distans i kilometer
 * - Start- och sluttid
 * - Medelhastighet i km/h
 * - Kadens (steg/min)
 * - Puls (slag/min)
 * Varje underpanel visar ett huvudvärde och eventuella min/max-värden.
 * @author Victor Vilches
 */
public class DataView extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L;

    // Konstanter för typsnitt och färger
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font VALUE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font DETAIL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color VALUE_COLOR = new Color(50, 50, 50);

    // Referens till controller och statistikpaneler
    private final Controller controller;
    private final StatPanel distancePanel;
    private final StatPanel timePanel;
    private final StatPanel endTimePanel;
    private final StatPanel speedPanel;
    private final StatPanel cadencePanel;
    private final StatPanel pulsePanel;

    /**
     * Skapar en ny DataView med sex statistikpaneler.
     * @param controller referens till applikationens controller
     * @throws IllegalArgumentException om controller är null
     */
    public DataView(Controller controller) {
        if (controller == null) {
            throw new IllegalArgumentException("Controller får inte vara null");
        }
        this.controller = controller;

        // Konfigurera huvudpanelen med 2x3 grid
        setLayout(new GridLayout(2, 3, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(BACKGROUND_COLOR);

        // Skapa alla statistikpaneler med respektive enheter
        distancePanel = new StatPanel("Distans", "km");
        timePanel = new StatPanel("Starttid", "");
        endTimePanel = new StatPanel("Sluttid", "");
        speedPanel = new StatPanel("Medelhastighet", "km/h");
        cadencePanel = new StatPanel("Medelvärde kadens", "steg/min");
        pulsePanel = new StatPanel("Medelvärde puls", "slag/min");

        // Lägg till panelerna i layouten
        setupPanels();
    }

    /**
     * Lägger till alla statistikpaneler i huvudpanelen.
     * Ordningen är viktig för layouten.
     */
    private void setupPanels() {
        add(distancePanel);
        add(timePanel);
        add(endTimePanel);
        add(speedPanel);
        add(cadencePanel);
        add(pulsePanel);
    }

    /**
     * Uppdaterar alla statistikpaneler med aktuell data från controllern.
     * Om ingen aktivitet är vald återställs alla värden.
     */
    public void updateView() {
        try {
            if (controller.getCurrentActivity() == null) {
                resetView();
                return;
            }

            // Uppdatera värden för distans och tid
            distancePanel.updateValues(controller.getTotalDistance(), "");
            timePanel.updateValues(controller.getStartTime(), "");
            endTimePanel.updateValues(controller.getEndTime(), "");

            // Uppdatera hastighetsvärden med min/max
            speedPanel.updateValues(
                    controller.getAverageSpeed(),
                    String.format("Min: %s  Max: %s",
                            controller.getMinSpeed(),
                            controller.getMaxSpeed())
            );

            // Uppdatera kadensvärden med min/max
            cadencePanel.updateValues(
                    controller.getAverageCadence(),
                    String.format("Min: %s  Max: %s",
                            controller.getMinCadence(),
                            controller.getMaxCadence())
            );

            // Uppdatera pulsvärden med min/max
            pulsePanel.updateValues(
                    controller.getAveragePulse(),
                    String.format("Min: %s  Max: %s",
                            controller.getMinPulse(),
                            controller.getMaxPulse())
            );

            repaint();
        } catch (Exception e) {
            System.err.println("Fel vid uppdatering av statistik: " + e.getMessage());
            resetView();
        }
    }

    /**
     * Återställer alla statistikpaneler till standardvärden.
     */
    public void resetView() {
        distancePanel.reset();
        timePanel.reset();
        endTimePanel.reset();
        speedPanel.reset();
        cadencePanel.reset();
        pulsePanel.reset();
        repaint();
    }

    /**
     * Intern klass som representerar en panel för ett statistikvärde.
     * Varje panel innehåller en titel, ett huvudvärde och eventuella detaljer (min/max).
     */
    private static class StatPanel extends JPanel {
        private final JLabel titleLabel;    // Panelens titel
        private final JLabel valueLabel;    // Huvudvärdet
        private final JLabel detailLabel;   // Extra information (t.ex. min/max)
        private final String unit;          // Enhet (km, km/h etc)

        /**
         * Skapar en ny statistikpanel.
         * @param title panelens titel
         * @param unit enhet för värdet
         */
        public StatPanel(String title, String unit) {
            this.unit = unit;
            setLayout(new GridBagLayout());
            setBackground(BACKGROUND_COLOR);
            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

            // Skapa och formatera labels
            titleLabel = new JLabel(title);
            titleLabel.setFont(HEADER_FONT);

            valueLabel = new JLabel("0");
            valueLabel.setFont(VALUE_FONT);
            valueLabel.setForeground(VALUE_COLOR);

            detailLabel = new JLabel("");
            detailLabel.setFont(DETAIL_FONT);

            // Lägg till komponenter med mellanrum
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            addComponents(gbc);
        }

        /**
         * Lägger till panelens komponenter i rätt ordning.
         */
        private void addComponents(GridBagConstraints gbc) {
            gbc.gridy = 0;
            add(titleLabel, gbc);
            gbc.gridy = 1;
            add(valueLabel, gbc);
            gbc.gridy = 2;
            add(detailLabel, gbc);
        }

        /**
         * Uppdaterar panelens värden.
         * @param value huvudvärdet
         * @param detail detaljinformation (t.ex. min/max)
         */
        public void updateValues(String value, String detail) {
            valueLabel.setText(value + (unit.isEmpty() ? "" : " " + unit));
            detailLabel.setText(detail);
        }

        /**
         * Återställer panelen till standardvärden.
         */
        public void reset() {
            valueLabel.setText("0" + (unit.isEmpty() ? "" : " " + unit));
            detailLabel.setText("");
        }
    }
}