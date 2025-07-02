package app.ui;

import app.api.API;
import app.data.DataRepository;
import app.model.Drone;
import app.model.DroneDynamics;
import app.model.DroneType;
import app.data.FlightData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.List;

/**
 * Panel to display drone flight dynamics with pagination and Google Maps integration.
 * Users can navigate through pages of real-time data and click to view location in browser.
 */
public class FlightDynamicsPanel extends JPanel {

    private int page = 0; // current page index for pagination
    private final DefaultTableModel model;
    private final JTable table;

    /**
     * Constructs the flight dynamics panel with table, navigation buttons, and data loading.
     *
     * @param frame reference to the main frame for panel switching
     */
    public FlightDynamicsPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        // Panel title
        JLabel title = new JLabel("Flight Dynamics", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // Table columns
        String[] cols = {"DroneID", "Drone", "Timestamp", "Speed", "Roll", "Pitch", "Yaw",
                "Longitude", "Latitude", "gmaps", "Battery", "Last Seen", "Status"};

        // Table setup
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Navigation and control buttons
        JButton prev = new JButton("Previous");
        JButton next = new JButton("Next");
        JButton back = new JButton("Back");
        JButton refreshBtn = new JButton("Refresh");

        JPanel navPanel = new JPanel();
        navPanel.add(back);
        navPanel.add(prev);
        navPanel.add(next);
        add(navPanel, BorderLayout.SOUTH);
        add(refreshBtn, BorderLayout.NORTH);

        FlightData flightData = new FlightData();
        flightData.loadDronePositions();

        // Pagination logic
        prev.addActionListener(e -> {
            if (page >= 1) {
                page--;
                flightData.changePageBackward();
                loadData(flightData);
            }
        });

        next.addActionListener(e -> {
            page++;
            flightData.loadDronePositions();
            loadData(flightData);
        });

        // Return to home panel
        back.addActionListener(e -> frame.showPanel("home"));

        // Click on "gmaps" column opens Google Maps in browser
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e){
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();

                if (row >= 0 && col == 9) {
                    Object lon = table.getValueAt(row, 7);
                    Object lat = table.getValueAt(row, 8);

                    if (lon != null && lat != null) {
                        String url = "https://www.google.com/maps/search/?api=1&query=" + lon + "," + lat;
                        try {
                            Desktop.getDesktop().browse(new java.net.URI(url));
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Error opening browser: " + ex.getMessage());
                        }
                    }
                }
            }
        });

        // Refresh reloads data from first page
        refreshBtn.addActionListener(e -> {
            page = 0;
            flightData.resetData();
            flightData.loadDronePositions();
            loadData(flightData);
        });

        // Initial load
        loadData(flightData);
    }

    /**
     * Loads dynamic data for the current page into the table.
     * Each row includes calculated battery percentage and location link.
     */
    private void loadData(FlightData flightData) {
        model.setRowCount(0); // clear table

        // Fetch current page of dynamics data

        List<DroneDynamics> dynamics = flightData.getCurrentDronePositions();


        for (DroneDynamics d : dynamics) {
            Drone instance = DataRepository.getInstance().getDroneById(d.getDrone());
            DroneType name = API.getDroneTypeInstance(instance.getDroneType());

            String droneName = name.getManufacturer() + ": " + name.getTypename();

            // Calculate battery percentage
            String batteryPercentage = String.format("%.2f", 
                (d.getBatteryStatus() / name.getBatteryCapacity()) * 100) + "%";

            // Add row to table
            model.addRow(new Object[]{
                    instance.getId(), droneName, d.getTimestamp(), d.getSpeed(), d.getAlignRoll(),
                    d.getAlignPitch(), d.getAlignYaw(), d.getLongitude(), d.getLatitude(),
                    "click here", batteryPercentage, d.getLastSeen(), d.getStatus()
            });
        }
    }
}