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
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        role
                );
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(StockManagerApp.RED + "Database error during login: " + e.getMessage() + StockManagerApp.RESET);
        } catch (IllegalArgumentException e) {
            System.out.println(StockManagerApp.RED + "Unknown role found in database." + StockManagerApp.RESET);
        }
        return null;
    }

    public boolean registerUser(String username, String password, Role role) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role.name());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println(StockManagerApp.RED + "Username already exists!" + StockManagerApp.RESET);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(StockManagerApp.RED + "Database error: " + e.getMessage() + StockManagerApp.RESET);
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
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        role
                ));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(StockManagerApp.RED + "Database error: " + e.getMessage() + StockManagerApp.RESET);
        }
        return users;
    }

    public boolean updateUserRole(int id, Role newRole) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newRole.name());
            pstmt.setInt(2, id);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(StockManagerApp.RED + "Database error: " + e.getMessage() + StockManagerApp.RESET);
        }
        return false;
    }
    
    public boolean updateUserPassword(int id, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setInt(2, id);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(StockManagerApp.RED + "Database error: " + e.getMessage() + StockManagerApp.RESET);
        }
        return false;
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(StockManagerApp.RED + "Database error: " + e.getMessage() + StockManagerApp.RESET);
        }
        return false;
    }
}
