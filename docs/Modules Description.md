
## Các Module Cần Có Cho Một Trang E-Commerce Truyền Thống

Dựa trên 4 quyền (`SUPER_ADMIN`, `ADMIN`, `STAFF`, `CUSTOMER`), hệ thống e-commerce của bạn cần được chia nhỏ thành các module chức năng sau. Việc bóc tách này giúp bạn ánh xạ (map) quyền của user vào các router/servlet cực kỳ dễ dàng.

### 1. Module Xác Thực & Phân Quyền (`auth`)

* **Chức năng:** Đăng nhập, đăng ký, đăng xuất, quên mật khẩu, kích hoạt tài khoản qua Mail (sử dụng Jakarta Mail nếu có).
* **Luồng xử lý:** Khi đăng nhập thành công, lưu thông tin User và Quyền (Role) vào `Session`. `SecurityFilter` ở tầng `core` sẽ chặn tất cả request để check xem User đó có quyền truy cập vào URL tương ứng hay không.

### 2. Module Quản Lý Người Dùng & Phân Quyền (`user`)

* **CUSTOMER:** Xem/Sửa profile cá nhân, đổi mật khẩu.
* **STAFF / ADMIN:** Xem danh sách khách hàng, khóa/mở khóa tài khoản vi phạm.
* **SUPER_ADMIN:** Có toàn quyền tối cao. Là người duy nhất có quyền tạo, sửa, xóa và chỉ định vai trò cho các tài khoản `ADMIN` và `STAFF`.

### 3. Module Catalogue (Sản Phẩm, Danh Mục, Thương Hiệu)

* **CUSTOMER / GUEST:** Xem danh sách sản phẩm (phân trang, lọc theo giá, danh mục, thương hiệu), tìm kiếm sản phẩm (sử dụng Ajax + Gson để gợi ý từ khóa), xem chi tiết sản phẩm.
* **STAFF / ADMIN:** Thêm mới sản phẩm, cập nhật số lượng tồn kho, giá bán, upload hình ảnh, quản lý danh mục (Category) và thương hiệu (Brand).

### 4. Module Giỏ Hàng (`cart`)

* **CUSTOMER / GUEST:** Thêm sản phẩm vào giỏ, cập nhật số lượng, xóa sản phẩm khỏi giỏ.
* **Lưu ý kỹ thuật:** Nên thiết kế giỏ hàng lưu trong `Session` cho khách vãng lai (Guest). Khi khách hàng log in, có thể đồng bộ giỏ hàng từ Session xuống Database (`CartItem` entity) để họ có thể xem lại giỏ hàng trên bất kỳ thiết bị nào.

### 5. Module Đơn Hàng & Quy Trình Xử Lý (`order`)

Đây là nơi thể hiện rõ nhất sự khác biệt giữa các quyền:

* **CUSTOMER:** Tiến hành Checkout (Đặt hàng), chọn phương thức thanh toán (COD, Chuyển khoản), xem lịch sử mua hàng, hủy đơn hàng (khi đơn ở trạng thái *Chờ xử lý*).
* **STAFF:** Tiếp nhận đơn hàng, chuyển trạng thái đơn hàng (Ví dụ: *Chờ xác nhận -> Đang đóng gói -> Đang giao -> Đã giao*). Staff là người trực tiếp xử lý các tác vụ vận hành hàng ngày.
* **ADMIN / SUPER_ADMIN:** Có quyền can thiệp sâu hơn như hủy đơn hàng ở các trạng thái đặc biệt, duyệt hoàn tiền, chỉnh sửa thông tin hóa đơn khi có sự cố.

### 6. Module Báo Cáo & Thống Kê (`dashboard`)

* **STAFF:** Xem thống kê lượng đơn hàng trong ngày, số sản phẩm sắp hết hàng trong kho để kịp thời báo cáo.
* **ADMIN / SUPER_ADMIN:** Xem biểu đồ doanh thu (theo ngày, tháng, năm), thống kê top sản phẩm bán chạy, thống kê số lượng user mới đăng ký (Dữ liệu thường được query từ Hibernate, parse sang JSON bằng Gson và vẽ biểu đồ ở Front-end bằng Chart.js).
