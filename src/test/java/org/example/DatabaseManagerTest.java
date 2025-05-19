package org.example;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerTest {

    @Test
    public void testDatabaseConnection() {
        DatabaseManager dbManager = new DatabaseManager();

        String dbType = dbManager.getDbType();
        assertNotNull(dbType, "DB_TYPE null olamaz");

        if (dbType.equals("mysql")) {
            Connection conn = dbManager.getMysqlConn();
            assertNotNull(conn, "MySQL bağlantısı null olmamalı");
            try {
                assertFalse(conn.isClosed(), "MySQL bağlantısı kapalı olmamalı");
            } catch (Exception e) {
                fail("MySQL bağlantısı kontrolünde hata: " + e.getMessage());
            }
        } else if (dbType.equals("mongodb")) {
            assertNotNull(dbManager.getMongoDatabase(), "MongoDB bağlantısı null olmamalı");
        } else {
            fail("Geçersiz DB_TYPE: " + dbType);
        }
    }
}
