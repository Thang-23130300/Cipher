<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đổi mật khẩu - INOLA</title>

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
                        <c:when test="${not empty sessionScope.userInSession.avatar}">
                            <img src="${pageContext.request.contextPath}/assets/image/Avatar/${sessionScope.userInSession.avatar}"
                                 class="avatar-img"
                                 style="width:80px;height:80px;border-radius:50%;object-fit:cover;">
                        </c:when>
                        <c:otherwise>
                            <i class="fa-solid fa-user-circle avatar-placeholder"></i>
                        </c:otherwise>
                    </c:choose>
                </div>

                <strong>${sessionScope.userInSession.fullName}</strong>
                <span>${sessionScope.userInSession.email}</span>
            </div>

            <hr class="sidebar-divider">

            <ul class="account-menu">
                <li>
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
                <li class="active">
                    <a href="${pageContext.request.contextPath}/user/change-password">
                        <i class="fa-solid fa-key"></i> Đổi Mật Khẩu
                    </a>
                </li>
            </ul>
        </aside>

        <!-- ===== CONTENT ===== -->
        <main class="account-content">

            <div class="profile-card">
                <h2>Đổi mật khẩu</h2>

                <!-- THÔNG BÁO -->
                <c:if test="${not empty error}">
                    <div class="form-error">${error}</div>
                </c:if>

                <c:if test="${not empty success}">
                    <div class="form-success">${success}</div>
                </c:if>

                <!-- FORM ĐỔI MẬT KHẨU -->
                <form action="${pageContext.request.contextPath}/user/change-password"
                      method="post"
                      class="profile-form">

                    <div class="form-group">
                        <label>Mật khẩu hiện tại</label>
                        <input type="password" name="currentPassword" required>
                    </div>

                    <div class="form-group">
                        <label>Mật khẩu mới</label>
                        <input type="password" name="newPassword" required>
                    </div>

                    <div class="form-group">
                        <label>Xác nhận mật khẩu mới</label>
                        <input type="password" name="confirmPassword" required>
                    </div>

                    <button type="submit" class="btn-save">
                        Cập nhật mật khẩu
                    </button>
                </form>
            </div>

        </main>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp"/>

</body>
</html>
