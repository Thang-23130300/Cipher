<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đơn hàng của tôi - INOLA</title>

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

                <li class="active">
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

            <h2 style="margin-bottom:20px;">Đơn hàng của tôi</h2>

            <div class="order-content">
                <div class="table-responsive">
                    <table class="order-list-table">
                        <thead>
                        <tr>
                            <th>MÃ ĐƠN HÀNG</th>
                            <th>SẢN PHẨM</th>
                            <th>NGÀY ĐẶT</th>
                            <th>TỔNG TIỀN</th>
                            <th>TRẠNG THÁI</th>
                        </tr>
                        </thead>
                        <tbody>

                        <c:forEach items="${orderList}" var="o">
                            <tr>
                                <td>
                                    <a href="${pageContext.request.contextPath}/user/orders?action=detail&id=${o.id}">
                                        #${o.id}
                                    </a>
                                </td>
                                <td>
                                    <strong>${o.productName}</strong>
                                    <span>x${o.quantity}</span>
                                </td>
                                <td>
                                    <fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy"/>
                                </td>
                                <td>
                                    <fmt:formatNumber value="${o.totalPrice}" pattern="#,###"/>₫
                                </td>
                                <td>
                                    <span class="badge">${o.statusName}</span>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty orderList}">
                            <tr>
                                <td colspan="5" style="text-align:center;padding:40px;color:#999;">
                                    Bạn chưa có đơn hàng nào.
                                </td>
                            </tr>
                        </c:if>

                        </tbody>
                    </table>
                </div>
            </div>

        </main>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp"/>

</body>
</html>
