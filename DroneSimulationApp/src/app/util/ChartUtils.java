package app.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.util.Map;

public class ChartUtils {

    public static JPanel createBarChart(Map<String, Double> data, String title, String catAxis, String valAxis) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((key, value) -> dataset.addValue(value, catAxis, key));
        JFreeChart chart = ChartFactory.createBarChart(title, catAxis, valAxis, dataset);
        return new ChartPanel(chart);
    }

    public static JPanel createBarChartInt(Map<String, Integer> data, String title, String catAxis, String valAxis) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((key, value) -> dataset.addValue(value, catAxis, key));
        JFreeChart chart = ChartFactory.createBarChart(title, catAxis, valAxis, dataset);
        return new ChartPanel(chart);
    }

    public static JPanel createPieChart(Map<String, Long> data, String title) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        data.forEach(dataset::setValue);
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
        return new ChartPanel(chart);
    }
}

