package se.status4u.gui.mainPanel;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serial;
import javax.swing.*;
import se.status4u.controller.Controller;
import se.status4u.gui.plot.MapPlotView;
import se.status4u.model.User;
import se.status4u.model.Activity;
import java.util.Collection;

/**
 * Panel som visar användarinformation och aktivitetslista.
 * Hanterar också tillägg av nya aktiviteter.
 * @author Victor Vilches
 */
public class UserView extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L;

    // Konstanter för utseende
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final int SPACING = 10;
    private static final int GRID_ROWS = 5;
    private static final int GRID_COLS = 1;

    // Referens till andra komponenter
    private final Controller controller;
    private final DataView dataView;
    private final GraphView graphView;
    private final MapPlotView mapView;

    // GUI-komponenter
    private final DefaultListModel<Activity> listModel;
    private final JList<Activity> activityList;
    private final JButton addActivityButton;
    private final JButton removeActivityButton;
    private final JLabel userNameLabel;
    private final JLabel ageLabel;
    private final JLabel weightLabel;
    private final JLabel maxPulseLabel;

    /**
     * Skapar en ny användarvy med specificerade komponenter.
     */
    public UserView(Controller controller, DataView dataView,
                    GraphView graphView, MapPlotView mapView) {
        // Validera indata
        if (controller == null || dataView == null ||
                graphView == null || mapView == null) {
            throw new IllegalArgumentException("Inga komponenter får vara null");
        }

        // Spara referenser till andra komponenter
        this.controller = controller;
        this.dataView = dataView;
        this.graphView = graphView;
        this.mapView = mapView;

        // Initiera GUI-komponenter
        this.listModel = new DefaultListModel<>();
        this.activityList = new JList<>(listModel);
        this.activityList.setCellRenderer(new ActivityListCellRenderer());
        // skapar knappar
        this.addActivityButton = new JButton("Lägg till aktivitet");
        this.removeActivityButton = new JButton("Ta bort aktivitet");
        this.removeActivityButton.setEnabled(false); // Inaktiverad som standard
        //skapar etiketter
        this.userNameLabel = createInfoLabel("Namn: ");
        this.ageLabel = createInfoLabel("Ålder: ");
        this.weightLabel = createInfoLabel("Vikt: ");
        this.maxPulseLabel = createInfoLabel("Maxpuls: ");

        // Konfigurera layout och komponenter
        setupLayout();
        setupListeners();
        initializeComponents();
    }

    /**
     * Custom renderer för att visa Activity-objekt i listan
     */
    private static class ActivityListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Activity) {
                Activity activity = (Activity) value;
                setText(activity.getName());
            }
            return this;
        }
    }

    /**
     * Konfigurerar layouten för panelen
     */
    private void setupLayout() {
        setLayout(new BorderLayout(SPACING, SPACING));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(SPACING, SPACING, SPACING, SPACING));

        add(createUserInfoPanel(), BorderLayout.NORTH);
        add(createScrollPane(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    /**
     * Skapar panel för användarinformation
     */
    private JPanel createUserInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(GRID_ROWS, GRID_COLS, SPACING, SPACING));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder("Användarinformation"));

        panel.add(userNameLabel);
        panel.add(ageLabel);
        panel.add(weightLabel);
        panel.add(maxPulseLabel);

        return panel;
    }

    /**
     * Skapar scrollpanel för aktivitetslistan
     */
    private JScrollPane createScrollPane() {
        activityList.setFont(LABEL_FONT);
        JScrollPane scrollPane = new JScrollPane(activityList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Aktiviteter"));
        return scrollPane;
    }

    /**
     * Skapar knappanel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(BACKGROUND_COLOR);
        panel.add(addActivityButton);
        panel.add(removeActivityButton);
        return panel;
    }

    /**
     * Skapar formaterad etikett
     */
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        return label;
    }

    /**
     * Konfigurerar händelsehanterare
     */
    private void setupListeners() {
        // Lyssnare för val i aktivitetslistan
        activityList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                handleActivitySelection();
                removeActivityButton.setEnabled(activityList.getSelectedValue() != null);
            }
        });

        // Lyssnare för att lägga till ny aktivitet
        addActivityButton.addActionListener(event -> addActivity());
        // Lyssnare för att ta bort aktivitet
        removeActivityButton.addActionListener(event -> removeSelectedActivity());
    }

    /**
     * Initialiserar komponenter
     */
    private void initializeComponents() {
        updateUserInfo();
        updateList();
    }

    /**
     * Hanterar val av aktivitet i listan
     */
    private void handleActivitySelection() {
        Activity selectedActivity = activityList.getSelectedValue();
        if (selectedActivity != null) {
            controller.setCurrentActivity(selectedActivity.getName());
            updateViews();
        }
    }

    /**
     * Tar bort den valda aktiviteten efter bekräftelse
     */
    private void removeSelectedActivity() {
        Activity selectedActivity = activityList.getSelectedValue();
        if (selectedActivity != null) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Är du säker på att du vill ta bort aktiviteten: " + selectedActivity.getName() + "?",
                    "Bekräfta borttagning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (controller.removeActivity(selectedActivity.getName())) {
                    updateList();
                    dataView.resetView();
                    graphView.resetPlot();
                    mapView.updateMap();
                } else {
                    showError("Kunde inte ta bort aktiviteten");
                }
            }
        }
    }

    /**
     * Uppdaterar alla vyer med aktuell data
     */
    private void updateViews() {
        dataView.updateView();
        graphView.updatePlot();
        mapView.updateMap();
    }

    /**
     * Lägger till ny aktivitet genom att läsa in CSV-fil
     */
    // Uppdatera addActivity-metoden i UserView.java
    private void addActivity() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Välj aktivitetsfil");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                String activityName = promptForActivityName();

                if (activityName != null && !activityName.trim().isEmpty()) {
                    // Importera aktiviteten
                    controller.importActivity(file.getAbsolutePath(), activityName);

                    // Uppdatera listan och välj den nya aktiviteten
                    updateList();

                    // Sätt den nya aktiviteten som aktiv i controller
                    controller.setCurrentActivity(activityName);

                    // Välj den nya aktiviteten i listan
                    selectActivityInList(activityName);

                    // Uppdatera statistik och vyer
                    dataView.updateView();
                    graphView.updatePlot();
                    mapView.updateMap();
                }
            } catch (FileNotFoundException e) {
                showError("Kunde inte hitta filen: " + e.getMessage());
            } catch (Exception e) {
                showError("Ett fel uppstod: " + e.getMessage());
                e.printStackTrace(); // För att se mer detaljer om felet
            }
        }
    }

    /**
     * Visar dialog för att ange aktivitetsnamn
     * @return det angivna namnet eller null om användaren avbryter
     */
    private String promptForActivityName() {
        return JOptionPane.showInputDialog(this,
                "Ange namn på aktiviteten:",
                "Ny aktivitet",
                JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Visar felmeddelande i en dialogruta
     * @param message meddelandet som ska visas
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Fel",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Väljer en specifik aktivitet i listan baserat på namn
     * @param activityName namnet på aktiviteten som ska väljas
     */
    private void selectActivityInList(String activityName) {
        for (int i = 0; i < listModel.size(); i++) {
            Activity activity = listModel.getElementAt(i);
            if (activity.getName().equals(activityName)) {
                activityList.setSelectedIndex(i);
                break;
            }
        }
    }

    /**
     * Uppdaterar aktivitetslistan med aktuella aktiviteter
     */
    public void updateList() {
        if (controller.getCurrentUser() != null) {
            listModel.clear();
            Collection<Activity> activities = controller.getActivities();
            activities.forEach(listModel::addElement);
        }
    }

    /**
     * Uppdaterar användarinformation i vyn
     */
    public void updateUserInfo() {
        User user = controller.getCurrentUser();
        if (user != null) {
            userNameLabel.setText(String.format("Namn: %s", user.getName()));
            ageLabel.setText(String.format("Ålder: %d", user.getAge()));
            weightLabel.setText(String.format("Vikt: %.1f kg", user.getWeight()));
            maxPulseLabel.setText(String.format("Maxpuls: %.0f slag/min",
                    user.getMaxPulse()));
        } else {
            resetView();
        }
    }

    /**
     * Återställer vyn till ursprungsläge
     */
    public void resetView() {
        listModel.clear();
        userNameLabel.setText("Namn: ");
        ageLabel.setText("Ålder: ");
        weightLabel.setText("Vikt: ");
        maxPulseLabel.setText("Maxpuls: ");
        removeActivityButton.setEnabled(false);
    }
}