import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        // Window
        JFrame frame = new JFrame();
        frame.setTitle("Drone Sim");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);


        //card layout
        CardLayout cardLayout = new CardLayout();
        JPanel cardPage = new JPanel(cardLayout);

        //loging
        JLabel userlabel = new JLabel("Username");
        JTextField username =  new JTextField(15);
        JLabel passwordlabel = new JLabel("Password");
        JPasswordField password =  new JPasswordField(15);
        JButton loginButton = new JButton("Login");
        loginButton.setMnemonic(KeyEvent.VK_1);
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 1, 10, 10));
        formPanel.add(userlabel);
        formPanel.add(username);
        formPanel.add(passwordlabel);
        formPanel.add(password);
        formPanel.add(loginButton);

        //center login

        JPanel center = new JPanel(new GridBagLayout());
        center.add(formPanel);

        JPanel loginPanel = new JPanel(new BorderLayout());
        loginPanel.add(center, BorderLayout.CENTER);

        cardPage.add(loginPanel, "login");
        cardLayout.show(cardPage, "login");


        //page 1
        JPanel page1 = new JPanel();
        cardPage.add(page1, "Page1");
        JButton start = new JButton("Click Me");
        start.setMnemonic(KeyEvent.VK_1);
        page1.add(start);

        //page 2
        JPanel page2 = new JPanel();
        cardPage.add(page2, "Page2");
        JButton back = new JButton("Back");
        back.setMnemonic(KeyEvent.VK_1);
        page2.add(back);

        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menu1 = new JMenu("something");
        menu1.setMnemonic(KeyEvent.VK_1);

        //Menu Items
        JMenuItem item1 = new JMenuItem("one");
        JMenuItem item2 = new JMenuItem("two");
        menu1.add(item1);
        menu1.add(item2);
        menuBar.add(menu1);

        //Event Listener
        start.addActionListener(e -> cardLayout.show(cardPage, "Page2"));
        back.addActionListener(e -> cardLayout.show(cardPage, "Page1"));
        loginButton.addActionListener(e -> {
            String user = username.getText();
            String pass = new String(password.getPassword());
            if(user.equals("something") && pass.equals("something")) {
                    cardLayout.show(cardPage, "Page1");
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