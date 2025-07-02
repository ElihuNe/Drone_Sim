package app.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * HomePanel shows the welcome screen with navigation buttons.
 * Allows access to the Drone Catalog, Dashboard, and Flight Dynamics.
 */
public class HomePanel extends JPanel {

    /**
     * Creates the home screen with buttons to access main sections.
     *
     * @param frame reference to the main frame for switching views
     */
    public HomePanel(MainFrame frame) {
        setLayout(new BorderLayout());

        // Add a titled border with a welcome message
        TitledBorder title = BorderFactory.createTitledBorder("Welcome to Drone Simulator");
        title.setTitleJustification(TitledBorder.CENTER);
        setBorder(title);

        // Navigation buttons
        JButton catalogBtn = new JButton("Drone Catalog");
        catalogBtn.setMaximumSize(new Dimension(300, 50));

        JButton dashboardBtn = new JButton("Dashboard");
        dashboardBtn.setMaximumSize(new Dimension(300, 50));

        JButton flightBtn = new JButton("Flight Dynamics");
        flightBtn.setMaximumSize(new Dimension(300, 50));

        // Arrange buttons vertically with spacing
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.add(catalogBtn);
        buttonContainer.add(Box.createVerticalStrut(10));
        buttonContainer.add(dashboardBtn);
        buttonContainer.add(Box.createVerticalStrut(10));
        buttonContainer.add(flightBtn);

        // Center the button container
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(buttonContainer);
        add(centerPanel, BorderLayout.CENTER);

        // Navigation actions
        dashboardBtn.addActionListener(e -> frame.showPanel("dashboard"));
        flightBtn.addActionListener(e -> frame.showPanel("flight"));
        catalogBtn.addActionListener(e -> frame.showPanel("catalog"));
    }
}
