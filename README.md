# Stock Management System - Complete Code Explanation

Welcome to the Stock Management System! This detailed guide explains exactly how our Java application connects to MySQL, and breaks down what is happening in the code row-by-row, file-by-file.

---

## 🏗️ 1. The Database (`schema.sql`)
Our database represents our real-world shop. 
* **`categories` Table:** Holds the distinct categories (e.g., food, electronics). `id INT AUTO_INCREMENT PRIMARY KEY` ensures every category is uniquely numbered, starting from 1. 
* **`products` Table:** Holds items for sale. It connects to the category using `FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL`. If a category is erased, the product's category simply becomes `NULL` (empty) but the product survives!
* **`suppliers` Table:** Records the companies that give us products. Contains `name`, `contact_info`, and `address`.
* **`supply_forms` Table:** Records the history of stock arrivals. Instead of linking IDs, we use `supplier_name VARCHAR(255)` and `product_name VARCHAR(255)` so the actual text names reflect in the history logs forever!

---

## 🔌 2. The Bridge: `DBConnection.java`
**Row-by-Row Explanation:**
```java
private static final String URL = "jdbc:mysql://127.0.0.1:3307/stock_manager";
private static final String USER = "root";
private static final String PASSWORD = "thongchu95"; 
```
These lines store the absolute coordinates to your MySQL database. 
```java
Class.forName("com.mysql.cj.jdbc.Driver");
return DriverManager.getConnection(URL, USER, PASSWORD);
```
`Class.forName` loads the special MySQL bridge driver (`mysql-connector-j`) from the `lib/` folder. `DriverManager.getConnection` forces Java to log into your MySQL using the username and password provided! 

---

## 🧱 3. The Blueprints: `models/` Directory
The `models/` directory holds simple Java classes: `Category.java`, `Product.java`, `Supplier.java`, and `SupplyForm.java`.
Each file contains:
1. **Private variables:** (e.g., `private int id; private String name;`) which mirror the database columns.
2. **Constructors:** To quickly create a complete object in one line of code.
3. **Getters and Setters:** (e.g., `public String getName() { return name; }`). Because the variables are `private`, other files MUST use these safe "Getter/Setter" methods to read or write the data!

---

## 🧠 4. The Brains: `daos/` Directory
DAO stands for **Data Access Object**. These files write the actual SQL commands to talk to MySQL.

### Data Deletion & Mass Erasures (`ProductDAO.java` & `CategoryDAO.java`)
**Row-by-Row Explanation:**
```java
String sqlDeleteProducts = "DELETE FROM products";
stmt.executeUpdate(sqlDeleteProducts);
stmt.executeUpdate("ALTER TABLE products AUTO_INCREMENT = 1");
```
When you choose "Erase ALL Products", it first runs `DELETE FROM products` to wipe the entire table. The magical row `ALTER TABLE ... AUTO_INCREMENT = 1` forces MySQL to reset its internal ID counter back to `1`. The very next product you add will start fresh at ID 1!

### Transactions in `SupplyFormDAO.java`
When a supply form goes through, we must log the form AND increase product stock. If the power goes out halfway, database corruption occurs. We stop this using **Transactions**:
**Row-by-Row Explanation:**
```java
conn.setAutoCommit(false); // Enable transaction
```
This forces MySQL to wait. It tells MySQL: "Do not save anything permanently yet until I say so!"
```java
// 1. We lookup the product ID
int productId = getProductIdByName(productName, conn); 

// 2. We INSERT the new form into supply_forms
String insertSql = "INSERT INTO supply_forms (supplier_name, product_name, quantity, total_price) VALUES (?, ?, ?, ?)";
stmt.executeUpdate();

// 3. We UPDATE the product's stock number
String updateStockSql = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE id = ?";
stmt.executeUpdate();
```
Because `setAutoCommit(false)` is active, MySQL holds these changes in its temporary memory.
```java
conn.commit(); 
```
If lines 1, 2, and 3 finish with ZERO errors, `conn.commit()` is called. This permanently saves *both* the log and the stock increase simultaneously in one atomic blast! 
```java
} catch (SQLException e) {
    conn.rollback(); 
}
```
If *anything* fails (like a typo or server crash), it triggers `conn.rollback()`, which deletes all the temporary changes so your database remains completely untouched and safe!

---

## 🎮 5. The User Interface: `StockManagerApp.java`
`StockManagerApp` is a standard terminal "App" built entirely around an infinite `while` loop!
```java
Scanner scanner = new Scanner(System.in);
boolean exit = false;
while (!exit) { ... }
```
The application halts and waits at `String choice = scanner.nextLine();`. This lets the user type into the console terminal.
```java
switch (choice) {
    case "1": categoryMenu(); break;
    case "2": productMenu(); break;
}
```
The `switch` statement acts as a traffic controller. If you press "1", it jumps into the `categoryMenu()` function which prints out an entirely new purple menu loop! 
If you type strings instead of numbers by accident, Java throws a `NumberFormatException` error which we catch beautifully:
```java
} catch (NumberFormatException e) {
    System.out.println(RED + "❌ Invalid input." + RESET);
}
```
The ANSI escape codes (like `RED` or `CYAN`) physically change the color output built into terminal configurations to make the app aesthetic!
