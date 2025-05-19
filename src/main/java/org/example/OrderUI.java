package org.example;// package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.io.*;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class OrderUI extends JFrame {
    private JTable orderTable;
    private DefaultTableModel tableModel;

    private Connection mysqlConn;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> orderCollection;

    private String dbType;

    public OrderUI() {
        setTitle("Siparişler");
        setSize(800, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        loadEnv();
        initUI();
        connectToDatabase();
        loadOrders();
    }

    private void loadEnv() {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream(".env")) {
            props.load(input);
            dbType = props.getProperty("DB_TYPE", "mysql").toLowerCase();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, ".env dosyası okunamadı, default mysql kullanılacak.");
            dbType = "mysql";
        }
    }

    private void initUI() {
        tableModel = new DefaultTableModel(new String[]{"Kaynak", "ID", "Ürün Adı", "Miktar", "Tarih"}, 0);
        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);

        JButton btnEkle = new JButton("Ekle");
        JButton btnGuncelle = new JButton("Güncelle");
        JButton btnSil = new JButton("Sil");
        JButton btnYenile = new JButton("Yenile");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnEkle);
        buttonPanel.add(btnGuncelle);
        buttonPanel.add(btnSil);
        buttonPanel.add(btnYenile);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        btnEkle.addActionListener(e -> siparisEkle());
        btnGuncelle.addActionListener(e -> siparisGuncelle());
        btnSil.addActionListener(e -> siparisSil());
        btnYenile.addActionListener(e -> loadOrders());
    }

    private void connectToDatabase() {
        try {
            Properties props = new Properties();
            try (InputStream input = new FileInputStream(".env")) {
                props.load(input);
            }

            if ("mysql".equalsIgnoreCase(dbType)) {
                String url = props.getProperty("MYSQL_URL", "jdbc:mysql://localhost:3306/eticaret");
                String user = props.getProperty("MYSQL_USER", "root");
                String pass = props.getProperty("MYSQL_PASS", "");
                mysqlConn = DriverManager.getConnection(url, user, pass);
            } else if ("mongodb".equalsIgnoreCase(dbType)) {
                String mongoUri = props.getProperty("MONGO_URI", "mongodb://localhost:27017");
                String mongoDbName = props.getProperty("MONGO_DB", "eticaret");
                mongoClient = MongoClients.create(mongoUri);
                mongoDatabase = mongoClient.getDatabase(mongoDbName);
                orderCollection = mongoDatabase.getCollection("orders");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Veritabanı bağlantı hatası: " + e.getMessage());
        }
    }

    private void loadOrders() {
        tableModel.setRowCount(0);
        if ("mysql".equalsIgnoreCase(dbType)) {
            try (Statement stmt = mysqlConn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT * FROM orders");
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            "MySQL",
                            rs.getInt("id"),
                            rs.getString("product_name"),
                            rs.getInt("quantity"),
                            rs.getTimestamp("created_at")
                    });
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "MySQL siparişleri yükleme hatası: " + e.getMessage());
            }
        } else if ("mongodb".equalsIgnoreCase(dbType)) {
            try {
                for (Document doc : orderCollection.find()) {
                    tableModel.addRow(new Object[]{
                            "MongoDB",
                            doc.getInteger("id"),
                            doc.getString("product_name"),
                            doc.getInteger("quantity"),
                            doc.getDate("created_at")
                    });
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "MongoDB siparişleri yükleme hatası: " + e.getMessage());
            }
        }
    }

    private void siparisEkle() {
        String productName = JOptionPane.showInputDialog(this, "Ürün adı:");
        if (productName == null) return;

        String quantityStr = JOptionPane.showInputDialog(this, "Miktar:");
        if (quantityStr == null) return;

        try {
            int quantity = Integer.parseInt(quantityStr);
            java.util.Date now = new java.util.Date();

            if ("mysql".equalsIgnoreCase(dbType)) {
                String sql = "INSERT INTO orders (product_name, quantity, created_at) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = mysqlConn.prepareStatement(sql)) {
                    stmt.setString(1, productName);
                    stmt.setInt(2, quantity);
                    stmt.setTimestamp(3, new Timestamp(now.getTime()));
                    stmt.executeUpdate();
                }
            } else if ("mongodb".equalsIgnoreCase(dbType)) {
                int newId = (int) (orderCollection.countDocuments() + 1);
                Document doc = new Document("id", newId)
                        .append("product_name", productName)
                        .append("quantity", quantity)
                        .append("created_at", now);
                orderCollection.insertOne(doc);
            }

            loadOrders();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Geçerli bir miktar girin.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ekleme hatası: " + ex.getMessage());
        }
    }

    private void siparisGuncelle() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) return;

        int id = (int) tableModel.getValueAt(selectedRow, 1);
        String newProductName = JOptionPane.showInputDialog(this, "Yeni ürün adı:", tableModel.getValueAt(selectedRow, 2));
        if (newProductName == null) return;

        String newQuantityStr = JOptionPane.showInputDialog(this, "Yeni miktar:", tableModel.getValueAt(selectedRow, 3));
        if (newQuantityStr == null) return;

        try {
            int newQuantity = Integer.parseInt(newQuantityStr);

            if ("mysql".equalsIgnoreCase(dbType)) {
                String sql = "UPDATE orders SET product_name = ?, quantity = ? WHERE id = ?";
                try (PreparedStatement stmt = mysqlConn.prepareStatement(sql)) {
                    stmt.setString(1, newProductName);
                    stmt.setInt(2, newQuantity);
                    stmt.setInt(3, id);
                    stmt.executeUpdate();
                }
            } else if ("mongodb".equalsIgnoreCase(dbType)) {
                orderCollection.updateOne(Filters.eq("id", id),
                        new Document("$set", new Document("product_name", newProductName)
                                .append("quantity", newQuantity)));
            }

            loadOrders();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Güncelleme hatası: " + ex.getMessage());
        }
    }

    private void siparisSil() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) return;

        int id = (int) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, "Bu siparişi silmek istediğinizden emin misiniz?", "Sil", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if ("mysql".equalsIgnoreCase(dbType)) {
                String sql = "DELETE FROM orders WHERE id = ?";
                try (PreparedStatement stmt = mysqlConn.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
            } else if ("mongodb".equalsIgnoreCase(dbType)) {
                orderCollection.deleteOne(Filters.eq("id", id));
            }

            loadOrders();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Silme hatası: " + ex.getMessage());
        }
    }
}
