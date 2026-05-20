# 📋 Roadmap Phát Triển Trang Thương Mại Điện Tử Souvenir E-Commerce

**Stack Công Nghệ:** Jakarta Servlet, Hibernate ORM, MySQL, JSP/JSTL, Cloudinary, SMTP
**Cấu Trúc Dự Án:** Feature-Based Architecture
**Ngày Tạo:** 2026-05-20

---

## 📊 Tóm Tắt Tiến Độ

| Giai đoạn | Tính năng | Trạng thái | % |
|-----------|----------|-----------|---|
| Phase 0 | Cấu trúc & Setup | ⏳ Đang làm | 0% |
| Phase 1 | Auth & User Management | ⏳ Đang làm | 30% |
| Phase 2 | Product Management | ⏳ Đang làm | 20% |
| Phase 3 | Shopping & Order | ⏳ Đang làm | 15% |
| Phase 4 | Admin Dashboard | ⏳ Đang làm | 10% |
| Phase 5 | Optimization & Deployment | ❌ Chưa bắt đầu | 0% |

---

## 🔴 PHASE 0: CẤU TRÚC VÀ SETUP

### 0.1 Refactor Cấu Trúc Dự Án (Feature-Based)
- [ ] **0.1.1** Tạo folder structure theo Feature-Based Architecture
  - [ ] Tạo thư mục `/features/auth/`
  - [ ] Tạo thư mục `/features/product/`
  - [ ] Tạo thư mục `/features/cart/`
  - [ ] Tạo thư mục `/features/order/`
  - [ ] Tạo thư mục `/features/user/`
  - [ ] Tạo thư mục `/features/review/`
  - [ ] Tạo thư mục `/features/admin/`
  - [ ] Tạo thư mục `/features/search/`
  - [ ] Tạo thư mục `/features/banner/`
  - [ ] Tạo thư mục `/features/promotion/`
  - [ ] Tạo thư mục `/features/payment/`
  - [ ] Tạo thư mục `/shared/` cho code dùng chung
  - [ ] Tạo thư mục `/config/` cho global configuration

- [ ] **0.1.2** Di chuyển code hiện tại vào structure mới
  - [ ] Move auth controllers → features/auth/controller/
  - [ ] Move product controllers → features/product/controller/
  - [ ] Move cart controllers → features/cart/controller/
  - [ ] Move order controllers → features/order/controller/
  - [ ] Move user controllers → features/user/controller/
  - [ ] Move review controllers → features/review/controller/
  - [ ] Move admin controllers → features/admin/controller/
  - [ ] Move all services → tương ứng trong features/
  - [ ] Move all DAOs → tương ứng trong features/
  - [ ] Move all DTOs → tương ứng trong features/
  - [ ] Move shared utilities → shared/util/
  - [ ] Move global enums → shared/enums/
  - [ ] Move filters → shared/filter/
  - [ ] Move listeners → shared/listener/

- [ ] **0.1.3** Update all import statements
  - [ ] Cập nhật imports trong tất cả Java files
  - [ ] Fix compile errors
  - [ ] Run unit tests để xác thực

### 0.2 Cải Thiện Database Schema
- [ ] **0.2.1** Review & optimize schema hiện tại
  - [ ] Thêm indexes cho performance
  - [ ] Thêm foreign keys đầy đủ
  - [ ] Thêm unique constraints
  - [ ] Thêm check constraints nếu cần

- [ ] **0.2.2** Thêm bảng còn thiếu
  - [ ] `product_images` - lưu nhiều ảnh cho sản phẩm
  - [ ] `promotions` - quản lý khuyến mãi
  - [ ] `promotion_products` - áp dụng khuyến mãi cho sản phẩm
  - [ ] `notifications` - thông báo cho user
  - [ ] `audit_logs` - ghi log các hoạt động
  - [ ] `settings` - cấu hình hệ thống

### 0.3 Setup & Configuration
- [ ] **0.3.1** Cấu hình build tool & dependencies
  - [ ] Update pom.xml với dependencies cần thiết
  - [ ] Thêm dependency cho logging
  - [ ] Thêm dependency cho validation
  - [ ] Thêm dependency cho JSON mapping
  - [ ] Thêm dependency cho testing (JUnit, Mockito)

- [ ] **0.3.2** Setup configuration files
  - [ ] Tạo application-dev.properties
  - [ ] Tạo application-prod.properties
  - [ ] Tạo application-test.properties
  - [ ] Setup logback.xml cho logging
  - [ ] Setup hibernate.cfg.xml

- [ ] **0.3.3** Setup CI/CD pipeline
  - [ ] Tạo GitHub Actions workflow
  - [ ] Setup automated testing
  - [ ] Setup automated build
  - [ ] Setup automated deployment

---

## 🟢 PHASE 1: AUTHENTICATION & USER MANAGEMENT

### 1.1 Authentication Feature
- [ ] **1.1.1** Enhance User Registration
  - [ ] ✅ Email verification system (SMTP)
  - [ ] ✅ OTP verification
  - [ ] ✅ Password strength validation
  - [ ] Implement CAPTCHA for bot prevention
  - [ ] Implement rate limiting
  - [ ] Add user profile picture upload
  - [ ] Validate business rules (email format, password policy)

- [ ] **1.1.2** Improve Login System
  - [ ] ✅ Basic login functionality
  - [ ] Social OAuth login (Google, Facebook)
  - [ ] Remember me functionality
  - [ ] Session timeout management
  - [ ] Login attempt tracking & lockout
  - [ ] IP whitelist for admin accounts
  - [ ] Two-factor authentication (2FA)

- [ ] **1.1.3** Password Management
  - [ ] ✅ Change password feature
  - [ ] Forgot password flow
  - [ ] Password reset via email
  - [ ] Password history (không reuse recent passwords)
  - [ ] Secure password reset token generation

- [ ] **1.1.4** Session Management
  - [ ] ✅ Session creation & management
  - [ ] Session timeout
  - [ ] Multi-device login tracking
  - [ ] Session invalidation on logout
  - [ ] Concurrent session management

### 1.2 User Management
- [ ] **1.2.1** User Profile Management
  - [ ] ✅ View & edit profile
  - [ ] Update avatar/profile picture
  - [ ] Update personal information (name, email, phone)
  - [ ] Update address information
  - [ ] Manage multiple addresses
  - [ ] Set default address for delivery
  - [ ] Soft delete user account
  - [ ] Export user data (GDPR compliance)

- [ ] **1.2.2** Address Management
  - [ ] ✅ Add address
  - [ ] Edit address
  - [ ] Delete address
  - [ ] Set default delivery address
  - [ ] Validate address format
  - [ ] Auto-fill address (Google Places API)
  - [ ] Save address for quick checkout

- [ ] **1.2.3** User Preferences
  - [ ] Set language preference (English/Vietnamese)
  - [ ] Set notification preferences
  - [ ] Set currency preference
  - [ ] Privacy settings
  - [ ] Newsletter subscription

### 1.3 Role & Permission Management
- [ ] **1.3.1** Role System
  - [ ] ✅ Admin role
  - [ ] ✅ Customer role
  - [ ] Create new roles (Staff, Manager, etc.)
  - [ ] Assign roles to users
  - [ ] Remove roles from users
  - [ ] Edit role permissions

- [ ] **1.3.2** Permission System
  - [ ] ✅ Permission-based access control
  - [ ] ✅ Role-permission mapping
  - [ ] Create custom permissions
  - [ ] Edit permissions
  - [ ] Delete permissions
  - [ ] Audit permission changes

### 1.4 User Authentication API
- [ ] **1.4.1** REST API Endpoints
  - [ ] POST /api/auth/register - User registration
  - [ ] POST /api/auth/login - User login
  - [ ] POST /api/auth/logout - User logout
  - [ ] POST /api/auth/forgot-password - Forgot password
  - [ ] POST /api/auth/reset-password - Reset password
  - [ ] PUT /api/users/profile - Update profile
  - [ ] GET /api/users/profile - Get profile
  - [ ] POST /api/users/change-password - Change password

---

## 🔵 PHASE 2: PRODUCT MANAGEMENT

### 2.1 Product Management
- [ ] **2.1.1** Product CRUD Operations
  - [ ] ✅ Get all products (với pagination)
  - [ ] ✅ Get product by ID
  - [ ] ✅ Create product (Backend)
  - [ ] ✅ Update product
  - [ ] ✅ Delete product (soft delete)
  - [ ] Bulk import products (CSV/Excel)
  - [ ] Bulk delete products
  - [ ] Duplicate product

- [ ] **2.1.2** Product Details
  - [ ] ✅ Product name & description
  - [ ] ✅ Product price & cost
  - [ ] ✅ Product stock/inventory
  - [ ] ✅ Product images (multiple)
  - [ ] ✅ Product category
  - [ ] Product SKU management
  - [ ] Product barcode/EAN
  - [ ] Product specifications (attributes)
  - [ ] Product variants (size, color, etc.)
  - [ ] Weight & dimensions
  - [ ] Manufacturing info (origin, date)

- [ ] **2.1.3** Product Images
  - [ ] ✅ Upload images (Cloudinary)
  - [ ] ✅ Display product images
  - [ ] Set primary image
  - [ ] Reorder images
  - [ ] Delete images
  - [ ] Image optimization (compression, resizing)
  - [ ] Generate thumbnails
  - [ ] Support for alt text for accessibility

- [ ] **2.1.4** Product Specifications
  - [ ] ✅ Add product specifications
  - [ ] ✅ Edit specifications
  - [ ] ✅ Delete specifications
  - [ ] Auto-populate common specs
  - [ ] Product comparison tool

### 2.2 Category Management
- [ ] **2.2.1** Category CRUD
  - [ ] ✅ Get all categories
  - [ ] ✅ Get category by ID
  - [ ] ✅ Create category
  - [ ] ✅ Update category
  - [ ] ✅ Delete category
  - [ ] Nested categories (multi-level)
  - [ ] Category image/icon

- [ ] **2.2.2** Category Organization
  - [ ] Parent-child relationship
  - [ ] Category ordering
  - [ ] Active/inactive categories
  - [ ] Category descriptions & SEO
  - [ ] Category permissions (who can manage)

### 2.3 Product Search & Filtering
- [ ] **2.3.1** Basic Search
  - [ ] ✅ Search by product name
  - [ ] ✅ Search suggestions
  - [ ] Search history
  - [ ] Advanced search form
  - [ ] Search results pagination

- [ ] **2.3.2** Filtering & Sorting
  - [ ] Filter by category
  - [ ] Filter by price range
  - [ ] Filter by rating
  - [ ] Filter by in-stock status
  - [ ] Filter by product attributes
  - [ ] Sort by name, price, rating, newest
  - [ ] Multi-select filters

- [ ] **2.3.3** Full-Text Search Optimization
  - [ ] Implement full-text search indices
  - [ ] Search query optimization
  - [ ] Fuzzy search for typos
  - [ ] Search analytics

### 2.4 Product Inventory Management
- [ ] **2.4.1** Stock Management
  - [ ] ✅ Track stock levels
  - [ ] ✅ Low stock alerts
  - [ ] Stock adjustment
  - [ ] Stock history/audit log
  - [ ] Reserve stock for orders
  - [ ] Backorder functionality

- [ ] **2.4.2** Inventory Reports
  - [ ] Stock level report
  - [ ] Stock movement report
  - [ ] Expiry date tracking (for perishables)
  - [ ] SKU management report

### 2.5 Product API Endpoints
- [ ] **2.5.1** REST API for Products
  - [ ] GET /api/products - List all
  - [ ] GET /api/products/{id} - Get by ID
  - [ ] POST /api/products - Create
  - [ ] PUT /api/products/{id} - Update
  - [ ] DELETE /api/products/{id} - Delete
  - [ ] GET /api/products/search - Search
  - [ ] GET /api/products/filter - Filter
  - [ ] GET /api/products/{id}/images - Get images
  - [ ] POST /api/products/{id}/images - Add image
  - [ ] DELETE /api/products/{id}/images/{imageId} - Delete image

---

## 🟡 PHASE 3: SHOPPING & ORDER MANAGEMENT

### 3.1 Shopping Cart
- [ ] **3.1.1** Cart CRUD Operations
  - [ ] ✅ Add to cart
  - [ ] ✅ View cart
  - [ ] ✅ Update cart quantity
  - [ ] ✅ Remove from cart
  - [ ] ✅ Clear cart
  - [ ] Save cart for later (wishlist)
  - [ ] Cart persistence (localStorage + database)
  - [ ] Cart sharing feature

- [ ] **3.1.2** Cart Features
  - [ ] Show product availability in cart
  - [ ] Show product price changes
  - [ ] Apply coupon/discount
  - [ ] Calculate shipping cost
  - [ ] Calculate tax
  - [ ] Show total price breakdown
  - [ ] Stock check before checkout
  - [ ] Note restrictions (expired items, unavailable)

- [ ] **3.1.3** Cart API
  - [ ] GET /api/cart - Get cart
  - [ ] POST /api/cart/items - Add item
  - [ ] PUT /api/cart/items/{itemId} - Update item
  - [ ] DELETE /api/cart/items/{itemId} - Remove item
  - [ ] POST /api/cart/checkout - Proceed to checkout

### 3.2 Checkout Process
- [ ] **3.2.1** Checkout Steps
  - [ ] ✅ Review cart
  - [ ] ✅ Shipping address selection
  - [ ] ✅ Shipping method selection
  - [ ] ✅ Payment method selection
  - [ ] ✅ Order review
  - [ ] ✅ Order confirmation

- [ ] **3.2.2** Shipping Management
  - [ ] Multiple shipping methods
  - [ ] Real-time shipping cost calculation
  - [ ] Shipping address validation
  - [ ] Tracking number generation
  - [ ] Carrier integration (if applicable)
  - [ ] Store pickup option

- [ ] **3.2.3** Payment Processing
  - [ ] ✅✅ Basic payment support
  - [ ] Credit/Debit card payment (via payment gateway)
  - [ ] Bank transfer payment
  - [ ] Wallet payment
  - [ ] COD (Cash on Delivery)
  - [ ] Payment webhook handling
  - [ ] Transaction logging

### 3.3 Order Management
- [ ] **3.3.1** Order CRUD
  - [ ] ✅ Create order
  - [ ] ✅ View order
  - [ ] ✅ Update order status
  - [ ] ✅ Cancel order
  - [ ] Edit order (if not shipped)
  - [ ] Reorder from previous order

- [ ] **3.3.2** Order Details
  - [ ] Order ID & date
  - [ ] Order items (product, quantity, price)
  - [ ] Shipping address
  - [ ] Billing address
  - [ ] Shipping method & cost
  - [ ] Tax & total amount
  - [ ] Payment method & status
  - [ ] Order status timeline

- [ ] **3.3.3** Order Status Management
  - [ ] ✅ Pending
  - [ ] ✅ Processing
  - [ ] ✅ Shipped
  - [ ] ✅ Delivered
  - [ ] ✅ Cancelled
  - [ ] ✅ Refunded
  - [ ] Custom status flows
  - [ ] Status change notifications

- [ ] **3.3.4** Order History
  - [ ] User order history
  - [ ] Order filtering (by status, date)
  - [ ] Order search
  - [ ] Re-order functionality
  - [ ] Order export (PDF, CSV)

- [ ] **3.3.5** Order API
  - [ ] POST /api/orders - Create order
  - [ ] GET /api/orders - Get user orders
  - [ ] GET /api/orders/{orderId} - Get order details
  - [ ] PUT /api/orders/{orderId} - Update order
  - [ ] DELETE /api/orders/{orderId} - Cancel order
  - [ ] POST /api/orders/{orderId}/refund - Refund order

### 3.4 Refund & Return Management
- [ ] **3.4.1** Return Process
  - [ ] Create return request
  - [ ] Return reason & description
  - [ ] Return status tracking
  - [ ] Return authorization number
  - [ ] Return shipping label
  - [ ] Bulk return management

- [ ] **3.4.2** Refund Processing
  - [ ] Automatic refund calculation
  - [ ] Refund status tracking
  - [ ] Refund payment method options
  - [ ] Refund timeline
  - [ ] Partial refund support

---

## 🟢 PHASE 4: PRODUCT REVIEWS & RATINGS

### 4.1 Review System
- [ ] **4.1.1** Customer Reviews
  - [ ] ✅ Add review
  - [ ] ✅ Edit review
  - [ ] ✅ Delete review
  - [ ] ✅ Display reviews
  - [ ] Review moderation
  - [ ] Review approval workflow
  - [ ] Photo upload with review
  - [ ] Video upload with review (optional)

- [ ] **4.1.2** Rating System
  - [ ] ✅ 5-star rating
  - [ ] Star distribution display
  - [ ] Product rating calculation
  - [ ] Review count
  - [ ] Helpful review voting
  - [ ] Review filtering (by rating, date, helpfulness)

- [ ] **4.1.3** Review Features
  - [ ] Review title & content
  - [ ] Verified purchase badge
  - [ ] Author name (can be anonymous)
  - [ ] Review date & last edited
  - [ ] Review report (spam, inappropriate)

- [ ] **4.1.4** Admin Review Management
  - [ ] Review approval/rejection
  - [ ] Review deletion
  - [ ] Spam detection
  - [ ] Review reports management

---

## 🔵 PHASE 5: ADMIN DASHBOARD

### 5.1 Dashboard Overview
- [ ] **5.1.1** Dashboard Metrics
  - [ ] ✅ Total sales (today, this week, this month)
  - [ ] ✅ Total orders (today, this week, this month)
  - [ ] ✅ Total customers
  - [ ] Total products
  - [ ] Revenue charts & graphs
  - [ ] Sales trend analysis
  - [ ] Top selling products
  - [ ] Top customers

- [ ] **5.1.2** Quick Stats
  - [ ] Pending orders count
  - [ ] New customers count
  - [ ] Low stock products count
  - [ ] Unreviewed reviews count
  - [ ] Support tickets count

### 5.2 Product Management Interface
- [ ] **5.2.1** Product Management Admin Panel
  - [ ] ✅ List all products (with search, filter, sort)
  - [ ] ✅ Create product form
  - [ ] ✅ Edit product form
  - [ ] ✅ Delete product (with confirmation)
  - [ ] Bulk actions (edit, delete, export)
  - [ ] Product import (CSV, Excel)
  - [ ] Product export
  - [ ] Quick edit feature

- [ ] **5.2.2** Category Management
  - [ ] ✅ List categories
  - [ ] ✅ Create category
  - [ ] ✅ Edit category
  - [ ] ✅ Delete category
  - [ ] Reorder categories
  - [ ] Category-product mapping view

- [ ] **5.2.3** Inventory Management
  - [ ] Stock level view
  - [ ] Stock adjustment
  - [ ] Low stock alerts & management
  - [ ] Stock history
  - [ ] Stock forecasting

### 5.3 Order Management Interface
- [ ] **5.3.1** Order Management Admin Panel
  - [ ] ✅ List all orders (with search, filter, sort)
  - [ ] ✅ View order details
  - [ ] ✅ Update order status
  - [ ] ✅ Edit order
  - [ ] ✅ Cancel order with reason
  - [ ] Print order/invoice
  - [ ] Generate shipping labels
  - [ ] Bulk order actions

- [ ] **5.3.2** Order Analytics
  - [ ] Order status breakdown
  - [ ] Order value distribution
  - [ ] Payment method statistics
  - [ ] Shipping method statistics
  - [ ] Refund statistics

### 5.4 Customer Management Interface
- [ ] **5.4.1** Customer Management
  - [ ] ✅ List all customers
  - [ ] ✅ View customer details
  - [ ] ✅ Edit customer info
  - [ ] ✅ Manage customer roles
  - [ ] Send message to customer
  - [ ] View customer order history
  - [ ] Customer segmentation
  - [ ] Customer lifetime value

- [ ] **5.4.2** Customer Actions
  - [ ] Create manual customer
  - [ ] Disable/enable customer account
  - [ ] Reset customer password
  - [ ] View customer addresses
  - [ ] Export customer data

### 5.5 Promotion & Discount Management
- [ ] **5.5.1** Promotion Management
  - [ ] Create promotion/discount
  - [ ] Edit promotion
  - [ ] Delete promotion
  - [ ] Apply to products/categories
  - [ ] Set promotion dates
  - [ ] Coupon code generation
  - [ ] Usage limitations (per user, total)
  - [ ] Bulk discount application

- [ ] **5.5.2** Coupon Management
  - [ ] Generate coupon codes
  - [ ] Set coupon validity
  - [ ] Track coupon usage
  - [ ] Disable/enable coupons
  - [ ] Bulk coupon operations

### 5.6 Banner Management
- [ ] **5.6.1** Banner Management
  - [ ] ✅ List banners
  - [ ] ✅ Create banner
  - [ ] ✅ Edit banner
  - [ ] ✅ Delete banner
  - [ ] Upload banner image
  - [ ] Set banner position (homepage, specific page)
  - [ ] Set active/inactive status
  - [ ] Banner duration (start/end date)
  - [ ] Click tracking

### 5.7 Settings & Configuration
- [ ] **5.7.1** System Settings
  - [ ] ✅ View settings (partial)
  - [ ] Edit system settings
  - [ ] Site name, logo, favicon
  - [ ] Email configuration (SMTP)
  - [ ] Payment gateway settings
  - [ ] Shipping settings
  - [ ] Currency & language settings
  - [ ] Tax settings
  - [ ] Terms & conditions

- [ ] **5.7.2** Email Templates
  - [ ] Edit email templates
  - [ ] Order confirmation email
  - [ ] Shipping notification email
  - [ ] Delivery confirmation email
  - [ ] Customer welcome email
  - [ ] Promotional emails
  - [ ] Email preview

### 5.8 Role & Permission Management
- [ ] **5.8.1** Role Management
  - [ ] ✅ List roles
  - [ ] ✅ Create role
  - [ ] ✅ Edit role
  - [ ] ✅ Delete role
  - [ ] Assign permissions to role
  - [ ] Assign users to role
  - [ ] Role analytics (users per role)

- [ ] **5.8.2** Permission Management
  - [ ] ✅ List permissions
  - [ ] ✅ View permission details
  - [ ] Create permission
  - [ ] Edit permission description
  - [ ] Delete permission
  - [ ] Permission-role mapping view

### 5.9 Reports & Analytics
- [ ] **5.9.1** Sales Reports
  - [ ] Sales by product
  - [ ] Sales by category
  - [ ] Sales by customer
  - [ ] Sales by date range
  - [ ] Sales by payment method
  - [ ] Sales by shipping method
  - [ ] Revenue report
  - [ ] Profit margin report

- [ ] **5.9.2** Customer Reports
  - [ ] New customers report
  - [ ] Customer retention report
  - [ ] Customer lifetime value report
  - [ ] Customer geographic distribution
  - [ ] Customer activity report

- [ ] **5.9.3** Inventory Reports
  - [ ] Stock level report
  - [ ] Stock movement report
  - [ ] Fast-moving products
  - [ ] Slow-moving products
  - [ ] Expiry date tracking

- [ ] **5.9.4** Advanced Analytics
  - [ ] Dashboard customization
  - [ ] Custom report builder
  - [ ] Scheduled reports (email)
  - [ ] Report export (PDF, CSV)

### 5.10 Admin Audit Logs
- [ ] **5.10.1** Activity Logging
  - [ ] Log all admin actions
  - [ ] Log product changes
  - [ ] Log order changes
  - [ ] Log customer changes
  - [ ] Log permission changes
  - [ ] View activity logs
  - [ ] Filter logs
  - [ ] Export logs

---

## 🟣 PHASE 6: PROMOTIONS & DISCOUNTS

### 6.1 Promotion System
- [ ] **6.1.1** Promotion Types
  - [ ] Percentage discount (e.g., 20% off)
  - [ ] Fixed amount discount (e.g., 50,000 VND off)
  - [ ] Buy X get Y free
  - [ ] Free shipping over X amount
  - [ ] Volume discounts (buy more, save more)

- [ ] **6.1.2** Promotion Rules
  - [ ] Apply to specific products
  - [ ] Apply to categories
  - [ ] Apply to customer segments
  - [ ] Apply to cart total over X
  - [ ] Time-based promotions (seasonal sales)
  - [ ] Usage limits (max uses, per customer)
  - [ ] Combine/stack discounts

- [ ] **6.1.3** Coupon Code Management
  - [ ] Generate coupon codes
  - [ ] Batch code generation
  - [ ] Share coupon (email, social)
  - [ ] Track coupon effectiveness
  - [ ] Coupon referral system

### 6.2 Flash Sale & Limited Time Offers
- [ ] **6.2.1** Flash Sale System
  - [ ] Create flash sale
  - [ ] Set time window
  - [ ] Apply discount
  - [ ] Quantity limitation
  - [ ] Featured flash sale display
  - [ ] Countdown timer

---

## 🟢 PHASE 7: NOTIFICATIONS & MESSAGING

### 7.1 Email Notifications
- [ ] **7.1.1** Transactional Emails
  - [ ] ✅ Registration confirmation email
  - [ ] ✅ OTP/2FA emails
  - [ ] Welcome email
  - [ ] Order confirmation email
  - [ ] Shipping update email
  - [ ] Delivery confirmation email
  - [ ] Refund email
  - [ ] Password reset email

- [ ] **7.1.2** Marketing Emails
  - [ ] Newsletter subscription
  - [ ] Promotional email
  - [ ] Abandoned cart recovery
  - [ ] Product recommendation
  - [ ] Review request email
  - [ ] Birthday discount email

- [ ] **7.1.3** Email Management
  - [ ] Email template management
  - [ ] Email scheduling
  - [ ] Email preview
  - [ ] A/B testing emails
  - [ ] Bounce & unsubscribe handling

### 7.2 In-App Notifications
- [ ] **7.2.1** User Notifications
  - [ ] Order status notification
  - [ ] Review published notification
  - [ ] Promotion notification
  - [ ] Product back in stock notification
  - [ ] Price drop notification

- [ ] **7.2.2** Admin Notifications
  - [ ] New order notification
  - [ ] Low stock alert
  - [ ] New customer registration
  - [ ] New review (pending approval)
  - [ ] Contact form submission

### 7.3 SMS Notifications (Optional)
- [ ] **7.3.1** SMS Alerts
  - [ ] Order confirmation SMS
  - [ ] Shipping update SMS
  - [ ] OTP via SMS
  - [ ] Promotional SMS

---

## 🔵 PHASE 8: SEO & FRONTEND OPTIMIZATION

### 8.1 SEO Optimization
- [ ] **8.1.1** Meta Tags & Structured Data
  - [ ] Meta title, description for all pages
  - [ ] Open Graph (OG) tags for social sharing
  - [ ] Schema.org structured data
  - [ ] Sitemap generation
  - [ ] Robots.txt configuration

- [ ] **8.1.2** URL Optimization
  - [ ] SEO-friendly URLs
  - [ ] URL slug generation from product names
  - [ ] 301 redirects for old URLs
  - [ ] Canonical tags

- [ ] **8.1.3** Content Optimization
  - [ ] Keyword research & targeting
  - [ ] Image alt text
  - [ ] Internal linking strategy
  - [ ] Mobile responsiveness
  - [ ] Page load speed

### 8.2 Frontend Improvements
- [ ] **8.2.1** UI/UX Enhancements
  - [ ] Responsive design for all screen sizes
  - [ ] Dark mode support
  - [ ] Accessibility improvements (WCAG compliance)
  - [ ] Loading states & animations
  - [ ] Error handling & user feedback
  - [ ] Toast notifications
  - [ ] Modal dialogs

- [ ] **8.2.2** Performance Optimization
  - [ ] Image lazy loading
  - [ ] CSS/JS minification
  - [ ] Caching strategy
  - [ ] CDN integration
  - [ ] Database query optimization
  - [ ] API response caching

---

## 🟡 PHASE 9: INTERNATIONALIZATION (i18n) & LOCALIZATION

### 9.1 Multi-Language Support
- [ ] **9.1.1** Language Management
  - [ ] ✅ English support
  - [ ] ✅ Vietnamese support
  - [ ] Add more languages (Chinese, Japanese, etc.)
  - [ ] Language switcher
  - [ ] Language detection (browser)
  - [ ] Language persistence (localStorage)

- [ ] **9.1.2** Content Translation
  - [ ] Translate all UI text
  - [ ] Translate product descriptions
  - [ ] Translate category names
  - [ ] Email template translation
  - [ ] Admin panel translation

### 9.2 Localization
- [ ] **9.2.1** Regional Settings
  - [ ] Currency conversion
  - [ ] Date/time format
  - [ ] Number format
  - [ ] Timezone support

---

## 🟣 PHASE 10: API DEVELOPMENT

### 10.1 REST API Structure
- [ ] **10.1.1** API Architecture
  - [ ] API versioning (v1, v2, etc.)
  - [ ] Request/response format standardization
  - [ ] Error handling & status codes
  - [ ] API documentation (Swagger/OpenAPI)
  - [ ] CORS configuration
  - [ ] Rate limiting

- [ ] **10.1.2** Authentication API
  - [ ] Token-based authentication (JWT)
  - [ ] API key management
  - [ ] OAuth 2.0 integration
  - [ ] Refresh token mechanism

### 10.2 API Endpoints
- [ ] **10.2.1** Comprehensive API Coverage
  - [ ] All product endpoints
  - [ ] All user endpoints
  - [ ] All order endpoints
  - [ ] All cart endpoints
  - [ ] All review endpoints
  - [ ] All admin endpoints
  - [ ] Search & filter endpoints
  - [ ] Analytics endpoints

### 10.3 API Security
- [ ] **10.3.1** Security Measures
  - [ ] Input validation & sanitization
  - [ ] SQL injection prevention
  - [ ] XSS prevention
  - [ ] CSRF protection
  - [ ] API rate limiting
  - [ ] DDoS protection

---

## 🟢 PHASE 11: TESTING

### 11.1 Unit Testing
- [ ] **11.1.1** Controller Tests
  - [ ] Test all controller endpoints
  - [ ] Test request validation
  - [ ] Test response format
  - [ ] Test error handling
  - [ ] Achieve >80% code coverage

- [ ] **11.1.2** Service Tests
  - [ ] Test business logic
  - [ ] Test edge cases
  - [ ] Mock external dependencies
  - [ ] Test data transformation

- [ ] **11.1.3** DAO Tests
  - [ ] Test database operations
  - [ ] Test query results
  - [ ] Test transaction handling

### 11.2 Integration Testing
- [ ] **11.2.1** Feature Integration Tests
  - [ ] Test complete user flows
  - [ ] Test API integration
  - [ ] Test database integration

### 11.3 Performance Testing
- [ ] **11.3.1** Load Testing
  - [ ] Test concurrent users
  - [ ] Test database performance
  - [ ] Test API response times
  - [ ] Identify bottlenecks

### 11.4 Security Testing
- [ ] **11.4.1** Vulnerability Testing
  - [ ] SQL injection testing
  - [ ] XSS testing
  - [ ] CSRF testing
  - [ ] Authentication testing
  - [ ] Authorization testing

---

## 🟡 PHASE 12: DEPLOYMENT & DEVOPS

### 12.1 Environment Setup
- [ ] **12.1.1** Development Environment
  - [ ] Local setup documentation
  - [ ] Docker support (optional)
  - [ ] Database setup script

- [ ] **12.1.2** Staging Environment
  - [ ] Staging server setup
  - [ ] Staging database (copy of production)
  - [ ] Staging deployment process

- [ ] **12.1.3** Production Environment
  - [ ] Production server setup
  - [ ] Production database
  - [ ] SSL/TLS certificate
  - [ ] Backup & recovery setup

### 12.2 CI/CD Pipeline
- [ ] **12.2.1** Automated Testing in Pipeline
  - [ ] Run unit tests
  - [ ] Run integration tests
  - [ ] Code quality checks
  - [ ] Security scanning

- [ ] **12.2.2** Automated Deployment
  - [ ] Build artifacts
  - [ ] Deploy to staging
  - [ ] Deploy to production
  - [ ] Rollback mechanism

### 12.3 Monitoring & Logging
- [ ] **12.3.1** Application Monitoring
  - [ ] Error tracking (Sentry or similar)
  - [ ] Performance monitoring
  - [ ] Uptime monitoring
  - [ ] Resource usage monitoring

- [ ] **12.3.2** Logging Infrastructure
  - [ ] Centralized logging
  - [ ] Log aggregation
  - [ ] Log analysis & alerting

---

## 🟣 PHASE 13: MAINTENANCE & ENHANCEMENTS

### 13.1 Bug Fixes
- [ ] **13.1.1** User-Reported Issues
  - [ ] Collection & triage process
  - [ ] Bug fix process
  - [ ] Regression prevention

### 13.2 Performance Tuning
- [ ] **13.2.1** Database Optimization
  - [ ] Query optimization
  - [ ] Index creation
  - [ ] Connection pooling

- [ ] **13.2.2** Caching Strategy
  - [ ] Application-level caching
  - [ ] Database query caching
  - [ ] Browser caching

### 13.3 Future Enhancements
- [ ] **13.3.1** Advanced Features
  - [ ] AI/ML recommendations
  - [ ] Chatbot support
  - [ ] Live chat support
  - [ ] Social commerce integration
  - [ ] Subscription products
  - [ ] Marketplace/vendor system

---

## 📋 DETAILED TASK BREAKDOWN BY FEATURE

### Feature: Auth Module
**Location:** `/src/main/java/nlu/fit/web/souvenirecommerce/features/auth/`

#### Controllers
- [ ] **LoginServlet** - Handle login requests
  - [ ] Validate email & password
  - [ ] Create session
  - [ ] Redirect to dashboard
  - [ ] Handle login errors
  - [ ] Add "remember me" functionality
  
- [ ] **SignupServlet** - Handle registration
  - [ ] Validate input
  - [ ] Check email availability
  - [ ] Send verification email
  - [ ] Store user data
  - [ ] Handle signup errors
  
- [ ] **LogoutServlet** - Handle logout
  - [ ] Clear session
  - [ ] Clear cookies
  - [ ] Redirect to home

- [ ] **ForgotPasswordServlet** - Handle password reset flow
  - [ ] Generate reset token
  - [ ] Send reset email
  - [ ] Validate token
  - [ ] Reset password

#### Services
- [ ] **AuthService** - Business logic for authentication
  - [ ] User registration logic
  - [ ] Login logic
  - [ ] Password validation
  - [ ] Token generation
  - [ ] OTP handling

#### DAOs
- [ ] **UserDAO** - User data access
  - [ ] CRUD operations
  - [ ] Find by email
  - [ ] Update password
  - [ ] Update last login

- [ ] **AuthDAO** - Auth-specific data access
  - [ ] Store verification tokens
  - [ ] Store reset tokens
  - [ ] Validate tokens

#### DTOs
- [ ] **LoginRequest, LoginResponse**
- [ ] **SignupRequest, SignupResponse**
- [ ] **UserDTO**
- [ ] **TokenDTO**

---

### Feature: Product Module
**Location:** `/src/main/java/nlu/fit/web/souvenirecommerce/features/product/`

#### Controllers
- [ ] **ProductController** - Main product endpoint
  - [ ] Get all products
  - [ ] Search products
  - [ ] Filter products
  - [ ] Get product details
  
- [ ] **ProductDetailController** - Detailed product view
  - [ ] Display full product info
  - [ ] Display related products
  - [ ] Display reviews

#### Services
- [ ] **ProductService** - Business logic
  - [ ] Get products
  - [ ] Search logic
  - [ ] Filter logic
  - [ ] Product recommendation

- [ ] **CategoryService** - Category management
  - [ ] Get categories
  - [ ] Get category products

#### DAOs
- [ ] **ProductDAO** - Product queries
  - [ ] CRUD operations
  - [ ] Search queries
  - [ ] Filter queries

- [ ] **CategoryDAO** - Category queries
  - [ ] CRUD operations
  - [ ] Hierarchy queries

#### DTOs
- [ ] **ProductDTO, ProductCardDTO, ProductDetailDTO**
- [ ] **CategoryDTO**

---

### Feature: Cart Module
**Location:** `/src/main/java/nlu/fit/web/souvenirecommerce/features/cart/`

#### Controllers
- [ ] **AddCart** - Add to cart
- [ ] **RemoveCartController** - Remove from cart
- [ ] **UpdateCartController** - Update quantities
- [ ] **ShoppingCartController** - View cart

#### Services
- [ ] **CartService** - Cart management logic
  - [ ] Add item
  - [ ] Remove item
  - [ ] Update quantity
  - [ ] Calculate total
  - [ ] Check stock availability

#### DTOs
- [ ] **CartDTO, CartItemDTO**

---

### Feature: Order Module
**Location:** `/src/main/java/nlu/fit/web/souvenirecommerce/features/order/`

#### Controllers
- [ ] **CheckoutController** - Handle checkout flow
- [ ] **OrderSuccessController** - Show success page
- [ ] **UserOrderController** - View user orders
- [ ] **AdminOrderController** - Admin order management

#### Services
- [ ] **OrderService** - Order management logic
  - [ ] Create order
  - [ ] Update status
  - [ ] Calculate totals
  - [ ] Send notifications

- [ ] **CheckoutService** - Checkout flow logic
  - [ ] Validate checkout data
  - [ ] Process payment
  - [ ] Reserve stock
  - [ ] Create order

#### DAOs
- [ ] **OrderDAO** - Order queries
  - [ ] CRUD operations
  - [ ] Status queries

#### DTOs
- [ ] **OrderDTO, OrderDetailDTO, OrderItemDTO**
- [ ] **CheckoutDTO**

---

## 🎯 PRIORITIZATION & TIMELINE

### Priority 1 (Critical - Week 1-2)
- [x] Project structure refactoring
- [x] Database schema completion
- [ ] Auth system (login, signup, logout)
- [ ] User profile management
- [ ] Product listing & search
- [ ] Shopping cart

### Priority 2 (High - Week 3-4)
- [ ] Order creation & management
- [ ] Admin dashboard basics
- [ ] Checkout process
- [ ] Payment integration
- [ ] Email notifications

### Priority 3 (Medium - Week 5-6)
- [ ] Promotion/discount system
- [ ] Product reviews & ratings
- [ ] Advanced admin features
- [ ] API endpoints
- [ ] Refund management

### Priority 4 (Low - Week 7+)
- [ ] Analytics & reporting
- [ ] Optimization & performance
- [ ] Testing & deployment
- [ ] Future enhancements

---

## 📝 TRACKING CHECKLIST

### Template for Each Task
When starting a new task, create a subtask with:
- [ ] **Task Name**: Description
  - [ ] Subtask 1
  - [ ] Subtask 2
  - [ ] Code review
  - [ ] Testing
  - [ ] Documentation
  - [ ] Deployment (if needed)

---

## 🔗 RELATED DOCUMENTS

- Database Schema: `/schema.sql`, `/update_schema.sql`
- Existing Code: `/src/main/java/nlu/fit/web/souvenirecommerce/`
- JSP Templates: `/src/main/webapp/`
- Configuration: `/src/main/resources/`

---

**Last Updated:** 2026-05-20
**Status:** In Progress
**Owner:** Development Team


