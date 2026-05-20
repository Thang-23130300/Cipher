```text
src/main/java/nlu/fit/web/souvenirecommerce/
├── features/
│   ├── auth/                 # Xác thực & đăng nhập
│   │   ├── controller/
│   │   │   ├── LoginServlet.java
│   │   │   ├── SignupServlet.java
│   │   │   └── LogoutServlet.java
│   │   ├── service/
│   │   │   └── AuthService.java
│   │   ├── dao/
│   │   │   └── AuthDAO.java
│   │   ├── dto/
│   │   │   ├── LoginRequest.java
│   │   │   └── AuthResponse.java
│   │   └── filter/
│   │       └── AuthFilter.java
│   │
│   ├── product/              # Quản lý sản phẩm
│   │   ├── controller/
│   │   │   ├── ProductController.java
│   │   │   ├── ProductDetailController.java
│   │   │   └── ProductTypeController.java
│   │   ├── service/
│   │   │   ├── ProductService.java
│   │   │   └── ProductTypeService.java
│   │   ├── dao/
│   │   │   ├── ProductDAO.java
│   │   │   └── CategoryDAO.java
│   │   ├── dto/
│   │   │   ├── ProductCardDTO.java
│   │   │   ├── ProductDetailDTO.java
│   │   │   └── ProductTypeDTO.java
│   │   └── model/
│   │       └── (entities liên quan)
│   │
│   ├── cart/                 # Giỏ hàng
│   │   ├── controller/
│   │   │   ├── AddCart.java
│   │   │   ├── UpdateCartController.java
│   │   │   ├── RemoveCartController.java
│   │   │   └── ShoppingCartController.java
│   │   ├── service/
│   │   │   └── CartService.java
│   │   └── dto/
│   │       └── CartItemDTO.java
│   │
│   ├── order/                # Đơn hàng
│   │   ├── controller/
│   │   │   ├── CheckoutController.java
│   │   │   ├── OrderSuccessController.java
│   │   │   ├── UserOrderController.java
│   │   │   └── admin/AdminOrderController.java
│   │   ├── service/
│   │   │   ├── OrderService.java
│   │   │   └── CheckoutService.java
│   │   └── dao/
│   │       └── OrderDAO.java
│   │
│   ├── review/               # Đánh giá sản phẩm
│   │   ├── controller/
│   │   │   ├── ReviewController.java
│   │   │   └── user/ReviewController.java
│   │   ├── service/
│   │   │   └── ReviewService.java
│   │   └── dao/
│   │       └── ReviewDAO.java
│   │
│   ├── user/                 # Hồ sơ người dùng
│   │   ├── controller/
│   │   │   ├── ProfileController.java
│   │   │   ├── ChangePassController.java
│   │   │   └── UserAddressController.java
│   │   ├── service/
│   │   │   └── UserService.java
│   │   ├── dao/
│   │   │   └── UserDAO.java
│   │   └── dto/
│   │       └── UserProfileDTO.java
│   │
│   ├── banner/               # Quản lý banner
│   │   ├── controller/
│   │   │   └── BannerController.java
│   │   ├── service/
│   │   │   └── BannerService.java
│   │   └── dao/
│   │       └── BannerDAO.java
│   │
│   ├── search/               # Tìm kiếm
│   │   ├── controller/
│   │   │   ├── SearchController.java
│   │   │   └── SearchSuggestionController.java
│   │   └── service/
│   │       └── SearchService.java
│   │
│   ├── admin/                # Admin dashboard
│   │   ├── controller/
│   │   │   ├── AdminDashboardController.java
│   │   │   ├── AdminProductController.java
│   │   │   ├── AdminCustomerController.java
│   │   │   ├── AdminCategoryController.java
│   │   │   ├── AdminRoleController.java
│   │   │   └── AdminSettingsController.java
│   │   ├── service/
│   │   │   └── AdminService.java
│   │   └── dao/
│   │       └── AdminDAO.java
│   │
│   ├── home/                 # Trang chủ
│   │   ├── controller/
│   │   │   └── HomeController.java
│   │   ├── service/
│   │   │   └── HomeService.java
│   │   └── dto/
│   │       ├── HomePageDTO.java
│   │       └── HomeCategoryDTO.java
│   │
│   └── cloudinary/           # Upload ảnh (Cloudinary)
│       ├── controller/
│       │   ├── UploadServlet.java
│       │   └── DashboardServlet.java
│       └── service/
│           └── CloudinaryService.java
│
├── shared/                   # Code dùng chung
│   ├── util/
│   │   ├── EmailUtil.java
│   │   ├── PasswordUtil.java
│   │   ├── HibernateUtil.java
│   │   ├── DBContext.java
│   │   ├── AuthorizationPolicy.java
│   │   └── ApplicationLoader.java
│   ├── dto/
│   │   ├── BaseResponse.java
│   │   └── ApiError.java
│   ├── filter/
│   │   └── HeaderFilter.java
│   ├── listener/
│   │   └── (common listeners)
│   ├── mapper/
│   │   └── ProductCardMapper.java
│   └── enums/
│       └── (shared enums)
│
├── model/                    # All entities
│   └── (toàn bộ entity models)
│
└── config/
    └── (global config)
```