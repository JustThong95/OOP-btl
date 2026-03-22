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
    public void addProduct(String name, int categoryId, double price, int stockQuantity) {
        if (!categoryExists(categoryId)) {
            System.out.println("Error: Category with ID " + categoryId + " does not exist. Please create it first or use a valid Category ID.");
            return;
        }
        
        String sql = "INSERT INTO products (name, category_id, price, stock_quantity) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, categoryId);
            stmt.setDouble(3, price);
            stmt.setInt(4, stockQuantity);
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
        String sql = "SELECT * FROM products";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("--- All Products ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + " | Name: " + rs.getString("name") + 
                                   " | Price: $" + rs.getDouble("price") + " | Stock: " + rs.getInt("stock_quantity"));
            }
            System.out.println("--------------------");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error fetching products: " + e.getMessage());
        }
    }

    // Look up product based on category
    public void getProductsByCategory(int categoryId) {
        String sql = "SELECT * FROM products WHERE category_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("--- Products in Category ID " + categoryId + " ---");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.println("ID: " + rs.getInt("id") + " | Name: " + rs.getString("name") + 
                                       " | Price: $" + rs.getDouble("price") + " | Stock: " + rs.getInt("stock_quantity"));
                }
                if (!found) {
                    System.out.println("No products found for this category.");
                }
                System.out.println("-------------------------------------");
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
}
