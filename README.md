#  SmartPhone Store Management (Java Console)

##  Giới thiệu

Đây là hệ thống quản lý bán điện thoại viết bằng **Java Console + JDBC + MySQL**.

Ứng dụng hỗ trợ:

* Quản lý sản phẩm, danh mục
* Đặt hàng và xử lý đơn hàng
* Phân quyền Admin / Customer
* Đảm bảo dữ liệu bằng Transaction

---

##  Công nghệ sử dụng

* Java 17
* JDBC
* MySQL
* Maven
* JUnit 5
* BCrypt (mã hóa mật khẩu)

---

##  Cài đặt Database

### Bước 1: tạo database

Mở MySQL và chạy:

```sql
source finalproject.sql;
```

---

##  Cấu hình kết nối DB

Mở file:

```java
util/DBConnection.java
```

Sửa lại thông tin:

```java
private static final String URL = "jdbc:mysql://localhost:3306/ten_database";
private static final String USER = "root";
private static final String PASS = "mat_khau_cua_ban";
```

---

## ▶ Chạy chương trình

Chạy file:

```
Main.java
```

---

## Tài khoản test

### Admin

* Email: [admin@gmail.com](mailto:admin@gmail.com)
* Password: 123456

### Customer

* Email: [user@gmail.com](mailto:user@gmail.com)
* Password: 123456

---

##  Chức năng chính

###  Admin

* Quản lý danh mục (CRUD)
* Quản lý sản phẩm (CRUD)
* Xem danh sách đơn hàng
* Cập nhật trạng thái đơn hàng:

  * PENDING → SHIPPING → DELIVERED
  * Có thể hủy đơn (CANCELLED)

---

###  Customer

* Xem sản phẩm
* Tìm kiếm / lọc / sắp xếp
* Thêm vào giỏ hàng
* Đặt hàng (checkout)
* Xem lịch sử đơn hàng
* Xem chi tiết đơn hàng

---

## Transaction (Quan trọng)

Ứng dụng sử dụng Transaction trong các nghiệp vụ:

### Checkout

* Tạo order
* Tạo order_detail
* Trừ stock
* Nếu lỗi → rollback toàn bộ

### Hủy đơn hàng

* Cập nhật trạng thái = CANCELLED
* Cộng lại stock sản phẩm
* Nếu lỗi → rollback

---

## Testing

### Unit Test

* Kiểm tra validation:

  * Email hợp lệ / không hợp lệ
  * Số điện thoại
  * Giá tiền
  * Stock

### SQL Injection Test

Test input:

```
' OR '1'='1
```

Kết quả:

* Không đăng nhập được → PASS

Vì sử dụng `PreparedStatement`

---

## Bảo mật

* Mật khẩu được mã hóa bằng BCrypt
* Sử dụng PreparedStatement chống SQL Injection

---

## Clean Code

* Áp dụng mô hình:

  * model
  * dao
  * service
  * presentation
  * util
* Phân tách rõ ràng các tầng

---

## Lưu ý

* Không commit thư mục `target/`
* Nên dùng `.gitignore`
* Không nên hard-code password trong thực tế

---

## Kết luận

Dự án hoàn thành các yêu cầu:

* CRUD đầy đủ
* Authentication
* Transaction đảm bảo dữ liệu
* Unit Test cơ bản
* Chống SQL Injection

Đáp ứng yêu cầu Java Advanced (SRS)

---
