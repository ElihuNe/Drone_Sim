package app.ui;

import app.api.API;
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

public class DashboardPanel extends JPanel {

    private JPanel chartPanel;
    private JLabel avgRangeLabel;

    public DashboardPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Dashboard", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(title);

        JPanel filterPanel = new JPanel(new FlowLayout());
        JLabel filterLabel = new JLabel("Drone Type: ");
        JComboBox<String> typeCombo = new JComboBox<>();
        final Map<String, String> typeMap = new HashMap<>();

        List<Drone> drones = DataRepository.getInstance().getAllDrones();
        Set<String> typeCodes = new HashSet<>();
        for (Drone d : drones) {
            typeCodes.add(d.getDroneType());
        }
        for (String code : typeCodes) {
            DroneType dt = API.getDroneTypeInstance(code);
            String typeName = dt.getManufacturer() + ": " + dt.getTypename();
            typeMap.put(typeName, code);
        }
        List<String> typeNames = new ArrayList<>(typeMap.keySet());
        Collections.sort(typeNames);
        typeCombo.addItem("All");
        for (String name : typeNames) {
            typeCombo.addItem(name);
        }
        filterPanel.add(filterLabel);
        filterPanel.add(typeCombo);
        filterPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(filterPanel);
        add(topPanel, BorderLayout.NORTH);

        JPanel mainCenterPanel = new JPanel(new BorderLayout());
        JPanel metricsPanel = new JPanel(new GridLayout(1, 1));
        avgRangeLabel = new JLabel();
        metricsPanel.add(avgRangeLabel);
        mainCenterPanel.add(metricsPanel, BorderLayout.NORTH);
        chartPanel = new JPanel(new GridLayout(2, 2));
        mainCenterPanel.add(chartPanel, BorderLayout.CENTER);
        add(mainCenterPanel, BorderLayout.CENTER);

        new FlightData();

        updateMetrics(null);
        updateCharts(null);

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

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(ev -> frame.showPanel("home"));
        add(backBtn, BorderLayout.SOUTH);
    }

    private void updateMetrics(String filterType) {
        double totalRange = 0.0;
        int countRange = 0;
        for (Drone d : DataRepository.getInstance().getAllDrones()) {
            if (filterType == null || d.getDroneType().equals(filterType)) {
                DroneType dt = API.getDroneTypeInstance(d.getDroneType());
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

    private void updateCharts(String filterType) {
        chartPanel.removeAll();

        if (filterType == null) {
            Map<String, Double> avgSpeeds = DataAggregator.getAverageSpeedPerType();
            Map<String, Double> avgSpeedsNamed = new LinkedHashMap<>();
            for (Map.Entry<String, Double> entry : avgSpeeds.entrySet()) {
                DroneType dt = API.getDroneTypeInstance(entry.getKey());
                String typeName = dt.getManufacturer() + ": " + dt.getTypename();
                avgSpeedsNamed.put(typeName, entry.getValue());
            }
            chartPanel.add(ChartUtils.createBarChart(avgSpeedsNamed, "Avg. Speed per Type", "", "Speed"));

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
                DroneType dt = API.getDroneTypeInstance(entry.getKey());
                String typeName = dt.getManufacturer() + ": " + dt.getTypename();
                avgWeightsNamed.put(typeName, avgW);
            }
            chartPanel.add(ChartUtils.createBarChart(avgWeightsNamed, "Avg. Carriage Weight per Type", "", "Weight"));

            Map<String, Long> batteryDist = DataAggregator.getBatteryDistribution();
            chartPanel.add(ChartUtils.createPieChart(batteryDist, "Battery Distribution"));

            Map<String, Integer> topTypes = DataAggregator.getTopDroneTypes();
            Map<String, Integer> topTypesNamed = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : topTypes.entrySet()) {
                DroneType dt = API.getDroneTypeInstance(entry.getKey());
                String typeName = dt.getManufacturer() + ": " + dt.getTypename();
                topTypesNamed.put(typeName, entry.getValue());
            }
            chartPanel.add(ChartUtils.createBarChartInt(topTypesNamed, "Top Drone Types", "", "Count"));

        } else {
            FlightData flightData = new FlightData();
            Map<String, List<DroneDynamics>> dronesByType = flightData.getDynamicsByType(filterType);

            Map<String, Double> speedPerDrone = new LinkedHashMap<>();
            for (Map.Entry<String, List<DroneDynamics>> entry : dronesByType.entrySet()) {
                double avgSpeed = entry.getValue().stream()
                        .mapToDouble(DroneDynamics::getSpeed)
                        .average()
                        .orElse(0.0);
                speedPerDrone.put(entry.getKey(), avgSpeed);
            }
            chartPanel.add(ChartUtils.createBarChart(speedPerDrone, "Avg. Speed per Drone", "", "Speed"));

            Map<String, Double> weightPerDrone = new LinkedHashMap<>();
            for (String label : dronesByType.keySet()) {
                String serial = label.substring(label.lastIndexOf(":") + 2);
                Drone drone = DataRepository.getInstance().getDroneBySerial(serial);
                double weight = (drone != null) ? drone.getCarriageWeight() : 0.0;
                weightPerDrone.put(label, weight);
            }
            chartPanel.add(ChartUtils.createBarChart(weightPerDrone, "Carriage Weight per Drone", "", "Weight"));

            Map<String, Long> filteredDist = new LinkedHashMap<>();
            for (List<DroneDynamics> dynList : dronesByType.values()) {
                for (DroneDynamics dyn : dynList) {
                    double b = dyn.getBatteryStatus();
                    String range;
                    if (b > 75) range = "75–100%";
                    else if (b > 50) range = "50–75%";
                    else if (b > 25) range = "25–50%";
                    else range = "0–25%";
                    filteredDist.merge(range, 1L, Long::sum);
                }
            }
            chartPanel.add(ChartUtils.createPieChart(filteredDist, "Battery Distribution"));

            int droneCount = dronesByType.size();
            String typeName = API.getDroneTypeInstance(filterType).getManufacturer()
                    + ": " + API.getDroneTypeInstance(filterType).getTypename();
            Map<String, Integer> oneTypeCount = Collections.singletonMap(typeName, droneCount);
            chartPanel.add(ChartUtils.createBarChartInt(oneTypeCount, "Drone Count", "", "Count"));
        }
        chartPanel.revalidate();
        chartPanel.repaint();
    }
}
