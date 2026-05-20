# 🏗️ Feature-Based Architecture Guide
## Hướng Dẫn Chi Tiết Cấu Trúc Thư Mục Dự Án

---

## 📁 Cấu Trúc Tổng Thể

```
src/main/java/nlu/fit/web/souvenirecommerce/
├── features/
│   ├── auth/                          # 🔐 Xác thực & Đăng nhập
│   ├── product/                       # 📦 Quản lý sản phẩm
│   ├── cart/                          # 🛒 Giỏ hàng
│   ├── order/                         # 📋 Quản lý đơn hàng
│   ├── user/                          # 👤 Quản lý người dùng
│   ├── review/                        # ⭐ Đánh giá & Bình luận
│   ├── admin/                         # 📊 Admin Dashboard
│   ├── search/                        # 🔍 Tìm kiếm
│   ├── banner/                        # 🎨 Quản lý banner
│   ├── promotion/                     # 🎉 Khuyến mãi & Giảm giá
│   ├── payment/                       # 💳 Thanh toán
│   ├── notification/                  # 📧 Thông báo
│   ├── cloudinary/                    # ☁️ Quản lý hình ảnh
│   └── analytics/                     # 📈 Phân tích dữ liệu
├── shared/                            # 🔗 Code dùng chung
│   ├── util/                          # Utility functions
│   ├── dto/                           # Data Transfer Objects
│   ├── filter/                        # Servlet Filters
│   ├── listener/                      # Event Listeners
│   ├── mapper/                        # Data Mappers
│   ├── enums/                         # Enumerations
│   └── constants/                     # Constants
├── config/                            # ⚙️ Global Configuration
├── model/                             # 🗄️ Database Entities (JPA/Hibernate)
└── exception/                         # ❌ Custom Exceptions
```

---

## 📂 Cấu Trúc Chi Tiết Cho Mỗi Feature

### Mộ Pattern Tiêu Chuẩn

```
features/[feature-name]/
├── controller/              # HTTP Request Handlers
│   ├── [Feature]Controller.java
│   ├── [Feature]DetailController.java
│   └── [Feature]APIController.java
├── service/                 # Business Logic
│   ├── [Feature]Service.java
│   ├── [Feature]Validator.java
│   └── I[Feature]Service.java (Interface)
├── dao/                     # Data Access Layer
│   ├── [Feature]DAO.java
│   └── [Feature]Repository.java
├── dto/                     # Data Transfer Objects
│   ├── [Feature]DTO.java
│   ├── [Feature]Request.java
│   └── [Feature]Response.java
├── model/                   # Entity Models (nếu riêng)
│   └── [Feature].java
├── exception/               # Custom Exceptions
│   └── [Feature]Exception.java
└── mapper/                  # Data Mappers
    └── [Feature]Mapper.java
```

---

## 🔐 Feature 1: AUTH (Xác Thực)

### Cấu Trúc Thư Mục
```
features/auth/
├── controller/
│   ├── LoginServlet.java              # Xử lý đăng nhập
│   ├── SignupServlet.java             # Xử lý đăng ký
│   ├── LogoutServlet.java             # Xử lý đăng xuất
│   ├── ForgotPasswordServlet.java      # Quên mật khẩu
│   ├── VerifyEmailServlet.java         # Xác minh email
│   ├── ResetPasswordServlet.java       # Đặt lại mật khẩu
│   ├── OTPServlet.java                 # Xác thực OTP
│   └── AuthAPIController.java          # REST API endpoints
├── service/
│   ├── AuthService.java                # Xử lý logic xác thực
│   ├── PasswordService.java             # Quản lý mật khẩu
│   ├── EmailVerificationService.java    # Xác minh email
│   ├── OTPService.java                  # Dịch vụ OTP
│   ├── SessionService.java              # Quản lý session
│   └── IAuthService.java               # Interface
├── dao/
│   ├── UserDAO.java                     # Truy cập dữ liệu user
│   ├── UserCredentialDAO.java           # Thông tin đăng nhập
│   ├── SessionDAO.java                  # Session management
│   ├── OAuthAccountDAO.java             # OAuth accounts
│   └── VerificationTokenDAO.java        # Verification tokens
├── dto/
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── SignupRequest.java
│   ├── SignupResponse.java
│   ├── PasswordResetRequest.java
│   ├── UserDTO.java
│   └── OTPRequest.java
├── mapper/
│   ├── UserMapper.java
│   └── SignupMapper.java
└── exception/
    ├── AuthenticationException.java
    ├── InvalidCredentialException.java
    ├── EmailAlreadyExistsException.java
    └── TokenExpiredException.java
```

### Tệp Quan Trọng
- `LoginServlet.java` - Entry point cho đăng nhập
- `SignupServlet.java` - Entry point cho đăng ký
- `AuthService.java` - Logic xácthực chính
- `UserDAO.java` - Truy cập database cho user

### REST API Endpoints
```
POST   /api/auth/login
POST   /api/auth/signup
POST   /api/auth/logout
POST   /api/auth/forgot-password
POST   /api/auth/reset-password
GET    /api/auth/verify-email?token=xxx
POST   /api/auth/resend-otp
POST   /api/auth/verify-otp
```

---

## 📦 Feature 2: PRODUCT (Sản Phẩm)

### Cấu Trúc Thư Mục
```
features/product/
├── controller/
│   ├── ProductController.java          # Danh sách sản phẩm
│   ├── ProductDetailController.java    # Chi tiết sản phẩm
│   ├── ProductSearchController.java    # Tìm kiếm sản phẩm
│   ├── ProductFilterController.java    # Lọc sản phẩm
│   ├── ProductImageController.java     # Quản lý hình ảnh
│   ├── ProductAPIController.java       # REST API
│   └── CategoryController.java         # Danh mục
├── service/
│   ├── ProductService.java             # Logic sản phẩm
│   ├── CategoryService.java             # Logic danh mục
│   ├── ProductImageService.java         # Quản lý ảnh
│   ├── ProductSearchService.java        # Tìm kiếm & lọc
│   ├── InventoryService.java            # Quản lý kho
│   ├── ProductValidationService.java    # Validation
│   └── IProductService.java
├── dao/
│   ├── ProductDAO.java
│   ├── CategoryDAO.java
│   ├── ProductImageDAO.java
│   ├── ProductSpecificationDAO.java
│   ├── InventoryDAO.java
│   └── ProductSearchDAO.java
├── dto/
│   ├── ProductDTO.java
│   ├── ProductCardDTO.java              # Cho product card
│   ├── ProductDetailDTO.java            # Chi tiết đầy đủ
│   ├── ProductCreateRequest.java
│   ├── ProductUpdateRequest.java
│   ├── CategoryDTO.java
│   ├── ProductImageDTO.java
│   └── ProductFilterRequest.java
├── model/
│   ├── Product.java                     # Entity Product
│   ├── Category.java
│   ├── ProductImage.java
│   └── ProductSpecification.java
├── mapper/
│   ├── ProductMapper.java
│   ├── ProductCardMapper.java
│   └── CategoryMapper.java
└── exception/
    ├── ProductNotFoundException.java
    ├── InvalidProductException.java
    ├── LowStockException.java
    └── DuplicateProductException.java
```

### REST API Endpoints
```
GET    /api/products                    # Danh sách tất cả
GET    /api/products?page=1&limit=20    # Pagination
GET    /api/products/{id}               # Chi tiết
POST   /api/products                    # Tạo mới (admin)
PUT    /api/products/{id}               # Cập nhật (admin)
DELETE /api/products/{id}               # Xóa (admin)
GET    /api/products/search?q=xxx       # Tìm kiếm
GET    /api/products/filter?category=xxx # Lọc
GET    /api/categories                  # Danh sách danh mục
POST   /api/products/{id}/images        # Upload ảnh
DELETE /api/products/{id}/images/{imgId} # Xóa ảnh
```

---

## 🛒 Feature 3: CART (Giỏ Hàng)

### Cấu Trúc Thư Mục
```
features/cart/
├── controller/
│   ├── CartController.java             # Hiển thị giỏ hàng
│   ├── AddToCartController.java        # Thêm vào giỏ
│   ├── RemoveFromCartController.java   # Xóa khỏi giỏ
│   ├── UpdateCartController.java       # Cập nhật số lượng
│   ├── CartAPIController.java          # REST API
│   └── SessionCartManager.java         # Quản lý giỏ session
├── service/
│   ├── CartService.java                # Logic giỏ hàng
│   ├── CartValidationService.java      # Validation
│   ├── CartCalculationService.java     # Tính toán (giá, thuế)
│   └── SessionCartService.java         # Session cart
├── dao/
│   ├── CartDAO.java
│   ├── CartItemDAO.java
│   └── CartRepository.java
├── dto/
│   ├── CartDTO.java
│   ├── CartItemDTO.java
│   ├── AddToCartRequest.java
│   ├── UpdateCartRequest.java
│   └── CartSummaryDTO.java
├── model/
│   ├── Cart.java
│   └── CartItem.java
├── mapper/
│   └── CartMapper.java
└── exception/
    ├── CartNotFoundException.java
    ├── InvalidCartItemException.java
    ├── OutOfStockException.java
    └── InvalidQuantityException.java
```

### REST API Endpoints
```
GET    /api/cart                        # Xem giỏ
POST   /api/cart/items                  # Thêm item
PUT    /api/cart/items/{itemId}         # Cập nhật item
DELETE /api/cart/items/{itemId}         # Xóa item
DELETE /api/cart                        # Xóa hết giỏ
POST   /api/cart/checkout               # Thanh toán
GET    /api/cart/summary                # Tóm tắt giỏ
```

---

## 📋 Feature 4: ORDER (Đơn Hàng)

### Cấu Trúc Thư Mục
```
features/order/
├── controller/
│   ├── CheckoutController.java         # Flow thanh toán
│   ├── OrderController.java
│   ├── OrderDetailController.java
│   ├── UserOrderController.java        # Đơn hàng của user
│   ├── OrderAPIController.java         # REST API
│   ├── OrderSuccessController.java
│   └── RefundController.java
├── service/
│   ├── OrderService.java               # Logic đơn hàng
│   ├── CheckoutService.java            # Flow thanh toán
│   ├── OrderValidationService.java
│   ├── RefundService.java              # Hoàn lại tiền
│   ├── ShippingService.java            # Vận chuyển
│   ├── PaymentService.java             # Thanh toán
│   └── OrderStatusService.java         # Quản lý trạng thái
├── dao/
│   ├── OrderDAO.java
│   ├── OrderItemDAO.java
│   ├── OrderStatusDAO.java
│   ├── ShippingDAO.java
│   ├── RefundDAO.java
│   └── PaymentDAO.java
├── dto/
│   ├── OrderDTO.java
│   ├── OrderDetailDTO.java
│   ├── OrderItemDTO.java
│   ├── CheckoutRequest.java
│   ├── CheckoutResponse.java
│   ├── OrderStatusDTO.java
│   ├── ShippingDTO.java
│   ├── RefundRequest.java
│   └── PaymentDTO.java
├── model/
│   ├── Order.java
│   ├── OrderItem.java
│   ├── OrderStatus.java
│   ├── Shipping.java
│   ├── Refund.java
│   └── Payment.java
├── mapper/
│   ├── OrderMapper.java
│   └── ShippingMapper.java
└── exception/
    ├── OrderNotFoundException.java
    ├── InvalidOrderException.java
    ├── PaymentFailedException.java
    ├── RefundFailedException.java
    └── InvalidShippingException.java
```

### REST API Endpoints
```
POST   /api/checkout                    # Tạo đơn hàng
GET    /api/orders                      # Danh sách đơn của user
GET    /api/orders/{orderId}            # Chi tiết đơn
PUT    /api/orders/{orderId}            # Cập nhật đơn
DELETE /api/orders/{orderId}            # Hủy đơn
POST   /api/orders/{orderId}/refund     # Hoàn lại tiền
GET    /api/orders/{orderId}/status     # Trạng thái đơn
POST   /api/orders/{orderId}/track      # Theo dõi đơn
```

---

## 👤 Feature 5: USER (Người Dùng)

### Cấu Trúc Thư Mục
```
features/user/
├── controller/
│   ├── ProfileController.java          # Hồ sơ user
│   ├── AddressController.java          # Địa chỉ
│   ├── ChangePasswordController.java   # Đổi mật khẩu
│   ├── UserPreferencesController.java  # Tùy chỉnh
│   └── UserAPIController.java
├── service/
│   ├── UserService.java
│   ├── ProfileService.java
│   ├── AddressService.java
│   ├── UserPreferenceService.java
│   └── AvatarService.java
├── dao/
│   ├── UserDAO.java
│   ├── UserAddressDAO.java
│   └── UserPreferenceDAO.java
├── dto/
│   ├── UserProfileDTO.java
│   ├── UserAddressDTO.java
│   ├── PreferenceDTO.java
│   └── PasswordChangeRequest.java
├── mapper/
│   ├── UserMapper.java
│   └── AddressMapper.java
└── exception/
    ├── UserNotFoundException.java
    ├── InvalidAddressException.java
    └── DuplicateAddressException.java
```

### REST API Endpoints
```
GET    /api/users/profile               # Lấy hồ sơ
PUT    /api/users/profile               # Cập nhật hồ sơ
POST   /api/users/avatar                # Upload avatar
GET    /api/users/addresses             # Danh sách địa chỉ
POST   /api/users/addresses             # Thêm địa chỉ
PUT    /api/users/addresses/{id}        # Cập nhật địa chỉ
DELETE /api/users/addresses/{id}        # Xóa địa chỉ
POST   /api/users/change-password       # Đổi mật khẩu
```

---

## ⭐ Feature 6: REVIEW (Đánh Giá)

### Cấu Trúc Thư Mục
```
features/review/
├── controller/
│   ├── ReviewController.java
│   ├── RatingController.java
│   └── ReviewAPIController.java
├── service/
│   ├── ReviewService.java
│   ├── ReviewModeration.java
│   ├── RatingCalculationService.java
│   └── ReviewValidationService.java
├── dao/
│   ├── ReviewDAO.java
│   └── ReviewPhotoDAO.java
├── dto/
│   ├── ReviewDTO.java
│   ├── ReviewCreateRequest.java
│   ├── ReviewResponse.java
│   └── RatingDTO.java
├── model/
│   ├── Review.java
│   └── ReviewPhoto.java
├── mapper/
│   └── ReviewMapper.java
└── exception/
    ├── ReviewNotFoundException.java
    ├── InvalidReviewException.java
    └── DuplicateReviewException.java
```

### REST API Endpoints
```
GET    /api/products/{id}/reviews       # Đánh giá của sản phẩm
POST   /api/reviews                     # Viết đánh giá
PUT    /api/reviews/{id}                # Cập nhật đánh giá
DELETE /api/reviews/{id}                # Xóa đánh giá
GET    /api/reviews/{id}                # Chi tiết đánh giá
POST   /api/reviews/{id}/helpful        # Đánh dấu hữu ích
```

---

## 📊 Feature 7: ADMIN (Bảng Điều Khiển Quản Trị)

### Cấu Trúc Thư Mục
```
features/admin/
├── controller/
│   ├── DashboardController.java        # Tổng quan
│   ├── AdminProductController.java     # Quản lý SP
│   ├── AdminOrderController.java       # Quản lý đơn
│   ├── AdminCustomerController.java    # Quản lý KH
│   ├── AdminCategoryController.java    # Quản lý danh mục
│   ├── AdminBannerController.java
│   ├── AdminRoleController.java
│   ├── AdminSettingsController.java
│   ├── AdminReportController.java
│   └── AdminAPIController.java
├── service/
│   ├── DashboardService.java
│   ├── AdminProductService.java
│   ├── AdminOrderService.java
│   ├── AdminCustomerService.java
│   ├── ReportService.java
│   ├── AnalyticsService.java
│   └── AdminAuthenticationService.java
├── dao/
│   ├── AdminDAO.java
│   ├── DashboardDAO.java
│   ├── ReportDAO.java
│   └── AnalyticsDAO.java
├── dto/
│   ├── DashboardDTO.java
│   ├── StatisticsDTO.java
│   ├── ReportDTO.java
│   ├── AdminFilterRequest.java
│   └── BulkActionRequest.java
├── mapper/
│   └── AdminMapper.java
└── exception/
    ├── AccessDeniedException.java
    └── InvalidAdminActionException.java
```

---

## 🔍 Feature 8: SEARCH (Tìm Kiếm)

### Cấu Trúc Thư Mục
```
features/search/
├── controller/
│   ├── SearchController.java
│   ├── SearchSuggestionController.java
│   └── SearchAPIController.java
├── service/
│   ├── SearchService.java
│   ├── SearchIndexService.java
│   ├── FullTextSearchService.java
│   └── SearchSuggestionService.java
├── dao/
│   ├── SearchDAO.java
│   └── SearchHistoryDAO.java
├── dto/
│   ├── SearchRequest.java
│   ├── SearchResult.java
│   ├── SearchSuggestion.java
│   └── SearchFilter.java
└── exception/
    └── SearchException.java
```

---

## 🎉 Feature 9: PROMOTION (Khuyến Mãi)

### Cấu Trúc Thư Mục
```
features/promotion/
├── controller/
│   ├── CouponController.java
│   ├── PromotionController.java
│   └── PromotionAPIController.java
├── service/
│   ├── CouponService.java
│   ├── PromotionService.java
│   ├── DiscountCalculationService.java
│   └── CouponValidationService.java
├── dao/
│   ├── CouponDAO.java
│   ├── PromotionDAO.java
│   └── PromotionProductDAO.java
├── dto/
│   ├── CouponDTO.java
│   ├── PromotionDTO.java
│   ├── ApplyCouponRequest.java
│   └── DiscountCalculationDTO.java
├── model/
│   ├── Coupon.java
│   ├── Promotion.java
│   └── PromotionProduct.java
└── exception/
    ├── InvalidCouponException.java
    └── CouponExpiredException.java
```

---

## 🔗 SHARED (Code Dùng Chung)

### Cấu Trúc
```
shared/
├── util/
│   ├── PasswordUtil.java               # Mã hóa mật khẩu
│   ├── EmailUtil.java                  # Gửi email
│   ├── DateTimeUtil.java               # Xử lý ngày giờ
│   ├── ValidationUtil.java             # Validation chung
│   ├── HibernateUtil.java              # Hibernate helper
│   ├── DBContext.java                  # Database context
│   ├── StringUtil.java                 # String utilities
│   └── FileUtil.java                   # File handling
├── dto/
│   ├── BaseResponse.java               # Response chuẩn
│   ├── ApiError.java                   # Error response
│   ├── PaginationDTO.java              # Pagination
│   └── BaseDTO.java                    # Base DTO
├── filter/
│   ├── AuthFilter.java                 # Authorization
│   ├── CorsFilter.java                 # CORS
│   ├── LoggingFilter.java              # Logging
│   ├── HeaderFilter.java               # Header validation
│   └── SecurityFilter.java             # Security
├── listener/
│   ├── ApplicationListener.java        # App startup
│   └── RequestListener.java            # Request tracking
├── mapper/
│   ├── BaseMapper.java
│   └── SpecificationMapper.java
├── enums/
│   ├── OrderStatus.java
│   ├── PaymentStatus.java
│   ├── UserRole.java
│   ├── UserStatus.java
│   ├── DiscountType.java
│   └── ShippingMethod.java
├── constants/
│   ├── AppConstants.java
│   ├── ErrorMessages.java
│   ├── SuccessMessages.java
│   └── EmailTemplates.java
└── exception/
    ├── BaseException.java
    ├── ValidationException.java
    ├── DataIntegrityException.java
    └── InternalServerException.java
```

---

## ⚙️ CONFIG (Cấu Hình Toàn Cục)

```
config/
├── HibernateConfig.java                # Cấu hình Hibernate
├── DatabaseConfig.java                 # Cấu hình Database
├── EmailConfig.java                    # Cấu hình Email
├── CloudinaryConfig.java               # Cấu hình Cloudinary
├── SecurityConfig.java                 # Cấu hình bảo mật
├── AppProperties.java                  # Properties
└── CORSConfig.java                     # CORS configuration
```

---

## 🗄️ MODEL (Entities)

```
model/
├── User.java
├── UserCredential.java
├── Product.java
├── Category.java
├── ProductImage.java
├── ProductSpecification.java
├── Cart.java
├── CartItem.java
├── Order.java
├── OrderItem.java
├── OrderStatus.java
├── Review.java
├── ReviewPhoto.java
├── Coupon.java
├── Promotion.java
├── Banner.java
├── Address.java
├── Payment.java
├── Shipping.java
├── Notification.java
├── AuditLog.java
└── Role.java
```

---

## 📋 Quy Tắc Đặt Tên (Naming Conventions)

### Controllers
- Servlet: `[Feature]Servlet.java` (e.g., `LoginServlet.java`)
- Normal Controller: `[Feature]Controller.java` (e.g., `ProductController.java`)
- API Controller: `[Feature]APIController.java` (e.g., `ProductAPIController.java`)

### Services
- Service Class: `[Feature]Service.java`
- Interface: `I[Feature]Service.java`
- Helper: `[Feature]Helper.java` or `[Feature]Processor.java`

### DAOs
- DAO: `[Feature]DAO.java` hoặc `[Feature]Repository.java`

### DTOs
- Transfer Object: `[Feature]DTO.java`
- Request: `[Feature]Request.java` hoặc `[Feature]CreateRequest.java`
- Response: `[Feature]Response.java` hoặc `[Feature]DTO.java`

### Models
- Entity: `[Feature].java` (singular, e.g., `Product.java`)
- Value Object: `[Feature]VO.java`

### Utilities
- Utility: `[Function]Util.java` (e.g., `PasswordUtil.java`)
- Helper: `[Function]Helper.java`

### Exceptions
- Custom: `[Feature]Exception.java` hoặc `[Specific]Exception.java`

---

## 🚀 Bước Tạo Feature Mới

### 1. Tạo thư mục cấu trúc
```bash
mkdir -p /features/[feature-name]/{controller,service,dao,dto,mapper,exception}
```

### 2. Tạo files cơ bản
- `[Feature]Service.java` - business logic
- `[Feature]DAO.java` - data access
- `[Feature]Controller.java` - request handler
- `[Feature]DTO.java` - data transfer

### 3. Update pom.xml nếu cần dependencies mới

### 4. Tạo unit tests cho từng component

### 5. Tạo JSP views nếu cần (trong `/src/main/webapp/`)

### 6. Update web.xml cho routing nếu dùng Servlet

---

## 📌 Import Chuẩn Cho Mỗi Feature

### Controller
```java
package nlu.fit.web.souvenirecommerce.features.[feature].controller;

import jakarta.servlet.*;
import nlu.fit.web.souvenirecommerce.features.[feature].service.*;
import nlu.fit.web.souvenirecommerce.features.[feature].dto.*;
import nlu.fit.web.souvenirecommerce.shared.util.*;
```

### Service
```java
package nlu.fit.web.souvenirecommerce.features.[feature].service;

import nlu.fit.web.souvenirecommerce.features.[feature].dao.*;
import nlu.fit.web.souvenirecommerce.features.[feature].dto.*;
import nlu.fit.web.souvenirecommerce.model.*;
import nlu.fit.web.souvenirecommerce.shared.util.*;
```

### DAO
```java
package nlu.fit.web.souvenirecommerce.features.[feature].dao;

import nlu.fit.web.souvenirecommerce.model.*;
import nlu.fit.web.souvenirecommerce.shared.util.*;
```

---

## 🧪 Testing Structure

```
src/test/java/nlu/fit/web/souvenirecommerce/features/[feature]/
├── controller/
│   └── [Feature]ControllerTest.java
├── service/
│   └── [Feature]ServiceTest.java
└── dao/
    └── [Feature]DAOTest.java
```

---

## 📦 Cách sử dụng Feature Module trong dự án khác

Khi một feature cần sử dụng components từ feature khác:

### ❌ Sai (Direct Import)
```java
import nlu.fit.web.souvenirecommerce.features.auth.service.AuthService;
```

### ✅ Đúng (Through Facade/Interface)
```java
// Tạo facade trong shared
import nlu.fit.web.souvenirecommerce.shared.service.AuthServiceFacade;
```

---

## 🔄 Dependency Injection Pattern

Khuyến khích sử dụng Constructor Injection:

```java
public class ProductService {
    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;
    
    // Constructor Injection
    public ProductService(ProductDAO productDAO, CategoryDAO categoryDAO) {
        this.productDAO = productDAO;
        this.categoryDAO = categoryDAO;
    }
}
```

---

**Cập nhật:** 2026-05-20
**Phiên bản:** 1.0


