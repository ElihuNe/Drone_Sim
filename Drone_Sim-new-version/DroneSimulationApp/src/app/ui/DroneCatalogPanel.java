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

/**
 * DroneCatalogPanel displays all drone types and shows drones of a selected type.
 * Consists of two tables: one for types, one for corresponding drones.
 */
public class DroneCatalogPanel extends JPanel {

    /**
     * Creates the drone catalog view with a list of drone types and their matching drones.
     *
     * @param frame reference to the main frame for switching panels
     */
    public DroneCatalogPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        // Header title
        JLabel title = new JLabel("Drone Catalog", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // Load all drone types from repository
        List<DroneType> droneTypes = DataRepository.getInstance().getAllDroneTypes();

        // Table to display drone types
        String[] cols = {"ID", "Manufacturer", "Typename"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model) {

            // Tooltip shows detailed type specs when hovering a row
            @Override
            public String getToolTipText(MouseEvent e) {
                Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                if (rowIndex < 0 || colIndex < 0) {
                    return null;
                }

                Object value = getValueAt(rowIndex, 0);
                for (DroneType d : droneTypes) {
                    if (d.getId() == (int) value) {
                        return "Weight: " + d.getWeight() +
                                ", Max Speed: " + d.getMaxSpeed() +
                                ", Battery: " + d.getBatteryCapacity() +
                                ", Range: " + d.getControlRange() +
                                ", Max Carriage: " + d.getMaxCarriage();
                    }
                }
                return null;
            }
        };
        add(new JScrollPane(table), BorderLayout.WEST);

        // Fill left table with type data
        loadData(droneTypes, model);

        // Right table to display drones matching selected type
        String[] cols2 = {"Created", "Serialnumber", "Carriage Weight", "Carriage Type"};
        DefaultTableModel model2 = new DefaultTableModel(cols2, 0);
        JTable table2 = new JTable(model2);
        add(new JScrollPane(table2), BorderLayout.CENTER);

        // Listener: when a type is clicked, list all drones of that type
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    Object id = table.getValueAt(row, 0);
                    List<Drone> drones = DataRepository.getInstance().getAllDrones();
                    List<Drone> matchingDrones = new ArrayList<>();

                    // Filter drones matching the selected type
                    for (Drone drone : drones) {
                        DroneType instance = API.getDroneTypeInstance(drone.getDroneType());
                        if (instance.getId() == (int) id) {
                            matchingDrones.add(drone);
                        }
                    }

                    // Update right table
                    model2.setRowCount(0);
                    model2.addRow(new Object[]{"Drones Found: " + matchingDrones.size()});
                    for (Drone drone : matchingDrones) {
                        model2.addRow(new Object[]{
                                drone.getCreated(),
                                drone.getSerialNumber(),
                                drone.getCarriageWeight(),
                                drone.getCarriageType()
                        });
                    }
                }
            }
        });

        // Back button to return to home
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> frame.showPanel("home"));
        add(backBtn, BorderLayout.SOUTH);

        // Refresh button to reload drone types
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadData(droneTypes, model));
        add(refreshBtn, BorderLayout.NORTH);
    }

    /**
     * Loads all drone types into the table.
     *
     * @param droneTypes list of drone types
     * @param model      table model to update
     */
    private static void loadData(List<DroneType> droneTypes, DefaultTableModel model) {
        model.setRowCount(0);
        for (DroneType d : droneTypes) {
            model.addRow(new Object[]{
                    d.getId(),
                    d.getManufacturer(),
                    d.getTypename()
            });
        }
    }
}
