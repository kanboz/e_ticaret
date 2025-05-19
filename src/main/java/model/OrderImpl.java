package model;

public class OrderImpl extends Order {
    private String productName;
    private int quantity;

    public OrderImpl(String productName, int quantity) {
        super(  );
        this.productName = productName;
        this.quantity = quantity;
    }

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    // Diğer methodları da implement et
}
