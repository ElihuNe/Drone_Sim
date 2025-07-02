package app.ui;

import app.data.DataRepository;
import app.util.StyleUtil;

import javax.swing.*;
import java.awt.*;

/**
 * MainFrame is the main window of the application.
 * It uses a CardLayout to switch between different panels.
 * All UI panels are initialized and managed from here.
 */
public class MainFrame extends JFrame {

    private CardLayout cardLayout; // Layout manager to switch between views
    private JPanel cardPanel;      // Container for all panels

    /**
     * Sets up the main window and initializes all UI panels.
     * Each panel is registered with a unique name for switching.
     */
    public MainFrame() {
        setTitle("Drone Simulation App");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center window on screen

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Initialize all views and pass reference to MainFrame
        ConfigPanel loginPanel = new ConfigPanel(this);
        DroneCatalogPanel catalogPanel = new DroneCatalogPanel(this);
        FlightDynamicsPanel flightPanel = new FlightDynamicsPanel(this);
        DashboardPanel dashboardPanel = new DashboardPanel(this);
        HomePanel homePanel = new HomePanel(this);

        // Add panels to card layout with string keys
        cardPanel.add(loginPanel, "login");
        cardPanel.add(catalogPanel, "catalog");
        cardPanel.add(flightPanel, "flight");
        cardPanel.add(dashboardPanel, "dashboard");
        cardPanel.add(homePanel, "home");

        // Set main content and show login screen initially
        setContentPane(cardPanel);
        showPanel("login");
    }

    /**
     * Switches the view to the selected panel.
     *
     * @param name name of the panel to show (e.g. "home", "dashboard")
     */
    public void showPanel(String name) {
        cardLayout.show(cardPanel, name);
    }
}
