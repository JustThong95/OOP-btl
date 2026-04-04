package com.stockmanager.app;

// .............. thêm vào
import com.stockmanager.daos.CustomerDAO;
import com.stockmanager.daos.ExportFormDAO;
import com.stockmanager.daos.ReportDAO;
import com.stockmanager.models.CustomerType;

import java.util.Scanner;

public class StockManagerApp {
    //..Thêm 
    private static final CustomerDAO customerDAO = new CustomerDAO();
    private static final ExportFormDAO exportFormDAO = new ExportFormDAO();
    private static final ReportDAO reportDAO = new ReportDAO();
    
public static void main(String[] args) {
    // .................................thêm
    if (role == com.stockmanager.models.Role.ADMIN || role == com.stockmanager.models.Role.EMPLOYEE) {
                System.out.println("  " + "[7]" + "  Customer Management");
                System.out.println("  " + "[8]" + "  Export Form (Outbound)");
                System.out.println("  " + "[9]" + "  Reports & Statistics");
            }
    System.out.println("  " + "[0]" + "  Logout");
            System.out.print(" Choose an option: ");
            
            String choice = scanner.nextLine();
            switch (choice) {
                case "7":
                    if (role == com.stockmanager.models.Role.ADMIN || role == com.stockmanager.models.Role.EMPLOYEE) {
                        customerMenu();
                    } else {
                        System.out.println(" Unauthorized.");
                    }
                    break;
                case "8":
                    if (role == com.stockmanager.models.Role.ADMIN || role == com.stockmanager.models.Role.EMPLOYEE) {
                        exportFormMenu();
                    } else {
                        System.out.println(" Unauthorized.");
                    }
                    break;
                case "9":
                    if (role == com.stockmanager.models.Role.ADMIN || role == com.stockmanager.models.Role.EMPLOYEE) {
                        reportsMenu();
                    } else {
                        System.out.println(" Unauthorized.");
                    }
                    break;
            }
            
            /// thêm phần cuối
            private static void customerMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + " CUSTOMER MANAGEMENT");
            System.out.println("  " + "[1]" + "  Add Customer (retail or dealer)");
            System.out.println("  " + "[2]" + "  Edit Customer");
            System.out.println("  " + "[3]" + "  Delete Customer");
            System.out.println("  " + "[4]" + "  List All Customers");
            System.out.println("  " + "[0]" + "  Back to Main Menu");
            System.out.print(" Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    System.out.print("Enter customer name: ");
                    String name = scanner.nextLine();
                    System.out.print("Type - [1] RETAIL  [2] DEALER: ");
                    CustomerType type = readCustomerType(scanner.nextLine());
                    if (type == null) {
                        System.out.println(" Invalid type.");
                        break;
                    }
                    System.out.print("Enter phone (optional): ");
                    String phone = scanner.nextLine();
                    System.out.print("Enter address (optional): ");
                    String address = scanner.nextLine();
                    customerDAO.addCustomer(name, type, phone, address);
                    break;
                case "2":
                    try {
                        System.out.print("Enter customer ID to edit: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter new name: ");
                        String newName = scanner.nextLine();
                        System.out.print("Type - [1] RETAIL  [2] DEALER: ");
                        CustomerType newType = readCustomerType(scanner.nextLine());
                        if (newType == null) {
                            System.out.println(" Invalid type.");
                            break;
                        }
                        System.out.print("Enter new phone: ");
                        String newPhone = scanner.nextLine();
                        System.out.print("Enter new address: ");
                        String newAddress = scanner.nextLine();
                        customerDAO.editCustomer(id, newName, newType, newPhone, newAddress);
                    } catch (NumberFormatException e) {
                        System.out.println(" Invalid input. ID must be a number.");
                    }
                    break;
                case "3":
                    try {
                        System.out.print("Enter customer ID to delete: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        customerDAO.deleteCustomer(id);
                    } catch (NumberFormatException e) {
                        System.out.println(" Invalid input. ID must be a number.");
                    }
                    break;
                case "4":
                    System.out.println("\n--- All Customers ---");
                    System.out.println(String.format("%-5s | %-20s | %-10s | %-15s | %-30s",
                            "ID", "Name", "Type", "Phone", "Address"));
                    System.out.println("------------------------------------------------------------------------------------------");
                    for (com.stockmanager.models.Customer c : customerDAO.getAllCustomers()) {
                        String ph = c.getPhone() != null ? c.getPhone() : "";
                        String ad = c.getAddress() != null ? c.getAddress() : "";
                        System.out.println(String.format("%-5d | %-20s | %-10s | %-15s | %-30s",
                                c.getId(), c.getName(), c.getCustomerType().name(), ph, ad));
                    }
                    System.out.println();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println(" Invalid choice.");
            }
        }
    }

    private static CustomerType readCustomerType(String input) {
        String t = input != null ? input.trim() : "";
        if ("1".equals(t)) {
            return CustomerType.RETAIL;
        }
        if ("2".equals(t)) {
            return CustomerType.DEALER;
        }
        return null;
    }

    private static void exportFormMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + " EXPORT FORM (OUTBOUND)");
            System.out.println("  " + "[1]" + "  Create Export Form");
            System.out.println("  " + "[2]" + "  Export History");
            System.out.println("  " + "[0]" + "  Back to Main Menu");
            System.out.print(" Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    try {
                        System.out.print("Enter customer name (must exist in Customer list): ");
                        String custName = scanner.nextLine();
                        System.out.print("Enter product name: ");
                        String prodName = scanner.nextLine();
                        System.out.print("Enter quantity to ship: ");
                        int qty = Integer.parseInt(scanner.nextLine());
                        exportFormDAO.addExportForm(custName, prodName, qty);
                    } catch (NumberFormatException e) {
                        System.out.println(" Invalid input. Quantity must be a number.");
                    }
                    break;
                case "2":
                    exportFormDAO.printExportHistory();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println(" Invalid choice.");
            }
        }
    }

    private static void reportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + " REPORTS & STATISTICS");
            System.out.println("  " + "[1]" + "  Import / export totals (all time)");
            System.out.println("  " + "[2]" + "  Import / export totals (date range)");
            System.out.println("  " + "[3]" + "  Best-selling products (by export qty)");
            System.out.println("  " + "[0]" + "  Back to Main Menu");
            System.out.print(" Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    reportDAO.printImportExportTotalsAllTime();
                    break;
                case "2":
                    System.out.print("Start date (YYYY-MM-DD): ");
                    String start = scanner.nextLine().trim();
                    System.out.print("End date (YYYY-MM-DD): ");
                    String end = scanner.nextLine().trim();
                    reportDAO.printImportExportTotalsBetween(start, end);
                    break;
                case "3":
                    try {
                        System.out.print("How many top products to show (default 10): ");
                        String n = scanner.nextLine().trim();
                        int limit = n.isEmpty() ? 10 : Integer.parseInt(n);
                        reportDAO.printTopSellingProducts(limit);
                    } catch (NumberFormatException e) {
                        System.out.println(" Invalid number.");
                    }
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println(" Invalid choice.");
            }
        }
    }
}
}