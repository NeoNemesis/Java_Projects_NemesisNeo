package se.status4u.gui.plot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Collectors;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;

import se.status4u.controller.Controller;
import se.status4u.model.TrackPoint;
/**
 * MapPlotView är en kartkomponent som visar en rutt på en OpenStreetMap-karta.
 * Komponenten stödjer zoomning, panorering och automatisk anpassning av vyn till rutten.
 * Den visar en röd linje för rutten samt markörer för start- och slutpunkter.
 * @author Victor Vilches
 */
public class MapPlotView extends JPanel {
    //konstanter för utseende
    private static final Color ROUTE_COLOR = new Color(255, 0, 0, 180);
    private static final int LINE_WIDTH = 3; // Definierad här
    //Huvudkomponenter
    private final JXMapViewer mapViewer;
    private final Controller controller;
    private final JButton zoomInButton;
    private final JButton zoomOutButton;
    private final JButton fitRouteButton;

    /**
     * Skapar en ny kartvy med kontroller för zoom och panorering.
     * @param controller Controller som tillhandahåller ruttdata
     */
    public MapPlotView(Controller controller) {
        this.controller = controller;

        // Initiera kartkomponenten
        mapViewer = new JXMapViewer();

        // Konfigurera kartkällan
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Skapa zoom-knappar
        zoomInButton = new JButton("+");
        zoomOutButton = new JButton("-");
        fitRouteButton = new JButton("Visa hela rutten");

        // Lägg till lyssnare för knapparna
        zoomInButton.addActionListener(e -> zoomIn());
        zoomOutButton.addActionListener(e -> zoomOut());
        fitRouteButton.addActionListener(e -> fitRoute());

        // Skapa panel för kontroller
        JPanel controlPanel = new JPanel();
        controlPanel.add(zoomInButton);
        controlPanel.add(zoomOutButton);
        controlPanel.add(fitRouteButton);

        // Konfigurera layout
        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(mapViewer, BorderLayout.CENTER);

        // Lägg till mushantering för panorering och zoomning
        MouseAdapter mouseAdapter = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mouseAdapter);
        mapViewer.addMouseMotionListener(mouseAdapter);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
    }
    /**
     * Zoomar in kartan ett steg.
     * Lägre zoomvärde betyder mer inzoomad vy.
     */
    private void zoomIn() {
        mapViewer.setZoom(Math.max(1, mapViewer.getZoom() - 1));
    }

    /**
     * Zoomar ut kartan ett steg.
     * Högre zoomvärde betyder mer utzoomad vy.
     */
    private void zoomOut() {
        mapViewer.setZoom(Math.min(15, mapViewer.getZoom() + 1));
    }

    /**
     * Anpassar kartvyn för att visa hela rutten.
     */
    private void fitRoute() {
        updateMap();
    }

    /**
     * Uppdaterar kartan med ny ruttdata och anpassar vyn.
     * Beräknar lämplig zoomnivå och centrerar kartan på rutten.
     */
    public void updateMap() {
        List<TrackPoint> points = controller.getTrackPoints();
        if (points == null || points.isEmpty()) {
            return;
        }
        // konvertera trackpoints till geografiska positioner
        List<GeoPosition> track = points.stream()
                .map(p -> new GeoPosition(p.getLatitude(), p.getLongitude()))
                .collect(Collectors.toList());

        // Beräkna ruttens gränser
        GeoPosition northEast = null;
        GeoPosition southWest = null;

        for (GeoPosition pos : track) {
            if (northEast == null) {
                northEast = pos;
                southWest = pos;
            } else {
                northEast = new GeoPosition(
                        Math.max(northEast.getLatitude(), pos.getLatitude()),
                        Math.max(northEast.getLongitude(), pos.getLongitude())
                );
                southWest = new GeoPosition(
                        Math.min(southWest.getLatitude(), pos.getLatitude()),
                        Math.min(southWest.getLongitude(), pos.getLongitude())
                );
            }
        }

        // Beräkna centrum och sätt zoom
        if (northEast != null && southWest != null) {
            double centerLat = (northEast.getLatitude() + southWest.getLatitude()) / 2;
            double centerLon = (northEast.getLongitude() + southWest.getLongitude()) / 2;
            mapViewer.setAddressLocation(new GeoPosition(centerLat, centerLon));

            // Beräkna lämplig zoomnivå baserat på ruttens storlek
            double latSpan = Math.abs(northEast.getLatitude() - southWest.getLatitude());
            double lonSpan = Math.abs(northEast.getLongitude() - southWest.getLongitude());
            int zoom = (int) (Math.log(360 / Math.max(latSpan, lonSpan)) / Math.log(2));
            mapViewer.setZoom(Math.min(15, Math.max(1, zoom)));
        }

        // Rita rutten
        List<Painter<JXMapViewer>> painters = List.of(
                new RoutePainter(track),
                new WaypointPainter(track)
        );

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
        mapViewer.setOverlayPainter(painter);

        repaint();
    }

    /**
     * Inre klass som hanterar ritning av själva ruttlinjen på kartan.
     */
    private static class RoutePainter implements Painter<JXMapViewer> {
        private final List<GeoPosition> track;

        public RoutePainter(List<GeoPosition> track) {
            this.track = track;
        }

        @Override
        public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
            g = (Graphics2D) g.create();

            // Förbättra rendering
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            // Sätt linjeegenskaper
            g.setStroke(new BasicStroke(LINE_WIDTH,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            g.setColor(ROUTE_COLOR);

            // Rita rutten
            Point2D prev = null;
            for (GeoPosition gp : track) {
                Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());
                Rectangle rect = map.getViewportBounds();
                pt.setLocation(pt.getX() - rect.getX(), pt.getY() - rect.getY());

                if (prev != null) {
                    g.drawLine((int) prev.getX(), (int) prev.getY(),
                            (int) pt.getX(), (int) pt.getY());
                }
                prev = pt;
            }
            g.dispose();
        }
    }
    /**
     * Inre klass som hanterar ritning av start- och slutpunkter på kartan.
     */
    private static class WaypointPainter implements Painter<JXMapViewer> {
        private final List<GeoPosition> track;
        private static final int POINT_SIZE = 10;

        public WaypointPainter(List<GeoPosition> track) {
            this.track = track;
        }

        @Override
        public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
            g = (Graphics2D) g.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Rita bara start- och slutpunkter
            if (!track.isEmpty()) {
                // Startpunkt (grön)
                drawPoint(g, map, track.get(0), new Color(0, 255, 0, 180));

                // Slutpunkt (röd)
                drawPoint(g, map, track.get(track.size() - 1), new Color(255, 0, 0, 180));
            }
            g.dispose();
        }

        /**
         * Hjälpmetod för att rita en waypoint-markör på kartan.
         * @param g Grafikkontexten
         * @param map Kartobjektet
         * @param pos Geografisk position
         * @param color Färg på markören
         */
        private void drawPoint(Graphics2D g, JXMapViewer map, GeoPosition pos, Color color) {
            Point2D pt = map.getTileFactory().geoToPixel(pos, map.getZoom());
            Rectangle rect = map.getViewportBounds();
            pt.setLocation(pt.getX() - rect.getX(), pt.getY() - rect.getY());

            g.setColor(color);
            g.fillOval((int)pt.getX() - POINT_SIZE/2,
                    (int)pt.getY() - POINT_SIZE/2,
                    POINT_SIZE, POINT_SIZE);
        }
    }
}