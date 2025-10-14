package com.loginapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() {
        String url = "jdbc:mysql://localhost:3306/campus_forge";
        String user = "root";        // your DB username
        String password = "root";    // your DB password

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
           
            return null;
        }
    }
}
