package app; 

import app.ui.MainFrame;

public class Main {
	
	// Starts the GUI on the Event Dispatch Thread
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
