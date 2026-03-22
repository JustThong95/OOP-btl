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
        String sql = "SELECT * FROM categories";
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
}
