# SmartPhone Store Management

## Giới thiệu

SmartPhone Store Management là ứng dụng quản lý bán điện thoại viết bằng **Java Console**, sử dụng **JDBC** để kết nối **MySQL**.

Hệ thống hỗ trợ:

- Đăng ký, đăng nhập, phân quyền **Admin / Customer**
- Quản lý danh mục và sản phẩm
- Giỏ hàng và đặt hàng
- Quản lý trạng thái đơn hàng
- Coupon / Flash Sale
- Thống kê doanh thu, top sản phẩm bán chạy
- Kiểm tra dữ liệu đầu vào và đảm bảo tính toàn vẹn dữ liệu bằng transaction

---

## Công nghệ sử dụng

- Java 17
- JDBC
- MySQL
- Maven
- JUnit 5
- BCrypt

---

## Cấu trúc project

Project được tổ chức theo mô hình nhiều tầng để dễ quản lý và bảo trì:

- `model`: chứa entity
- `dao`: thao tác dữ liệu với database
- `service`: xử lý nghiệp vụ
- `presentation`: giao diện console/menu
- `util`: hàm dùng chung như validate, kết nối DB, mã hóa mật khẩu

---

## Chức năng chính

### 1. Authentication

- Đăng ký tài khoản khách hàng
- Đăng nhập theo email và mật khẩu
- Mật khẩu được mã hóa bằng BCrypt trước khi lưu
- Phân quyền:
  - `ADMIN`
  - `CUSTOMER`

### 2. Quản lý danh mục

Admin có thể:

- Xem danh sách danh mục
- Thêm danh mục
- Sửa danh mục
- Xóa danh mục

### 3. Quản lý sản phẩm

Admin có thể:

- Xem danh sách sản phẩm
- Thêm sản phẩm
- Sửa sản phẩm
- Xóa sản phẩm
- Tìm kiếm sản phẩm
- Sắp xếp sản phẩm theo giá

Customer có thể:

- Xem danh sách sản phẩm
- Tìm kiếm, lọc, sắp xếp sản phẩm
- Thêm sản phẩm vào giỏ hàng

### 4. Giỏ hàng và đặt hàng

Customer có thể:

- Xem giỏ hàng
- Checkout đơn hàng
- Xem lịch sử đơn hàng cá nhân
- Xem chi tiết từng đơn hàng

### 5. Quản lý đơn hàng

Admin có thể:

- Xem toàn bộ đơn hàng trong hệ thống
- Cập nhật trạng thái đơn hàng:
  - `PENDING`
  - `SHIPPING`
  - `DELIVERED`
  - `CANCELLED`

### 6. Khuyến mãi

- Flash Sale theo phần trăm giảm giá trong khoảng thời gian nhất định
- Coupon áp dụng cho hóa đơn khi đặt hàng

### 7. Báo cáo

- Thống kê doanh thu theo tháng
- Top 5 sản phẩm bán chạy nhất tháng

---

## Validate dữ liệu đầu vào

Project sử dụng `ValidationUtil` để kiểm tra dữ liệu nhập vào.

### Các validate đang dùng

- Không được để trống
- Email đúng định dạng
- Số điện thoại phải:
  - gồm 10 chữ số
  - bắt đầu bằng số `0`
- Mật khẩu tối thiểu 6 ký tự, gồm chữ và số
- Giá phải lớn hơn 0
- Stock phải lớn hơn hoặc bằng 0

### Luồng đăng ký

Trong chức năng đăng ký:

- Nếu nhập sai họ tên / email / số điện thoại / mật khẩu thì hệ thống sẽ báo lỗi ngay
- Người dùng có thể nhập lại ngay tại field đang sai
- Người dùng có thể chọn thoát đăng ký nếu không muốn tiếp tục

Lưu ý:

- Kiểm tra **định dạng** được xử lý ở `util`
- Kiểm tra **email/số điện thoại đã tồn tại** thuộc nghiệp vụ nên được xử lý ở `service`

---

## Transaction

Một số nghiệp vụ quan trọng sử dụng transaction để tránh sai lệch dữ liệu.

### Checkout

Khi customer đặt hàng, hệ thống thực hiện trong cùng một transaction:

- Tạo order
- Tạo order detail
- Cập nhật tồn kho

Nếu có lỗi ở bất kỳ bước nào thì rollback toàn bộ.

### Hủy đơn

Khi hủy đơn:

- Cập nhật trạng thái đơn hàng
- Hoàn lại số lượng sản phẩm vào kho nếu cần

Nếu có lỗi thì rollback.

---

## Bảo mật

- Mật khẩu được mã hóa bằng BCrypt
- Sử dụng `PreparedStatement` để chống SQL Injection

Ví dụ test SQL Injection:

```sql
' OR '1'='1
```
