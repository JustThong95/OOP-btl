package com.stockmanager.daos;

import com.stockmanager.db.DBConnection;

import java.sql.*;

public class ExportFormDAO {

    private int getCustomerIdByName(String name, Connection conn) throws SQLException {
        String sql = "SELECT id FROM customers WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                return -1;
            }
        }
    }

    private int getProductIdByName(String name, Connection conn) throws SQLException {
        String sql = "SELECT id FROM products WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                return -1;
            }
        }
    }

    private double getProductPrice(int productId, Connection conn) throws SQLException {
        String sql = "SELECT price FROM products WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("price");
                }
                return -1;
            }
        }
    }

    /**
     * Creates an export slip: validates customer and product, checks stock, inserts row and decrements stock.
     */
    public void addExportForm(String customerName, String productName, int quantity) {
        if (quantity <= 0) {
            System.out.println("Error: Quantity must be positive.");
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            if (getCustomerIdByName(customerName, conn) == -1) {
                System.out.println("Error: Customer '" + customerName + "' does not exist.");
                conn.rollback();
                return;
            }

            int productId = getProductIdByName(productName, conn);
            if (productId == -1) {
                System.out.println("Error: Product '" + productName + "' does not exist.");
                conn.rollback();
                return;
            }

            double price = getProductPrice(productId, conn);
            if (price < 0) {
                System.out.println("Error: Product price lookup failed.");
                conn.rollback();
                return;
            }

            String decSql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?";
            try (PreparedStatement stmt = conn.prepareStatement(decSql)) {
                stmt.setInt(1, quantity);
                stmt.setInt(2, productId);
                stmt.setInt(3, quantity);
                int updated = stmt.executeUpdate();
                if (updated == 0) {
                    System.out.println("Error: Insufficient stock for '" + productName + "' or product unavailable.");
                    conn.rollback();
                    return;
                }
            }

            double totalPrice = price * quantity;
            String insertSql = "INSERT INTO export_forms (customer_name, product_name, quantity, total_price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setString(1, customerName);
                stmt.setString(2, productName);
                stmt.setInt(3, quantity);
                stmt.setDouble(4, totalPrice);
                stmt.executeUpdate();
            }

            conn.commit();
            System.out.println("Export form created successfully! Stock reduced. Total revenue: $"
                    + String.format("%.2f", totalPrice));

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error adding export form: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back: " + ex.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    public void printExportHistory() {
        String sql = "SELECT id, customer_name, product_name, quantity, total_price, created_date "
                + "FROM export_forms ORDER BY created_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("--- Export History ---");
            System.out.println(String.format("%-5s | %-20s | %-20s | %-10s | %-12s | %-20s",
                    "ID", "Customer", "Product", "Qty", "Total Price", "Date"));
            System.out.println("-------------------------------------------------------------------------------------------------------");
            while (rs.next()) {
                System.out.println(String.format("%-5d | %-20s | %-20s | %-10d | $%-11.2f | %-20s",
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("total_price"),
                        rs.getTimestamp("created_date")));
            }
            System.out.println("-------------------------------------------------------------------------------------------------------");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error fetching export history: " + e.getMessage());
        }
    }
}
