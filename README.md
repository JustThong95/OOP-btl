# Hệ Thống Quản Lý Kho - Giải Thích Mã Nguồn Chi Tiết

Chào mừng bạn đến với Hệ Thống Quản Lý Kho! Hướng dẫn chi tiết này sẽ giải thích chính xác cách ứng dụng Java của chúng ta kết nối với MySQL, đồng thời phân tích logic, cấu trúc và các cơ chế bảo mật có trong mã nguồn.

---

## 🏗️ 1. Cấu Trúc Cơ Sở Dữ Liệu
Cơ sở dữ liệu của chúng ta mô phỏng logic của một nhà kho và cửa hàng thực tế.
* **Bảng `categories`:** Lưu trữ các danh mục riêng biệt (v.d.: thực phẩm, đồ điện tử, quần áo). Bảng này sử dụng khóa chính dạng chuỗi với chữ cái tiền tố (ví dụ: `CA01`).
* **Bảng `products`:** Lưu trữ các mặt hàng để bán. Nó liên kết với danh mục bằng khóa ngoại `FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL`. Nếu một danh mục bị xóa, sản phẩm vẫn tiếp tục tồn tại, nhưng liên kết danh mục của nó sẽ chuyển thành trống (`NULL`).
* **Bảng `suppliers`:** Ghi lại thông tin các công ty cung cấp sản phẩm, chứa tên, thông tin liên lạc và địa chỉ của họ.
* **Bảng `supply_forms`:** Ghi lại lịch sử nhập hàng. Để tránh mất dữ liệu nếu một nhà cung cấp hoặc sản phẩm bị xóa trong tương lai, chúng ta lưu `supplier_name` và `product_name` dưới dạng văn bản tĩnh `VARCHAR(255)` thay vì dùng các ID tĩnh. Điều này đảm bảo nhật ký nhập hàng lịch sử luôn có thể đọc được vĩnh viễn!
* **Bảng `users`:** Lưu trữ tài khoản quản trị hệ thống, mật khẩu và cấp độ quyền hạn của họ (`ADMIN` hoặc `EMPLOYEE`).

---

## 🔌 2. Cầu Nối: `DBConnection.java`
**Giải thích chi tiết từng dòng:**
```java
private static final String URL = "jdbc:mysql://127.0.0.1:3307/stock_manager?useSSL=false&allowPublicKeyRetrieval=true";
private static final String USER = "root";
private static final String PASSWORD = "thongchu95"; 
```
Những dòng này lưu tọa độ tuyệt đối để kết nối với cơ sở dữ liệu MySQL thông qua cổng localhost 3307 với tên định danh.
```java
Class.forName("com.mysql.cj.jdbc.Driver");
return DriverManager.getConnection(URL, USER, PASSWORD);
```
`Class.forName` tải thủ công driver JDBC của MySQL (`mysql-connector-j`). Lệnh `DriverManager.getConnection` khởi tạo kết nối an toàn với MySQL bằng các thông số xác thực.

---

## 🧱 3. Bản Thiết Kế: Thư mục `models/`
Thư mục `models/` đóng vai trò là các Đối tượng Truyền Dữ liệu (Data Transfer Objects - DTOs), chứa các đại diện Java chính xác cho các hàng trong cơ sở dữ liệu của chúng ta: `Category.java`, `Product.java`, `Supplier.java`, `User.java`, `Customer.java`, v.v.
Mỗi file chứa:
1. **Các biến private:** (ví dụ: `private String id; private String name;`) phản ánh chính xác các kiểu dữ liệu của cột trong database.
2. **Hàm khởi tạo (Constructors):** Để nhanh chóng tạo ra một đối tượng Java hoàn chỉnh chứa dữ liệu được lấy thẳng từ các bản ghi `ResultSet`.
3. **Getters và Setters:** Các phương thức đóng gói (encapsulation) tiêu chuẩn được sử dụng xuyên suốt để lấy và cập nhật trạng thái dữ liệu một cách an toàn.

---

## 🧠 4. Bộ Não: Thư mục `daos/`
DAO là viết tắt của **Data Access Object**. Những file này cô lập toàn bộ phần kết nối phần mềm, các truy vấn SQL khỏi thân chính của ứng dụng.

### Khởi tạo chuỗi ID tự động
Đội ngũ phát triển đã gõ bỏ hoàn toàn kỹ thuật `AUTO_INCREMENT` của MySQL. Mã nền được chuyển đổi sang định dạng chuỗi: phần mềm sẽ sử dụng hàm `generateNextId()` của từng đối tượng để đọc cấu trúc Database, bóc tách tiền tố chuỗi (ví dụ: `P01`, `CA02`, `U01`) rồi cộng dồn phần số để tạo các mã vạch tùy biến tự động.

### Giao dịch (Transactions) trong Nhập Hàng
Khi lưu một phiếu nhập hàng, chúng ta vừa ghi nhận biên lai ĐỒNG THỜI vừa tăng số lượng tồn kho của sản phẩm đó. Nếu ứng dụng dừng bất ngờ, dữ liệu có thể bị rò rỉ. Nguy cơ này được bịt kín thông qua **Transactions**:
```java
conn.setAutoCommit(false); // Bật chế độ kiểm soát giao dịch thủ công
// 1. Lệnh INSERT phiếu nhập mới vào supply_forms
// 2. Lệnh UPDATE tăng số lượng stock_quantity trong products
conn.commit(); // Lưu tất cả mọi thứ cùng lúc một cách đồng bộ
```
Nếu xảy ra sự cố máy chủ hoặc lỗi `SQLException` trong nhánh thi hành, lệnh `conn.rollback()` sẽ lập tức được kích hoạt dọn dẹp các mảnh vụn thay đổi sai sót và bảo toàn tuổi thọ hệ thống.

### Tra cứu trực diện
Để cải thiện độ thân thiện với UI (Giao diện người dùng), mọi chức năng truy xuất hiện phân giải chuỗi văn bản tự nhiên, xóa bỏ yêu cầu phải bắt ép người dùng tra từ điển chỉ số.
```java
String sql = "SELECT name, stock_quantity FROM products WHERE name = ?";
```

---

## 🔐 5. Bảo mật & Vai trò định hạng: `AccountManager.java`
Hệ thống kích hoạt cơ chế **Kiểm soát Truy cập Dựa trên Vai trò Cơ Sở (RBAC)** nhằm xây dựng bức tường phân cấp tác vụ.
```java
public enum Role {
    ADMIN,
    EMPLOYEE
}
```
Khác biệt với giao diện truyền thống, biến tĩnh `currentUser` khóa bộ nhớ phiên làm việc trên console hiện tại. Các Menu đều ẩn sâu trong cú pháp `AccountManager.getCurrentUser().getRole()`. Nhân viên `EMPLOYEE` sẽ có khả năng giám sát hoặc xuất đơn trong khi các hoạt động dọn dẹp Database, phân phối tài nguyên, sửa đổi nhân sự chỉ xuất hiện dưới quyền chỉ định của `ADMIN`.

---

## 🎮 6. Tầng Giao Diện: `StockManagerApp.java`
`StockManagerApp` là ứng dụng nhập-xuất môi trường dòng lệnh console sử dụng các vòng lặp vĩnh viễn không điểm dừng. 
```java
Scanner scanner = new Scanner(System.in);
boolean exit = false;
while (!exit) { ... }
```
Xuyên suốt file, mạng lưới các menu đa tầng được sắp xếp vào các trạm trung chuyển cụ thể `switch(choice)` router. Nhờ việc loại bỏ hoàn toàn hệ sinh thái ID số Nguyên `int`, chúng ta đã cắt bỏ hiệu quả mạng lưới bắt lỗi `try-catch` cho `Integer.parseInt()` – thúc đẩy code đơn sắc mượt mà thông qua `scanner.nextLine().trim()`. Mọi thành phần hiển thị đều được định hình bảng lưới thông tin đồng điệu và tinh xảo nhờ cấu trúc căn lề tự động `String.format("%-5s | %-20s", ...)`.

Tất cả các khối đồ họa dư thừa đều không tồn tại ở đây, bảo đảm độ phản hồi ở cấp bậc siêu tốc với 100% tương thích cho các thiết bị máy chủ hoặc máy thực hành đơn giản ở trường học.
