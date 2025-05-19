package dao;



import model.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLOrderDAO {
    private Connection connection;

    public MySQLOrderDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders (product_name, quantity, created_at) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, order.getProductName());
            stmt.setInt(2, order.getQuantity());
            stmt.setTimestamp(3, order.getCreatedAt());
            stmt.executeUpdate();
        }
    }

    public List<Order> getAllOrders() throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getTimestamp("created_at")
                );
                list.add(order);
            }
        }
        return list;
    }
}
