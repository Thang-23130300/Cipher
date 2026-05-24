# 📚 ROADMAP & TASK PLANNING - SOUVENIR E-COMMERCE

## 📋 Tài Liệu Quan Trọng (Đọc Theo Thứ Tự Này)

### 🎯 Bắt Đầu Từ Đây
1. **[QUICK_START.md](QUICK_START.md)** ⭐ **START HERE** 
   - Mô tả hiện trạng dự án
   - Phân chia công việc theo ưu tiên
   - Timeline ước tính
   - Công cụ & lệnh cần thiết

### 📖 Hướng Dẫn Kiến Trúc
2. **[ARCHITECTURE_GUIDE.md](ARCHITECTURE_GUIDE.md)**
   - Cấu trúc Feature-Based chi tiết
   - Quy tắc đặt tên cho mỗi thành phần
   - Template cấu trúc cho mỗi feature
   - Best practices & patterns

### 🗺️ Ánh Xạ File Di Chuyển
3. **[FILE_MIGRATION_MAPPING.md](FILE_MIGRATION_MAPPING.md)**
   - Danh sách các file hiện tại
   - Nơi mỗi file nên di chuyển đến
   - Thứ tự di chuyển được khuyến nghị
   - Bash script hỗ trợ

### 📊 Danh Sách Chi Tiết Tất Cả Tasks
4. **[TASK_ROADMAP.md](TASK_ROADMAP.md)** (287 tasks)
   - Phase 0-13 đầy đủ
   - Mô tả chi tiết từng task
   - REST API endpoints
   - Database schema improvements

### 🎨 Dashboard Theo Dõi Tiến Độ
5. **[TASK_DASHBOARD.html](TASK_DASHBOARD.html)**
   - Mở trong trình duyệt
   - Hiển thị tiến độ dự án
   - Checkbox để track tasks
   - Visual progress bars

---

## 📊 Current Status

### Cấu Trúc Hiện Tại
```
✅ 120 Java files hiện có
✅ Layer-Based architecture
❌ Chưa refactor sang Feature-Based
❌ Chưa có unit tests
❌ Chưa có REST API hoàn chỉnh
```

### Tính Năng Đã Có
```
✅ Auth: login/signup cơ bản, email verification, OTP
✅ Product: CRUD, images (Cloudinary)
✅ Cart: CRUD, add/remove items
✅ Order: basic order creation
✅ Admin: basic dashboard
✅ Review: basic review system
```

### Tính Năng Chưa Hoàn Chỉnh
```
⏳ Code organization (cần refactor)
⏳ User profile management
⏳ Advanced search & filtering
⏳ Promotions & discounts
⏳ Payment gateway integration
⏳ Email notifications (partial)
⏳ API endpoints (partial)
❌ Testing (unit, integration)
❌ Analytics & reporting
❌ SEO optimization
❌ Performance tuning
```

---

## 🎯 QUICK ACTIONS

### Công Việc Tuần 1 (ASAP)
```bash
# 1. Backup Project
git commit -m "Backup before refactoring"
git branch backup-layer-based

# 2. Tạo Folder Structure
mkdir -p src/main/java/nlu/fit/web/souvenirecommerce/features/{auth,product,cart,order,user,review,admin,search,banner,promotion,payment,notification,cloudinary}
mkdir -p src/main/java/nlu/fit/web/souvenirecommerce/shared/{util,dto,filter,listener,mapper,enums,constants,exception}
mkdir -p src/main/java/nlu/fit/web/souvenirecommerce/config

# 3. Di Chuyển Shared Components
# Xem FILE_MIGRATION_MAPPING.md để chi tiết

# 4. Compile & Test
mvn clean compile
```

### Công Việc Tuần 1-2 (Auth & User)
- [ ] Refactor structure mới
- [ ] Di chuyển tất cả auth files
- [ ] Update imports
- [ ] Fix compile errors
- [ ] Thêm missing features:
  - [ ] Forgot password flow
  - [ ] User profile management
  - [ ] Address management
  - [ ] Password reset

### Công Việc Tuần 2-3 (Product)
- [ ] Di chuyển product files
- [ ] Search & filtering
- [ ] Inventory management
- [ ] Product specifications
- [ ] Product variants

### Công Việc Tuần 3-4 (Cart & Order)
- [ ] Di chuyển cart files
- [ ] Di chuyển order files
- [ ] Checkout flow
- [ ] Payment integration
- [ ] Refund system

---

## 📈 Metrics & KPIs

### Theo Dõi Hằng Ngày
- [ ] Tasks hoàn thành / Total tasks
- [ ] Code coverage %
- [ ] Compile errors = 0
- [ ] Test pass rate %

### Mục Tiêu
- **Tuần 1:** 0% → 20% (Structure setup)
- **Tuần 2:** 20% → 35% (Auth & User complete)
- **Tuần 3:** 35% → 55% (Product complete)
- **Tuần 5:** 55% → 75% (Order & Admin)
- **Tuần 8:** 75% → 90% (Testing)
- **Tuần 10:** 90% → 100% (Deployment)

---

## 🔴 PRIORITY LEVELS

### Critical (Làm Ngay)
```
Priority 1: Phase 0 - Refactor cấu trúc
Priority 2: Phase 1 - Auth hoàn chỉnh
Priority 3: Phase 2 - Product hoàn chỉnh
Priority 4: Phase 3 - Order & Checkout
```

### High
```
Priority 5: Phase 4 - Admin Dashboard complete
Priority 6: Phase 5-7 - Reviews, Promotions, Notifications
```

### Medium
```
Priority 7: Phase 8-12 - SEO, i18n, API, Testing
```

### Low
```
Priority 8: Phase 13 - Analytics, Optimization, Enhancements
```

---

## 🛠️ Tech Stack

### Backend
- **Framework:** Jakarta Servlet (not Spring)
- **ORM:** Hibernate 7.3
- **Database:** MySQL 8.0+
- **Build:** Maven
- **JDK:** Java 21

### Frontend
- **View:** JSP/JSTL
- **CSS:** Bootstrap 5 (recommended)
- **JS:** Vanilla JS (or jQuery)
- **Image:** Cloudinary

### External Services
- **Email**: SMTP (Gmail, SendGrid, etc.)
- **Image:** Cloudinary API
- **Payment:** (Needs to be selected - Stripe, PayPal, VNPay, etc.)

### DevOps
- **VCS:** Git/GitHub
- **CI/CD:** GitHub Actions
- **Hosting:** (To be decided)
- **Monitoring:** (To be added)

---

## 📁 File Structure After Refactor

```
src/main/java/nlu/fit/web/souvenirecommerce/
├── features/
│   ├── auth/              (27 files)
│   ├── product/           (29 files)
│   ├── cart/              (6 files)
│   ├── order/             (8 files)
│   ├── user/              (8 files)
│   ├── review/            (5 files)
│   ├── admin/             (13 files)
│   ├── search/            (3 files)
│   ├── banner/            (3 files)
│   ├── promotion/         (5 files)
│   ├── payment/           (5 files)
│   ├── notification/      (4 files)
│   ├── cloudinary/        (3 files)
│   └── home/              (3 files)
├── shared/                (30+ files)
│   ├── util/
│   ├── dto/
│   ├── filter/
│   ├── listener/
│   ├── mapper/
│   ├── enums/
│   ├── constants/
│   └── exception/
├── config/                (5 files)
├── model/                 (20+ entities)
└── exception/             (custom exceptions)
```

---

## 🔗 Related Files

### Configuration Files
- `pom.xml` - Maven dependencies
- `hibernate.cfg.xml` - Hibernate configuration
- `web.xml` - Servlet mapping
- `application.properties` - Application settings
- `logback.xml` - Logging configuration

### Database Files
- `schema.sql` - Original schema (đã có)
- `update_schema.sql` - Schema updates (đã có)
- `olbweb_data.sql` - Sample data (đã có)

### Documents
- `docs/folder_structure.md`
- `docs/hibernate-basic.md`
- `README.md` - Project README

---

## 🚦 Traffic Light Status

### 🔴 Red (Critical Issues)
- Code organization (Layer vs Feature-based)
- Missing unit tests
- Incomplete features

### 🟡 Yellow (Important)
- API endpoints incomplete
- Database schema could be optimized
- Performance optimization needed

### 🟢 Green (Good)
- Basic functionality works
- Database connection stable
- Cloud image storage (Cloudinary) integrated

---

## 💡 Key Decisions Made

### 1. Architecture: Feature-Based ✅
- **Why:** Better scalability, easier team collaboration, easier to add/remove features
- **Alternative Considered:** Layered (current), Microservices (overkill)

### 2. ORM: Hibernate ✅
- **Why:** Already used, powerful, good for this project size
- **Alternative:** JPA, Spring Data JPA

### 3. Frontend: JSP + Bootstrap ✅
- **Why:** Simple, server-side rendering, low overhead
- **Alternative:** React, Angular, Vue (would need major refactor)

### 4. Testing: JUnit + Mockito ✅
- **Why:** Standard for Java projects, good for unit & integration testing
- **Alternative:** TestNG

---

## 📞 Support & Resources

### When Stuck
1. Check error message carefully
2. Read relevant documentation (see "Tài Liệu" section)
3. Check StackOverflow
4. Review existing similar code in codebase
5. Ask team members

### Useful Links
- [Hibernate ORM 7.3 Docs](https://hibernate.org/orm/documentation/7.3/)
- [Jakarta Servlet API](https://eclipse-ee4j.github.io/servlet-api/)
- [JSP/JSTL Tutorials](https://docs.oracle.com/javaee/7/tutorial/)
- [Maven Guide](https://maven.apache.org/guides/)

---

## 🎓 Learning Path

### Week 1-2: Foundation
```
- Feature-Based Architecture
- Maven & Build Tools
- Hibernate ORM basics
- Jakarta Servlet advanced
```

### Week 3-4: Features
```
- Auth systems (JWT, sessions)
- CRUD operations
- Database transactions
- DAO patterns
```

### Week 5-6: Advanced
```
- REST APIs
- Error handling
- Testing (Unit, Integration)
- Performance optimization
```

### Week 7-8: DevOps
```
- CI/CD pipelines
- Docker (optional)
- Deployment
- Monitoring
```

---

## ✅ Checklist Before Starting

- [ ] Read QUICK_START.md
- [ ] Read ARCHITECTURE_GUIDE.md
- [ ] Review FILE_MIGRATION_MAPPING.md
- [ ] Backup current project
- [ ] Create development branch
- [ ] Setup IDE for Java 21
- [ ] Install Maven 3.8+
- [ ] Verify MySQL connection
- [ ] Verify GitHub access
- [ ] Schedule team meetings

---

## 📞 Questions?

Refer to specific documentation:
- **Cấu trúc thư mục:** [ARCHITECTURE_GUIDE.md](ARCHITECTURE_GUIDE.md)
- **Danh sách tasks:** [TASK_ROADMAP.md](TASK_ROADMAP.md)
- **Di chuyển file:** [FILE_MIGRATION_MAPPING.md](FILE_MIGRATION_MAPPING.md)
- **Bắt đầu nhanh:** [QUICK_START.md](QUICK_START.md)
- **Theo dõi tiến độ:** Mở [TASK_DASHBOARD.html](TASK_DASHBOARD.html) trong browser

---

## 🎯 Success Criteria

### Phase 0 (Week 1): ✅ Complete
- [ ] Folder structure created
- [ ] All files moved to new locations
- [ ] No compile errors
- [ ] Tests passing

### Phase 1-3 (Week 2-5): ⏳ In Progress
- [ ] Auth system 100% complete
- [ ] Product management 100% complete
- [ ] Shopping flow 100% complete
- [ ] All unit tests passing
- [ ] 70%+ code coverage

### Phase 4+ (Week 6+): 🔄 Next
- [ ] Admin dashboard complete
- [ ] All APIs working
- [ ] 80%+ code coverage
- [ ] Performance tuned
- [ ] Ready for deployment

---

## 📝 Update Log

| Date | Status | Notes |
|------|--------|-------|
| 2026-05-20 | ✅ Created | Initial roadmap creation |
| | | - TASK_ROADMAP.md (287 tasks) |
| | | - ARCHITECTURE_GUIDE.md (detailed structure) |
| | | - FILE_MIGRATION_MAPPING.md (file mapping) |
| | | - QUICK_START.md (quick guide) |
| | | - TASK_DASHBOARD.html (visual tracker) |

---

**Created:** May 20, 2026
**Version:** 1.0
**Owner:** Development Team
**Last Update:** 2026-05-20

🚀 **Ready to start? Begin with [QUICK_START.md](QUICK_START.md)**


