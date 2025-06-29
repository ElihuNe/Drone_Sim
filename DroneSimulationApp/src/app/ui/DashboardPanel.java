package app.ui;

import app.api.API;
import app.logic.DataAggregator;
import app.data.DataRepository;
import app.model.Drone;
import app.model.DroneType;
import app.util.ChartUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardPanel extends JPanel {

    public DashboardPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Dashboard", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        String[] cols = {"ID", "Drone Type", "Created", "Serialnumber", "Carrige Weight", "Carriage Type"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.WEST);

        List<Drone> drones = DataRepository.getInstance().getAllDrones();

        for (Drone d : drones) {
            DroneType instance = API.getDroneTypeInstance(d.getDroneType());
            String name = instance.getManufacturer() + ": " + instance.getTypename();
            model.addRow(new Object[]{
                    d.getId(),name , d.getCreated(), d.getSerialNumber(),
                    d.getCarriageWeight(), d.getCarriageType()
            });

        }

        JPanel chartPanel = new JPanel(new GridLayout(2, 2));
        Map<String, Double> avgSpeeds = DataAggregator.getAverageSpeedPerType();
        Map<String, Long> batteryDist = DataAggregator.getBatteryDistribution();
        Map<String, Integer> topTypes = DataAggregator.getTopDroneTypes();

        chartPanel.add(ChartUtils.createBarChart(avgSpeeds, "Avg. Speed per Type", "Type", "Speed"));
        chartPanel.add(ChartUtils.createPieChart(batteryDist, "Battery Distribution"));
        chartPanel.add(ChartUtils.createBarChartInt(topTypes, "Top Drone Types", "Type", "Count"));

        add(chartPanel, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> frame.showPanel("home"));
        add(backBtn, BorderLayout.SOUTH);
    }
}