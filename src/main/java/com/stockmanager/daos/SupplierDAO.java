package com.stockmanager.daos;

import com.stockmanager.db.DBConnection;
import com.stockmanager.models.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    private String generateNextId() {
        String sql = "SELECT id FROM suppliers";
        int maxId = 0;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String idStr = rs.getString("id");
                if (idStr != null && idStr.startsWith("S") && !idStr.startsWith("SF") && !idStr.startsWith("SU")) {
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
        return String.format("S%02d", maxId + 1);
    }

    public void addSupplier(String name, String contactInfo, String address) {
        String nextId = generateNextId();

        String sql = "INSERT INTO suppliers (id, name, contact_info, address) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nextId);
            stmt.setString(2, name);
            stmt.setString(3, contactInfo);
            stmt.setString(4, address);
            stmt.executeUpdate();
            System.out.println("Supplier added successfully with ID " + nextId + ".");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error adding supplier: " + e.getMessage());
        }
    }

    public void editSupplier(String id, String name, String contactInfo, String address) {
        String sql = "UPDATE suppliers SET name = ?, contact_info = ?, address = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, contactInfo);
            stmt.setString(3, address);
            stmt.setString(4, id);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Supplier updated successfully.");
            } else {
                System.out.println("Supplier with ID " + id + " not found.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error updating supplier: " + e.getMessage());
        }
    }

    public void deleteSupplier(String id) {
        String sql = "DELETE FROM suppliers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Supplier deleted successfully.");
            } else {
                System.out.println("Supplier with ID " + id + " not found.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error deleting supplier: " + e.getMessage());
        }
    }

    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers ORDER BY id ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                suppliers.add(new Supplier(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("contact_info"),
                        rs.getString("address")
                ));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error fetching suppliers: " + e.getMessage());
        }
        return suppliers;
    }
}
