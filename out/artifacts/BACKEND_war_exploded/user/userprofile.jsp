<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hồ sơ của tôi - INOLA</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/HomePageFooter.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/User.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/HomePageAlter.css">
</head>
<body>

<jsp:include page="/views/common/header-renew.jsp"/>

<div class="page-container">
    <div class="account-container">

        <!-- ===== SIDEBAR ===== -->
        <aside class="account-sidebar">
            <div class="sidebar-profile">

                <div class="avatar-container">
                    <c:choose>
                        <c:when test="${not empty sessionScope.userInSession.avatarUrl}">
                            <img src="${sessionScope.userInSession.avatarUrl}"
                                 class="avatar-img"
                                 style="width:80px;height:80px;border-radius:50%;object-fit:cover;"
                                 alt="Avatar">
                        </c:when>
                        <c:otherwise>
                            <i class="fa-solid fa-user-circle avatar-placeholder"></i>
                        </c:otherwise>
                    </c:choose>
                </div>

                <strong>${sessionScope.userInSession.fullName}</strong>
                <span>${sessionScope.userInSession.email}</span>

                <!-- FORM ĐỔI AVATAR -->
                <form id="avatarForm"
                      action="${pageContext.request.contextPath}/user/profile"
                      method="post"
                      enctype="multipart/form-data"
                      style="margin-top:8px;">

                    <input type="hidden" name="action" value="change_avatar">

                    <input type="file"
                           id="avatarInput"
                           name="avatarFile"
                           accept="image/*"
                           style="display:none;">

                    <button type="button"
                            class="btn-change-avatar"
                            onclick="triggerAvatarUpload()"
                            style="margin-top: 10px; padding: 8px 16px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer;">
                        <i class="fa-solid fa-camera"></i> Thay đổi ảnh
                    </button>
                </form>
            </div>

            <hr class="sidebar-divider">

            <ul class="account-menu">
                <li class="active">
                    <a href="${pageContext.request.contextPath}/user/profile">
                        <i class="fa-solid fa-user-circle"></i> Hồ Sơ Của Tôi
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/user/orders">
                        <i class="fa-solid fa-receipt"></i> Đơn Hàng
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/user/review">
                        <i class="fa-solid fa-star"></i> Đánh Giá Của Tôi
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/user/change-password">
                        <i class="fa-solid fa-key"></i> Đổi Mật Khẩu
                    </a>
                </li>
            </ul>
        </aside>

        <!-- ===== CONTENT ===== -->
        <main class="account-content">

            <!-- MESSAGE DISPLAY -->
            <c:if test="${not empty sessionScope.profileMessage}">
                <div class="alert alert-${sessionScope.profileMessageType eq 'success' ? 'success' : 'danger'}"
                     style="padding: 12px 15px; margin-bottom: 20px; border-radius: 4px;
                            background-color: ${sessionScope.profileMessageType eq 'success' ? '#d4edda' : '#f8d7da'};
                            color: ${sessionScope.profileMessageType eq 'success' ? '#155724' : '#721c24'};
                            border: 1px solid ${sessionScope.profileMessageType eq 'success' ? '#c3e6cb' : '#f5c6cb'};">
                    ${sessionScope.profileMessage}
                </div>
                <c:remove var="profileMessage" scope="session"/>
                <c:remove var="profileMessageType" scope="session"/>
            </c:if>
            <div class="profile-card">
                <h2>Thông tin cá nhân</h2>

                <form class="profile-form"
                      action="${pageContext.request.contextPath}/user/profile"
                      method="post">

                    <input type="hidden" name="action" value="update_profile">

                    <div class="form-group">
                        <label>Họ và tên</label>
                        <input type="text" name="fullName"
                               value="${sessionScope.userInSession.fullName}">
                    </div>

                    <div class="form-group">
                        <label>Email</label>
                        <input type="email"
                               value="${sessionScope.userInSession.email}"
                               disabled>
                    </div>

                    <div class="form-group">
                        <label>Số điện thoại</label>
                        <input type="tel" name="phone"
                               value="${sessionScope.userInSession.phone}">
                    </div>

                    <div class="form-group radio-group">
                        <label>Giới tính</label>
                        <input type="radio" name="gender" value="Nam"
                               <c:if test="${sessionScope.userInSession.gender == 'Nam'}">checked</c:if>> Nam
                        <input type="radio" name="gender" value="Nữ"
                               <c:if test="${sessionScope.userInSession.gender == 'Nữ'}">checked</c:if>> Nữ
                    </div>

                    <div class="form-group">
                        <label>Ngày sinh</label>
                        <input type="date" name="dob"
                               value="${sessionScope.userInSession.dob}">
                    </div>

                    <button type="submit" class="btn-save">Lưu thay đổi</button>
                </form>
            </div>

            <!-- ADDRESS -->
            <div class="profile-card">
                <div class="card-header" style="display:flex;justify-content:space-between;align-items:center;">
                    <h2>Địa chỉ</h2>

                    <c:if test="${param.view ne 'add-address'}">
                        <a href="${pageContext.request.contextPath}/user/profile?view=add-address"
                           class="btn-add-address">
                            + Thêm địa chỉ
                        </a>
                    </c:if>
                </div>

                <!-- FORM ADD -->
                <c:if test="${param.view == 'add-address'}">
                    <form action="${pageContext.request.contextPath}/user/address/add"
                          method="post"
                          class="profile-form"
                          style="max-width:600px;">

                        <div class="form-group">
                            <label>Địa chỉ chi tiết</label>
                            <input type="text" name="addressDetail"
                                   placeholder="Số nhà, tên đường..." required>
                        </div>

                        <div class="form-group">
                            <label>Phường / Xã</label>
                            <input type="text" name="ward" required>
                        </div>

                        <div class="form-group">
                            <label>Quận / Huyện</label>
                            <input type="text" name="district" required>
                        </div>

                        <div class="form-group">
                            <label>Tỉnh / Thành phố</label>
                            <input type="text" name="city" required>
                        </div>

                        <div style="margin-top:20px;">
                            <button type="submit" class="btn-save">Lưu địa chỉ</button>
                            <a href="${pageContext.request.contextPath}/user/profile"
                               style="margin-left:14px;">Hủy</a>
                        </div>
                    </form>
                </c:if>

                <!-- LIST -->
                <c:if test="${param.view ne 'add-address'}">
                    <c:choose>
                        <c:when test="${not empty listAddr}">
                            <c:forEach items="${listAddr}" var="addr">
                                <div class="address-item">

                                    <div class="address-header">
                                        <strong>${sessionScope.userInSession.fullName}</strong>
                                        <c:if test="${addr.isDefault == 1}">
                                            <span class="address-default">Mặc định</span>
                                        </c:if>
                                    </div>

                                    <p class="address-text">
                                            ${addr.addressDetail}, ${addr.ward},
                                            ${addr.district}, ${addr.city}
                                    </p>

                                    <div class="address-actions">
                                        <c:if test="${addr.isDefault == 0}">
                                            <a href="${pageContext.request.contextPath}/user/address/default?id=${addr.id}">
                                                Đặt mặc định
                                            </a>

                                            <a href="${pageContext.request.contextPath}/user/address/delete?id=${addr.id}"
                                               onclick="return confirm('Bạn có chắc muốn xóa địa chỉ này?')"
                                               class="danger">
                                                Xóa
                                            </a>
                                        </c:if>
                                    </div>

                                </div>
                            </c:forEach>
                        </c:when>

                        <c:otherwise>
                            <div style="text-align:center;padding:20px;color:#999;">
                                Bạn chưa có địa chỉ nào.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </div>

        <jsp:include page="/views/common/footer.jsp"/>

<!-- ===== JS ĐỔI AVATAR ===== -->
<script>
    function triggerAvatarUpload() {
        document.getElementById('avatarInput').click();
    }

    document.getElementById('avatarInput').addEventListener('change', function () {
        if (this.files.length > 0) {
            const file = this.files[0];

            // Validate file type
            if (!file.type.startsWith('image/')) {
                alert('Vui lòng chọn một tệp hình ảnh');
                return;
            }

            // Validate file size (5MB)
            const maxSize = 5 * 1024 * 1024;
            if (file.size > maxSize) {
                alert('Hình ảnh không được vượt quá 5MB');
                return;
            }

            // Show preview
            const reader = new FileReader();
            reader.onload = function(e) {
                const avatarImg = document.querySelector('.avatar-img');
                if (avatarImg) {
                    avatarImg.src = e.target.result;
                }
            };
            reader.readAsDataURL(file);

            // Submit form
            document.getElementById('avatarForm').submit();
        }
    });
</script>

</body>
</html>
