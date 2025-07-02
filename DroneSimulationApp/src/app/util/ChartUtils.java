package app.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.util.Map;

public class ChartUtils {

    public static JPanel createBarChart(Map<String, Double> data, String title, String catAxis, String valAxis) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), "Type", entry.getKey());
        }
        JFreeChart chart = ChartFactory.createBarChart(
                title,
                catAxis,
                valAxis,
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        // Tooltips
        BarRenderer renderer = (BarRenderer) chart.getCategoryPlot().getRenderer();
        renderer.setDefaultToolTipGenerator(
                new StandardCategoryToolTipGenerator("{1}: {2}", java.text.NumberFormat.getInstance())
        );

        // X-Achsen-Beschriftungen entfernen
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelsVisible(false);

        return new ChartPanel(chart);
    }

    public static JPanel createBarChartInt(Map<String, Integer> data, String title, String catAxis, String valAxis) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), "Type", entry.getKey());
        }
        JFreeChart chart = ChartFactory.createBarChart(
                title,
                catAxis,
                valAxis,
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        // Tooltips
        BarRenderer renderer = (BarRenderer) chart.getCategoryPlot().getRenderer();
        renderer.setDefaultToolTipGenerator(
                new StandardCategoryToolTipGenerator("{1}: {2}", java.text.NumberFormat.getInstance())
        );

        // X-Achsen-Beschriftungen entfernen
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelsVisible(false);

        return new ChartPanel(chart);
    }

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

