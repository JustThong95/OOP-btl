package com.stockmanager.daos;

import com.stockmanager.db.DBConnection;
import com.stockmanager.models.Customer;
import com.stockmanager.models.CustomerType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private String generateNextId() {
        String sql = "SELECT id FROM customers";
        int maxId = 0;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String idStr = rs.getString("id");
                if (idStr != null && idStr.startsWith("C") && !idStr.startsWith("CA") && !idStr.startsWith("CU")) {
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
        return String.format("C%02d", maxId + 1);
    }

    public void addCustomer(String name, CustomerType customerType, String phone, String address) {
        String nextId = generateNextId();

        String sql = "INSERT INTO customers (id, name, customer_type, phone, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nextId);
            stmt.setString(2, name);
            stmt.setString(3, customerType.name());
            stmt.setString(4, phone != null && !phone.isBlank() ? phone : null);
            stmt.setString(5, address != null && !address.isBlank() ? address : null);
            stmt.executeUpdate();
            System.out.println("Customer added successfully with ID " + nextId + ".");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error adding customer: " + e.getMessage());
        }
    }

    public void editCustomer(String id, String name, CustomerType customerType, String phone, String address) {
        String sql = "UPDATE customers SET name = ?, customer_type = ?, phone = ?, address = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, customerType.name());
            stmt.setString(3, phone != null && !phone.isBlank() ? phone : null);
            stmt.setString(4, address != null && !address.isBlank() ? address : null);
            stmt.setString(5, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Customer updated successfully.");
            } else {
                System.out.println("Customer with ID " + id + " not found.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error updating customer: " + e.getMessage());
        }
    }

    public void deleteCustomer(String id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Customer deleted successfully.");
            } else {
                System.out.println("Customer with ID " + id + " not found.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
        }
    }

    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY id ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error fetching customers: " + e.getMessage());
        }
        return list;
    }

    public Customer getCustomerById(String id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error fetching customer: " + e.getMessage());
        }
        return null;
    }

    private static Customer mapRow(ResultSet rs) throws SQLException {
        String typeStr = rs.getString("customer_type");
        CustomerType type = typeStr != null ? CustomerType.valueOf(typeStr) : CustomerType.RETAIL;
        return new Customer(
                rs.getString("id"),
                rs.getString("name"),
                type,
                rs.getString("phone"),
                rs.getString("address")
        );
    }
}
