package com.stockmanager.app;

import com.stockmanager.daos.CategoryDAO;
import com.stockmanager.daos.ProductDAO;
import com.stockmanager.daos.CustomerDAO;
import com.stockmanager.daos.ExportFormDAO;
import com.stockmanager.daos.ReportDAO;
import com.stockmanager.models.CustomerType;

import java.util.Scanner;

public class StockManagerApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final CategoryDAO categoryDAO = new CategoryDAO();
    private static final ProductDAO productDAO = new ProductDAO();
    private static final com.stockmanager.daos.SupplierDAO supplierDAO = new com.stockmanager.daos.SupplierDAO();
    private static final com.stockmanager.daos.SupplyFormDAO supplyFormDAO = new com.stockmanager.daos.SupplyFormDAO();
    private static final CustomerDAO customerDAO = new CustomerDAO();
    private static final ExportFormDAO exportFormDAO = new ExportFormDAO();
    private static final ReportDAO reportDAO = new ReportDAO();

    public static void main(String[] args) {
        boolean exit = false;

        System.out.println("\n" + "==========================================");
        System.out.println("         STOCK MANAGEMENT SYSTEM          ");
        System.out.println("==========================================");

        while (!exit) {
            if (AccountManager.getCurrentUser() == null) {
                if (!AccountManager.showAuthMenu()) {
                    System.out.println("\n Exiting the system. Goodbye!\n");
                    break;
                }
            }

            com.stockmanager.models.Role role = AccountManager.getCurrentUser().getRole();

            System.out.println("\n" + " MAIN MENU (" + role.name() + ")");

            if (role == com.stockmanager.models.Role.ADMIN || role == com.stockmanager.models.Role.EMPLOYEE) {
                System.out.println("  " + "[1]" + "  Category Management");
                System.out.println("  " + "[2]" + "  Product Management");
                System.out.println("  " + "[3]" + "  Looking Up Products");
            }
            if (role == com.stockmanager.models.Role.ADMIN) {
                System.out.println("  " + "[4]" + "  Supplier Management");
            }

            System.out.println("  " + "[5]" + "  Supply Form Management");

            if (role == com.stockmanager.models.Role.ADMIN) {
                System.out.println("  " + "[6]" + "  User & Staff Management");
            }

            if (role == com.stockmanager.models.Role.ADMIN || role == com.stockmanager.models.Role.EMPLOYEE) {
                System.out.println("  " + "[7]" + "  Customer Management");
                System.out.println("  " + "[8]" + "  Export Form (Outbound)");
                System.out.println("  " + "[9]" + "  Reports & Statistics");
            }

            System.out.println("  " + "[0]" + "  Logout");
            System.out.print(" Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    if (role == com.stockmanager.models.Role.ADMIN || role == com.stockmanager.models.Role.EMPLOYEE)
                        categoryMenu();
                    else
                        System.out.println(" Unauthorized.");
                    break;
                case "2":
                    if (role == com.stockmanager.models.Role.ADMIN || role == com.stockmanager.models.Role.EMPLOYEE)
                        productMenu();
                    else
                        System.out.println(" Unauthorized.");
                    break;
                case "3":
                    if (role == com.stockmanager.models.Role.ADMIN || role == com.stockmanager.models.Role.EMPLOYEE)
                        lookupMenu();
                    else
                        System.out.println(" Unauthorized.");
                    break;
                case "4":
                    if (role == com.stockmanager.models.Role.ADMIN)
                        supplierMenu();
                    else
                        System.out.println(" Unauthorized.");
                    break;
                case "5":
                    supplyFormMenu();
                    break;
                case "6":
                    if (role == com.stockmanager.models.Role.ADMIN)
                        AccountManager.staffManagementMenu();
                    else
                        System.out.println(" Unauthorized.");
                    break;
                case "7":
                    if (role == com.stockmanager.models.Role.ADMIN || role == com.stockmanager.models.Role.EMPLOYEE)
                        customerMenu();
                    else
                        System.out.println(" Unauthorized.");
                    break;
                case "8":
                    if (role == com.stockmanager.models.Role.ADMIN || role == com.stockmanager.models.Role.EMPLOYEE)
                        exportFormMenu();
                    else
                        System.out.println(" Unauthorized.");
                    break;
                case "9":
                    if (role == com.stockmanager.models.Role.ADMIN || role == com.stockmanager.models.Role.EMPLOYEE)
                        reportsMenu();
                    else
                        System.out.println(" Unauthorized.");
                    break;
                case "0":
                    AccountManager.logout();
                    break;
                default:
                    System.out.println(" Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void categoryMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + " CATEGORY MANAGEMENT");
            System.out.println("  " + "[1]" + "  Add Category");
            System.out.println("  " + "[2]" + "  Edit Category");
            System.out.println("  " + "[3]" + "  Erase Category");
            System.out.println("  " + "[4]" + "  List All Categories");
            System.out.println("  " + "[5]" + "  Erase ALL Categories and Products");
            System.out.println("  " + "[0]" + "  Back to Main Menu");
            System.out.print(" Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    System.out.print("Enter category name: ");
                    String name = scanner.nextLine();
                    categoryDAO.addCategory(name);
                    break;
                case "2":
                    System.out.print("Enter category ID to edit: ");
                    String idToEdit = scanner.nextLine().trim();
                    System.out.print("Enter new category name: ");
                    String newName = scanner.nextLine();
                    categoryDAO.editCategory(idToEdit, newName);
                    break;
                case "3":
                    System.out.println("\n--- All Categories ---");
                    System.out.println(String.format("%-5s | %-20s", "ID", "Name"));
                    System.out.println("------------------------------");
                    for (com.stockmanager.models.Category c : categoryDAO.getAllCategories()) {
                        System.out.println(String.format("%-5s | %-20s", c.getId(), c.getName()));
                    }
                    System.out.println();
                    System.out.print("Enter category ID to erase: ");
                    String idToErase = scanner.nextLine().trim();
                    categoryDAO.eraseCategory(idToErase);
                    break;
                case "4":
                    System.out.println("\n--- All Categories ---");
                    System.out.println(String.format("%-5s | %-20s", "ID", "Name"));
                    System.out.println("------------------------------");
                    for (com.stockmanager.models.Category c : categoryDAO.getAllCategories()) {
                        System.out.println(String.format("%-5s | %-20s", c.getId(), c.getName()));
                    }
                    System.out.println();
                    break;
                case "5":
                    System.out.print(" Are you sure you want to erase ALL categories and products? (yes/no): ");
                    String confirmCat = scanner.nextLine();
                    if (confirmCat.equalsIgnoreCase("yes")) {
                        categoryDAO.deleteAllCategoriesAndProducts();
                    } else {
                        System.out.println("Operation cancelled.");
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

    private static void productMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + " PRODUCT MANAGEMENT");
            System.out.println("  " + "[1]" + "  Add Product");
            System.out.println("  " + "[2]" + "  Update Price");
            System.out.println("  " + "[3]" + "  Delete Product");
            System.out.println("  " + "[4]" + "  Print All Products");
            System.out.println("  " + "[5]" + "  Erase ALL Products");
            System.out.println("  " + "[0]" + "  Back to Main Menu");
            System.out.print(" Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    try {
                        System.out.print("Enter product name: ");
                        String name = scanner.nextLine();

                        System.out.println("\n--- Available Categories ---");
                        System.out.println(String.format("%-5s | %-20s", "ID", "Name"));
                        for (com.stockmanager.models.Category c : categoryDAO.getAllCategories()) {
                            System.out.println(String.format("%-5s | %-20s", c.getId(), c.getName()));
                        }
                        System.out.println();

                        System.out.print("Enter category ID: ");
                        String categoryId = scanner.nextLine().trim();
                        System.out.print("Enter price: ");
                        double price = Double.parseDouble(scanner.nextLine());
                        System.out.print("Enter stock quantity: ");
                        int stock = Integer.parseInt(scanner.nextLine());
                        productDAO.addProduct(name, categoryId, price, stock);
                    } catch (NumberFormatException e) {
                        System.out.println(" Invalid number format entered.");
                    }
                    break;
                case "2":
                    try {
                        System.out.print("Enter product ID to update: ");
                        String updateId = scanner.nextLine().trim();
                        System.out.print("Enter new price: ");
                        double newPrice = Double.parseDouble(scanner.nextLine());
                        productDAO.updatePrice(updateId, newPrice);
                    } catch (NumberFormatException e) {
                        System.out.println(" Invalid number format entered.");
                    }
                    break;
                case "3":
                    productDAO.printAllProducts();
                    System.out.print("Enter product ID to delete: ");
                    String deleteProdId = scanner.nextLine().trim();
                    productDAO.deleteProduct(deleteProdId);
                    break;
                case "4":
                    productDAO.printAllProducts();
                    break;
                case "5":
                    System.out.print(" Are you sure you want to erase ALL products? (yes/no): ");
                    String confirmProd = scanner.nextLine();
                    if (confirmProd.equalsIgnoreCase("yes")) {
                        productDAO.deleteAllProducts();
                    } else {
                        System.out.println("Operation cancelled.");
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

    private static void lookupMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + " LOOKING UP PRODUCTS");
            System.out.println("  " + "[1]" + "  Look up product based on category");
            System.out.println("  " + "[2]" + "  Check stock quantity");
            System.out.println("  " + "[0]" + "  Back to Main Menu");
            System.out.print(" Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    System.out.print("Enter category name: ");
                    String catName = scanner.nextLine();
                    productDAO.getProductsByCategoryName(catName);
                    break;
                case "2":
                    System.out.print("Enter product name: ");
                    String prodName = scanner.nextLine();
                    productDAO.checkStock(prodName);
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println(" Invalid choice.");
            }
        }
    }

    private static void supplierMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + " SUPPLIER MANAGEMENT");
            System.out.println("  " + "[1]" + "  Add Supplier");
            System.out.println("  " + "[2]" + "  Edit Supplier");
            System.out.println("  " + "[3]" + "  Delete Supplier");
            System.out.println("  " + "[4]" + "  List All Suppliers");
            System.out.println("  " + "[0]" + "  Back to Main Menu");
            System.out.print(" Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    System.out.print("Enter supplier name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter contact info: ");
                    String contact = scanner.nextLine();
                    System.out.print("Enter address: ");
                    String address = scanner.nextLine();
                    supplierDAO.addSupplier(name, contact, address);
                    break;
                case "2":
                    System.out.print("Enter supplier ID to edit: ");
                    String id = scanner.nextLine().trim();
                    System.out.print("Enter new supplier name: ");
                    String newName = scanner.nextLine();
                    System.out.print("Enter new contact info: ");
                    String newContact = scanner.nextLine();
                    System.out.print("Enter new address: ");
                    String newAddress = scanner.nextLine();
                    supplierDAO.editSupplier(id, newName, newContact, newAddress);
                    break;
                case "3":
                    System.out.println("\n--- All Suppliers ---");
                    System.out.println(
                            String.format("%-5s | %-20s | %-20s | %-30s", "ID", "Name", "Contact Info", "Address"));
                    System.out.println(
                            "----------------------------------------------------------------------------------");
                    for (com.stockmanager.models.Supplier s : supplierDAO.getAllSuppliers()) {
                        System.out.println(String.format("%-5s | %-20s | %-20s | %-30s", s.getId(), s.getName(),
                                s.getContactInfo(), s.getAddress()));
                    }
                    System.out.println();
                    System.out.print("Enter supplier ID to delete: ");
                    String deleteId = scanner.nextLine().trim();
                    supplierDAO.deleteSupplier(deleteId);
                    break;
                case "4":
                    System.out.println("\n--- All Suppliers ---");
                    System.out.println(
                            String.format("%-5s | %-20s | %-20s | %-30s", "ID", "Name", "Contact Info", "Address"));
                    System.out.println(
                            "----------------------------------------------------------------------------------");
                    for (com.stockmanager.models.Supplier s : supplierDAO.getAllSuppliers()) {
                        System.out.println(String.format("%-5s | %-20s | %-20s | %-30s", s.getId(), s.getName(),
                                s.getContactInfo(), s.getAddress()));
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

    private static void supplyFormMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + " SUPPLY FORM MANAGEMENT");
            System.out.println("  " + "[1]" + "  Create Supply Form");
            System.out.println("  " + "[2]" + "  Check Storage History");
            System.out.println("  " + "[3]" + "  Filter History by Date");
            System.out.println("  " + "[0]" + "  Back to Main Menu");
            System.out.print(" Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    try {
                        System.out.print("Enter supplier name: ");
                        String suppName = scanner.nextLine();
                        System.out.print("Enter product name: ");
                        String prodName = scanner.nextLine();
                        System.out.print("Enter quantity: ");
                        int qty = Integer.parseInt(scanner.nextLine());
                        supplyFormDAO.addSupplyForm(suppName, prodName, qty);
                    } catch (NumberFormatException e) {
                        System.out.println(" Invalid input. Quantity must be a number.");
                    }
                    break;
                case "2":
                    supplyFormDAO.printStorageHistory();
                    break;
                case "3":
                    System.out.print("Enter date (YYYY-MM-DD): ");
                    String dateStr = scanner.nextLine();
                    supplyFormDAO.filterFormsByDate(dateStr);
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println(" Invalid choice.");
            }
        }
    }

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
                    System.out.print("Enter customer ID to edit: ");
                    String id = scanner.nextLine().trim();
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
                    break;
                case "3":
                    System.out.println("\n--- All Customers ---");
                    System.out.println(String.format("%-5s | %-20s | %-10s | %-15s | %-30s",
                            "ID", "Name", "Type", "Phone", "Address"));
                    System.out.println(
                            "------------------------------------------------------------------------------------------");
                    for (com.stockmanager.models.Customer c : customerDAO.getAllCustomers()) {
                        String ph = c.getPhone() != null ? c.getPhone() : "";
                        String ad = c.getAddress() != null ? c.getAddress() : "";
                        System.out.println(String.format("%-5s | %-20s | %-10s | %-15s | %-30s",
                                c.getId(), c.getName(), c.getCustomerType().name(), ph, ad));
                    }
                    System.out.println();
                    System.out.print("Enter customer ID to delete: ");
                    String deleteCustId = scanner.nextLine().trim();
                    customerDAO.deleteCustomer(deleteCustId);
                    break;
                case "4":
                    System.out.println("\n--- All Customers ---");
                    System.out.println(String.format("%-5s | %-20s | %-10s | %-15s | %-30s",
                            "ID", "Name", "Type", "Phone", "Address"));
                    System.out.println(
                            "------------------------------------------------------------------------------------------");
                    for (com.stockmanager.models.Customer c : customerDAO.getAllCustomers()) {
                        String ph = c.getPhone() != null ? c.getPhone() : "";
                        String ad = c.getAddress() != null ? c.getAddress() : "";
                        System.out.println(String.format("%-5s | %-20s | %-10s | %-15s | %-30s",
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
        if ("1".equals(t))
            return CustomerType.RETAIL;
        if ("2".equals(t))
            return CustomerType.DEALER;
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
