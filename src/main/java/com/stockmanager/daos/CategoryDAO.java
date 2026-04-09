package com.stockmanager.daos;

import com.stockmanager.db.DBConnection;
import com.stockmanager.models.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    
    private String generateNextId() {
        String sql = "SELECT id FROM categories";
        int maxId = 0;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String idStr = rs.getString("id");
                if (idStr != null && idStr.startsWith("CA")) {
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
        return String.format("CA%02d", maxId + 1);
    }

    // Add category
    public void addCategory(String name) {
        String id = generateNextId();
        String sql = "INSERT INTO categories (id, name) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.executeUpdate();
            System.out.println("Category added successfully.");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error adding category: " + e.getMessage());
        }
    }

    // Edit category
    public void editCategory(String id, String newName) {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setString(2, id);
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
    public void eraseCategory(String id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
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
                categories.add(new Category(rs.getString("id"), rs.getString("name")));
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
            }
            
            String sqlDeleteCategories = "DELETE FROM categories";
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(sqlDeleteCategories);
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
