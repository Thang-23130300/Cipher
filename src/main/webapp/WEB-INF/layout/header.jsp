<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<header class="site-header">
    <div class="header-top">
        <div class="layout-shell header-top__inner">
            <div class="header-utility">
                <a href="${pageContext.request.contextPath}/home">INOLA Souvenir</a>
                <span aria-hidden="true">|</span>
                <a href="${pageContext.request.contextPath}/admin/dashboard">Kênh quản trị</a>
            </div>

            <div class="header-account">
                <c:choose>
                    <c:when test="${not empty sessionScope.currentUser}">
                        <span>Xin chào, ${sessionScope.currentUser.fullName}</span>
                        <a href="${pageContext.request.contextPath}/user/account/profile">Tài khoản</a>
                        <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                        <a href="${pageContext.request.contextPath}/signup" class="header-register">Đăng ký</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <div class="layout-shell header-main">
        <a class="header-logo" href="${pageContext.request.contextPath}/home" aria-label="Trang chủ INOLA">
            <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="INOLA">
        </a>

        <form class="header-search" action="${pageContext.request.contextPath}/search" method="get" role="search">
            <input type="search" name="keyword" placeholder="Tìm đặc sản, quà tặng, thủ công..." autocomplete="off">
            <button type="submit" aria-label="Tìm kiếm">
                <i class="fa-solid fa-magnifying-glass"></i>
            </button>
        </form>

        <div class="header-actions">
            <a class="header-icon-link" href="${pageContext.request.contextPath}/user/orders" aria-label="Đơn hàng">
                <i class="fa-solid fa-receipt"></i>
            </a>
            <a class="header-cart" href="${pageContext.request.contextPath}/cart" aria-label="Giỏ hàng">
                <i class="fa-solid fa-cart-shopping"></i>
                <span id="header-cart-count">
                    <c:choose>
                        <c:when test="${sessionScope.cart != null}">
                            ${sessionScope.cart.totalQuantity()}
                        </c:when>
                        <c:otherwise>0</c:otherwise>
                    </c:choose>
                </span>
            </a>
        </div>
    </div>

    <nav class="header-nav" aria-label="Danh mục sản phẩm">
        <div class="layout-shell header-nav__inner">
            <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
            <c:forEach var="category" items="${topCategories}">
                <a href="${pageContext.request.contextPath}/category?id=${category.id}">
                    ${category.categoryName}
                </a>
            </c:forEach>
        </div>
    </nav>
</header>
