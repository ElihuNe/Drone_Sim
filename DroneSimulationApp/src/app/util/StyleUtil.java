package app.util;

import javax.swing.*;
import java.awt.*;


public class StyleUtil {

    public static void applyTitleFont(JLabel label) {
        label.setFont(new Font("Arial", Font.BOLD, 18));
    }

    public static void centerComponent(JPanel panel, JComponent child) {
        panel.setLayout(new GridBagLayout());
        panel.add(child);
    }

    public static void setPadding(JPanel panel, int top, int left, int bottom, int right) {
        panel.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
    }
}

