package app.ui;

import app.data.FlightData;
import app.data.DataRepository;
import app.logic.DataAggregator;
import app.model.Drone;
import app.model.DroneType;
import app.model.DroneDynamics;
import app.util.ChartUtils;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * DashboardPanel is the main UI panel that displays aggregated metrics
 * and charts about all drones and their types.
 *
 * This class is fully decoupled from direct API calls.
 * All data is obtained via the DataRepository singleton.
 */
public class DashboardPanel extends JPanel {

    private JPanel chartPanel;
    private JLabel avgRangeLabel;
    private JLabel droneCountLabel;

    /**
     * Constructs the dashboard panel and sets up the UI layout,
     * filters, charts, and metrics.
     *
     * @param frame reference to the main application frame
     */
    public DashboardPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        // --- Panel Header Title ---
        JLabel title = new JLabel("Dashboard", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(title);

        // --- Drone Type Filter Dropdown ---
        JPanel filterPanel = new JPanel(new FlowLayout());
        JLabel filterLabel = new JLabel("Drone Type: ");
        JComboBox<String> typeCombo = new JComboBox<>();
        final Map<String, String> typeMap = new HashMap<>();

        // Build dropdown entries from available drone types
        List<Drone> drones = DataRepository.getInstance().getAllDrones();
        Set<String> typeCodes = new HashSet<>();
        for (Drone d : drones) {
            typeCodes.add(d.getDroneType());
        }

        // Map human-readable names to internal type codes
        for (String code : typeCodes) {
            DroneType dt = DataRepository.getInstance().getDroneTypeByUrl(code);
            String typeName = dt.getManufacturer() + ": " + dt.getTypename();
            typeMap.put(typeName, code);
        }

        List<String> typeNames = new ArrayList<>(typeMap.keySet());
        Collections.sort(typeNames);
        typeCombo.addItem("All"); // allow viewing all types together
        for (String name : typeNames) {
            typeCombo.addItem(name);
        }

        filterPanel.add(filterLabel);
        filterPanel.add(typeCombo);
        filterPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(filterPanel);
        add(topPanel, BorderLayout.NORTH);

        // --- Metrics and Charts Area ---
        JPanel mainCenterPanel = new JPanel(new BorderLayout());
        JPanel metricsPanel = new JPanel(new GridLayout(2, 1));
        avgRangeLabel = new JLabel();
        droneCountLabel = new JLabel();
        metricsPanel.add(avgRangeLabel);
        metricsPanel.add(droneCountLabel);
        mainCenterPanel.add(metricsPanel, BorderLayout.NORTH);

        chartPanel = new JPanel(new GridLayout(2, 2));
        mainCenterPanel.add(chartPanel, BorderLayout.CENTER);
        add(mainCenterPanel, BorderLayout.CENTER);

        // Ensure FlightData class is initialized
        new FlightData();

        // Load initial charts and metrics with no filter (i.e. all drones)
        updateMetrics(null);
        updateCharts(null);

        // React to dropdown selection changes
        typeCombo.addActionListener(e -> {
            String selectedName = (String) typeCombo.getSelectedItem();
            if (selectedName == null || selectedName.equals("All")) {
                updateMetrics(null);
                updateCharts(null);
            } else {
                String selectedCode = typeMap.get(selectedName);
                updateMetrics(selectedCode);
                updateCharts(selectedCode);
            }
        });

        // --- Back Button ---
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(ev -> frame.showPanel("home"));
        add(backBtn, BorderLayout.SOUTH);
    }

    /**
     * Updates metric labels for average drone range.
     *
     * @param filterType if provided, limits the calculation to a single drone type
     */
    private void updateMetrics(String filterType) {
        double totalRange = 0.0;
        int countRange = 0;

        // Sum the control range for matching drones
        for (Drone d : DataRepository.getInstance().getAllDrones()) {
            if (filterType == null || d.getDroneType().equals(filterType)) {
                DroneType dt = DataRepository.getInstance().getDroneTypeByUrl(d.getDroneType());
                double r = dt.getControlRange();
                totalRange += r;
                countRange++;
            }
        }

        if (countRange > 0) {
            double avgRange = totalRange / countRange;
            avgRangeLabel.setText(String.format("Average Range: %.2f", avgRange));
        } else {
            avgRangeLabel.setText("Average Range: N/A");
        }
    }

    /**
     * Generates or refreshes dashboard charts based on the selected filter.
     *
     * @param filterType if provided, limits charts to a single drone type
     */
    private void updateCharts(String filterType) {
        chartPanel.removeAll();

        if (filterType == null) {
            // --- Show Global Statistics for All Types ---
            chartPanel.setLayout(new GridLayout(2, 2));
            droneCountLabel.setText("");

            // 1) Average speed per drone type
            Map<String, Double> avgSpeeds = DataAggregator.getAverageSpeedPerType();
            Map<String, Double> avgSpeedsNamed = new LinkedHashMap<>();
            for (Map.Entry<String, Double> entry : avgSpeeds.entrySet()) {
                DroneType dt = DataRepository.getInstance().getDroneTypeByUrl(entry.getKey());
                String typeName = dt.getManufacturer() + ": " + dt.getTypename();
                avgSpeedsNamed.put(typeName, entry.getValue());
            }
            chartPanel.add(ChartUtils.createBarChart(
                    avgSpeedsNamed,
                    "Avg. Speed per Type",
                    "",
                    ""
            ));

            // 2) Average carriage weight per drone type
            List<Drone> allDrones = DataRepository.getInstance().getAllDrones();
            Map<String, Double> totalWeights = new HashMap<>();
            Map<String, Integer> countByType = new HashMap<>();
            for (Drone d : allDrones) {
                totalWeights.merge(d.getDroneType(), d.getCarriageWeight(), Double::sum);
                countByType.merge(d.getDroneType(), 1, Integer::sum);
            }

            Map<String, Double> avgWeightsNamed = new LinkedHashMap<>();
            for (Map.Entry<String, Double> entry : totalWeights.entrySet()) {
                int count = countByType.getOrDefault(entry.getKey(), 1);
                double avgW = entry.getValue() / count;
                DroneType dt = DataRepository.getInstance().getDroneTypeByUrl(entry.getKey());
                String typeName = dt.getManufacturer() + ": " + dt.getTypename();
                avgWeightsNamed.put(typeName, avgW);
            }
            chartPanel.add(ChartUtils.createBarChart(
                    avgWeightsNamed,
                    "Avg. Carriage Weight per Type",
                    "",
                    ""
            ));

            // 3) Overall battery distribution
            Map<String, Long> batteryDist = DataAggregator.getBatteryDistribution();
            chartPanel.add(ChartUtils.createPieChart(batteryDist, "Battery Distribution"));

            // 4) Top drone types by occurrence
            Map<String, Integer> topTypes = DataAggregator.getTopDroneTypes();
            Map<String, Integer> topTypesNamed = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : topTypes.entrySet()) {
                DroneType dt = DataRepository.getInstance().getDroneTypeByUrl(entry.getKey());
                String typeName = dt.getManufacturer() + ": " + dt.getTypename();
                topTypesNamed.put(typeName, entry.getValue());
            }
            chartPanel.add(ChartUtils.createBarChartInt(
                    topTypesNamed,
                    "Top Drone Types",
                    "",
                    ""
            ));

        } else {
            // --- Show Detailed Statistics for a Specific Drone Type ---
            chartPanel.setLayout(new GridLayout(2, 1));

            // Gather all drone dynamics for the selected type
            FlightData flightData = new FlightData();
            Map<String, List<DroneDynamics>> dronesByType = flightData.getDynamicsByType(filterType);

            JPanel topCharts = new JPanel(new GridLayout(1, 2));

            // Average speed per individual drone
            Map<String, Double> speedPerDrone = new LinkedHashMap<>();
            for (Map.Entry<String, List<DroneDynamics>> entry : dronesByType.entrySet()) {
                double avgSpeed = entry.getValue().stream()
                        .mapToDouble(DroneDynamics::getSpeed)
                        .average()
                        .orElse(0.0);
                speedPerDrone.put(entry.getKey(), avgSpeed);
            }
            topCharts.add(ChartUtils.createBarChart(
                    speedPerDrone,
                    "Avg. Speed per Drone",
                    "Drone",
                    "Speed"
            ));

            // Carriage weight per individual drone
            Map<String, Double> weightPerDrone = new LinkedHashMap<>();
            for (String label : dronesByType.keySet()) {
                // Extract serial number from the label (after last ": ")
                String serial = label.substring(label.lastIndexOf(":") + 2);
                Drone drone = DataRepository.getInstance().getDroneBySerial(serial);
                double weight = (drone != null) ? drone.getCarriageWeight() : 0.0;
                weightPerDrone.put(label, weight);
            }
            topCharts.add(ChartUtils.createBarChart(
                    weightPerDrone,
                    "Carriage Weight per Drone",
                    "Drone",
                    "Weight"
            ));

            chartPanel.add(topCharts);

            // Battery distribution across all drones of this type
            Map<String, Long> filteredDist = DataAggregator.getBatteryDistributionFiltered(dronesByType);
            chartPanel.add(ChartUtils.createPieChart(filteredDist, "Battery Distribution"));

            // Display total number of drones for this type
            int droneCount = dronesByType.size();
            DroneType dt = DataRepository.getInstance().getDroneTypeByUrl(filterType);
            String typeName = dt.getManufacturer() + ": " + dt.getTypename();
            droneCountLabel.setText("Drone Count: " + droneCount);
        }
        chartPanel.revalidate();
        chartPanel.repaint();
    }
}


