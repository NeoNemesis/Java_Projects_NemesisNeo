package se.status4u.gui.mainPanel;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.Serial;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import se.status4u.controller.Controller;
import se.status4u.model.TrackPoint;
import se.status4u.gui.plot.PlotView;

/**
 * Panel som visar grafer för olika mätvärden från en aktivitet.
 * Innehåller tre separata grafer för att visualisera:
 * - Puls (slag/min)
 * - Hastighet (km/h)
 * - Höjd (m)
 * Varje graf använder PlotView för att rita data från TrackPoints.
 * @author Victor Vilches
 */
public class GraphView extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L;

    // Konstanter för utseende och layout
    private static final int GRAPH_SPACING = 5;
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);

    // Konstanter för grafernas titlar
    private static final String PULSE_TITLE = "Pulsdata";
    private static final String SPEED_TITLE = "Hastighetsdata";
    private static final String ALTITUDE_TITLE = "Höjddata";

    // Konstanter för mätenheter
    private static final String PULSE_UNIT = "Puls (slag/min)";
    private static final String SPEED_UNIT = "Hastighet (km/h)";
    private static final String ALTITUDE_UNIT = "Höjd (m)";

    // Referenser till de olika plottarna
    private final PlotView pulseGraph;
    private final PlotView speedGraph;
    private final PlotView altitudeGraph;

    // Referens till controllern
    private final Controller controller;

    /**
     * Skapar en ny grafvy med tre olika grafer för visualisering av aktivitetsdata.
     *
     * @param controller referens till applikationens controller
     * @throws IllegalArgumentException om controller är null
     */
    public GraphView(Controller controller) {
        if (controller == null) {
            throw new IllegalArgumentException("Controller får inte vara null");
        }
        this.controller = controller;

        // Konfigurera panelens grundläggande utseende
        configurePanel();

        // Skapa de tre olika plottarna med specifika datahämtare
        pulseGraph = createPulsePlot();
        speedGraph = createSpeedPlot();
        altitudeGraph = createAltitudePlot();

        // Lägg till plottar i panelen med titlar och ramar
        add(createPlotPanel(pulseGraph, PULSE_TITLE));
        add(createPlotPanel(speedGraph, SPEED_TITLE));
        add(createPlotPanel(altitudeGraph, ALTITUDE_TITLE));
    }

    /**
     * Konfigurerar panelens grundläggande utseende och layout.
     */
    private void configurePanel() {
        setLayout(new GridLayout(3, 1, 0, GRAPH_SPACING));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    /**
     * Skapar plotten för pulsdata.
     *
     * @return en ny PlotView konfigurerad för pulsdata
     */
    private PlotView createPulsePlot() {
        return new PlotView(PULSE_UNIT, controller, TrackPoint::getHeartRate);
    }

    /**
     * Skapar plotten för hastighetsdata.
     *
     * @return en ny PlotView konfigurerad för hastighetsdata
     */
    private PlotView createSpeedPlot() {
        return new PlotView(SPEED_UNIT, controller, TrackPoint::getSpeed);
    }

    /**
     * Skapar plotten för höjddata.
     *
     * @return en ny PlotView konfigurerad för höjddata
     */
    private PlotView createAltitudePlot() {
        return new PlotView(ALTITUDE_UNIT, controller, TrackPoint::getAltitude);
    }

    /**
     * Skapar en panel för en plott med ram och titel.
     *
     * @param plot PlotView som ska läggas i panelen
     * @param title titel för plotten
     * @return en formaterad JPanel innehållande plotten
     */
    private JPanel createPlotPanel(PlotView plot, String title) {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(plot);
        return panel;
    }

    /**
     * Uppdaterar alla plottar med ny data.
     * Anropas när ny aktivitet väljs eller när data uppdateras.
     */
    public void updatePlot() {
        if (controller.getCurrentActivity() != null) {
            pulseGraph.repaint();
            speedGraph.repaint();
            altitudeGraph.repaint();
        } else {
            resetPlot();
        }
    }

    /**
     * Återställer alla plottar till ursprungsläget.
     * Anropas när ingen aktivitet är vald eller när vyn ska rensas.
     */
    public void resetPlot() {
        pulseGraph.reset();
        speedGraph.reset();
        altitudeGraph.reset();
        repaint();
    }
}