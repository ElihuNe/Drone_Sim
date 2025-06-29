package app.ui;

import app.data.DataRepository;
import app.util.Constants;

import javax.swing.*;
import java.awt.*;


public class ConfigPanel extends JPanel {

    public ConfigPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        JLabel urlLabel = new JLabel("API URL:");
        JTextField urlField = new JTextField("http://dronesim.facets-labs.com");
        JLabel tokenLabel = new JLabel("Token:");
        JTextField tokenField = new JTextField("Token 06a36f0d16c34735ba23a08de0fd6bf9e4d81e52");

        JButton loginButton = new JButton("Login");
        JCheckBox rememberBox = new JCheckBox("Remember me");
        rememberBox.setSelected(true);

        formPanel.add(urlLabel);
        formPanel.add(urlField);
        formPanel.add(tokenLabel);
        formPanel.add(tokenField);
        formPanel.add(loginButton);
        formPanel.add(rememberBox);

        JPanel center = new JPanel(new GridBagLayout());
        center.add(formPanel);

        add(center, BorderLayout.CENTER);

        rememberBox.addActionListener(e -> {
            if (rememberBox.isSelected()) {
                urlField.setText(Constants.BASE_URL);
                tokenField.setText(Constants.TOKEN);
            }
        });

        loginButton.addActionListener(e -> {
            String url = urlField.getText().trim();
            String token = tokenField.getText().trim();
            Constants.BASE_URL = url;
            Constants.TOKEN = token;

            if (!url.isEmpty() && token.startsWith("Token")) {
                frame.showPanel("home");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid URL or Token.");
            }
        });
    }
}
