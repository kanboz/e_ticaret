import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLOrderDAOTest {

    private static Connection conn;
    private static dao.MySQLOrderDAO dao;

    @BeforeAll
    public static void setup() throws Exception {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/eticaret", "root", "");
        dao = new dao.MySQLOrderDAO(conn);
    }

    @BeforeEach
    public void cleanTable() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM orders");
        }
    }

    @Test
    public void testInsertAndFetch() throws Exception {
        model.Order newOrder = new model.Order("Test Ürün",5);
        dao.insertOrder((model.Order) newOrder);

        List<model.Order> orders = dao.getAllOrders();
        assertEquals(1, orders.size());

        Order inserted = (Order) orders.get(0);
        assertEquals("Test Ürün", inserted.getClass());
        assertEquals(5, ((model.Order) inserted).getQuantity());
    }

    @AfterAll
    public static void closeConnection() throws Exception {
        if (conn != null && !conn.isClosed()) conn.close();
    }
}
