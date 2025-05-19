


package org.example;

import com.mongodb.client.*;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class ProductUI extends JFrame {
    private JTextField nameField, priceField, stockField;
    private JButton addButton, updateButton, deleteButton, refreshButton;
    JTable productTable;
    private DefaultTableModel tableModel;

    private Connection mysqlConn;
    private MongoCollection<Document> mongoCollection;

    public ProductUI() {
        setTitle("Ürün Yönetimi");
        setSize(700, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        connectToDatabases();
        loadProducts();
    }

    private void initUI() {
        nameField = new JTextField(10);
        priceField = new JTextField(5);
        stockField = new JTextField(5);
        addButton = new JButton("Ekle");
        updateButton = new JButton("Güncelle");
        deleteButton = new JButton("Sil");
        refreshButton = new JButton("Yenile");

        JPanel formPanel = new JPanel();
        formPanel.add(new JLabel("Ad: "));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Fiyat: "));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Stok: "));
        formPanel.add(stockField);
        formPanel.add(addButton);
        formPanel.add(updateButton);
        formPanel.add(deleteButton);
        formPanel.add(refreshButton);

        tableModel = new DefaultTableModel(new String[]{"ID", "Ad", "Fiyat", "Stok"}, 0);
        productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        refreshButton.addActionListener(e -> loadProducts());

        productTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = productTable.getSelectedRow();
                nameField.setText(tableModel.getValueAt(row, 1).toString());
                priceField.setText(tableModel.getValueAt(row, 2).toString());
                stockField.setText(tableModel.getValueAt(row, 3).toString());
            }
        });
    }

    private void connectToDatabases() {
        try {
            mysqlConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/eticaret", "root", "");
            MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase mongoDb = mongoClient.getDatabase("eticaret");
            mongoCollection = mongoDb.getCollection("products");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Veritabanı bağlantı hatası: " + e.getMessage());
        }
    }

    void addProduct() {
        String name = nameField.getText();
        double price = Double.parseDouble(priceField.getText());
        int stock = Integer.parseInt(stockField.getText());

        try {
            PreparedStatement ps = mysqlConn.prepareStatement("INSERT INTO products (name, price, stock) VALUES (?, ?, ?)");
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, stock);
            ps.executeUpdate();

            mongoCollection.insertOne(new Document("name", name).append("price", price).append("stock", stock));

            JOptionPane.showMessageDialog(this, "Ürün eklendi.");
            loadProducts();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ekleme hatası: " + e.getMessage());
        }
    }

    private void updateProduct() {
        int row = productTable.getSelectedRow();
        if (row == -1) return;

        int id = (int) tableModel.getValueAt(row, 0);
        String name = nameField.getText();
        double price = Double.parseDouble(priceField.getText());
        int stock = Integer.parseInt(stockField.getText());

        try {
            PreparedStatement ps = mysqlConn.prepareStatement("UPDATE products SET name=?, price=?, stock=? WHERE id=?");
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, stock);
            ps.setInt(4, id);
            ps.executeUpdate();

            mongoCollection.updateOne(new Document("name", name),
                    new Document("$set", new Document("price", price).append("stock", stock)));

            JOptionPane.showMessageDialog(this, "Ürün güncellendi.");
            loadProducts();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Güncelleme hatası: " + e.getMessage());
        }
    }

    private void deleteProduct() {
        int row = productTable.getSelectedRow();
        if (row == -1) return;

        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);

        try {
            PreparedStatement ps = mysqlConn.prepareStatement("DELETE FROM products WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();

            mongoCollection.deleteOne(new Document("name", name));

            JOptionPane.showMessageDialog(this, "Ürün silindi.");
            loadProducts();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Silme hatası: " + e.getMessage());
        }
    }

    void loadProducts() {
        try {
            tableModel.setRowCount(0);
            Statement stmt = mysqlConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM products");

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                row.add(rs.getDouble("price"));
                row.add(rs.getInt("stock"));
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Yükleme hatası: " + e.getMessage());
        }
    }
}