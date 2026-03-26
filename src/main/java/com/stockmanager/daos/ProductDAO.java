package com.stockmanager.daos;

import com.stockmanager.db.DBConnection;
import com.stockmanager.models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // Helper to check if a category exists
    private boolean categoryExists(int categoryId) {
        String sql = "SELECT id FROM categories WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException | ClassNotFoundException e) {
            return false;
        }
    }

    // Add product
    public void addProduct(Integer id, String name, int categoryId, double price, int stockQuantity) {
        if (!categoryExists(categoryId)) {
            System.out.println("Error: Category with ID " + categoryId + " does not exist. Please create it first or use a valid Category ID.");
            return;
        }
        
        String sql;
        if (id != null) {
            sql = "INSERT INTO products (id, name, category_id, price, stock_quantity) VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO products (name, category_id, price, stock_quantity) VALUES (?, ?, ?, ?)";
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (id != null) {
                stmt.setInt(1, id);
                stmt.setString(2, name);
                stmt.setInt(3, categoryId);
                stmt.setDouble(4, price);
                stmt.setInt(5, stockQuantity);
            } else {
                stmt.setString(1, name);
                stmt.setInt(2, categoryId);
                stmt.setDouble(3, price);
                stmt.setInt(4, stockQuantity);
            }
            
            stmt.executeUpdate();
            System.out.println("Product added successfully.");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error adding product: " + e.getMessage());
        }
    }

    // Update price
    public void updatePrice(int productId, double newPrice) {
        String sql = "UPDATE products SET price = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newPrice);
            stmt.setInt(2, productId);
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

    // Print product info
    public void printProduct(int productId) {
        String sql = "SELECT p.*, c.name as category_name FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.id WHERE p.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("--- Product Info ---");
                    System.out.println("ID: " + rs.getInt("id"));
                    System.out.println("Name: " + rs.getString("name"));
                    System.out.println("Category: " + rs.getString("category_name"));
                    System.out.println("Price: $" + rs.getDouble("price"));
                    System.out.println("Stock: " + rs.getInt("stock_quantity"));
                    System.out.println("--------------------");
                } else {
                    System.out.println("Product with ID " + productId + " not found.");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error printing product Info: " + e.getMessage());
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
                System.out.println(String.format("%-5d | %-20s | %-20s | $%-11.2f | %-10d", 
                        rs.getInt("id"), rs.getString("name"), categoryName, rs.getDouble("price"), rs.getInt("stock_quantity")));
            }
            System.out.println("-----------------------------------------------------------------------------");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error fetching products: " + e.getMessage());
        }
    }

    // Look up product based on category
    public void getProductsByCategory(int categoryId) {
        String sql = "SELECT * FROM products WHERE category_id = ? ORDER BY id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("--- Products in Category ID " + categoryId + " ---");
                System.out.println(String.format("%-5s | %-20s | %-12s | %-10s", "ID", "Name", "Price", "Stock"));
                System.out.println("----------------------------------------------------------");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.println(String.format("%-5d | %-20s | $%-11.2f | %-10d", 
                            rs.getInt("id"), rs.getString("name"), rs.getDouble("price"), rs.getInt("stock_quantity")));
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
    public void checkStock(int productId) {
        String sql = "SELECT name, stock_quantity FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Product: " + rs.getString("name"));
                    System.out.println("Stock Quantity: " + rs.getInt("stock_quantity"));
                } else {
                    System.out.println("Product with ID " + productId + " not found.");
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
            stmt.executeUpdate("ALTER TABLE products AUTO_INCREMENT = 1");
            System.out.println(rowsDeleted + " products successfully erased.");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error erasing products: " + e.getMessage());
        }
    }
}
