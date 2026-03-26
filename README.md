# Stock Management System - Code Explanation for Beginners

Welcome to your Stock Management System! This guide is designed specifically to help you understand how the Java code connects to the MySQL database and how the overall application is structured.

---

## 🏗️ 1. The Project Structure
This app is built using a common architectural pattern called **MVC / DAO** (Model-View-Controller / Data Access Object). Here is a breakdown of the folders in `src/main/java/com/stockmanager/`:

* `models/` - These are plain Java classes that represent rows in your database tables.
* `db/` - Contains the setup to actually talk to the database.
* `daos/` - Provides methods to write and read data to/from the database.
* `app/` - The visual "Frontend" part where the user enters choices in the terminal.

---

## 🗄️ 2. The Database Connection (`DBConnection.java`)
**What it does:** It creates a bridge between your Java code and your MySQL server.

Java uses something called **JDBC** (Java Database Connectivity) to talk to SQL servers. In `DBConnection.java`, we use the `DriverManager.getConnection()` method to pass your `URL`, `username`, and `password` to MySQL. 
Whenever our code needs to execute a query (like `SELECT` or `INSERT`), it calls `DBConnection.getConnection()` to open that bridge and send the command.

---

## 📦 3. The Models (`Category.java`, `Product.java`, `Supplier.java` & `SupplyForm.java`)
**What they do:** They represent real-world "things" in your program.

In Object-Oriented Programming (OOP), we use *Classes* as blueprints. 
* Your `categories` MySQL table has an `id` and `name`.
* Your Java `Category.java` class has `private int id` and `private String name`.
* The new `Supplier` and `SupplyForm` models represent the vendors providing products and the records of incoming product stock.

We use **Getters and Setters** (`getId()`, `setName()`) to safely access and change these variables. The Models don't talk to the database at all; they just hold the data in memory.

---

## 🛠️ 4. Data Access Objects or DAOs (`CategoryDAO.java`, `ProductDAO.java`, `SupplierDAO.java` & `SupplyFormDAO.java`)
**What they do:** They take Java Models and actually save/load them from MySQL.

**DAO** stands for Data Access Object. If you look inside `ProductDAO.java` or `SupplyFormDAO.java`, you will see methods like `addProduct` and `addSupplyForm`. This is where the magic happens!

To prevent security risks (like SQL Injection), we use a special Java object called `PreparedStatement`:
```java
String sql = "INSERT INTO categories (name) VALUES (?)";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, name); // This replaces the "?" with the actual name securely!
stmt.executeUpdate(); // This sends the SQL to the database
```
* Use `executeUpdate()` when you change data (`INSERT`, `UPDATE`, `DELETE`).
* Use `executeQuery()` when you read data (`SELECT`).

---

## 🖥️ 5. The Main Interface (`StockManagerApp.java`)
**What it does:** The starting point of your program that shows the menus and takes user keyboard input.

* `public static void main(String[] args)`: The entry point where Java starts running the code.
* `Scanner scanner = new Scanner(System.in)`: This tells Java to listen for you typing on your keyboard.
* `while (!exit)` loop: This is known as an infinite loop. It keeps the program and the Menu running forever until you explicitly type `0` to quit (which sets `exit = true` and breaks the loop).
* `switch (choice)`: Based on what number you typed, it routes you to the correct helper menu (`categoryMenu()`, `productMenu()`).

---

## 🔄 Example Workflow: Editing a Category
Let's see step-by-step what happens when you press `2` to Edit a Category:
1. **The User Interface:** `StockManagerApp` prints the purple menu to your console. You type `2`.
2. **The Scanner:** The `Scanner` reads your keyboard and asks you for the Category ID and the New Name.
3. **The Delegation:** The App takes those two variables (`idToEdit` and `newName`), and hands them off to the DAO by calling `categoryDAO.editCategory(idToEdit, newName)`.
4. **The Database Call:** `CategoryDAO` builds the SQL string: `UPDATE categories SET name = ? WHERE id = ?`.
5. **The Bridge:** The DAO opens the JDBC bridge, passes the `newName` and `idToEdit` into the question marks (`?`), and presses send!
6. **The Result:** The terminal prints out "✔ Category updated successfully" in green text!
