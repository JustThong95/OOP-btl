package com.stockmanager.daos;

import com.stockmanager.db.DBConnection;
import com.stockmanager.models.Role;
import com.stockmanager.models.User;
import com.stockmanager.app.StockManagerApp; // for colors

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Role role = Role.valueOf(rs.getString("role").toUpperCase());
                return new User(
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        role
                );
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Database error during login: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown role found in database.");
        }
        return null;
    }

    private String generateNextId() {
        String sql = "SELECT id FROM users";
        int maxId = 0;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String idStr = rs.getString("id");
                if (idStr != null && idStr.startsWith("U")) {
                    try {
                        int num = Integer.parseInt(idStr.substring(1));
                        if (num > maxId) maxId = num;
                    } 
                    catch (NumberFormatException e) {
                    }
                }
            }
        } 
        catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return String.format("U%02d", maxId + 1);
    }

    public boolean registerUser(String username, String password, Role role) {
        String nextId = generateNextId();

        String sql = "INSERT INTO users (id, username, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nextId);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, role.name());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } 
        catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Username already exists!");
        } 
        catch (SQLException | ClassNotFoundException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return false;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Role role = Role.valueOf(rs.getString("role").toUpperCase());
                users.add(new User(
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        role
                ));
            }
        }
        catch (SQLException | ClassNotFoundException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return users;
    }

    public boolean updateUserRole(String id, Role newRole) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newRole.name());
            pstmt.setString(2, id);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } 
        catch (SQLException | ClassNotFoundException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return false;
    }
    
    public boolean updateUserPassword(String id, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setString(2, id);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } 
        catch (SQLException | ClassNotFoundException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        }
        catch (SQLException | ClassNotFoundException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return false;
    }
}
