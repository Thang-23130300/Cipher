## 1. Phân Hệ Khách Hàng (Storefront / Client Website)

Đây là giao diện người dùng tương tác chính. Với Servlet/JSP, bạn sẽ dùng Session để quản lý trạng thái của khách hàng.

### Chức năng Tài khoản & Bảo mật

* **Đăng ký / Đăng nhập:** Hỗ trợ đăng nhập bằng Email/Mật khẩu. Sử dụng bộ lọc (`Filter` trong Servlet) để chặn các trang cần bảo mật.
* **Mã hóa mật khẩu:** Bắt buộc sử dụng Bcrypt hoặc SHA-256 dưới Database, không lưu mật khẩu dạng plain-text.
* **Quản lý hồ sơ:** Thay đổi thông tin cá nhân, đổi mật khẩu, xem lịch sử đơn hàng.

### Khám phá Sản phẩm (Mua sắm)

* **Trang chủ ấn tượng:** Hiển thị banner banner sản phẩm nổi bật, sản phẩm mới về, sản phẩm bán chạy (Best Seller).
* **Bộ lọc & Tìm kiếm (Filter & Search):**
* Tìm kiếm theo tên sản phẩm (sử dụng câu lệnh `LIKE %...%` hoặc Full-text search).
* Lọc theo danh mục (Ví dụ: Thủy tinh, Gốm sứ, Thú bông, Quà lưu niệm theo mùa).
* Sắp xếp theo giá (Tăng/Giảm dần), theo độ phổ biến.


* **Trang chi tiết sản phẩm:** Hiển thị nhiều góc chụp của sản phẩm, mô tả chi tiết, chất liệu, kích thước, và số lượng còn lại trong kho.

### Giỏ hàng & Thanh toán (Cart & Checkout)

* **Quản lý giỏ hàng:** Lưu giỏ hàng vào `HttpSession` (nếu khách chưa đăng nhập) hoặc lưu vào Database (nếu đã đăng nhập). Chức năng: Thêm, sửa số lượng, xóa sản phẩm, tự động tính tổng tiền bằng JavaScript/JSP EL.
* **Xử lý đặt hàng (Checkout):** Nhập địa chỉ giao hàng, số điện thoại, ghi chú (ví dụ: "Gói quà giúp mình").
* **Phương thức thanh toán:**
* COD (Thanh toán khi nhận hàng) - Đơn giản nhất cho Servlet.
* Tích hợp cổng thanh toán (VNPAY / Momo API) - Điểm cộng lớn cho project nếu bạn làm tính năng này (sử dụng IPN Servlet để nhận kết quả thanh toán).



---

## 2. Phân Hệ Quản Trị (Admin Dashboard)

Phân hệ này dành cho chủ cửa hàng quản lý toàn bộ hệ thống. Toàn bộ khu vực này phải được bảo vệ nghiêm ngặt bằng `AdminFilter`.

### Quản lý Nghiệp vụ (CRUD)

* **Quản lý Sản phẩm & Danh mục:** Thêm sản phẩm mới, upload ảnh (sử dụng `@MultipartConfig` và `Part` trong Servlet 3.0+), cập nhật số lượng tồn kho, ẩn/hiển thị sản phẩm.
* **Quản lý Đơn hàng:** Xem danh sách đơn hàng mới, cập nhật trạng thái đơn hàng (Chờ xử lý -> Đang giao -> Đã giao -> Đã hủy).
* **Quản lý Người dùng:** Xem danh sách khách hàng, khóa/mở khóa tài khoản vi phạm.

### Thống kê & Báo cáo (Analytics)

* **Tổng quan doanh thu:** Thống kê doanh thu theo ngày/tháng/năm.
* **Sản phẩm chạy nhất:** Liệt kê các mặt hàng được mua nhiều nhất để chủ shop nhập thêm hàng.

---

## 3. Các Tính Năng "Đặc Thù" Cho Shop Đồ Lưu Niệm (Nâng cao)

Để project của bạn nổi bật và thực tế hơn, hãy cân nhắc thêm các tính năng nhỏ nhưng "ăn tiền" này:

* **Dịch vụ Gói quà & Thiệp chúc mừng:** Khi checkout, cho phép khách hàng tích chọn "Gói quà (+10.000đ)" và nhập lời chúc để in lên thiệp.
* **Quản lý mã giảm giá (Coupon/Discount):** Áp dụng mã giảm giá (giảm theo % hoặc số tiền cố định) khi thanh toán.
* **Sản phẩm yêu thích (Wishlist):** Cho phép khách hàng lưu lại các món đồ lưu niệm họ thích nhưng chưa mua ngay.