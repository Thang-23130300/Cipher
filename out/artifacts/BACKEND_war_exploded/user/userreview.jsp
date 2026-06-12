<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đánh giá của tôi - INOLA</title>

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
                    <a href="${pageContext.request.contextPath}/user/order">
                        <i class="fa-solid fa-receipt"></i> Đơn Hàng
                    </a>
                </li>
                <li class="active">
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

            <!-- REVIEW TABS -->
            <nav class="review-tabs">
                <a href="?tab=all"
                   class="tab-item ${param.tab == 'all' || empty param.tab ? 'active' : ''}">
                    Tất cả
                </a>
                <a href="?tab=pending"
                   class="tab-item ${param.tab == 'pending' ? 'active' : ''}">
                    Chưa đánh giá
                </a>
            </nav>

            <!-- REVIEW LIST -->
            <div class="review-list-container">

                <c:forEach items="${reviewList}" var="r">
                    <div class="review-card">

                        <div class="review-product-info">
                            <img src="${r.productImage}"
                                 alt="${r.productName}">
                            <div class="product-details">
                                <strong>${r.productName}</strong>
                                <p>Phân loại: ${r.categoryName}</p>
                            </div>
                        </div>

                        <hr class="review-divider">

                        <div class="review-content">
                            <div class="review-rating">
                                <c:forEach begin="1" end="5" var="i">
                                    <i class="${i <= r.rating ? 'fa-solid' : 'fa-regular'} fa-star"></i>
                                </c:forEach>
                            </div>

                            <span class="review-date">
                                Đánh giá ngày ${r.reviewDate}
                            </span>

                            <p class="review-comment">
                                    ${r.comment}
                            </p>
                        </div>
                    </div>
                </c:forEach>

                <c:if test="${empty reviewList}">
                    <div style="text-align:center; padding:50px; color:#666;">
                        <i class="fa-regular fa-comment-dots"
                           style="font-size:48px; margin-bottom:15px;"></i>
                        <p>Bạn chưa có đánh giá nào.</p>
                    </div>
                </c:if>

            </div>
        </main>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp"/>

</body>
</html>
