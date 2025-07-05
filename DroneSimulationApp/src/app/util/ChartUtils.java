package app.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.util.Map;

/**
 * Utility class for creating different types of charts
 * used across the application UI.
 *
 * Centralizes chart styles and rendering logic
 * to avoid duplicate code in UI panels.
 */
public class ChartUtils {

    /**
     * Creates a bar chart panel based on key-value pairs.
     * Automatically wraps the chart into a ChartPanel for use in Swing.
     *
     * @param data                 map where key = category label, value = numeric value
     * @param title                chart title
     * @param categoryAxisLabel    label for the category axis (X)
     * @param valueAxisLabel       label for the value axis (Y)
     * @return JPanel containing the rendered bar chart
     */
    public static JPanel createBarChart(Map<String, Double> data,
                                        String title,
                                        String categoryAxisLabel,
                                        String valueAxisLabel) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), title, entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                title,
                categoryAxisLabel,
                valueAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        // Reduce bar width if only one column exists
        if (dataset.getColumnCount() == 1) {
            renderer.setMaximumBarWidth(0.05);
        }

        // Remove axis tick labels and titles if axis label is empty
        if (categoryAxisLabel.isEmpty()) {
            plot.getDomainAxis().setTickLabelsVisible(false);
            plot.getDomainAxis().setLabel("");
        }

        if (valueAxisLabel.isEmpty()) {
            plot.getRangeAxis().setTickLabelsVisible(false);
            plot.getRangeAxis().setLabel("");
        }

        // Custom tooltip: show only the category label (e.g. drone name)
        renderer.setDefaultToolTipGenerator((dataset1, row, column) ->
                dataset1.getColumnKey(column).toString());

        return new ChartPanel(chart);
    }

    /**
     * Creates a bar chart panel with integer values.
     * Useful for count data instead of floating-point measurements.
     *
     * @param data                 map where key = category label, value = integer count
     * @param title                chart title
     * @param categoryAxisLabel    label for the category axis (X)
     * @param valueAxisLabel       label for the value axis (Y)
     * @return JPanel containing the rendered bar chart
     */
    public static JPanel createBarChartInt(Map<String, Integer> data,
                                           String title,
                                           String categoryAxisLabel,
                                           String valueAxisLabel) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), title, entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                title,
                categoryAxisLabel,
                valueAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        if (dataset.getColumnCount() == 1) {
            renderer.setMaximumBarWidth(0.05);
        }

        if (categoryAxisLabel.isEmpty()) {
            plot.getDomainAxis().setTickLabelsVisible(false);
            plot.getDomainAxis().setLabel("");
        }

        if (valueAxisLabel.isEmpty()) {
            plot.getRangeAxis().setTickLabelsVisible(false);
            plot.getRangeAxis().setLabel("");
        }

        // Custom tooltip: show only the category label
        renderer.setDefaultToolTipGenerator((dataset1, row, column) ->
                dataset1.getColumnKey(column).toString());

        return new ChartPanel(chart);
    }

    /**
     * Creates a pie chart panel from a data map.
     *
     * @param data   map where key = category label, value = count or metric
     * @param title  chart title
     * @return JPanel containing the pie chart
     */
    public static JPanel createPieChart(Map<String, Long> data, String title) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        for (Map.Entry<String, Long> entry : data.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                title,
                dataset,
                true,
                true,
                false
        );

        return new ChartPanel(chart);
    }
}
