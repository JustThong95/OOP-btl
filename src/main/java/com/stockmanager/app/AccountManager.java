package com.stockmanager.app;

import com.stockmanager.daos.UserDAO;
import com.stockmanager.models.Role;
import com.stockmanager.models.User;

import java.io.Console;
import java.util.List;
import java.util.Scanner;

public class AccountManager {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserDAO userDAO = new UserDAO();
    private static User currentUser = null;

    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Loops until a user successfully logs in or registers.
     * Returns true if user is logged in, false if system should exit.
     */
    public static boolean showAuthMenu() {
        while (currentUser == null) {
            System.out.println("\n" + StockManagerApp.CYAN + StockManagerApp.BOLD + "==========================================" + StockManagerApp.RESET);
            System.out.println(StockManagerApp.PURPLE + StockManagerApp.BOLD + "          🔐 SYSTEM AUTHENTICATION          " + StockManagerApp.RESET);
            System.out.println(StockManagerApp.CYAN + StockManagerApp.BOLD + "==========================================" + StockManagerApp.RESET);
            System.out.println("  " + StockManagerApp.CYAN + "[1]" + StockManagerApp.RESET + " 🔑 Login");
            System.out.println("  " + StockManagerApp.CYAN + "[2]" + StockManagerApp.RESET + " 📝 Register new default 'EMPLOYEE' account");
            System.out.println("  " + StockManagerApp.RED + "[0]" + StockManagerApp.RESET + " ❌ Exit System");
            System.out.print(StockManagerApp.BOLD + "👉 Choose an option: " + StockManagerApp.RESET);

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    register(Role.EMPLOYEE); // By default, registration from the auth screen makes an EMPLOYEE
                    break;
                case "0":
                    return false; // Exit signal
                default:
                    System.out.println(StockManagerApp.RED + "❌ Invalid choice." + StockManagerApp.RESET);
            }
        }
        return true;
    }

    private static void login() {
        System.out.println("\n" + StockManagerApp.YELLOW + StockManagerApp.BOLD + "--- LOGIN ---" + StockManagerApp.RESET);
        System.out.print("Username: ");
        String username = scanner.nextLine();

        String passwordStr;
        Console console = System.console();
        if (console != null) {
            char[] passwordChars = console.readPassword("Password: ");
            passwordStr = new String(passwordChars);
        } else {
            // Fallback if not running in a true console environment (e.g., inside an IDE)
            System.out.print("Password (will be visible): ");
            passwordStr = scanner.nextLine();
        }

        User user = userDAO.login(username, passwordStr);
        if (user != null) {
            currentUser = user;
            System.out.println(StockManagerApp.GREEN + "✅ Login successful! Welcome, " + currentUser.getUsername() + " (" + currentUser.getRole() + ")" + StockManagerApp.RESET);
        } else {
            System.out.println(StockManagerApp.RED + "❌ Invalid username or password." + StockManagerApp.RESET);
        }
    }

    private static void register(Role defaultRole) {
        System.out.println("\n" + StockManagerApp.YELLOW + StockManagerApp.BOLD + "--- REGISTER ---" + StockManagerApp.RESET);
        System.out.print("Choose a username: ");
        String username = scanner.nextLine();

        String passwordStr;
        Console console = System.console();
        if (console != null) {
            char[] passwordChars = console.readPassword("Choose a password: ");
            passwordStr = new String(passwordChars);
        } else {
            System.out.print("Choose a password (will be visible): ");
            passwordStr = scanner.nextLine();
        }

        boolean success = userDAO.registerUser(username, passwordStr, defaultRole);
        if (success) {
            System.out.println(StockManagerApp.GREEN + "✅ Registration successful! You can now log in." + StockManagerApp.RESET);
        } else {
            System.out.println(StockManagerApp.RED + "❌ Registration failed. Try a different username." + StockManagerApp.RESET);
        }
    }

    public static void logout() {
        currentUser = null;
        System.out.println(StockManagerApp.YELLOW + "👋 You have been logged out." + StockManagerApp.RESET);
    }

    public static void staffManagementMenu() {
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
            System.out.println(StockManagerApp.RED + "❌ ACCESS DENIED: Requires ADMIN role." + StockManagerApp.RESET);
            return;
        }

        boolean back = false;
        while (!back) {
            System.out.println("\n" + StockManagerApp.PURPLE + StockManagerApp.BOLD + "▶ USER & STAFF MANAGEMENT" + StockManagerApp.RESET);
            System.out.println("  " + StockManagerApp.CYAN + "[1]" + StockManagerApp.RESET + " 📋 List all users");
            System.out.println("  " + StockManagerApp.CYAN + "[2]" + StockManagerApp.RESET + " ➕ Add new user (choose role)");
            System.out.println("  " + StockManagerApp.CYAN + "[3]" + StockManagerApp.RESET + " ✏️  Edit user role");
            System.out.println("  " + StockManagerApp.CYAN + "[4]" + StockManagerApp.RESET + " 🔐 Edit user password");
            System.out.println("  " + StockManagerApp.RED + "[5]" + StockManagerApp.RESET + " 🗑️  Delete user");
            System.out.println("  " + StockManagerApp.YELLOW + "[0]" + StockManagerApp.RESET + " 🔙 Back to Main Menu");
            System.out.print(StockManagerApp.BOLD + "👉 Choose an option: " + StockManagerApp.RESET);

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    listUsers();
                    break;
                case "2":
                    addNewStaff();
                    break;
                case "3":
                    editUserRole();
                    break;
                case "4":
                    editUserPassword();
                    break;
                case "5":
                    deleteStaff();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println(StockManagerApp.RED + "❌ Invalid choice." + StockManagerApp.RESET);
            }
        }
    }

    private static void listUsers() {
        System.out.println(StockManagerApp.GREEN + "\n--- All System Users ---" + StockManagerApp.RESET);
        System.out.println(StockManagerApp.CYAN + String.format("%-5s | %-15s | %-15s", "ID", "Username", "Role") + StockManagerApp.RESET);
        System.out.println(StockManagerApp.CYAN + "----------------------------------------" + StockManagerApp.RESET);
        List<User> users = userDAO.getAllUsers();
        for (User u : users) {
             System.out.println(String.format("%-5d | %-15s | %-15s", u.getId(), u.getUsername(), u.getRole().name()));
        }
    }

    private static void addNewStaff() {
        System.out.println("\n" + StockManagerApp.YELLOW + "--- Add New Staff ---" + StockManagerApp.RESET);
        System.out.print("Available Roles - 1. ADMIN, 2. EMPLOYEE, 3. SUPPLIER\nChoice: ");
        String roleChoice = scanner.nextLine();
        Role role = Role.EMPLOYEE;
        if (roleChoice.equals("1")) role = Role.ADMIN;
        else if (roleChoice.equals("2")) role = Role.EMPLOYEE;
        else if (roleChoice.equals("3")) role = Role.SUPPLIER;
        else {
             System.out.println(StockManagerApp.RED + "Invalid role choice." + StockManagerApp.RESET);
             return;
        }
        
        register(role); // reused the register method, passing specific role
    }

    private static void editUserRole() {
         System.out.print(StockManagerApp.YELLOW + "Enter User ID to edit role: " + StockManagerApp.RESET);
         try {
             int targetId = Integer.parseInt(scanner.nextLine());
             System.out.print("New Role - 1. ADMIN, 2. EMPLOYEE, 3. SUPPLIER\nChoice: ");
             String roleChoice = scanner.nextLine();
             Role role;
             if (roleChoice.equals("1")) role = Role.ADMIN;
             else if (roleChoice.equals("2")) role = Role.EMPLOYEE;
             else if (roleChoice.equals("3")) role = Role.SUPPLIER;
             else {
                 System.out.println(StockManagerApp.RED + "Invalid role choice." + StockManagerApp.RESET);
                 return;
             }
             
             if (userDAO.updateUserRole(targetId, role)) {
                 System.out.println(StockManagerApp.GREEN + "Role updated successfully." + StockManagerApp.RESET);
             } else {
                 System.out.println(StockManagerApp.RED + "Update failed. ID might not exist." + StockManagerApp.RESET);
             }
         } catch (NumberFormatException e) {
             System.out.println(StockManagerApp.RED + "❌ Need a valid numeric ID." + StockManagerApp.RESET);
         }
    }

    private static void editUserPassword() {
        System.out.print(StockManagerApp.YELLOW + "Enter User ID to edit password: " + StockManagerApp.RESET);
         try {
             int targetId = Integer.parseInt(scanner.nextLine());
             System.out.print("Enter New Password: ");
             String newPass = scanner.nextLine();
             
             if (userDAO.updateUserPassword(targetId, newPass)) {
                 System.out.println(StockManagerApp.GREEN + "Password updated successfully." + StockManagerApp.RESET);
             } else {
                 System.out.println(StockManagerApp.RED + "Update failed. ID might not exist." + StockManagerApp.RESET);
             }
         } catch (NumberFormatException e) {
             System.out.println(StockManagerApp.RED + "❌ Need a valid numeric ID." + StockManagerApp.RESET);
         }
    }

    private static void deleteStaff() {
         System.out.print(StockManagerApp.YELLOW + "Enter User ID to delete: " + StockManagerApp.RESET);
         try {
             int targetId = Integer.parseInt(scanner.nextLine());
             if (targetId == currentUser.getId()) {
                 System.out.println(StockManagerApp.RED + "You cannot delete yourself!" + StockManagerApp.RESET);
                 return;
             }
             if (userDAO.deleteUser(targetId)) {
                 System.out.println(StockManagerApp.GREEN + "User deleted successfully." + StockManagerApp.RESET);
             } else {
                 System.out.println(StockManagerApp.RED + "Delete failed. ID might not exist." + StockManagerApp.RESET);
             }
         } catch (NumberFormatException e) {
             System.out.println(StockManagerApp.RED + "❌ Need a valid numeric ID." + StockManagerApp.RESET);
         }
    }
}
