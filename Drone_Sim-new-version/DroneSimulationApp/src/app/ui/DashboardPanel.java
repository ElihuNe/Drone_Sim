package app.ui;

import app.api.API;
import app.data.FlightData;
import app.logic.DataAggregator;
import app.data.DataRepository;
import app.model.Drone;
import app.model.DroneType;
import app.util.ChartUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DashboardPanel displays an overview of all drones and charts with aggregated data.
 * Combines tabular and visual elements to summarize key drone statistics.
 */
public class DashboardPanel extends JPanel {

    /**
     * Creates the dashboard view with drone table and statistics charts.
     *
     * @param frame reference to the main frame to allow panel switching
     */
    public DashboardPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        // Title label at the top
        JLabel title = new JLabel("Dashboard", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // Table structure for drone information
        String[] cols = {"ID", "Drone Type", "Created", "Serialnumber", "Carrige Weight", "Carriage Type"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.WEST);

        // Load drones and populate the table with type details
        List<Drone> drones = DataRepository.getInstance().getAllDrones();
        for (Drone d : drones) {
            DroneType instance = API.getDroneTypeInstance(d.getDroneType());
            String name = instance.getManufacturer() + ": " + instance.getTypename();

            model.addRow(new Object[]{
                    d.getId(),
                    name,
                    d.getCreated(),
                    d.getSerialNumber(),
                    d.getCarriageWeight(),
                    d.getCarriageType()
            });
        }

        FlightData flightData = new FlightData();

        //TODO: finish click event for chart display

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    Object id = table.getValueAt(row, 0);

                }
            }
        });

        // Create a panel with 3 charts
        JPanel chartPanel = new JPanel(new GridLayout(2, 2));

        // Chart 1: Average speed by drone type
        Map<String, Double> avgSpeeds = DataAggregator.getAverageSpeedPerType();
        chartPanel.add(ChartUtils.createBarChart(
                avgSpeeds, "Avg. Speed per Type", "Type", "Speed"
        ));

        // Chart 2: Battery level distribution
        Map<String, Long> batteryDist = DataAggregator.getBatteryDistribution();
        chartPanel.add(ChartUtils.createPieChart(
                batteryDist, "Battery Distribution"
        ));

        // Chart 3: Top 5 most common drone types
        Map<String, Integer> topTypes = DataAggregator.getTopDroneTypes();
        chartPanel.add(ChartUtils.createBarChartInt(
                topTypes, "Top Drone Types", "Type", "Count"
        ));

        add(chartPanel, BorderLayout.CENTER);

        // Back button to return to home panel
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> frame.showPanel("home"));
        add(backBtn, BorderLayout.SOUTH);
    }
}
