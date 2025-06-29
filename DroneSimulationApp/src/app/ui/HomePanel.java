package app.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class HomePanel extends JPanel{

    public HomePanel(MainFrame frame) {
        setLayout(new BorderLayout());

        TitledBorder title = BorderFactory.createTitledBorder("Welcome to Drone Simulator");
        title.setTitleJustification(TitledBorder.CENTER);
        setBorder(title);

        JButton catalogBtn = new JButton("Drone Catalog");
        catalogBtn.setMaximumSize(new Dimension(300, 50));
        JButton dashboardBtn = new JButton("Dashboard");
        dashboardBtn.setMaximumSize(new Dimension(3000, 50));
        JButton flightBtn = new JButton("Flight Dynamics");
        flightBtn.setMaximumSize(new Dimension(300, 50));

        //center buttons in home page
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.add(catalogBtn);
        buttonContainer.add(Box.createVerticalStrut(10));
        buttonContainer.add(dashboardBtn);
        buttonContainer.add(Box.createVerticalStrut(10));
        buttonContainer.add(flightBtn);



        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(buttonContainer);

        add(centerPanel, BorderLayout.CENTER);

        dashboardBtn.addActionListener( e ->{
            frame.showPanel("dashboard");
        });

        flightBtn.addActionListener( e ->{
            frame.showPanel("flight");
        });

        catalogBtn.addActionListener( e ->{
            frame.showPanel("catalog");
        });
    }

}
