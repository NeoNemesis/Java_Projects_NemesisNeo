package se.status4u.gui.mainPanel;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JPanel;
import se.status4u.controller.Controller;
import se.status4u.gui.plot.MapPlotView;

/**
 * Panel som visar kartvy för aktiviteten.
 * @author Victor Vilches
 */
public class MapView extends JPanel {
    private final Controller controller;
    private final MapPlotView mapPlotView;

    public MapView(Controller controller) {
        this.controller = controller;
        setLayout(new GridLayout(1, 1));
        mapPlotView = new MapPlotView(controller);
        setBackground(Color.ORANGE);
        add(mapPlotView);
    }

    public void updateMap() {
        mapPlotView.repaint();
    }
}