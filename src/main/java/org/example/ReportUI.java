package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReportUI extends JFrame {
    private JLabel totalProductsLabel;
    private JLabel totalOrdersLabel;
    private JLabel totalStockLabel;

    public ReportUI() {
        setTitle("Raporlama Paneli");
        setSize(800, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadData();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        totalProductsLabel = new JLabel("Toplam Ürün: Yükleniyor...");
        totalOrdersLabel = new JLabel("Toplam Sipariş: Yükleniyor...");
        totalStockLabel = new JLabel("Toplam Stok: Yükleniyor...");

        statsPanel.add(totalProductsLabel);
        statsPanel.add(totalOrdersLabel);
        statsPanel.add(totalStockLabel);

        panel.add(statsPanel, BorderLayout.NORTH);

        // Grafik paneli
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.setValue(10, "Ürünler", "Elektronik");
        dataset.setValue(7, "Ürünler", "Kitap");
        dataset.setValue(5, "Ürünler", "Kozmetik");
        dataset.setValue(8, "Ürünler", "Giyim");

        JFreeChart barChart = ChartFactory.createBarChart(
                "Kategori Bazlı Ürün Sayısı",
                "Kategori",
                "Ürün Sayısı",
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(barChart);
        panel.add(chartPanel, BorderLayout.CENTER);

        add(panel);
    }

    private void loadData() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/eticaret", "root", "");
            Statement stmt = conn.createStatement();

            // Toplam ürün
            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM products");
            if (rs1.next()) {
                totalProductsLabel.setText("Toplam Ürün: " + rs1.getInt(1));
            }

            // Toplam sipariş
            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM orders");
            if (rs2.next()) {
                totalOrdersLabel.setText("Toplam Sipariş: " + rs2.getInt(1));
            }

            // Toplam stok
            ResultSet rs3 = stmt.executeQuery("SELECT SUM(stock) FROM products");
            if (rs3.next()) {
                totalStockLabel.setText("Toplam Stok: " + rs3.getInt(1));
            }

            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Veri çekme hatası: " + e.getMessage());
        }
    }
}
