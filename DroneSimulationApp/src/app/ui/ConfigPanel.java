package app.ui;

import app.data.DataRepository;
import app.util.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * ConfigPanel allows the user to enter API URL and token before accessing the application.
 * Handles simple login and saving of connection data.
 */
public class ConfigPanel extends JPanel {

    /**
     * Creates the config panel with URL/token fields and login logic.
     *
     * @param frame reference to the main frame for switching views
     */
    public ConfigPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        // Form layout with 5 rows (URL, Token, Login button, checkbox)
        JPanel formPanel = new JPanel(new GridLayout(5, 1, 10, 10));

        JLabel urlLabel = new JLabel("API URL:");
        JTextField urlField = new JTextField("http://dronesim.facets-labs.com");

        JLabel tokenLabel = new JLabel("Token:");
        JTextField tokenField = new JTextField("Token 06a36f0d16c34735ba23a08de0fd6bf9e4d81e52");

        JButton loginButton = new JButton("Login");

        JCheckBox rememberBox = new JCheckBox("Remember me");
        rememberBox.setSelected(true); // Checked by default

        // Add components to form panel
        formPanel.add(urlLabel);
        formPanel.add(urlField);
        formPanel.add(tokenLabel);
        formPanel.add(tokenField);
        formPanel.add(loginButton);
        formPanel.add(rememberBox);

        // Center the form using GridBagLayout
        JPanel center = new JPanel(new GridBagLayout());
        center.add(formPanel);
        add(center, BorderLayout.CENTER);

        // Checkbox logic: restore saved constants
        rememberBox.addActionListener(e -> {
            if (rememberBox.isSelected()) {
                urlField.setText(Constants.BASE_URL);
                tokenField.setText(Constants.TOKEN);
            }
        });

        // Login button logic: validate and save user input
        loginButton.addActionListener(e -> {
            String url = urlField.getText().trim();
            String token = tokenField.getText().trim();

            // Save to constants (used globally)
            Constants.BASE_URL = url;
            Constants.TOKEN = token;

            // Validate format before proceeding
            if (!url.isEmpty() && token.startsWith("Token")) {
                frame.showPanel("home");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid URL or Token.");
            }
        });
    }
}