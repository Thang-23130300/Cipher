# Tính Năng Thay Đổi Avatar Người Dùng - Cloudinary Integration

## 📋 Tổng Quan

Tính năng thay đổi avatar của người dùng đã được cải thiện để sử dụng **Cloudinary** làm dịch vụ lưu trữ ảnh đám mây, thay vì lưu trữ cục bộ.

## ✨ Những Thay Đổi Chính

### 1. **User Entity Enhancement**
- **File:** `src/main/java/nlu/fit/web/souvenirecommerce/model/entity/User.java`
- **Thêm field mới:**
  ```java
  @Column(name = "avatar_public_id", length = 255)
  private String avatarPublicId;
  ```
- **Mục đích:** Lưu trữ ID công khai của Cloudinary để phục vụ xóa/sửa ảnh sau này

### 2. **ChangeAvatarServlet - Backend Handler**
- **File:** `src/main/java/nlu/fit/web/souvenirecommerce/account/servlet/ChangeAvatarServlet.java`
- **URL Endpoint:** `/user/account/change-avatar`
- **Phương thức:** POST (multipart/form-data)
- **Chức năng:**
  - ✅ Nhận file upload từ client
  - ✅ Upload ảnh lên Cloudinary
  - ✅ Xóa ảnh cũ trước khi lưu ảnh mới
  - ✅ Cập nhật User entity trong database
  - ✅ Cập nhật session
  - ✅ Trả về JSON response với avatar URL

### 3. **Frontend - JavaScript Handler**
- **File:** `src/main/webapp/assets/js/account/profile.js`
- **Tính năng:**
  - Click button → Mở file picker
  - Select ảnh → Tự động upload via AJAX
  - Show loading state during upload
  - Display notification (success/error)
  - Real-time avatar preview update

### 4. **JSP View Update**
- **File:** `src/main/webapp/WEB-INF/views/account/account_layout.jsp`
- **Thay đổi:**
  - Form action: `/user/account/change-avatar`
  - Xóa `action=change_avatarUrl` hidden input
  - Input name: `avatarUrlFile`

### 5. **Database Migration**
- **File:** `update_schema.sql`
- **SQL Command:**
  ```sql
  ALTER TABLE users ADD COLUMN avatar_public_id VARCHAR(255) NULL AFTER avatar_url;
  ```

## 🔄 Quy Trình Hoạt Động

```
1. User click "Đổi ảnh" button
   ↓
2. JavaScript gọi file picker
   ↓
3. User select ảnh
   ↓
4. JavaScript gửi AJAX POST đến /user/account/change-avatar
   ↓
5. ChangeAvatarServlet nhận request
   ↓
6. Cloudinary upload ảnh → nhận avatar_url + public_id
   ↓
7. Xóa ảnh cũ bằng public_id (nếu có)
   ↓
8. Update User entity: avatarUrl + avatarPublicId
   ↓
9. Update session
   ↓
10. Return JSON {success: true, avatarUrl: "..."}
   ↓
11. JavaScript update avatar preview
   ↓
12. Show success notification
```

## 📦 Dependencies

- **Cloudinary SDK:** Đã được cấu hình trong `pom.xml`
- **Hibernate/JPA:** Quản lý User entity
- **Jakarta Servlet API:** Multipart form handling

## 🛠️ Setup & Configuration

### 1. Database Migration
Run SQL migration script:
```bash
mysql -u your_user -p your_database < update_schema.sql
```

### 2. Cloudinary Configuration
Đảm bảo file `application.properties` có Cloudinary credentials:
```properties
cloud_name=your_cloudinary_name
cloud_api_key=your_api_key
cloud_api_secret=your_api_secret
```

### 3. Build & Deploy
```bash
./mvnw clean package
# Deploy target/BACKEND-1.0-SNAPSHOT.war to Tomcat
```

## 🎯 API Response Format

### Success Response
```json
{
  "success": true,
  "message": "Thay đổi ảnh đại diện thành công",
  "avatarUrl": "https://res.cloudinary.com/..."
}
```

### Error Response
```json
{
  "error": "Lỗi khi tải ảnh: Invalid file format"
}
```

## 🔒 Security Features

✅ **File Validation:**
- Check file size (max 5MB)
- Accept only image files (`accept="image/*"`)

✅ **Session Validation:**
- Check user logged in
- Return 401 if unauthorized

✅ **Error Handling:**
- Try-catch for Cloudinary operations
- Graceful fallback if old image deletion fails
- Transaction rollback on database error

✅ **Cleanup:**
- Automatically delete old image from Cloudinary
- Prevents storage bloat

## 📝 File Manifest

| File | Type | Status |
|------|------|--------|
| User.java | Entity | ✅ Modified |
| ChangeAvatarServlet.java | Backend | ✅ Implemented |
| profile.js | Frontend | ✅ Updated |
| account_layout.jsp | View | ✅ Updated |
| ProfileServlet.java | Controller | ✅ Fixed typo |
| update_schema.sql | Migration | ✅ Added |

## 🧪 Testing Checklist

- [ ] Run `update_schema.sql` migration
- [ ] Build project: `mvnw clean package`
- [ ] Deploy WAR file
- [ ] Login to user account
- [ ] Click "Đổi ảnh" button
- [ ] Select image file (< 5MB)
- [ ] Verify:
  - [ ] Avatar updates in sidebar
  - [ ] Success notification shows
  - [ ] Old avatar deleted from Cloudinary
  - [ ] New avatar_public_id saved in database
  - [ ] Session updated with new avatar URL

## 🚀 Future Enhancements

- [ ] Image cropping before upload
- [ ] Progress bar for large files
- [ ] Support for avatar history/rollback
- [ ] Image optimization (resize, compress)

## 📞 Support

For issues or questions, check:
1. Cloudinary configuration in `application.properties`
2. Database migration was applied
3. Browser console for JavaScript errors
4. Server logs for backend errors

---

**Last Updated:** May 21, 2026  
**Version:** 1.0

