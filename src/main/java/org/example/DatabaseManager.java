package org.example;



import io.github.cdimascio.dotenv.Dotenv;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseManager {
    private String dbType;
    private Connection mysqlConn;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    private Dotenv dotenv;

    public DatabaseManager() {
        dotenv = Dotenv.load();
        dbType = dotenv.get("DB_TYPE").toLowerCase();

        if(dbType.equals("mysql")){
            connectMySQL();
        } else if(dbType.equals("mongodb")){
            connectMongoDB();
        } else {
            throw new RuntimeException("Geçersiz DB_TYPE değeri: " + dbType);
        }
    }

    private void connectMySQL() {
        try {
            String url = dotenv.get("MYSQL_URL");
            String user = dotenv.get("MYSQL_USER");
            String pass = dotenv.get("MYSQL_PASS");
            mysqlConn = DriverManager.getConnection(url, user, pass);
            System.out.println("MySQL bağlantısı başarılı.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectMongoDB() {
        try {
            String uri = dotenv.get("MONGO_URI");
            String dbName = dotenv.get("MONGO_DB");
            mongoClient = MongoClients.create(uri);
            mongoDatabase = mongoClient.getDatabase(dbName);
            System.out.println("MongoDB bağlantısı başarılı.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDbType() {
        return dbType;
    }

    public Connection getMysqlConn() {
        return mysqlConn;
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }
}
