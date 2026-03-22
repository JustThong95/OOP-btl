package com.stockmanager.app;

import com.stockmanager.daos.CategoryDAO;
import com.stockmanager.daos.ProductDAO;

import java.util.Scanner;

public class StockManagerApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final CategoryDAO categoryDAO = new CategoryDAO();
    private static final ProductDAO productDAO = new ProductDAO();

    public static void main(String[] args) {
        boolean exit = false;
        System.out.println("Welcome to the Stock Management System!");
        
        while (!exit) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Category Management");
            System.out.println("2. Product Management");
            System.out.println("3. Looking Up Products");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            
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
                case "0":
                    exit = true;
                    System.out.println("Exiting the system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void categoryMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- CATEGORY MANAGEMENT ---");
            System.out.println("1. Add Category");
            System.out.println("2. Edit Category");
            System.out.println("3. Erase Category");
            System.out.println("4. List All Categories");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    System.out.print("Enter category name: ");
                    String name = scanner.nextLine();
                    categoryDAO.addCategory(name);
                    break;
                case "2":
                    System.out.print("Enter category ID to edit: ");
                    try {
                        int idToEdit = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter new category name: ");
                        String newName = scanner.nextLine();
                        categoryDAO.editCategory(idToEdit, newName);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. ID must be a number.");
                    }
                    break;
                case "3":
                    System.out.print("Enter category ID to erase: ");
                    try {
                        int idToErase = Integer.parseInt(scanner.nextLine());
                        categoryDAO.eraseCategory(idToErase);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. ID must be a number.");
                    }
                    break;
                case "4":
                    System.out.println("Categories: " + categoryDAO.getAllCategories());
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void productMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- PRODUCT MANAGEMENT ---");
            System.out.println("1. Add Product");
            System.out.println("2. Update Price");
            System.out.println("3. Print Product Info");
            System.out.println("4. Print All Products");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    try {
                        System.out.print("Enter product name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter category ID: ");
                        int categoryId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter price: ");
                        double price = Double.parseDouble(scanner.nextLine());
                        System.out.print("Enter stock quantity: ");
                        int stock = Integer.parseInt(scanner.nextLine());
                        productDAO.addProduct(name, categoryId, price, stock);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format entered.");
                    }
                    break;
                case "2":
                    try {
                        System.out.print("Enter product ID to update: ");
                        int updateId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter new price: ");
                        double newPrice = Double.parseDouble(scanner.nextLine());
                        productDAO.updatePrice(updateId, newPrice);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format entered.");
                    }
                    break;
                case "3":
                    try {
                        System.out.print("Enter product ID to print: ");
                        int printId = Integer.parseInt(scanner.nextLine());
                        productDAO.printProduct(printId);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format entered.");
                    }
                    break;
                case "4":
                    productDAO.printAllProducts();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void lookupMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- LOOKING UP PRODUCTS ---");
            System.out.println("1. Look up product based on category");
            System.out.println("2. Check stock quantity");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    try {
                        System.out.print("Enter category ID: ");
                        int catId = Integer.parseInt(scanner.nextLine());
                        productDAO.getProductsByCategory(catId);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format entered.");
                    }
                    break;
                case "2":
                    try {
                        System.out.print("Enter product ID: ");
                        int prodId = Integer.parseInt(scanner.nextLine());
                        productDAO.checkStock(prodId);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format entered.");
                    }
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
