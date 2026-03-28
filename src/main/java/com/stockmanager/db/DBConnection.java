package com.stockmanager.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Modify these basic credentials if your local config differs
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/stock_manager?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "thongchu95"; 

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // Ensure JDBC driver is registered
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
