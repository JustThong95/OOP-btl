package com.stockmanager.app;

import com.stockmanager.daos.CategoryDAO;
import com.stockmanager.daos.ProductDAO;

import java.util.Scanner;

public class StockManagerApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final CategoryDAO categoryDAO = new CategoryDAO();
    private static final ProductDAO productDAO = new ProductDAO();
    private static final com.stockmanager.daos.SupplierDAO supplierDAO = new com.stockmanager.daos.SupplierDAO();
    private static final com.stockmanager.daos.SupplyFormDAO supplyFormDAO = new com.stockmanager.daos.SupplyFormDAO();

    // ANSI Escape Codes for Colors
    public static final String RESET = "\u001B[0m";
    public static final String CYAN = "\u001B[36m";
    public static final String YELLOW = "\u001B[33m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String PURPLE = "\u001B[35m";
    public static final String BOLD = "\u001B[1m";

    public static void main(String[] args) {
        boolean exit = false;
        
        System.out.println("\n" + CYAN + BOLD + "==========================================" + RESET);
        System.out.println(PURPLE + BOLD + "      ✨ STOCK MANAGEMENT SYSTEM ✨      " + RESET);
        System.out.println(CYAN + BOLD + "==========================================" + RESET);
        
        while (!exit) {
            System.out.println("\n" + YELLOW + BOLD + "▶ MAIN MENU" + RESET);
            System.out.println("  " + CYAN + "[1]" + RESET + " 🗂️  Category Management");
            System.out.println("  " + CYAN + "[2]" + RESET + " 📦 Product Management");
            System.out.println("  " + CYAN + "[3]" + RESET + " 🔍 Looking Up Products");
            System.out.println("  " + CYAN + "[4]" + RESET + " 🏢 Supplier Management");
            System.out.println("  " + CYAN + "[5]" + RESET + " 📝 Supply Form Management");
            System.out.println("  " + RED + "[0]" + RESET + " ❌ Exit");
            System.out.print(BOLD + "👉 Choose an option: " + RESET);
            
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    categoryMenu();
                    break;
                case "2":
                    productMenu();
                    break;
                case "3":
                    lookupMenu();
                    break;
                case "4":
                    supplierMenu();
                    break;
                case "5":
                    supplyFormMenu();
                    break;
                case "0":
                    exit = true;
                    System.out.println(GREEN + BOLD + "\n👋 Exiting the system. Goodbye!\n" + RESET);
                    break;
                default:
                    System.out.println(RED + "❌ Invalid choice. Please try again." + RESET);
            }
        }
        scanner.close();
    }

    private static void categoryMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + PURPLE + BOLD + "▶ CATEGORY MANAGEMENT" + RESET);
            System.out.println("  " + CYAN + "[1]" + RESET + " ➕ Add Category");
            System.out.println("  " + CYAN + "[2]" + RESET + " ✏️  Edit Category");
            System.out.println("  " + CYAN + "[3]" + RESET + " 🗑️  Erase Category");
            System.out.println("  " + CYAN + "[4]" + RESET + " 📋 List All Categories");
            System.out.println("  " + RED + "[5]" + RESET + " 🗑️  Erase ALL Categories and Products");
            System.out.println("  " + YELLOW + "[0]" + RESET + " 🔙 Back to Main Menu");
            System.out.print(BOLD + "👉 Choose an option: " + RESET);

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    System.out.print(YELLOW + "Enter category name: " + RESET);
                    String name = scanner.nextLine();
                    categoryDAO.addCategory(name);
                    break;
                case "2":
                    System.out.print(YELLOW + "Enter category ID to edit: " + RESET);
                    try {
                        int idToEdit = Integer.parseInt(scanner.nextLine());
                        System.out.print(YELLOW + "Enter new category name: " + RESET);
                        String newName = scanner.nextLine();
                        categoryDAO.editCategory(idToEdit, newName);
                    } catch (NumberFormatException e) {
                        System.out.println(RED + "❌ Invalid input. ID must be a number." + RESET);
                    }
                    break;
                case "3":
                    System.out.print(YELLOW + "Enter category ID to erase: " + RESET);
                    try {
                        int idToErase = Integer.parseInt(scanner.nextLine());
                        categoryDAO.eraseCategory(idToErase);
                    } catch (NumberFormatException e) {
                        System.out.println(RED + "❌ Invalid input. ID must be a number." + RESET);
                    }
                    break;
                case "4":
                    System.out.println(GREEN + "\n--- All Categories ---" + RESET);
                    System.out.println(CYAN + String.format("%-5s | %-20s", "ID", "Name") + RESET);
                    System.out.println(CYAN + "------------------------------" + RESET);
                    for (com.stockmanager.models.Category c : categoryDAO.getAllCategories()) {
                        System.out.println(String.format("%-5d | %-20s", c.getId(), c.getName()));
                    }
                    System.out.println();
                    break;
                case "5":
                    System.out.print(RED + "⚠️ Are you sure you want to erase ALL categories and products? (yes/no): " + RESET);
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
                    System.out.println(RED + "❌ Invalid choice." + RESET);
            }
        }
    }

    private static void productMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + PURPLE + BOLD + "▶ PRODUCT MANAGEMENT" + RESET);
            System.out.println("  " + CYAN + "[1]" + RESET + " ➕ Add Product");
            System.out.println("  " + CYAN + "[2]" + RESET + " 💲 Update Price");
            System.out.println("  " + CYAN + "[3]" + RESET + " ℹ️  Print Product Info");
            System.out.println("  " + CYAN + "[4]" + RESET + " 📋 Print All Products");
            System.out.println("  " + RED + "[5]" + RESET + " 🗑️  Erase ALL Products");
            System.out.println("  " + YELLOW + "[0]" + RESET + " 🔙 Back to Main Menu");
            System.out.print(BOLD + "👉 Choose an option: " + RESET);

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    try {
                        System.out.print(YELLOW + "Enter product ID (leave blank for auto-assign): " + RESET);
                        String idInput = scanner.nextLine();
                        Integer id = idInput.trim().isEmpty() ? null : Integer.parseInt(idInput);
                        System.out.print(YELLOW + "Enter product name: " + RESET);
                        String name = scanner.nextLine();
                        
                        System.out.println(GREEN + "\n--- Available Categories ---" + RESET);
                        System.out.println(CYAN + String.format("%-5s | %-20s", "ID", "Name") + RESET);
                        for (com.stockmanager.models.Category c : categoryDAO.getAllCategories()) {
                            System.out.println(String.format("%-5d | %-20s", c.getId(), c.getName()));
                        }
                        System.out.println();
                        
                        System.out.print(YELLOW + "Enter category ID: " + RESET);
                        int categoryId = Integer.parseInt(scanner.nextLine());
                        System.out.print(YELLOW + "Enter price: " + RESET);
                        double price = Double.parseDouble(scanner.nextLine());
                        System.out.print(YELLOW + "Enter stock quantity: " + RESET);
                        int stock = Integer.parseInt(scanner.nextLine());
                        productDAO.addProduct(id, name, categoryId, price, stock);
                    } catch (NumberFormatException e) {
                        System.out.println(RED + "❌ Invalid number format entered." + RESET);
                    }
                    break;
                case "2":
                    try {
                        System.out.print(YELLOW + "Enter product ID to update: " + RESET);
                        int updateId = Integer.parseInt(scanner.nextLine());
                        System.out.print(YELLOW + "Enter new price: " + RESET);
                        double newPrice = Double.parseDouble(scanner.nextLine());
                        productDAO.updatePrice(updateId, newPrice);
                    } catch (NumberFormatException e) {
                        System.out.println(RED + "❌ Invalid number format entered." + RESET);
                    }
                    break;
                case "3":
                    try {
                        System.out.print(YELLOW + "Enter product ID to print: " + RESET);
                        int printId = Integer.parseInt(scanner.nextLine());
                        productDAO.printProduct(printId);
                    } catch (NumberFormatException e) {
                        System.out.println(RED + "❌ Invalid number format entered." + RESET);
                    }
                    break;
                case "4":
                    productDAO.printAllProducts();
                    break;
                case "5":
                    System.out.print(RED + "⚠️ Are you sure you want to erase ALL products? (yes/no): " + RESET);
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
                    System.out.println(RED + "❌ Invalid choice." + RESET);
            }
        }
    }

    private static void lookupMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + PURPLE + BOLD + "▶ LOOKING UP PRODUCTS" + RESET);
            System.out.println("  " + CYAN + "[1]" + RESET + " 🔍 Look up product based on category");
            System.out.println("  " + CYAN + "[2]" + RESET + " 📊 Check stock quantity");
            System.out.println("  " + YELLOW + "[0]" + RESET + " 🔙 Back to Main Menu");
            System.out.print(BOLD + "👉 Choose an option: " + RESET);

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    try {
                        System.out.print(YELLOW + "Enter category ID: " + RESET);
                        int catId = Integer.parseInt(scanner.nextLine());
                        productDAO.getProductsByCategory(catId);
                    } catch (NumberFormatException e) {
                        System.out.println(RED + "❌ Invalid number format entered." + RESET);
                    }
                    break;
                case "2":
                    try {
                        System.out.print(YELLOW + "Enter product ID: " + RESET);
                        int prodId = Integer.parseInt(scanner.nextLine());
                        productDAO.checkStock(prodId);
                    } catch (NumberFormatException e) {
                        System.out.println(RED + "❌ Invalid number format entered." + RESET);
                    }
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println(RED + "❌ Invalid choice." + RESET);
            }
        }
    }

    private static void supplierMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + PURPLE + BOLD + "▶ SUPPLIER MANAGEMENT" + RESET);
            System.out.println("  " + CYAN + "[1]" + RESET + " ➕ Add Supplier");
            System.out.println("  " + CYAN + "[2]" + RESET + " ✏️  Edit Supplier");
            System.out.println("  " + CYAN + "[3]" + RESET + " 🗑️  Delete Supplier");
            System.out.println("  " + CYAN + "[4]" + RESET + " 📋 List All Suppliers");
            System.out.println("  " + YELLOW + "[0]" + RESET + " 🔙 Back to Main Menu");
            System.out.print(BOLD + "👉 Choose an option: " + RESET);

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    System.out.print(YELLOW + "Enter supplier name: " + RESET);
                    String name = scanner.nextLine();
                    System.out.print(YELLOW + "Enter contact info: " + RESET);
                    String contact = scanner.nextLine();
                    System.out.print(YELLOW + "Enter address: " + RESET);
                    String address = scanner.nextLine();
                    supplierDAO.addSupplier(name, contact, address);
                    break;
                case "2":
                    try {
                        System.out.print(YELLOW + "Enter supplier ID to edit: " + RESET);
                        int id = Integer.parseInt(scanner.nextLine());
                        System.out.print(YELLOW + "Enter new supplier name: " + RESET);
                        String newName = scanner.nextLine();
                        System.out.print(YELLOW + "Enter new contact info: " + RESET);
                        String newContact = scanner.nextLine();
                        System.out.print(YELLOW + "Enter new address: " + RESET);
                        String newAddress = scanner.nextLine();
                        supplierDAO.editSupplier(id, newName, newContact, newAddress);
                    } catch (NumberFormatException e) {
                        System.out.println(RED + "❌ Invalid input. ID must be a number." + RESET);
                    }
                    break;
                case "3":
                    try {
                        System.out.print(YELLOW + "Enter supplier ID to delete: " + RESET);
                        int id = Integer.parseInt(scanner.nextLine());
                        supplierDAO.deleteSupplier(id);
                    } catch (NumberFormatException e) {
                        System.out.println(RED + "❌ Invalid input. ID must be a number." + RESET);
                    }
                    break;
                case "4":
                    System.out.println(GREEN + "\n--- All Suppliers ---" + RESET);
                    System.out.println(CYAN + String.format("%-5s | %-20s | %-20s | %-30s", "ID", "Name", "Contact Info", "Address") + RESET);
                    System.out.println(CYAN + "----------------------------------------------------------------------------------" + RESET);
                    for (com.stockmanager.models.Supplier s : supplierDAO.getAllSuppliers()) {
                        System.out.println(String.format("%-5d | %-20s | %-20s | %-30s", s.getId(), s.getName(), s.getContactInfo(), s.getAddress()));
                    }
                    System.out.println();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println(RED + "❌ Invalid choice." + RESET);
            }
        }
    }

    private static void supplyFormMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + PURPLE + BOLD + "▶ SUPPLY FORM MANAGEMENT" + RESET);
            System.out.println("  " + CYAN + "[1]" + RESET + " 📝 Create Supply Form");
            System.out.println("  " + CYAN + "[2]" + RESET + " 📋 Check Storage History");
            System.out.println("  " + CYAN + "[3]" + RESET + " 📅 Filter History by Date");
            System.out.println("  " + YELLOW + "[0]" + RESET + " 🔙 Back to Main Menu");
            System.out.print(BOLD + "👉 Choose an option: " + RESET);

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    try {
                        System.out.print(YELLOW + "Enter supplier name: " + RESET);
                        String suppName = scanner.nextLine();
                        System.out.print(YELLOW + "Enter product name: " + RESET);
                        String prodName = scanner.nextLine();
                        System.out.print(YELLOW + "Enter quantity: " + RESET);
                        int qty = Integer.parseInt(scanner.nextLine());
                        supplyFormDAO.addSupplyForm(suppName, prodName, qty);
                    } catch (NumberFormatException e) {
                        System.out.println(RED + "❌ Invalid input. Quantity must be a number." + RESET);
                    }
                    break;
                case "2":
                    supplyFormDAO.printStorageHistory();
                    break;
                case "3":
                    System.out.print(YELLOW + "Enter date (YYYY-MM-DD): " + RESET);
                    String dateStr = scanner.nextLine();
                    supplyFormDAO.filterFormsByDate(dateStr);
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println(RED + "❌ Invalid choice." + RESET);
            }
        }
    }
}
