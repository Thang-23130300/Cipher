## 📂 FILE MIGRATION MAPPING
### Từ Layer-Based sang Feature-Based Architecture

---

## 🔐 FEATURE: AUTH (27 files)

### Controllers (7 files)
```
Hiện Tại                                    → Nơi Mới
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
controller/auth/LoginServlet.java              → features/auth/controller/LoginServlet.java
controller/auth/SignupServlet.java             → features/auth/controller/SignupServlet.java
controller/auth/LogoutServlet.java             → features/auth/controller/LogoutServlet.java
controller/auth/SendSignupCodeServlet.java     → features/auth/controller/SendSignupCodeServlet.java
controller/auth/VerifySignupCodeServlet.java   → features/auth/controller/VerifySignupCodeServlet.java
controller/auth/CheckEmailServlet.java         → features/auth/controller/CheckEmailServlet.java
controller/BaseController.java                 → shared/util/BaseController.java (hoặc)
```

### Services (5 files)
```
Hiện Tại                                    → Nơi Mới
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
service/IEmailService.java                     → features/auth/service/IEmailService.java
service/impl/EmailServiceImpl.java              → features/auth/service/impl/EmailServiceImpl.java
service/impl/UserServiceImpl.java               → features/auth/service/impl/UserServiceImpl.java
service/UserService.java                       → shared/service/UserService.java (shared)
(thêm) UserAuthService.java                    → features/auth/service/UserAuthService.java
```

### DAOs (9 files)
```
Hiện Tại                                    → Nơi Mới
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
dao/IUserDAO.java                              → features/auth/dao/IUserDAO.java
dao/impl/UserDAOImpl.java                       → features/auth/dao/impl/UserDAOImpl.java
dao/impl/UserDAOImpl2.java                      → features/auth/dao/impl/UserDAOImpl2.java
dao/IUserCredentialIDAO.java                   → features/auth/dao/IUserCredentialIDAO.java
dao/impl/IUserCredentialIDAOImpl.java           → features/auth/dao/impl/IUserCredentialIDAOImpl.java
dao/IOAuthAccountEntityIDAO.java               → features/auth/dao/IOAuthAccountEntityIDAO.java
dao/impl/OAuthAccountIDAOImpl.java              → features/auth/dao/impl/OAuthAccountIDAOImpl.java
dao/ISessionEntityIDAO.java                    → features/auth/dao/ISessionEntityIDAO.java
dao/impl/SessionDAOImpl.java                    → features/auth/dao/impl/SessionDAOImpl.java
```

### Models (3 files)
```
model/entity/User.java                         → features/auth/model/User.java
model/entity/UserCredential.java               → features/auth/model/UserCredential.java
model/entity/UserSession.java                  → features/auth/model/UserSession.java
```

### DTOs (3 files)
```
Cần tạo mới                                    → features/auth/dto/
- LoginRequest.java
- LoginResponse.java
- SignupRequest.java
```

---

## 📦 FEATURE: PRODUCT (29 files)

### Controllers (7 files)
```
Hiện Tại                                    → Nơi Mới
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
controller/ProductController.java              → features/product/controller/ProductController.java
controller/ProductDetailController.java        → features/product/controller/ProductDetailController.java
controller/ProductDetailServlet.java           → features/product/controller/ProductDetailServlet.java
controller/ListProduct.java                    → features/product/controller/ListProductController.java
controller/ProductTypeController.java          → features/product/controller/CategoryController.java
controller/HeaderController.java               → shared/controller/HeaderController.java (shared)
controller/HomeController.java                 → features/home/controller/HomeController.java
```

### Services (4 files)
```
Hiện Tại                                    → Nơi Mới
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
service/ProductService.java                    → features/product/service/ProductService.java
service/ProductTypeService.java                → features/product/service/CategoryService.java
service/HomeService.java                       → features/home/service/HomeService.java
(thêm) ProductSearchService.java               → features/product/service/ProductSearchService.java
```

### DAOs (8 files)
```
Hiện Tại                                    → Nơi Mới
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
dao/ProductDAO.java                            → features/product/dao/ProductDAO.java
dao/IProductEntityIDAO.java                    → features/product/dao/IProductEntityIDAO.java
dao/impl/ProductIDAOImpl2.java                  → features/product/dao/impl/ProductIDAOImpl2.java
dao/CategoryDAO.java                           → features/product/dao/CategoryDAO.java
dao/ICategoryEntityIDAO.java                   → features/product/dao/ICategoryEntityIDAO.java
dao/impl/ICategoryIDAOImpl2.java                → features/product/dao/impl/ICategoryIDAOImpl2.java
dao/ProductSpecificationDAO.java               → features/product/dao/ProductSpecificationDAO.java
dao/HomeDAO.java                               → features/home/dao/HomeDAO.java
```

### Models (4 files)
```
model/entity/Product.java                      → features/product/model/Product.java
model/entity/Category.java                     → features/product/model/Category.java
model/ProductSpecification.java                → features/product/model/ProductSpecification.java
(thêm) ProductImage.java                       → features/product/model/ProductImage.java
```

### DTOs (3 files)
```
dto/ProductCardDTO.java                        → features/product/dto/ProductCardDTO.java
dto/ProductDetailDTO.java                      → features/product/dto/ProductDetailDTO.java
dto/ProductTypeDTO.java                        → features/product/dto/CategoryDTO.java
```

### Utils (3 files)
```
util/ProductCardMapper.java                    → features/product/mapper/ProductCardMapper.java
```

---

## 🛒 FEATURE: CART (6 files)

### Controllers (4 files)
```
Hiện Tại                                    → Nơi Mới
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
controller/cart/AddCart.java                   → features/cart/controller/AddCartController.java
controller/cart/RemoveCartController.java      → features/cart/controller/RemoveCartController.java
controller/cart/UpdateCartController.java      → features/cart/controller/UpdateCartController.java
controller/cart/ShoppingCartController.java    → features/cart/controller/ShoppingCartController.java
```

### Models (2 files)
```
model/cart/Cart.java                           → features/cart/model/Cart.java
model/cart/CartItem.java                       → features/cart/model/CartItem.java
```

### Cần Tạo
```
features/cart/service/CartService.java
features/cart/dto/CartDTO.java
features/cart/dto/CartItemDTO.java
```

---

## 📋 FEATURE: ORDER (6 files)

### Controllers (5 files)
```
Hiện Tại                                    → Nơi Mới
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
controller/CheckoutController.java             → features/order/controller/CheckoutController.java
controller/OrderSuccessController.java         → features/order/controller/OrderSuccessController.java
controller/UserOrderController.java            → features/order/controller/UserOrderController.java
controller/admin/AdminOrderController.java     → features/admin/controller/AdminOrderController.java
controller/ExportReportController.java         → features/admin/controller/ExportReportController.java
```

### DAOs (2 files)
```
dao/OrderDAO.java                              → features/order/dao/OrderDAO.java
(thêm interface) IOrderDAO.java                → features/order/dao/IOrderDAO.java
```

### Models (2 files)
```
model/Order.java                               → features/order/model/Order.java
model/OrderItem.java                           → features/order/model/OrderItem.java
```

### Cần Tạo
```
features/order/service/OrderService.java
features/order/service/CheckoutService.java
features/order/dto/OrderDTO.java
features/order/dto/OrderDetailDTO.java
```

---

## ⭐ FEATURE: REVIEW (3 files)

### Controllers (3 files)
```
Hiện Tại                                    → Nơi Mới
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
controller/ReviewController.java               → features/review/controller/ReviewController.java
controller/user/ReviewController.java          → features/review/controller/UserReviewController.java
(admin review) → features/admin/controller/AdminReviewController.java
```

### DAOs (1 file)
```
dao/ReviewDAO.java                             → features/review/dao/ReviewDAO.java
```

### Services (1 file)
```
service/ReviewService.java                     → features/review/service/ReviewService.java
```

### Models (2 files)
```
model/Review.java                              → features/review/model/Review.java
model/ReviewSummary.java                       → features/review/model/ReviewSummary.java
```

### Cần Tạo
```
features/review/dto/ReviewDTO.java
features/review/dto/ReviewCreateRequest.java
```

---

## 👤 FEATURE: USER (5 files)

### Controllers (4 files)
```
Hiện Tại                                    → Nơi Mới
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
controller/user/ProfileController.java         → features/user/controller/ProfileController.java
controller/user/ChangePassController.java      → features/user/controller/ChangePasswordController.java
controller/user/UserAddressController.java     → features/user/controller/AddressController.java
(admin) controller/admin/AdminCustomerController.java → features/admin/controller/
```

### Models (2 files)
```
model/entity/Address.java                      → features/user/model/Address.java
(cũng có model/Address.java) → xóa file cũ
```

### Cần Tạo
```
features/user/service/UserProfileService.java
features/user/service/AddressService.java
features/user/dao/UserAddressDAO.java
features/user/dto/UserProfileDTO.java
features/user/dto/AddressDTO.java
```

---

## 🎉 FEATURE: PROMOTION (2 files)

### DAOs (1 file)
```
dao/PromotionDAO.java                          → features/promotion/dao/PromotionDAO.java
```

### Models (1 file)
```
model/Promotion.java                           → features/promotion/model/Promotion.java
```

### Cần Tạo
```
features/promotion/service/PromotionService.java
features/promotion/service/CouponService.java
features/promotion/controller/CouponController.java
features/promotion/dto/CouponDTO.java
features/promotion/dto/PromotionDTO.java
```

---

## 🎨 FEATURE: BANNER (2 files)

### Controllers (1 file)
```
controller/BannerController.java               → features/banner/controller/BannerController.java
```

### DAOs (1 file)
```
dao/BannerDAO.java                             → features/banner/dao/BannerDAO.java
```

### Models (1 file)
```
model/Banner.java                              → features/banner/model/Banner.java
```

### Cần Tạo
```
features/banner/service/BannerService.java
features/banner/dto/BannerDTO.java
```

---

## 🔍 FEATURE: SEARCH (2 files)

### Controllers (2 files)
```
controller/SearchController.java               → features/search/controller/SearchController.java
controller/SearchSuggestionController.java     → features/search/controller/SearchSuggestionController.java
```

### DTOs (1 file)
```
dto/SearchSuggestionDTO.java                   → features/search/dto/SearchSuggestionDTO.java
```

### Cần Tạo
```
features/search/service/SearchService.java
features/search/dto/SearchRequest.java
features/search/dto/SearchResult.java
```

---

## ☁️ FEATURE: CLOUDINARY (2 files)

### Controllers (2 files)
```
controller/cloudinary/UploadServlet.java       → features/cloudinary/controller/UploadServlet.java
controller/cloudinary/DashboardServlet.java    → features/cloudinary/controller/DashboardServlet.java
```

### Services (1 file)
```
service/CloudinaryService.java                 → features/cloudinary/service/CloudinaryService.java
```

---

## 📊 FEATURE: ADMIN (7 files)

### Controllers (7 files)
```
Hiện Tại                                    → Nơi Mới
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
controller/admin/AdminDashboardController.java → features/admin/controller/AdminDashboardController.java
controller/admin/AdminProductController.java   → features/admin/controller/AdminProductController.java
controller/admin/AdminOrderController.java     → features/admin/controller/AdminOrderController.java
controller/admin/AdminCustomerController.java  → features/admin/controller/AdminCustomerController.java
controller/admin/AdminCategoryController.java  → features/admin/controller/AdminCategoryController.java
controller/admin/AdminRoleController.java      → features/admin/controller/AdminRoleController.java
controller/admin/AdminSettingsController.java  → features/admin/controller/AdminSettingsController.java
```

### DAOs (3 files)
```
dao/IPermissionEntityIDAO.java                 → features/admin/dao/IPermissionEntityIDAO.java
dao/impl/IPermissionIDAOImpl.java               → features/admin/dao/impl/IPermissionIDAOImpl.java
dao/IRoleEntityIDAO.java                       → features/admin/dao/IRoleEntityIDAO.java
dao/impl/RoleIDAOImpl.java                      → features/admin/dao/impl/RoleIDAOImpl.java
```

### Models (3 files)
```
model/PermissionGroup.java                     → features/admin/model/PermissionGroup.java
model/PermissionItem.java                      → features/admin/model/PermissionItem.java
model/entity/Role.java                         → features/admin/model/Role.java
model/entity/Permission.java                   → features/admin/model/Permission.java
```

### Cần Tạo
```
features/admin/service/AdminService.java
features/admin/service/DashboardService.java
features/admin/dto/DashboardDTO.java
features/admin/dto/StatisticsDTO.java
```

---

## 🔗 SHARED (Code Dùng Chung)

### Utilities (8 files)
```
Hiện Tại                                    → Nơi Mới
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
util/DBContext.java                            → shared/util/DBContext.java
util/HibernateUtil.java                        → shared/util/HibernateUtil.java
util/PasswordUtil.java                         → shared/util/PasswordUtil.java
util/EmailUtil.java                            → shared/util/EmailUtil.java
util/ApplicationLoader.java                    → shared/config/ApplicationLoader.java
util/AuthorizationPolicy.java                  → shared/util/AuthorizationPolicy.java
```

### Filters (2 files)
```
filter/AuthFilter.java                         → shared/filter/AuthFilter.java
filter/HeaderFilter.java                       → shared/filter/HeaderFilter.java
```

### Listeners (1 file)
```
listener/DbContextListener.java                → shared/listener/DbContextListener.java
```

### Enums (3 files)
```
enums/EmailType.java                           → shared/enums/EmailType.java
enums/Gender.java                              → shared/enums/Gender.java
enums/ProductSort.java                         → shared/enums/ProductSort.java
```

### DTOs (3 files)
```
dto/HomeCategoryDTO.java                       → features/home/dto/HomeCategoryDTO.java
dto/HomePageDTO.java                           → features/home/dto/HomePageDTO.java
```

### Base DAO Class (2 files)
```
dao/IDAO.java                                  → shared/dao/IDAO.java
dao/impl/AbstractHibernateIDAO.java            → shared/dao/impl/AbstractHibernateIDAO.java
```

### Cần Tạo trong shared/
```
dto/BaseResponse.java
dto/BaseDTO.java
dto/ApiError.java
exception/BaseException.java
exception/ValidationException.java
constants/AppConstants.java
constants/ErrorMessages.java
constants/SuccessMessages.java
```

---

## 📝 Models: Base Class

```
model/entity/BaseEntity.java                   → shared/model/BaseEntity.java
```

### Settings DAO
```
dao/SettingsDAO.java                           → config/dao/SettingsDAO.java
hoặc features/admin/dao/SettingsDAO.java
```

---

## 📊 Tóm Tắt Di Chuyển

| Thành Phần | Lượng File | Nới Di Chuyển | Ghi Chú |
|-----------|----------|------------|--------|
| Controllers | 39 | /features/ | Theo feature |
| Services | 15+ | /features/ + /shared/ | Nếu dùng chung → shared |
| DAOs | 30+ | /features/ + /shared/ | Abstract class → shared |
| DTOs | 10+ | /features/ + /shared/ | Chung → shared |
| Models | 20+ | /features/ + /shared/ | BaseEntity → shared |
| Utils | 8 | /shared/util | Tất cả dùng chung |
| Filters | 2 | /shared/filter | |
| Listeners | 1 | /shared/listener | |
| Enums | 3+ | /shared/enums | |
| **TỔNG CỘNG** | **~150** | | |

---

## 🚀 Thứ Tự Di Chuyển (Recommended)

### 1️⃣ Tạo Folder Structure (Day 1)
- [ ] Tạo tất cả /features/[name]/ folders
- [ ] Tạo /shared folders
- [ ] Tạo /config folder

### 2️⃣ Di Chuyển Shared Components (Day 1-2)
- [ ] Move /util/ → /shared/util
- [ ] Move /filter/ → /shared/filter
- [ ] Move /listener/ → /shared/listener
- [ ] Move /enums/ → /shared/enums
- [ ] Move base DAOs & DTOs → /shared/
- [ ] Move BaseEntity & model bases → /shared/model

### 3️⃣ Di Chuyển Từng Feature (Day 2-3)
- [ ] Auth → Complete move of all auth-related files
- [ ] Product → Complete move
- [ ] Cart → Complete move
- [ ] Order → Complete move
- [ ] User → Complete move
- [ ] Review → Complete move
- [ ] Promotion → Complete move
- [ ] Banner → Complete move
- [ ] Search → Complete move
- [ ] Cloudinary → Complete move
- [ ] Admin → Complete move
- [ ] Home → Complete move

### 4️⃣ Fix Imports & Compile (Day 3-4)
- [ ] Update tất cả import statements
- [ ] Run `mvn clean compile`
- [ ] Fix compile errors
- [ ] Verify build success

---

## 📌 Công Cụ Hỗ Trợ

### Bash Script untuk Di Chuyển File
```bash
#!/bin/bash
# Move auth files
mkdir -p src/main/java/nlu/fit/web/souvenirecommerce/features/auth/{controller,service,dao,dto}
mv src/main/java/nlu/fit/web/souvenirecommerce/controller/auth/* \
   src/main/java/nlu/fit/web/souvenirecommerce/features/auth/controller/
```

### IDE Refactoring
- Sử dụng "Move" refactoring trong IDE (IntelliJ IDEA)
- Nó sẽ tự động cập nhật imports
- Safer hơn manual move

---

## ⚠️ Lưu Ý Quan Trọng

1. **Backup** trước khi refactor
2. **Commit** thường xuyên
3. **Test** sau mỗi feature hoàn thành
4. **Update web.xml** nếu cần (servlet mapping)
5. Kiểm tra **circular dependencies**

---

**Cập nhật:** 2026-05-20
**Phiên bản:** 1.0


