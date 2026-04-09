package com.stockmanager.daos;

import com.stockmanager.db.DBConnection;
import com.stockmanager.models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // Helper to check if a category exists
    private boolean categoryExists(String categoryId) {
        String sql = "SELECT id FROM categories WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException | ClassNotFoundException e) {
            return false;
        }
    }

    private String generateNextId() {
        String sql = "SELECT id FROM products";
        int maxId = 0;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String idStr = rs.getString("id");
                if (idStr != null && idStr.startsWith("P")) {
                    try {
                        int num = Integer.parseInt(idStr.substring(1));
                        if (num > maxId) maxId = num;
                    } catch (NumberFormatException e) {
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return String.format("P%02d", maxId + 1);
    }

    // Add product
    public void addProduct(String id, String name, String categoryId, double price, int stockQuantity) {
        if (!categoryExists(categoryId)) {
            System.out.println("Error: Category with ID " + categoryId + " does not exist. Please create it first or use a valid Category ID.");
            return;
        }
        
        String sql;
    
            sql = "INSERT INTO products (id, name, category_id, price, stock_quantity) VALUES (?, ?, ?, ?, ?)";
    
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (id == null) {
                id = generateNextId();
            }
            if (id != null) {
                stmt.setString(1, id);
                stmt.setString(2, name);
                stmt.setString(3, categoryId);
                stmt.setDouble(4, price);
                stmt.setInt(5, stockQuantity);
            } else {
                stmt.setString(2, name);
                stmt.setString(3, categoryId);
                stmt.setDouble(4, price);
                stmt.setInt(5, stockQuantity);
            }
            
            stmt.executeUpdate();
            System.out.println("Product added successfully.");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error adding product: " + e.getMessage());
        }
    }

    // Update price
    public void updatePrice(String productId, double newPrice) {
        String sql = "UPDATE products SET price = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newPrice);
            stmt.setString(2, productId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Product price updated successfully.");
            } else {
                System.out.println("Product with ID " + productId + " not found.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error updating product price: " + e.getMessage());
        }
    }

    // Delete product
    public void deleteProduct(String productId) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productId);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Product deleted successfully.");
            } else {
                System.out.println("Product with ID " + productId + " not found.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error deleting product: " + e.getMessage());
        }
    }

    // Print all products helper
    public void printAllProducts() {
        String sql = "SELECT p.*, c.name as category_name FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.id " +
                     "ORDER BY p.id ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("--- All Products ---");
            System.out.println(String.format("%-5s | %-20s | %-20s | %-12s | %-10s", "ID", "Name", "Category", "Price", "Stock"));
            System.out.println("-----------------------------------------------------------------------------");
            while (rs.next()) {
                String categoryName = rs.getString("category_name");
                if (categoryName == null) categoryName = "None";
                System.out.println(String.format("%-5s | %-20s | %-20s | $%-11.2f | %-10d", 
                        rs.getString("id"), rs.getString("name"), categoryName, rs.getDouble("price"), rs.getInt("stock_quantity")));
            }
            System.out.println("-----------------------------------------------------------------------------");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error fetching products: " + e.getMessage());
        }
    }

    // Look up product based on category
    public void getProductsByCategoryName(String categoryName) {
        String sql = "SELECT p.* FROM products p JOIN categories c ON p.category_id = c.id WHERE c.name = ? ORDER BY p.id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoryName);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("--- Products in Category " + categoryName + " ---");
                System.out.println(String.format("%-5s | %-20s | %-12s | %-10s", "ID", "Name", "Price", "Stock"));
                System.out.println("----------------------------------------------------------");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.println(String.format("%-5s | %-20s | $%-11.2f | %-10d", 
                            rs.getString("id"), rs.getString("name"), rs.getDouble("price"), rs.getInt("stock_quantity")));
                }
                if (!found) {
                    System.out.println("No products found for this category.");
                }
                System.out.println("----------------------------------------------------------");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error fetching products by category: " + e.getMessage());
        }
    }

    // Check stock quantity
    public void checkStock(String productName) {
        String sql = "SELECT name, stock_quantity FROM products WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Product: " + rs.getString("name"));
                    System.out.println("Stock Quantity: " + rs.getInt("stock_quantity"));
                } else {
                    System.out.println("Product with name '" + productName + "' not found.");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error checking stock: " + e.getMessage());
        }
    }

    // Erase all products
    public void deleteAllProducts() {
        String sql = "DELETE FROM products";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            int rowsDeleted = stmt.executeUpdate(sql);
            System.out.println(rowsDeleted + " products successfully erased.");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error erasing products: " + e.getMessage());
        }
    }
}
