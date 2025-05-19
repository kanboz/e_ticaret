package org.example;

import javax.swing.*;
import java.awt.*;

public class UserUI extends JFrame {

    public UserUI() {
        setTitle("E-Ticaret Yönetim Paneli");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        // Başlık etiketi
        JLabel titleLabel = new JLabel("E-Ticaret Yönetim Paneli", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(30, 60, 90));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Butonlar
        JButton productButton = new JButton("📦 Ürünleri Yönet");
        JButton orderButton = new JButton("🛒 Siparişleri Görüntüle");
        JButton reportButton = new JButton("📊 Raporları Görüntüle");

        JButton[] buttons = {productButton, orderButton, reportButton};
        for (JButton btn : buttons) {
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            btn.setBackground(new Color(70, 130, 180));
            btn.setForeground(Color.BLACK);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(250, 50));
        }

        // Butonlara action listener
        productButton.addActionListener(e -> new ProductUI().setVisible(true));
        orderButton.addActionListener(e -> new OrderUI().setVisible(true));
        reportButton.addActionListener(e -> new ReportUI().setVisible(true));

        // Butonları içeren panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));
        buttonPanel.setBackground(Color.WHITE);

        buttonPanel.add(productButton);
        buttonPanel.add(orderButton);
        buttonPanel.add(reportButton);

        // Ana panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
    }
}
