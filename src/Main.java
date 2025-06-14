import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static int pageNum = 1;
    private static DefaultTableModel tableModel;

    // function for paginging through drone dynamics
    private static void loadDynamicTableData() {
        List<API.DroneDynamics> droneDynamics = API.getDroneDynamics(pageNum);

        tableModel.setRowCount(0);

        for (int i = 0; i < droneDynamics.size(); i++) {
            API.DroneDynamics droneDynamic = droneDynamics.get(i);
            tableModel.addRow(new Object[]{
                    droneDynamic.drone,
                    droneDynamic.timestamp,
                    droneDynamic.speed,
                    droneDynamic.alignRoll,
                    droneDynamic.alignPitch,
                    droneDynamic.alignYaw,
                    droneDynamic.longitude,
                    droneDynamic.latitude,
                    droneDynamic.batteryStatus,
                    droneDynamic.lastSeen,
                    droneDynamic.status
            });
        }
    }

    public static void main(String[] args) {

        // Window
        JFrame frame = new JFrame();
        frame.setTitle("Drone Sim");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);


        //card layout
        CardLayout cardLayout = new CardLayout();
        JPanel cardPage = new JPanel(cardLayout);

        //loging
        JLabel websiteLabel = new JLabel("Website URL");
        JTextField website =  new JTextField(45);
        JLabel tokenLabel = new JLabel("Password");
        JTextField token =  new JTextField(55);
        JButton loginButton = new JButton("Login");
        JCheckBox  rememberMe = new JCheckBox("Save Login Data");
        rememberMe.setSelected(true);

        if (rememberMe.isSelected()) {
            website.setText("http://dronesim.facets-labs.com");
            token.setText("Token 06a36f0d16c34735ba23a08de0fd6bf9e4d81e52");
        }

        loginButton.setMnemonic(KeyEvent.VK_1);
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 1, 10, 10));
        formPanel.add(websiteLabel);
        formPanel.add(website);
        formPanel.add(tokenLabel);
        formPanel.add(token);
        formPanel.add(loginButton);
        formPanel.add(rememberMe);

        //center login
        JPanel center = new JPanel(new GridBagLayout());
        center.add(formPanel);

        JPanel loginPanel = new JPanel(new BorderLayout());
        loginPanel.add(center, BorderLayout.CENTER);

        cardPage.add(loginPanel, "login");
        cardLayout.show(cardPage, "login");

        //home
        JPanel homePanel = new JPanel(new BorderLayout());
        TitledBorder title = BorderFactory.createTitledBorder("Welcome to Drone Sim");
        title.setTitleJustification(TitledBorder.CENTER);
        homePanel.setBorder(title);
        cardPage.add(homePanel, "home");
        JButton droneCatalog = new JButton("Drone Catalog");
        droneCatalog.setMnemonic(KeyEvent.VK_1);
        droneCatalog.setMaximumSize(new Dimension(300, 50));
        JButton droneDashboard = new JButton("Drone Dashboard");
        droneDashboard.setMnemonic(KeyEvent.VK_1);
        droneDashboard.setMaximumSize(new Dimension(300, 50));
        JButton flightDynamics = new JButton("Flight Dynamics");
        flightDynamics.setMnemonic(KeyEvent.VK_1);
        flightDynamics.setMaximumSize(new Dimension(300, 50));


        //center buttons in home page
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.add(droneCatalog);
        buttonContainer.add(Box.createVerticalStrut(10));
        buttonContainer.add(droneDashboard);
        buttonContainer.add(Box.createVerticalStrut(10));
        buttonContainer.add(flightDynamics);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(buttonContainer);

        homePanel.add(centerPanel, BorderLayout.CENTER);

        //Drone Catalog Page
        JPanel catalog = new JPanel();
        cardPage.add(catalog, "droneCatalog");
        JButton catalogBackButton = new JButton("Back");
        catalogBackButton.setMnemonic(KeyEvent.VK_1);
        catalog.add(catalogBackButton);

        //Drone Dashboard Page
        JPanel dashboard = new JPanel();
        cardPage.add(dashboard, "droneDashboard");
        JButton dashboardBackButton = new JButton("Back");
        dashboardBackButton.setMnemonic(KeyEvent.VK_1);
        dashboard.add(dashboardBackButton);

        //Flight Dynamics Page
        JPanel flight = new JPanel();
        JButton flightBackButton = new JButton("Back");
        flightBackButton.setMnemonic(KeyEvent.VK_1);
        cardPage.add(flight, "droneDynamics");
        flight.add(flightBackButton);
        JButton nextPage = new JButton("next");
        nextPage.setMnemonic(KeyEvent.VK_1);
        JButton prevPage = new JButton("prev");
        prevPage.setMnemonic(KeyEvent.VK_1);
        flight.add(prevPage);
        flight.add(nextPage);


        //Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menu1 = new JMenu("something");
        menu1.setMnemonic(KeyEvent.VK_1);

        //Drone Table
        List<API.Drone> drones = API.getDrones();
        String [] DroneColumns = {"ID", "Drone Type", "Created", "Serial Number", "Carriage Weight", "Carriage Type"};
        Object[][] DroneData = new Object[drones.size()][DroneColumns.length];

        for (int i = 0; i < drones.size(); i++) {
            API.Drone drone = drones.get(i);
            DroneData[i] = new Object[]{
                    drone.id,
                    drone.droneType,
                    drone.created,
                    drone.serialNumber,
                    drone.carriageWeight,
                    drone.carriageType
            };
        }


        JTable DroneTable = new JTable(DroneData, DroneColumns);
        DroneTable.setRowSelectionAllowed(true);
        JScrollPane DroneScrollPane = new JScrollPane(DroneTable);
        DroneScrollPane.setPreferredSize(new Dimension(800, 600));
        catalog.add(DroneScrollPane, "droneCatalog");

        //DroneTypes Table
        List<API.DroneType> droneTypes = API.getDroneTypes();
        String [] DroneTypeColumns = {"ID", "Manufacturer", "Typename", "Weight", "Maximum Speed", "Battery Capacity", "Control Range", "Maximum Carriage"};
        Object[][] DroneTypeData = new Object[droneTypes.size()][DroneTypeColumns.length];

        for (int i = 0; i < droneTypes.size(); i++) {
            API.DroneType droneType = droneTypes.get(i);
            DroneTypeData[i] = new Object[]{
                    droneType.id,
                    droneType.manufacturer,
                    droneType.typename,
                    droneType.weight,
                    droneType.maxSpeed,
                    droneType.batteryCapacity,
                    droneType.controlRange,
                    droneType.maxCarriage
            };
        }

        JTable DroneTypeTable = new JTable(DroneTypeData, DroneTypeColumns);
        DroneTypeTable.setRowSelectionAllowed(true);
        JScrollPane DroneTypeScrollPane = new JScrollPane(DroneTypeTable);
        DroneTypeScrollPane.setPreferredSize(new Dimension(800, 600));
        dashboard.add(DroneTypeScrollPane, "droneDashboard");

        //DroneDynamics Table
        List<API.DroneDynamics> droneDynamics = API.getDroneDynamics(pageNum);
        String [] DroneDynamicsColumns = {"Drone", "Timestamp", "Speed", "Alignment Roll", "Alignment Pitch", "Alignment Yaw", "Longitude", "Latitude", "Battery Status", "Last Seen", "Status"};
        tableModel = new DefaultTableModel(DroneDynamicsColumns, 0);

        loadDynamicTableData();

        JTable DroneDynamicsTable = new JTable(tableModel);
        DroneDynamicsTable.setRowSelectionAllowed(true);
        JScrollPane DroneDynamicsScrollPane = new JScrollPane(DroneDynamicsTable);
        DroneDynamicsScrollPane.setPreferredSize(new Dimension(800, 600));
        flight.add(DroneDynamicsScrollPane, "droneDynamics");


        //Menu Items
        JMenuItem item1 = new JMenuItem("one");
        JMenuItem item2 = new JMenuItem("two");
        menu1.add(item1);
        menu1.add(item2);
        menuBar.add(menu1);

        //Event Listener
        droneCatalog.addActionListener(e -> cardLayout.show(cardPage, "droneCatalog"));
        droneDashboard.addActionListener(e -> cardLayout.show(cardPage, "droneDashboard"));
        flightDynamics.addActionListener(e -> cardLayout.show(cardPage, "droneDynamics"));
        catalogBackButton.addActionListener(e -> cardLayout.show(cardPage, "home"));
        dashboardBackButton.addActionListener(e -> cardLayout.show(cardPage, "home"));
        flightBackButton.addActionListener(e -> cardLayout.show(cardPage, "home"));
        nextPage.addActionListener(e -> {pageNum++;
            loadDynamicTableData();
        });
        prevPage.addActionListener(e -> {pageNum--;
            loadDynamicTableData();
        });
        loginButton.addActionListener(e -> {
            String web = website.getText();
            String tok = token.getText();
            /*
            if (rememberMe.isSelected()) {
                web = "http://dronesim.facets-labs.com";
                tok = "Token 06a36f0d16c34735ba23a08de0fd6bf9e4d81e52";
                website.setText("http://dronesim.facets-labs.com");
                token.setText("Token 06a36f0d16c34735ba23a08de0fd6bf9e4d81e52");
            }else{
                web = websiteLabel.getText();
                tok = tokenLabel.getText();
            }*/
            if(web.equals("http://dronesim.facets-labs.com") &&
                    tok.equals("Token 06a36f0d16c34735ba23a08de0fd6bf9e4d81e52")) {
                    cardLayout.show(cardPage, "home");
            }else{
                JOptionPane.showMessageDialog(cardPage, "Invalid Username or Password", "Error", JOptionPane.ERROR_MESSAGE );
                Object[] options = {"Aight", "My Bad"};
            }
        });



        //Frame organizer
        frame.setJMenuBar(menuBar);
        frame.add(cardPage);
        frame.setVisible(true);

    }
}