package com.stockmanager.daos;

import com.stockmanager.db.DBConnection;
import com.stockmanager.models.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    
    // Add category
    public void addCategory(String name) {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            System.out.println("Category added successfully.");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error adding category: " + e.getMessage());
        }
    }

    // Edit category
    public void editCategory(int id, String newName) {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setInt(2, id);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Category updated successfully.");
            } else {
                System.out.println("Category with ID " + id + " not found.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error editing category: " + e.getMessage());
        }
    }

    // Erase category
    public void eraseCategory(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Category erased successfully.");
            } else {
                System.out.println("Category with ID " + id + " not found.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error erasing category: " + e.getMessage());
        }
    }
    
    // Helper to list categories
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY id ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             
            while (rs.next()) {
                categories.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
        }
        return categories;
    }

    // Erase all categories and their associated products
    public void deleteAllCategoriesAndProducts() {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Enable transaction
            
            String sqlDeleteProducts = "DELETE FROM products";
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(sqlDeleteProducts);
                stmt.executeUpdate("ALTER TABLE products AUTO_INCREMENT = 1");
            }
            
            String sqlDeleteCategories = "DELETE FROM categories";
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(sqlDeleteCategories);
                stmt.executeUpdate("ALTER TABLE categories AUTO_INCREMENT = 1");
            }
            
            conn.commit();
            System.out.println("All categories and their associated products erased successfully.");
        } catch (SQLException | ClassNotFoundException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { }
            }
            System.err.println("Error erasing all categories: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { }
            }
        }
    }
}
