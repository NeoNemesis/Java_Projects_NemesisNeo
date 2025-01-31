package se.status4u.gui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import se.status4u.controller.Controller;
import se.status4u.gui.mainPanel.MainPanel;

/**
 * Huvudfönstret för applikationen.
 * Hanterar växling mellan inloggningspanel och huvudpanel.
 * @author Victor Vilches
 */
public class MainFrame extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;

    // Konstanter för fönsterstorlek och namn
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    private static final String WINDOW_TITLE = "Gizmo2020";

    // Panel-identifierare för CardLayout
    private static final String LOGIN_PANEL = "LOGIN";
    private static final String MAIN_PANEL = "MAIN";

    // Komponenter
    private final Controller controller;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final LoginPanel loginPanel;
    private MainPanel mainPanel;

    /**
     * Skapar huvudfönstret för applikationen.
     *
     * @param controller kontrollerinstans för hantering av applikationslogik
     * @throws IllegalArgumentException om controller är null
     */
    public MainFrame(Controller controller) {
        super(WINDOW_TITLE);

        if (controller == null) {
            throw new IllegalArgumentException("Controller får inte vara null");
        }

        this.controller = controller;

        // Initiera layout och paneler
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);
        this.loginPanel = new LoginPanel(this, controller);

        // Lägg till login-panel
        contentPanel.add(loginPanel, LOGIN_PANEL);

        setupFrame();
        setupWindowListeners();
    }

    /**
     * Konfigurerar fönstrets grundläggande egenskaper.
     */
    private void setupFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setContentPane(contentPanel);

        // Visa login-panel först
        cardLayout.show(contentPanel, LOGIN_PANEL);

        // Centrera och visa fönstret
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Konfigurerar fönstrets händelselyssnare.
     */
    private void setupWindowListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
    }

    /**
     * Hanterar händelsen när fönstret stängs.
     * Sparar användardata om det finns en inloggad användare.
     */
    private void handleWindowClosing() {
        if (controller.getCurrentUser() != null) {
            controller.save();
        }
    }

    /**
     * Skapar och visar huvudpanelen efter lyckad inloggning.
     * Denna metod körs på EDT (Event Dispatch Thread).
     */
    public void createMainPanel() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Skapa en ny huvudpanel varje gång
                mainPanel = new MainPanel(controller, this);
                contentPanel.add(mainPanel, MAIN_PANEL);

                // Uppdatera huvudpanelen och visa den
                mainPanel.updateList();
                cardLayout.show(contentPanel, MAIN_PANEL);

                // Uppdatera fönstret
                revalidate();
                repaint();

            } catch (Exception e) {
                System.err.println("Fel vid skapande av huvudpanel: " + e.getMessage());
                e.printStackTrace();
                showLoginPanel();
            }
        });
    }

    /**
     * Växlar tillbaka till inloggningspanelen och rensar tidigare användardata.
     */
    public void showLoginPanel() {
        // Logga ut nuvarande användare
        controller.logout();

        // Ta bort den gamla huvudpanelen
        if (mainPanel != null) {
            contentPanel.remove(mainPanel);
            mainPanel = null;
        }

        // Visa login-panelen
        cardLayout.show(contentPanel, LOGIN_PANEL);

        // Uppdatera gränssnittet
        revalidate();
        repaint();
    }
}