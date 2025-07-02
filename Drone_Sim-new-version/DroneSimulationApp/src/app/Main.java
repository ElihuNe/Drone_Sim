package app; 

import app.ui.MainFrame;

/**
 * Main class to start the application.
 */
public class Main {
	
	/**
     * Starts the GUI on the Event Dispatch Thread.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}

