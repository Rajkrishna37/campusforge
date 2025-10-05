package com.loginapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/campus_forge";
        String user = "root";        // your DB username
        String password = "root";    // your DB password
        return DriverManager.getConnection(url, user, password);
    }
}
