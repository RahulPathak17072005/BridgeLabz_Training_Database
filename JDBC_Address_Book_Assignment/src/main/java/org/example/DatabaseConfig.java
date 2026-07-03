package org.example;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();

    static {
        try {
            InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("db.properties");

            if (input == null) {
                throw new RuntimeException("db.properties file not found!");
            }

            properties.load(input);


            Class.forName("org.postgresql.Driver");

            System.out.println("PostgreSQL Driver Loaded Successfully!");

        } catch (Exception e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    public static Connection getConnection() throws SQLException {

        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        return DriverManager.getConnection(url, username, password);
    }

    // Testing Method
    public static void main(String[] args) {

        try (Connection con = getConnection()) {

            if (con != null) {
                System.out.println("Database Connected Successfully!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
