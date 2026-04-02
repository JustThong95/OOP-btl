# Stock Management System - Complete Code Explanation

Welcome to the Stock Management System! This detailed guide explains exactly how our Java application connects to MySQL, and breaks down the logic, structure, and security controls found in the code.

---

## 🏗️ 1. The Database Setup
Our database logically models a real-world warehouse and shop. 
* **`categories` Table:** Holds the distinct categories (e.g., food, electronics, clothes). It uses an auto-incrementing primary key ID.
* **`products` Table:** Holds items for sale. It connects to the category using `FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL`. If a category is erased, the product continues to exist, but its category pointer cleanly falls back to `NULL`.
* **`suppliers` Table:** Records the companies supplying products, holding their name, contact details, and address.
* **`supply_forms` Table:** Records the history of stock arrivals. To avoid data loss if a supplier or product is deleted in the future, we record the `supplier_name` and `product_name` as plain `VARCHAR(255)` text instead of foreign key IDs. This guarantees historical supply logs remain perfectly readable forever!
* **`users` Table:** Stores application users, their passwords, and their role access level (`ADMIN` or `EMPLOYEE`).

---

## 🔌 2. The Bridge: `DBConnection.java`
**Row-by-Row Explanation:**
```java
private static final String URL = "jdbc:mysql://127.0.0.1:3307/stock_manager";
private static final String USER = "root";
private static final String PASSWORD = "thongchu95"; 
```
These lines store the absolute coordinates to the MySQL Docker database (connecting on your local port 3307). 
```java
Class.forName("com.mysql.cj.jdbc.Driver");
return DriverManager.getConnection(URL, USER, PASSWORD);
```
`Class.forName` dynamically loads the MySQL JDBC driver (`mysql-connector-j`). `DriverManager.getConnection` securely connects and authenticates with MySQL. 

---

## 🧱 3. The Blueprints: `models/` Directory
The `models/` directory serves as Data Transfer Objects (DTOs), consisting of exact Java representations of our database rows: `Category.java`, `Product.java`, `Supplier.java`, `User.java` and `SupplyForm.java`.
Each file contains:
1. **Private variables:** (e.g., `private int id; private String name;`) mirroring the specific database column data types.
2. **Constructors:** To quickly instantiate a complete Java object containing data retrieved directly from `ResultSet` records.
3. **Getters and Setters:** Standard encapsulation methods used consistently across the application to retrieve and update object states safely.

---

## 🧠 4. The Brains: `daos/` Directory
DAO stands for **Data Access Object**. These files isolate all database connectivity, queries, and SQL logic from the main application.

### Supplier ID Assignment (`SupplierDAO.java`)
Instead of blindly relying on MySQL auto-increment processing, we wrote an algorithm to recycle erased IDs sequentially.
```java
int nextId = 1;
String findIdSql = "SELECT id FROM suppliers ORDER BY id ASC";
// ... loops through rs.next()
if (rs.getInt("id") == nextId) { nextId++; } else { break; }
```
When a new supplier is added, the system scans the sorted IDs. If supplier `1` and `3` exist, the `nextId` counter increments to `2`, notices that `2` is missing, breaks the loop, and securely inserts the new supplier perfectly into the gap at ID `2`. 

### Transactions in `SupplyFormDAO.java`
When saving a supply form, we log the record AND increase product stock. If the application crashes halfway, database corruption could occur. We prevent this using standard **Transactions**:
```java
conn.setAutoCommit(false); // Enable manual transaction control
// 1. INSERT new form into supply_forms
// 2. UPDATE products SET stock_quantity = stock_quantity + qty 
conn.commit(); // Save everything together atomically
```
If a server issue or `SQLException` happens during execution, we trigger `conn.rollback()`, safely discarding all temporary alterations and keeping database integrity intact.

### Name-Based Lookups (`ProductDAO.java`)
To vastly improve UX, category and product retrievals accept human-readable `String` names rather than requiring the user to memorize database identity integers. 
```java
String sql = "SELECT name, stock_quantity FROM products WHERE name = ?";
```

---

## 🔐 5. Security & RBAC: `AccountManager.java`
The application implements **Role-Based Access Control (RBAC)** to enforce authorization mappings across application functionality boundaries.
```java
public enum Role {
    ADMIN,
    EMPLOYEE
}
```
A static `currentUser` variable holds the active session state. Methods dynamically query `AccountManager.getCurrentUser().getRole()` prior to showing menus or permitting executions. Base `EMPLOYEE` accounts can only access Categories, Products, and lookup functions, while `ADMIN` accounts are entrusted to modify Supply Forms, Suppliers, and other Staff configurations. 

---

## 🎮 6. The User Interface: `StockManagerApp.java`
`StockManagerApp` is a robust input-processing console application safely wrapped in continuous verification loops.
```java
Scanner scanner = new Scanner(System.in);
boolean exit = false;
while (!exit) { ... }
```
It implements user input sanitation and branching menu networks entirely via `switch(choice)` pipeline routers. 
```java
} catch (NumberFormatException e) {
    System.out.println(" Invalid input.");
}
```
If a user purposefully or accidentally keys a text-string into an `Integer.parseInt()` scan, Java naturally triggers a `NumberFormatException`. This is elegantly caught using a standard `try-catch` block preventing the dreaded stack trace error or application failure. 

All graphics, icons, and ANSI color injection modifications have been strictly scrubbed to ensure 100% universal compatibility naturally parsing across any standard OS compiler and system output structures.
