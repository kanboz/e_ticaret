package model;



import java.sql.Timestamp;

public class Order {
    private int id;
    private String productName;
    private int quantity;
    private Timestamp createdAt;

    public Order(int id, String productName, int quantity, Timestamp createdAt) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }

    public Order(String productName, int quantity, Timestamp createdAt) {
        this.productName = productName;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }

    public Order() {

    }

    public Order(String testÜrün, int i) {

    }

    public int getId() { return id; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
}
