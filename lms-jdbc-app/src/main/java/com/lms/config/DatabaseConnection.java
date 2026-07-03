package com.lms.config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Database URL
    private static final String URL = "jdbc:postgresql://localhost:5432/lms_db";

    // PostgreSQL Username
    private static final String USER = "postgres";

    // PostgreSQL Password
    private static final String PASSWORD = "rahull5";

    // Static block loads the PostgreSQL Driver
    static {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL Driver Loaded Successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver Not Found!");
            e.printStackTrace();
        }
    }

    // Method to return Connection object
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}