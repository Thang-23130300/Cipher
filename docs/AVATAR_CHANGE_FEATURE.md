# Chức Năng Thay Đổi Hình Ảnh Đại Diện (Avatar Change Feature)

## Tổng Quan
Tính năng cho phép người dùng thay đổi hình ảnh đại diện (avatar) trên trang hồ sơ cá nhân (`/user/profile`). Hình ảnh được lưu trữ trên Cloudinary - dịch vụ lưu trữ hình ảnh đám mây.

## Thành Phần Chính

### 1. CloudinaryUtil (`src/main/java/nlu/fit/web/souvenirecommerce/util/CloudinaryUtil.java`)
Lớp tiện ích xử lý tất cả các tương tác với Cloudinary.

**Các phương thức chính:**
- `uploadImage(InputStream, String, String, String)`: Tải hình ảnh lên Cloudinary
  - Tham số: input stream của file, tên file, thư mục lưu trữ, public ID
  - Trả về: Map chứa URL hình ảnh và public ID
  
- `deleteImage(String)`: Xóa hình ảnh khỏi Cloudinary
  - Tham số: public ID của hình ảnh
  - Trả về: boolean (thành công/thất bại)
  
- `isConfigured()`: Kiểm tra Cloudinary có được cấu hình hay không
  - Trả về: boolean

### 2. ProfileServlet (`src/main/java/nlu/fit/web/souvenirecommerce/profile/servlet/ProfileServlet.java`)
Servlet xử lý các yêu cầu liên quan đến hồ sơ người dùng.

**Phương thức mới:**
- `changeUserAvatar(HttpServletRequest, HttpServletResponse, User, HttpSession)`: Xử lý thay đổi avatar
  - Xác thực Cloudinary có được cấu hình
  - Xác thực file được upload
  - Xóa avatar cũ (nếu có)
  - Tải avatar mới lên Cloudinary
  - Cập nhật thông tin user trong database
  - Cập nhật session

### 3. User Entity (`src/main/java/nlu/fit/web/souvenirecommerce/model/entity/User.java`)
Đã có 2 trường để lưu trữ thông tin avatar:
- `avatarUrl`: URL của hình ảnh trên Cloudinary (ví dụ: https://res.cloudinary.com/...)
- `avatarPublicId`: ID công khai của hình ảnh trên Cloudinary (dùng để xóa)

### 4. JSP Profile Page (`src/main/webapp/user/userprofile.jsp`)
Cập nhật để hỗ trợ hiển thị và upload avatar:
- Hiển thị avatar từ `avatarUrl` thay vì local path
- Nút "Thay đổi ảnh" với icon camera
- Form ẩn để upload file
- JavaScript xử lý preview ảnh trước upload
- Hiển thị thông báo success/error

## Cấu Hình Cloudinary

### 1. Cấu Hình trong `application.properties`
```ini
cloud_name=YOUR_CLOUD_NAME
cloud_api_key=YOUR_API_KEY
cloud_api_secret=YOUR_API_SECRET
```

### 2. Từ đâu lấy thông tin?
1. Tạo tài khoản tại https://cloudinary.com
2. Vào Dashboard → Settings → Account
3. Copy các giá trị:
   - Cloud Name: tên của bạn trên Cloudinary
   - API Key: khóa API
   - API Secret: khóa bí mật

## Quy Trình Thay Đổi Avatar

### Trên Frontend (userprofile.jsp)
1. Người dùng nhấp nút "Thay đổi ảnh"
2. Cửa sổ chọn file mở
3. Người dùng chọn hình ảnh
4. JavaScript kiểm tra:
   - Loại file (phải là image/*)
   - Kích thước file (≤ 5MB)
5. Hiển thị preview của hình ảnh
6. Gửi form POST đến `/user/profile` với action=`change_avatar`

### Trên Backend (ProfileServlet.changeUserAvatar)
1. Kiểm tra Cloudinary có được cấu hình
2. Lấy file từ request (name: `avatarFile`)
3. Xác thực:
   - File không trống
   - Content-type bắt đầu bằng `image/`
   - Kích thước ≤ 5MB
4. Xóa avatar cũ từ Cloudinary (nếu có)
5. Tải avatar mới lên Cloudinary với public ID: `avatar_user_{userId}_{timestamp}`
6. Cập nhật fields trong User:
   - `avatarUrl`: URL từ Cloudinary (secure_url)
   - `avatarPublicId`: public_id từ Cloudinary
7. Lưu vào database qua `ProfileService.updateProfile()`
8. Cập nhật session attributes:
   - `currentUser`
   - `userInSession`
   - `user`
   - `authUser`
9. Đặt thông báo và redirect về trang profile

### Hiển thị Avatar
- Kiểm tra xem `userInSession.avatarUrl` có tồn tại không
- Nếu có: hiển thị từ URL Cloudinary
- Nếu không: hiển thị icon mặc định `fa-user-circle`

## Xác Thực Dữ Liệu

### Client-side (JavaScript)
```javascript
// Kiểm tra loại file
if (!file.type.startsWith('image/')) {
    alert('Vui lòng chọn một tệp hình ảnh');
    return;
}

// Kiểm tra kích thước (5MB)
const maxSize = 5 * 1024 * 1024;
if (file.size > maxSize) {
    alert('Hình ảnh không được vượt quá 5MB');
    return;
}
```

### Server-side (Java)
```java
// Kiểm tra file không rỗng
if (filePart == null || filePart.getSize() == 0) {
    // Lỗi
}

// Kiểm tra content-type
String contentType = filePart.getContentType();
if (!contentType.startsWith("image/")) {
    // Lỗi
}

// Kiểm tra kích thước
if (filePart.getSize() > 5 * 1024 * 1024) {
    // Lỗi
}
```

## Thông Báo (Messages)

### Success Message
- **Text**: "Cập nhật ảnh đại diện thành công!"
- **Type**: success
- **Màu nền**: #d4edda (xanh nhạt)

### Error Messages
- **Chưa cấu hình**: "Hệ thống chưa được cấu hình để tải hình ảnh"
- **Không chọn file**: "Vui lòng chọn một tệp hình ảnh"
- **File không phải ảnh**: "Tệp phải là một hình ảnh"
- **File quá lớn**: "Hình ảnh không được vượt quá 5MB"
- **Lỗi upload**: "Lỗi tải lên hình ảnh: [chi tiết lỗi]"
- **Màu nền**: #f8d7da (đỏ nhạt)

## Dependencies
```xml
<dependency>
    <groupId>com.cloudinary</groupId>
    <artifactId>cloudinary-http5</artifactId>
    <version>2.3.2</version>
</dependency>
```

## Multipart Configuration
```java
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1MB
    maxFileSize = 5 * 1024 * 1024,         // 5MB
    maxRequestSize = 10 * 1024 * 1024      // 10MB
)
```

## File Upload Flow

```
User Interface
    ↓
JavaScript Validation
    ↓ (if valid)
POST /user/profile?action=change_avatar
    ↓
ProfileServlet.doPost()
    ↓
changeUserAvatar()
    ↓
Server-side Validation
    ↓ (if valid)
Delete Old Avatar (Cloudinary)
    ↓
Upload New Avatar (Cloudinary)
    ↓
Update User Entity
    ↓
Save to Database
    ↓
Update Session
    ↓
Display Success Message
```

## Logging
Các sự kiện được log:
- Avatar upload thành công: `Logger.info()`
- Lỗi upload: `Logger.log(Level.SEVERE)`
- Lỗi cập nhật: `Logger.log(Level.SEVERE)`

## Kỹ Thuật

### Public ID Generation
```java
String publicId = "avatar_user_" + currentUser.getId() + "_" + System.currentTimeMillis();
```
Ví dụ: `avatar_user_123_1717062768000`

### Session Attributes (Tất cả được cập nhật)
- `currentUser`: User object
- `userInSession`: User object
- `user`: User object
- `authUser`: User object

### Xóa Avatar Cũ
```java
if (currentUser.getAvatarPublicId() != null && !currentUser.getAvatarPublicId().isEmpty()) {
    CloudinaryUtil.deleteImage(currentUser.getAvatarPublicId());
}
```

## Testing

### Test Upload
1. Đăng nhập vào `/user/profile`
2. Nhấp "Thay đổi ảnh"
3. Chọn file hình ảnh từ máy
4. Kiểm tra:
   - Avatar được cập nhật trên trang
   - Thông báo "Cập nhật ảnh đại diện thành công!" xuất hiện
   - Avatar vẫn còn sau khi refresh trang

### Test Error Cases
1. **File quá lớn** (>5MB): Hiển thị lỗi
2. **File không phải ảnh**: Hiển thị lỗi
3. **Không chọn file**: Hiển thị lỗi
4. **Cloudinary chưa cấu hình**: Hiển thị lỗi

## Troubleshooting

### Avatar không hiển thị
- Kiểm tra `avatarUrl` có giá trị trong database
- Kiểm tra Cloudinary credentials có đúng
- Kiểm tra network (có thể bị chặn)

### Upload thất bại
- Kiểm tra Cloudinary credentials trong `application.properties`
- Kiểm tra kích thước file (≤ 5MB)
- Kiểm tra loại file (phải là image/*)
- Kiểm tra logs cho chi tiết lỗi

### Session không cập nhật
- Kiểm tra `profileService.updateProfile()` có throw exception
- Kiểm tra session có valid

## Phát Triển Trong Tương Lai
1. Crop/Edit hình ảnh trước upload
2. Hỗ trợ drag & drop
3. Caching cho performance
4. Compression hình ảnh tự động
5. CloudFront CDN để tăng tốc độ

## Tài Liệu Tham Khảo
- Cloudinary Docs: https://cloudinary.com/documentation
- Jakarta Servlet: https://jakarta.ee/specifications/servlet/

## Version History
- **v1.0** (2026-05-29): Initial implementation
  - Avatar upload to Cloudinary
  - Avatar display in profile
  - Validation on client and server side

