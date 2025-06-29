package app.ui;

import app.api.API;
import app.data.DataRepository;
import app.model.Drone;
import app.model.DroneDynamics;
import app.model.DroneType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.List;

public class FlightDynamicsPanel extends JPanel {

    private int page = 0;
    private final DefaultTableModel model;
    private final JTable table;

    public FlightDynamicsPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Flight Dynamics", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        String[] cols = {"Drone", "Timestamp", "Speed", "Roll", "Pitch", "Yaw",
                "Longitude", "Latitude", "gmaps", "Battery", "Last Seen", "Status"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

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

        prev.addActionListener(e -> {
            if (page >= 1) page--;
            loadData();
        });
        next.addActionListener(e -> {
            page++;
            loadData();
        });
        back.addActionListener(e -> frame.showPanel("home"));

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e){
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                if (row >= 0 && col == 8){
                    Object lon = table.getValueAt(row, 6);
                    Object lat = table.getValueAt(row, 7);
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

        refreshBtn.addActionListener(e -> {
            page = 0;
            loadData();
        });

        loadData();
    }



    private void loadData() {
        model.setRowCount(0);
        List<DroneDynamics> dynamics = DataRepository.getInstance().getDroneDynamics(page);
        for (DroneDynamics d : dynamics) {
            Drone instance = DataRepository.getInstance().getDroneById(d.getDrone());
            DroneType name = API.getDroneTypeInstance(instance.getDroneType());
            String droneName = name.getManufacturer() + ": " + name.getTypename();
            String batterypersentage = String.format("%.2f", (d.getBatteryStatus() / name.getBatteryCapacity()) * 100) + "%";
            model.addRow(new Object[]{
                    droneName, d.getTimestamp(), d.getSpeed(), d.getAlignRoll(),
                    d.getAlignPitch(), d.getAlignYaw(), d.getLongitude(), d.getLatitude(),
                    "click here", batterypersentage, d.getLastSeen(), d.getStatus()
            });
        }
    }
}
