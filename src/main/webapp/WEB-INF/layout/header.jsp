<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<header class="site-header">

    <div class="header-top">

        <div class="layout-shell header-top__inner">

            <div class="header-utility">

                <a href="${pageContext.request.contextPath}/home">
                    INOLA Souvenir
                </a>

                <span>|</span>

                <a href="${pageContext.request.contextPath}/admin/dashboard">
                    Kênh quản trị
                </a>

            </div>


            <div class="header-account">

                <c:choose>

                    <c:when test="${not empty authUser}">

                        <div class="user-box"
                             id="userToggle">

                            <span class="user-name">
                                Xin chào, ${authUser.fullName}
                            </span>

                            <i class="fa fa-caret-down"></i>

                        </div>

                        <div class="user-dropdown"
                             id="userDropdown">

                            <a href="${pageContext.request.contextPath}/user/profile">

                                <i class="fa fa-user"></i>

                                Tài khoản

                            </a>

                            <a href="${pageContext.request.contextPath}/user/orders">

                                <i class="fa fa-receipt"></i>

                                Đơn hàng

                            </a>

                            <a href="${pageContext.request.contextPath}/logout">

                                <i class="fa fa-sign-out-alt"></i>

                                Đăng xuất

                            </a>

                        </div>

                    </c:when>


                    <c:otherwise>

                        <a href="${pageContext.request.contextPath}/login">
                            Đăng nhập
                        </a>

                        <span>|</span>

                        <a href="${pageContext.request.contextPath}/signup">
                            Đăng ký
                        </a>

                    </c:otherwise>

                </c:choose>

            </div>

        </div>

    </div>


    <div class="layout-shell header-main">

        <div class="header-left">

            <button
                    id="headerMenuButton"
                    class="header-menu-button"
                    type="button"
                    aria-expanded="false">

                <i class="fa-solid fa-bars"></i>

            </button>


            <a href="${pageContext.request.contextPath}/home"
               class="header-logo">

                <img
                        src="${pageContext.request.contextPath}/assets/images/logo.png"
                        alt="INOLA">

            </a>


            <div
                    id="headerCategoryDropdown"
                    class="header-category-dropdown"
                    aria-hidden="true">

                <c:forEach
                        var="category"
                        items="${headerCategories}">

                    <a href="${pageContext.request.contextPath}/category?id=${category.id}">

                        <span class="header-category-icon">

                            <i class="fa-regular fa-square"></i>

                        </span>

                        <span class="header-category-name">

                                ${category.name}

                        </span>

                        <span class="header-category-arrow">

                            <i class="fa-solid fa-chevron-right"></i>

                        </span>

                    </a>

                </c:forEach>

            </div>

        </div>


        <form
                class="header-search"
                action="${pageContext.request.contextPath}/search"
                method="get">

            <input
                    type="search"
                    name="keyword"
                    placeholder="Tìm đặc sản, quà tặng, thủ công...">

            <button type="submit">

                <i class="fa-solid fa-magnifying-glass"></i>

            </button>

        </form>


        <div class="header-actions">

            <button class="theme-toggle-button"
                    type="button"
                    data-theme-toggle
                    aria-label="Chuyển giao diện sáng tối"
                    aria-pressed="false">

                <i class="fa-solid fa-moon"
                   data-theme-icon
                   aria-hidden="true"></i>

                <span class="theme-label"
                      data-theme-label>Tối</span>

            </button>

            <a class="header-icon-link"
               href="${pageContext.request.contextPath}/user/orders">

                <i class="fa-solid fa-receipt"></i>

            </a>


            <a class="header-cart"
               href="${pageContext.request.contextPath}/cart">

                <i class="fa-solid fa-cart-shopping"></i>

                <span id="header-cart-count">

                    ${cartItemCount}

                </span>

            </a>

        </div>

    </div>


    <c:choose>

        <c:when test="${headerMode == 'MENU_BAR'}">

            <nav class="header-menu-bar">

                <div class="layout-shell header-menu-bar__inner">

                    <c:forEach
                            var="category"
                            items="${headerTopCategories}">

                        <a href="#Loai${category.id}">
                                ${category.name}

                        </a>

                    </c:forEach>

                </div>

            </nav>

        </c:when>


        <c:when test="${headerMode == 'BREADCRUMB'}">

            <div class="header-breadcrumb">

                <div class="layout-shell header-breadcrumb__inner">

                    <a href="${pageContext.request.contextPath}/home">

                        Trang chủ

                    </a>


                    <c:if test="${not empty breadcrumbCategory}">

                        <span>/</span>

                        <a href="${pageContext.request.contextPath}/category?id=${breadcrumbCategory.id}">

                                ${breadcrumbCategory.categoryName}

                        </a>

                    </c:if>


                    <c:if test="${not empty breadcrumbProduct}">

                        <span>/</span>

                        <span class="current">

                                ${breadcrumbProduct.name}
                        </span>

                    </c:if>

                </div>

            </div>

        </c:when>

    </c:choose>

</header>


<div id="headerOverlay"
     class="header-overlay">
</div>
