package se.status4u.gui.plot;

import java.awt.*;
import java.io.Serial;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import se.status4u.controller.Controller;
import se.status4u.model.DataFetcher;
import se.status4u.model.TrackPoint;

/**
 * En visualiseringskomponent som ritar en graf över trackpoints.
 * Komponenten visar data över tid med en y-axel med värden och
 * en graf som representerar förändringar i datan.
 * @author Victor Vilches
 */

public class PlotView extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L;

    // Färgkonstanter för grafens olika delar
    private static final Color PLOT_COLOR = new Color(255, 140, 0); // Orange
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color AXIS_COLOR = Color.GRAY;

    // Konstanter för axlar och marginaler
    private static final int AXIS_MARGIN = 50;      // Marginal för y-axeln
    private static final int TICK_LENGTH = 5;       // Längd på skalstreck
    private static final int VALUE_COUNT = 5;       // Antal värden på y-axeln
    private static final int TEXT_MARGIN = 5;       // Marginal för värdetext

    // huvudkomponenter för datahämtning och kontroll
    private final Controller controller;
    private final DataFetcher dataFetcher;
    private final String title;

    // Variabler för grafdata
    private List<TrackPoint> trackPoints;
    private double totalElapsedTime;
    private double minDataValue;
    private double maxDataValue;
    private int[] xPixels;
    private int[] yPixels;

    /**
     * Skapar en ny PlotView-komponent.
     * @param title Titel som visas i grafens ram
     * @param controller Kontroller för applikationslogik
     * @param dataFetcher Datahämtare för specifika värden
     * @throws IllegalArgumentException om någon parameter är null
     */
    public PlotView(String title, Controller controller, DataFetcher dataFetcher) {
        if (controller == null || dataFetcher == null || title == null) {
            throw new IllegalArgumentException("Inga parametrar får vara null");
        }

        this.controller = controller;
        this.dataFetcher = dataFetcher;
        this.title = title;

        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createTitledBorder(title));
    }
    /**
     * Uppdaterar listan med trackpoints från den aktuella aktiviteten.
     * Anropas innan grafen ritas om.
     */
    private void updateTrackPoints() {
        if (controller.getCurrentActivity() != null) {
            this.trackPoints = controller.getTrackPoints();
        }
    }
    /**
     * Beräknar min- och maxvärden samt total tid för datan.
     * Dessa värden används för att skala grafen korrekt.
     */
    private void calculateDataLimits() {
        if (trackPoints == null || trackPoints.isEmpty()) {
            return;
        }

        TrackPoint firstPoint = trackPoints.get(0);
        TrackPoint lastPoint = trackPoints.get(trackPoints.size() - 1);

        minDataValue = maxDataValue = dataFetcher.fetch(firstPoint);
        totalElapsedTime = lastPoint.getElapsedTime();

        for (TrackPoint point : trackPoints) {
            double value = dataFetcher.fetch(point);
            maxDataValue = Math.max(maxDataValue, value);
            minDataValue = Math.min(minDataValue, value);
        }
    }
    /**
     * Konverterar trackpoints till pixelkoordinater för ritning.
     * Hanterar skalning och positionering av datapunkter.
     */
    private void createPixelArrays() {
        if (trackPoints == null || trackPoints.isEmpty()) {
            return;
        }

        calculateDataLimits();

        int width = getWidth() - AXIS_MARGIN;  // Justerad bredd för axelmarginal
        int height = getHeight();

        yPixels = new int[width];
        xPixels = new int[width];

        double timeStep = totalElapsedTime / width;
        double valueRange = maxDataValue - minDataValue;

        if (valueRange == 0) {
            valueRange = 1.0;
        }

        double yScale = (height - 20) / valueRange;  // Justerad höjd för marginaler

        Iterator<TrackPoint> pointIterator = trackPoints.iterator();
        if (!pointIterator.hasNext()) {
            return;
        }

        TrackPoint currentPoint = pointIterator.next();
        for (int x = 0; x < width; x++) {
            double currentTime = x * timeStep;

            while (pointIterator.hasNext() &&
                    currentPoint.getElapsedTime() < currentTime) {
                currentPoint = pointIterator.next();
            }

            double value = dataFetcher.fetch(currentPoint) - minDataValue;
            yPixels[x] = height - 10 - (int) (yScale * value);
            xPixels[x] = x + AXIS_MARGIN;  // Förskjut x-koordinater för axelmarginal
        }
    }
    /**
     * Ritar hela grafen inklusive axlar, skalstreck och själva datakurvan.
     * Använder antialiasing för bättre visuell kvalitet.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Aktivera antialiasing för snyggare linjer
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        updateTrackPoints();
        createPixelArrays();

        if (trackPoints == null || trackPoints.isEmpty()) {
            return;
        }

        // Rita y-axel
        g2d.setColor(AXIS_COLOR);
        g2d.drawLine(AXIS_MARGIN, 10, AXIS_MARGIN, getHeight() - 10);

        // Rita skalL markeringar och värden
        double valueRange = maxDataValue - minDataValue;
        double stepSize = valueRange / (VALUE_COUNT - 1);
        int heightStep = (getHeight() - 20) / (VALUE_COUNT - 1);

        for (int i = 0; i < VALUE_COUNT; i++) {
            int y = getHeight() - 10 - (i * heightStep);

            // Rita skalstreck
            g2d.drawLine(AXIS_MARGIN - TICK_LENGTH, y, AXIS_MARGIN, y);

            // Rita värde
            double value = minDataValue + (i * stepSize);
            String valueText = String.format("%.1f", value);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(valueText);
            g2d.drawString(valueText,
                    AXIS_MARGIN - textWidth - TICK_LENGTH - TEXT_MARGIN,
                    y + fm.getHeight()/4);
        }

        // Rita grafen
        if (xPixels != null && yPixels != null && xPixels.length > 0) {
            g2d.setColor(PLOT_COLOR);
            g2d.drawPolyline(xPixels, yPixels, xPixels.length);
        }
    }
    /**
     * Återställer grafens data och tvingar fram en omritning.
     * Används när man vill rensa grafen.
     */
    public void reset() {
        trackPoints = null;
        xPixels = null;
        yPixels = null;
        repaint();
    }
}