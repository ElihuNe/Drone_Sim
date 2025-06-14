package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MainFrame extends JFrame {

   public MainFrame() {

       // Window
       setTitle("Drone Sim");
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       setSize(800, 600);

       // different layers
       CardLayout cardLayout = new CardLayout();
       JPanel cardPage = new JPanel(cardLayout);

       // taskbar
       JMenuBar menuBar = new JMenuBar();
       JMenu menu1 = new JMenu("something");
       menu1.setMnemonic(KeyEvent.VK_1);
   }
}
