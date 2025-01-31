package se.status4u.gui.mainPanel;

import java.awt.*;
import java.io.Serial;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import se.status4u.controller.Controller;
import se.status4u.gui.MainFrame;
import se.status4u.gui.plot.MapPlotView;

/**
 * Huvudpanel för applikationen som organiserar alla vyer i ett 2x2 rutnät.
 * Innehåller fyra huvudkomponenter:
 * - Användarvy (övre vänstra hörnet): Visar användarinfo och aktivitetslista
 * - Datavy (övre högra hörnet): Visar statistik för vald aktivitet
 * - Kartvy (nedre vänstra hörnet): Visar aktivitetens rutt
 * - Grafvy (nedre högra hörnet): Visar diagram över aktivitetsdata
 * Panelen har också en utloggningsknapp i övre kanten.
 * @author Victor Vilches
 */
public class MainPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L;

    // Layout-konstanter
    private static final int PANEL_PADDING = 10;     // Yttre padding för huvudpanelen
    private static final int PANEL_SPACING = 5;      // Mellanrum mellan paneler
    private static final int COMPONENT_PADDING = 5;  // Inre padding för komponenter
    private static final int GRID_ROWS = 2;          // Antal rader i rutnätet
    private static final int GRID_COLS = 2;          // Antal kolumner i rutnätet

    // Titel-texter för delpanelerna
    private static final String USER_PANEL_TITLE = "Användarinformation";
    private static final String DATA_PANEL_TITLE = "Aktivitetsdata";
    private static final String MAP_PANEL_TITLE = "Karta";
    private static final String GRAPH_PANEL_TITLE = "Grafer";

    // Panelens huvudkomponenter
    private final UserView userView;          // Användarinfo och aktivitetslista
    private final DataView dataView;          // Statistikvy
    private final MapPlotView mapView;        // Kartvy
    private final GraphView graphView;        // Grafvy
    private final Controller controller;      // Referens till applikationens controller
    private final MainFrame mainFrame;        // Referens till huvudfönstret
    private final JButton logoutButton;       // Utloggningsknapp

    /**
     * Skapar huvudpanelen och initierar alla dess komponenter.
     * Komponenterna skapas i en specifik ordning för att hantera beroenden korrekt.
     *
     * @param controller styrenheten för applikationslogik
     * @param mainFrame huvudfönstret som innehåller denna panel
     * @throws IllegalArgumentException om controller eller mainFrame är null
     */
    public MainPanel(Controller controller, MainFrame mainFrame) {
        if (controller == null || mainFrame == null) {
            throw new IllegalArgumentException("Controller och MainFrame får inte vara null");
        }

        // Spara referenser
        this.controller = controller;
        this.mainFrame = mainFrame;

        // Initiera utloggningsknapp
        logoutButton = new JButton("Logga ut");
        logoutButton.addActionListener(e -> handleLogout());

        // Konfigurera huvudpanelens layout
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(PANEL_PADDING, PANEL_PADDING,
                PANEL_PADDING, PANEL_PADDING));

        // Skapa vyer i rätt ordning (DataView måste skapas före UserView)
        dataView = new DataView(controller);
        graphView = new GraphView(controller);
        mapView = new MapPlotView(controller);
        userView = new UserView(controller, dataView, graphView, mapView);

        // Sätt upp alla komponenter i layouten
        setupComponents();
    }

    /**
     * Organiserar alla komponenter i layouten med GridBagLayout.
     * Utloggningsknappen placeras överst, sedan kommer 2x2-rutnätet med vyerna.
     */
    private void setupComponents() {
        GridBagConstraints gbc = new GridBagConstraints();

        // Lägg till utloggningsknapp i övre kanten
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(logoutButton);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, PANEL_SPACING, 0);
        add(buttonPanel, gbc);

        // Skapa panelen för huvudinnehållet (2x2-rutnät)
        JPanel mainContent = new JPanel(new GridLayout(GRID_ROWS, GRID_COLS,
                PANEL_SPACING, PANEL_SPACING));

        // Lägg till vyerna i rutnätet med ramar och titlar
        addViewWithBorder(mainContent, userView, USER_PANEL_TITLE);
        addViewWithBorder(mainContent, dataView, DATA_PANEL_TITLE);
        addViewWithBorder(mainContent, mapView, MAP_PANEL_TITLE);
        addViewWithBorder(mainContent, graphView, GRAPH_PANEL_TITLE);

        // Lägg till huvudinnehållet i panelen
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        add(mainContent, gbc);
    }

    /**
     * Lägger till en delpanel med formaterad ram och titel.
     *
     * @param container containern som vyn ska läggas till i
     * @param view vyn som ska läggas till
     * @param title titel för vyns ram
     */
    private void addViewWithBorder(JPanel container, JPanel view, String title) {
        view.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                BorderFactory.createEmptyBorder(COMPONENT_PADDING, COMPONENT_PADDING,
                        COMPONENT_PADDING, COMPONENT_PADDING)
        ));
        container.add(view);
    }

    /**
     * Hanterar utloggning genom att meddela controller och visa inloggningspanelen.
     */
    private void handleLogout() {
        controller.logout();
        mainFrame.showLoginPanel();
    }

    /**
     * Uppdaterar aktivitetslistan i användarvyn.
     * Anropas när användaren loggar in eller när en ny aktivitet läggs till.
     */
    public void updateList() {
        userView.updateList();
    }
}