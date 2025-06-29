package app.ui;

import app.data.DataRepository;
import app.model.Drone;
import app.model.DroneType;
import app.api.API;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class DroneCatalogPanel extends JPanel {

    public DroneCatalogPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Drone Catalog", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        List<DroneType> droneTypes= DataRepository.getInstance().getAllDroneTypes();

        String[] cols = {"ID", "Manufacturer", "Typename"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model){
            @Override
            public String getToolTipText(MouseEvent e) {
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                if (rowIndex < 0 || colIndex < 0) {
                    return null;
                }
                Object value = getValueAt(rowIndex, 0);
                String s = new String();
                for(DroneType d : droneTypes){
                    if(d.getId() == (int) value){
                        s = "Weight: " + d.getWeight() + " Maximum Speed: " + d.getMaxSpeed() + " Battery Capacity: " +
                                d.getBatteryCapacity() + " Control Range: " + d.getControlRange() +
                                " Maximum Carriage: " + d.getMaxCarriage();
                    }
                }
                return s;
            }
        };
        add(new JScrollPane(table), BorderLayout.WEST);

        loadData(droneTypes, model);

        String[] cols2 = {"Created", "Serialnumber", "Carriage Weight", "Carriage Type"};
        DefaultTableModel model2 = new DefaultTableModel(cols2, 0);
        JTable table2 = new JTable(model2);
        add(new JScrollPane(table2), BorderLayout.CENTER);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int i = table.getSelectedRow();
                    Object id = table.getValueAt(i, 0);
                    List<Drone> drones = DataRepository.getInstance().getAllDrones();
                    List<Drone> matchingDrones = new ArrayList<>();
                    for (Drone drone : drones) {
                        DroneType instance = API.getDroneTypeInstance(drone.getDroneType());
                        if (instance.getId() == (int) id) {
                            matchingDrones.add(drone);
                        };
                    }
                    model2.setRowCount(0);
                    model2.addRow(new Object[]{"Drones Found: " + matchingDrones.size()});
                    for (Drone drone : matchingDrones) {
                        model2.addRow(new Object[]{
                                drone.getCreated(), drone.getSerialNumber(), drone.getCarriageWeight(), drone.getCarriageType()
                        });
                    }
                }
            }
        });

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> frame.showPanel("home"));
        add(backBtn, BorderLayout.SOUTH);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            loadData(droneTypes, model);
        });
        add(refreshBtn, BorderLayout.NORTH);
    }

    private static void loadData(List<DroneType> droneTypes, DefaultTableModel model){
        model.setRowCount(0);
        for (DroneType d : droneTypes) {
            model.addRow(new Object[]{
                    d.getId(), d.getManufacturer(), d.getTypename()
            });
        }
    }

}
