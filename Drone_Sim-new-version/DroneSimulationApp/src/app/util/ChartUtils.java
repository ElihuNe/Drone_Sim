package app.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.util.Map;

/**
 * Utility class to create different types of charts for the GUI.
 * Supports bar and pie charts using JFreeChart.
 */
public class ChartUtils {

    /**
     * Creates a bar chart from string-double map data.
     * Each entry in the map represents a category and its numeric value.
     *
     * @param data    values to plot (category → value)
     * @param title   chart title
     * @param catAxis category axis label (x-axis)
     * @param valAxis value axis label (y-axis)
     * @return a JPanel containing the bar chart
     */
    public static JPanel createBarChart(Map<String, Double> data, String title, String catAxis, String valAxis) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((key, value) -> dataset.addValue(value, catAxis, key));
        JFreeChart chart = ChartFactory.createBarChart(title, catAxis, valAxis, dataset);
        return new ChartPanel(chart);
    }

    /**
     * Creates a bar chart from string-integer map data.
     * Useful for counts (e.g., top drone types).
     *
     * @param data    values to plot (category → value)
     * @param title   chart title
     * @param catAxis category axis label
     * @param valAxis value axis label
     * @return a JPanel containing the bar chart
     */
    public static JPanel createBarChartInt(Map<String, Integer> data, String title, String catAxis, String valAxis) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((key, value) -> dataset.addValue(value, catAxis, key));
        JFreeChart chart = ChartFactory.createBarChart(title, catAxis, valAxis, dataset);
        return new ChartPanel(chart);
    }

    /**
     * Creates a pie chart from string-long map data.
     * Typically used for percentage distributions (e.g., battery ranges).
     *
     * @param data  values to plot (label → value)
     * @param title chart title
     * @return a JPanel containing the pie chart
     */
    public static JPanel createPieChart(Map<String, Long> data, String title) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        data.forEach(dataset::setValue);
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
        return new ChartPanel(chart);
    }
}
