## 1. Cấu Trúc Thư Mục Toàn Project (Feature-Based)

Trong kiến trúc Feature-based, thay vì gom tất cả Controller vào một nơi, Entity vào một nơi, bạn sẽ gom chúng lại theo từng **tính năng lớn**. Các thành phần dùng chung (như HibernateUtil, Filter phân quyền, BaseRepository...) sẽ được đưa vào module `common` hoặc `core`.

```text
src/main/java/com/yourdomain/ecommerce/
│
├── core/                           # Các cấu hình hệ thống cốt lõi
│   ├── config/                     # Cấu hình Hibernate, AppConfig
│   │   └── HibernateUtil.java
│   ├── filters/                    # Bộ lọc Authentication & Authorization (RBAC)
│   │   └── SecurityFilter.java     # Kiểm tra quyền: SUPER_ADMIN, ADMIN, STAFF, CUSTOMER
│   └── exception/                  # Xử lý ngoại lệ toàn hệ thống
│
├── common/                         # Tiện ích và lớp dùng chung (Shared Components)
│   ├── base/
│   │   ├── BaseRepository.java     # CRUD chung bằng Hibernate
│   │   └── BaseService.java
│   └── utils/
│       ├── GsonUtil.java           # Helper cho Gson (parse JSON)
│       └── StringUtil.java
│
├── features/                       # NƠI CHỨA CÁC TÍNH NĂNG (FEATURE-BASED)
│   │
│   ├── auth/                       # Module Xác thực
│   │   ├── controller/             # LoginServlet, RegisterServlet, LogoutServlet
│   │   ├── dto/                    # LoginRequest, RegisterDTO
│   │   ├── service/
│   │   └── AuthRepository.java     # (Nếu có query riêng liên quan đến auth)
│   │
│   ├── user/                       # Quản lý tài khoản (Profiles, Phân quyền)
│   │   ├── model/                  # User.java, Role.java (Entity Hibernate mới)
│   │   ├── controller/             # UserAdminServlet, ProfileServlet
│   │   ├── dto/                    # UserResponseDTO (Dùng Gson để trả về cho Ajax)
│   │   └── service/
│   │
│   ├── product/                    # Module Sản phẩm & Danh mục
│   │   ├── model/                  # Product.java, Category.java, Brand.java
│   │   ├── controller/             # ProductDetailServlet, ProductManagementServlet
│   │   ├── dto/
│   │   └── service/
│   │
│   ├── cart/                       # Module Giỏ hàng
│   │   ├── model/                  # CartItem.java (Có thể lưu DB hoặc Session)
│   │   ├── controller/             # CartServlet (Thêm, sửa, xóa item qua Ajax)
│   │   └── service/
│   │
│   ├── order/                      # Module Đơn hàng & Thanh toán
│   │   ├── model/                  # Order.java, OrderDetail.java, PaymentMethod.java
│   │   ├── controller/             # CheckoutServlet, OrderHistoryServlet, OrderTrackingServlet
│   │   └── service/
│   │
│   └── dashboard/                  # Thống kê (Dành cho Admin/Staff/SuperAdmin)
│       ├── controller/             # ReportServlet
│       └── service/                # Tính toán doanh thu, top sản phẩm
│
└── legacy/                         # BIỆN PHÁP TẠM THỜI: Chứa code JDBC + Model cũ để tránh conflict 
    ├── model/                      # Giữ lại các class cũ chưa kịp migrate
    └── dao/                        # Các class JDBC cũ (Sẽ xóa dần khi migrate xong)

```

### Cách quản lý giao diện (`webapp`) tương ứng:

Để đồng bộ với backend, thư mục `webapp/WEB-INF/views/` cũng nên chia theo tính năng hoặc theo phân quyền đối tượng:

```text
src/main/webapp/
├── WEB-INF/
│   └── views/
│       ├── admin/                  # Giao diện cho SUPER_ADMIN, ADMIN, STAFF
│       │   ├── product-manage.jsp
│       │   └── order-list.jsp
│       ├── client/                 # Giao diện cho CUSTOMER và Guest
│       │   ├── home.jsp
│       │   ├── product-detail.jsp
│       │   └── cart.jsp
│       └── auth/
│           ├── login.jsp
│           └── register.jsp
├── assets/                         # CSS, JS, Images
└── index.jsp

```

---

## 2. Các Module Cần Có Cho Một Trang E-Commerce Truyền Thống

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
