package se.status4u.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import se.status4u.controller.Controller;
import se.status4u.model.User;

/**
 * Panel för användarinloggning.
 * Hanterar val av användare och inloggningsprocess.
 * @author Victor Vilches
 */
public class LoginPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    // Konstanter för utseende
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;
    private static final Color BUTTON_COLOR = new Color(0, 0, 0);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);

    // GUI-komponenter
    private final JLabel headerLabel;
    private final JLabel usernameLabel;
    private final JComboBox<String> usernameComboBox;
    private final JButton loginButton;
    private final MainFrame mainFrame;
    private final Controller controller;

    // Fördefinierade användare
    private static final String[] USERS = {
            "Julia", "Oliver", "Victor"
    };

    /**
     * Skapar en ny inloggningspanel.
     */
    public LoginPanel(MainFrame mainFrame, Controller controller) {
        this.mainFrame = mainFrame;
        this.controller = controller;

        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        // Panel för att gruppera komponenter
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Skapa och lägg till komponenter
        headerLabel = createHeaderLabel();
        contentPanel.add(headerLabel, gbc);

        usernameLabel = createUsernameLabel();
        contentPanel.add(usernameLabel, gbc);

        usernameComboBox = createUsernameComboBox();
        contentPanel.add(usernameComboBox, gbc);

        loginButton = createLoginButton();
        contentPanel.add(loginButton, gbc);

        add(contentPanel);
    }

    private JLabel createHeaderLabel() {
        JLabel header = new JLabel("<html><h1><strong><i>Gizmo2020</i></strong></h1><hr></html>");
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setHorizontalAlignment(SwingConstants.CENTER);
        return header;
    }

    private JLabel createUsernameLabel() {
        JLabel label = new JLabel("Användarnamn:");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JComboBox<String> createUsernameComboBox() {
        JComboBox<String> comboBox = new JComboBox<>(USERS);
        comboBox.setSelectedIndex(0);
        return comboBox;
    }

    private JButton createLoginButton() {
        JButton button = new JButton("Logga in");
        button.setForeground(TEXT_COLOR);
        button.setBackground(BUTTON_COLOR);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.addActionListener(e -> handleLogin());
        return button;
    }

    /**
     * Hanterar inloggningsprocessen när användaren klickar på login-knappen.
     */
    private void handleLogin() {
        String selectedUser = (String) usernameComboBox.getSelectedItem();

        // Rensa eventuellt tidigare tillstånd
        controller.logout();

        // Försök först ladda existerande användardata
        User loadedUser = controller.loadUser(selectedUser);

        try {
            if (loadedUser != null) {
                // Om vi hittar sparad data, använd den
                controller.setCurrentUser(loadedUser);
            } else {
                // Om ingen sparad data finns, skapa ny användare
                switch (selectedUser) {
                    case "Julia" -> controller.setCurrentUser(selectedUser, 25, 65, 178);
                    case "Oliver" -> controller.setCurrentUser(selectedUser, 25, 86, 200);
                    case "Victor" -> controller.setCurrentUser(selectedUser, 35, 78, 180);
                    default -> {
                        showError("Ogiltig användare vald");
                        return;
                    }
                }
            }

            // Skapa och visa huvudpanelen
            setVisible(false);
            mainFrame.createMainPanel();

        } catch (Exception e) {
            showError("Ett fel uppstod vid inloggning: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Visar ett felmeddelande för användaren.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Inloggningsfel",
                JOptionPane.ERROR_MESSAGE);
    }
}