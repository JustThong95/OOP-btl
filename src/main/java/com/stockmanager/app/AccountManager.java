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

  
    public static boolean showAuthMenu() {
        while (currentUser == null) {
            System.out.println("\n" + "==========================================");
            System.out.println("          SYSTEM AUTHENTICATION           ");
            System.out.println("==========================================");
            System.out.println("  " + "[1]" + "  Login");
            System.out.println("  " + "[2]" + "  Register new default 'EMPLOYEE' account");
            System.out.println("  " + "[0]" + "  Exit System");
            System.out.print(" Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    register(Role.EMPLOYEE); // Theo mặc định, đăng ký từ màn hình xác thực tạo tài khoản EMPLOYEE
                    break;
                case "0":
                    return false; 
                default:
                    System.out.println(" Invalid choice.");
            }
        }
        return true;
    }

    private static void login() {
        System.out.println("\n" + "--- LOGIN ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();

        String passwordStr;
        Console console = System.console();
        if (console != null) {
            char[] passwordChars = console.readPassword("Password: ");
            passwordStr = new String(passwordChars);
        } 
        else {
            // Dự phòng nếu không chạy trong môi trường console thực (ví dụ: bên trong IDE)
            System.out.print("Password (will be visible): ");
            passwordStr = scanner.nextLine();
        }

        User user = userDAO.login(username, passwordStr);
        if (user != null) {
            currentUser = user;
            System.out.println(" Login successful! Welcome, " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        } 
        else {
            System.out.println(" Invalid username or password.");
        }
    }

    private static void register(Role defaultRole) {
        System.out.println("\n" + "--- REGISTER ---");
        System.out.print("Choose a username: ");
        String username = scanner.nextLine();

        String passwordStr;
        Console console = System.console();
        if (console != null) {
            char[] passwordChars = console.readPassword("Choose a password: ");
            passwordStr = new String(passwordChars);
        } 
        else {
            System.out.print("Choose a password (will be visible): ");
            passwordStr = scanner.nextLine();
        }

        boolean success = userDAO.registerUser(username, passwordStr, defaultRole);
        if (success) {
            System.out.println(" Registration successful! You can now log in.");
        } 
        else {
            System.out.println(" Registration failed. Try a different username.");
        }
    }

    public static void logout() {
        currentUser = null;
        System.out.println(" You have been logged out.");
    }

    public static void staffManagementMenu() {
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
            System.out.println(" ACCESS DENIED: Requires ADMIN role.");
            return;
        }

        boolean back = false;
        while (!back) {
            System.out.println("\n" + " USER & STAFF MANAGEMENT");
            System.out.println("  " + "[1]" + "  List all users");
            System.out.println("  " + "[2]" + "  Add new user (choose role)");
            System.out.println("  " + "[3]" + "  Edit user role");
            System.out.println("  " + "[4]" + "  Edit user password");
            System.out.println("  " + "[5]" + "  Delete user");
            System.out.println("  " + "[0]" + "  Back to Main Menu");
            System.out.print(" Choose an option: ");

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
                    System.out.println(" Invalid choice.");
            }
        }
    }

    private static void listUsers() {
        System.out.println("\n--- All System Users ---");
        System.out.println(String.format("%-5s | %-15s | %-15s", "ID", "Username", "Role"));
        System.out.println("----------------------------------------");
        List<User> users = userDAO.getAllUsers();
        for (User u : users) {
             System.out.println(String.format("%-5s | %-15s | %-15s", u.getId(), u.getUsername(), u.getRole().name()));
        }
    }

    private static void addNewStaff() {
        System.out.println("\n" + "--- Add New Staff ---");
        System.out.print("Available Roles - 1. ADMIN, 2. EMPLOYEE\nChoice: ");
        String roleChoice = scanner.nextLine();
        Role role = Role.EMPLOYEE;
        if (roleChoice.equals("1")) role = Role.ADMIN;
        else if (roleChoice.equals("2")) role = Role.EMPLOYEE;
        else {
             System.out.println("Invalid role choice.");
             return;
        }
        
        register(role); // tái sử dụng phương thức register, truyền vai trò cụ thể
    }

    private static void editUserRole() {
         System.out.print("Enter User ID to edit role: ");
         String targetId = scanner.nextLine().trim();
         System.out.print("New Role - 1. ADMIN, 2. EMPLOYEE\nChoice: ");
         String roleChoice = scanner.nextLine();
         Role role;
         if (roleChoice.equals("1")) role = Role.ADMIN;
         else if (roleChoice.equals("2")) role = Role.EMPLOYEE;
         else {
             System.out.println("Invalid role choice.");
             return;
         }
         
         if (userDAO.updateUserRole(targetId, role)) {
             System.out.println("Role updated successfully.");
         } 
         else {
             System.out.println("Update failed. ID might not exist.");
         }
    }

    private static void editUserPassword() {
        System.out.print("Enter User ID to edit password: ");
        String targetId = scanner.nextLine().trim();
        System.out.print("Enter New Password: ");
        String newPass = scanner.nextLine();
        
        if (userDAO.updateUserPassword(targetId, newPass)) {
            System.out.println("Password updated successfully.");
        } 
        else {
            System.out.println("Update failed. ID might not exist.");
        }
    }

    private static void deleteStaff() {
         listUsers();
         System.out.print("Enter Username to delete: ");
         String targetUsername = scanner.nextLine();
         if (targetUsername.equals(currentUser.getUsername())) {
             System.out.println("You cannot delete yourself!");
             return;
         }
         if (userDAO.deleteUser(targetUsername)) {
             System.out.println("User deleted successfully.");
         } 
         else {
             System.out.println("Delete failed. Username might not exist.");
         }
    }
}
