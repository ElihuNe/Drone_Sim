package app.util;

import javax.swing.*;
import java.awt.*;

/**
 * Utility class for styling and layout helpers used in the GUI.
 * Contains reusable static methods to improve visual consistency.
 */
public class StyleUtil {

    /**
     * Sets a bold title font on a JLabel.
     * Useful for panel headers or section titles.
     *
     * @param label the label to style
     */
    public static void applyTitleFont(JLabel label) {
        label.setFont(new Font("Arial", Font.BOLD, 18));
    }

    /**
     * Centers a component inside a panel using GridBagLayout.
     * This clears any previous layout manager.
     *
     * @param panel the parent panel
     * @param child the component to center
     */
    public static void centerComponent(JPanel panel, JComponent child) {
        panel.setLayout(new GridBagLayout());
        panel.add(child);
    }

    /**
     * Sets empty padding around a panel using an EmptyBorder.
     * Creates spacing inside the panel’s boundaries.
     *
     * @param panel  the panel to set padding on
     * @param top    padding at the top (pixels)
     * @param left   padding at the left (pixels)
     * @param bottom padding at the bottom (pixels)
     * @param right  padding at the right (pixels)
     */
    public static void setPadding(JPanel panel, int top, int left, int bottom, int right) {
        panel.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
    }
}