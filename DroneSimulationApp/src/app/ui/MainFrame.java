package app.ui;

import app.data.DataRepository;
import app.util.StyleUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;


public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    public MainFrame() {
        setTitle("Drone Simulation App");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Panels
        ConfigPanel loginPanel = new ConfigPanel(this);
        DroneCatalogPanel catalogPanel = new DroneCatalogPanel(this);
        FlightDynamicsPanel flightPanel = new FlightDynamicsPanel(this);
        DashboardPanel dashboardPanel = new DashboardPanel(this);
        HomePanel homePanel = new HomePanel(this);

        cardPanel.add(loginPanel, "login");
        cardPanel.add(catalogPanel, "catalog");
        cardPanel.add(flightPanel, "flight");
        cardPanel.add(dashboardPanel, "dashboard");
        cardPanel.add(homePanel, "home");

        setContentPane(cardPanel);
        showPanel("login");
    }

    public void showPanel(String name) {
        cardLayout.show(cardPanel, name);
    }
}

