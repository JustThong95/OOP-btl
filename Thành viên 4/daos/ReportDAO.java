package com.stockmanager.daos;

import com.stockmanager.db.DBConnection;

import java.sql.*;

public class ReportDAO {

    public void printImportExportTotalsAllTime() {
        try (Connection conn = DBConnection.getConnection()) {
            double importTotal = sumColumn(conn, "SELECT COALESCE(SUM(total_price), 0) AS v FROM supply_forms");
            double exportTotal = sumColumn(conn, "SELECT COALESCE(SUM(total_price), 0) AS v FROM export_forms");
            System.out.println("\n--- Revenue / Value Summary (all time) ---");
            System.out.println("Total import value (supply forms):  $" + String.format("%.2f", importTotal));
            System.out.println("Total export revenue (sales):       $" + String.format("%.2f", exportTotal));
            System.out.println("Net (export - import):              $" + String.format("%.2f", exportTotal - importTotal));
            System.out.println("------------------------------------------");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error loading totals: " + e.getMessage());
        }
    }

    public void printImportExportTotalsBetween(String startDate, String endDate) {
        String supplySql = "SELECT COALESCE(SUM(total_price), 0) AS v FROM supply_forms "
                + "WHERE DATE(created_date) BETWEEN ? AND ?";
        String exportSql = "SELECT COALESCE(SUM(total_price), 0) AS v FROM export_forms "
                + "WHERE DATE(created_date) BETWEEN ? AND ?";
        try (Connection conn = DBConnection.getConnection()) {
            double importTotal;
            try (PreparedStatement stmt = conn.prepareStatement(supplySql)) {
                stmt.setString(1, startDate);
                stmt.setString(2, endDate);
                importTotal = sumPrepared(stmt);
            }
            double exportTotal;
            try (PreparedStatement stmt = conn.prepareStatement(exportSql)) {
                stmt.setString(1, startDate);
                stmt.setString(2, endDate);
                exportTotal = sumPrepared(stmt);
            }
            System.out.println("\n--- Summary from " + startDate + " to " + endDate + " ---");
            System.out.println("Import value (supply):   $" + String.format("%.2f", importTotal));
            System.out.println("Export revenue:          $" + String.format("%.2f", exportTotal));
            System.out.println("Net:                     $" + String.format("%.2f", exportTotal - importTotal));
            System.out.println("--------------------------------------------------");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error loading period totals: " + e.getMessage());
        }
    }

    public void printTopSellingProducts(int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        String sql = "SELECT product_name, SUM(quantity) AS qty_sold, SUM(total_price) AS revenue "
                + "FROM export_forms GROUP BY product_name ORDER BY qty_sold DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n--- Top selling products (by export quantity) ---");
                System.out.println(String.format("%-5s | %-30s | %-12s | %-14s", "Rank", "Product", "Qty sold", "Revenue"));
                System.out.println("----------------------------------------------------------------------------");
                int rank = 1;
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    System.out.println(String.format("%-5d | %-30s | %-12d | $%-13.2f",
                            rank++,
                            truncate(rs.getString("product_name"), 30),
                            rs.getLong("qty_sold"),
                            rs.getDouble("revenue")));
                }
                if (!any) {
                    System.out.println("No export data yet.");
                }
                System.out.println("----------------------------------------------------------------------------");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error loading best sellers: " + e.getMessage());
        }
    }

    private static double sumColumn(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("v");
            }
            return 0;
        }
    }

    private static double sumPrepared(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("v");
            }
            return 0;
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }
}
