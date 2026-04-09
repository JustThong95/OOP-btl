package com.stockmanager.daos;

import com.stockmanager.db.DBConnection;

import java.sql.*;

public class SupplyFormDAO {

    private String generateNextId() {
        String sql = "SELECT id FROM supply_forms";
        int maxId = 0;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String idStr = rs.getString("id");
                if (idStr != null && idStr.startsWith("SF")) {
                    try {
                        int num = Integer.parseInt(idStr.substring(2));
                        if (num > maxId) maxId = num;
                    } catch (NumberFormatException e) {
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return String.format("SF%02d", maxId + 1);
    }

    private String getSupplierIdByName(String name, Connection conn) throws SQLException {
        String sql = "SELECT id FROM suppliers WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("id");
                return null;
            }
        }
    }

    private String getProductIdByName(String name, Connection conn) throws SQLException {
        String sql = "SELECT id FROM products WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("id");
                return null;
            }
        }
    }

    private double getProductPriceAndCheckExists(String productId, Connection conn) throws SQLException {
        String sql = "SELECT price FROM products WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("price");
                }
                return -1; // -1 means doesn't exist
            }
        }
    }

    public void addSupplyForm(String supplierName, String productName, int quantity) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Enable transaction

            String supplierId = getSupplierIdByName(supplierName, conn);
            if (supplierId == null) {
                System.out.println("Error: Supplier '" + supplierName + "' does not exist.");
                conn.rollback();
                return;
            }

            String productId = getProductIdByName(productName, conn);
            if (productId == null) {
                System.out.println("Error: Product '" + productName + "' does not exist.");
                conn.rollback();
                return;
            }

            double price = getProductPriceAndCheckExists(productId, conn);
            if (price < 0) {
                System.out.println("Error: Product '" + productName + "' price lookup failed.");
                conn.rollback();
                return;
            }

            double totalPrice = price * quantity;

            String id = generateNextId();

            // Insert into supply_forms
            String insertSql = "INSERT INTO supply_forms (id, supplier_name, product_name, quantity, total_price) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setString(1, id);
                stmt.setString(2, supplierName);
                stmt.setString(3, productName);
                stmt.setInt(4, quantity);
                stmt.setDouble(5, totalPrice);
                stmt.executeUpdate();
            }

            // Update product stock
            String updateStockSql = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateStockSql)) {
                stmt.setInt(1, quantity);
                stmt.setString(2, productId);
                stmt.executeUpdate();
            }

            conn.commit();
            System.out.println("Supply form created successfully! Stock updated. Total price: $" + String.format("%.2f", totalPrice));

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error adding supply form: " + e.getMessage());
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

    public void printStorageHistory() {
        String sql = "SELECT id, quantity, total_price, created_date, supplier_name, product_name " +
                     "FROM supply_forms " +
                     "ORDER BY created_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("--- Storage History ---");
            System.out.println(String.format("%-5s | %-20s | %-20s | %-10s | %-12s | %-20s", "ID", "Supplier", "Product", "Qty", "Total Price", "Date"));
            System.out.println("-------------------------------------------------------------------------------------------------------");
            while (rs.next()) {
                System.out.println(String.format("%-5s | %-20s | %-20s | %-10d | $%-11.2f | %-20s", 
                        rs.getString("id"), 
                        rs.getString("supplier_name"), 
                        rs.getString("product_name"), 
                        rs.getInt("quantity"), 
                        rs.getDouble("total_price"),
                        rs.getTimestamp("created_date")));
            }
            System.out.println("-------------------------------------------------------------------------------------------------------");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error fetching storage history: " + e.getMessage());
        }
    }

    public void filterFormsByDate(String dateStr) {
        // Expected dateStr Format: YYYY-MM-DD
        String sql = "SELECT id, quantity, total_price, created_date, supplier_name, product_name " +
                     "FROM supply_forms " +
                     "WHERE DATE(created_date) = ? " +
                     "ORDER BY created_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dateStr);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("--- Storage History for " + dateStr + " ---");
                System.out.println(String.format("%-5s | %-20s | %-20s | %-10s | %-12s | %-20s", "ID", "Supplier", "Product", "Qty", "Total Price", "Date"));
                System.out.println("-------------------------------------------------------------------------------------------------------");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.println(String.format("%-5s | %-20s | %-20s | %-10d | $%-11.2f | %-20s", 
                            rs.getString("id"), 
                            rs.getString("supplier_name"), 
                            rs.getString("product_name"), 
                            rs.getInt("quantity"), 
                            rs.getDouble("total_price"),
                            rs.getTimestamp("created_date")));
                }
                if (!found) {
                    System.out.println("No records found for this date.");
                }
                System.out.println("-------------------------------------------------------------------------------------------------------");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error filtering storage history: " + e.getMessage());
        }
    }
}
