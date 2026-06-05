<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<nav class="navbar admin-navbar navbar-expand bg-white">
    <div class="container-fluid px-3 px-lg-4">
        <button class="sidebar-toggle" type="button" data-sidebar-toggle aria-controls="adminSidebar" aria-expanded="true" aria-label="Toggle sidebar">
            <span></span>
            <span></span>
            <span></span>
        </button>

        <form class="d-none d-md-flex ms-3 flex-grow-1" role="search" action="${ctx}/admin/products" method="get">
            <input class="form-control search-input" type="search" name="search" placeholder="Search products, orders, reports" value="${param.search}" aria-label="Search">
        </form>

        <div class="navbar-actions ms-auto">
            <button class="icon-button theme-toggle" type="button" data-theme-toggle aria-label="Switch color theme" title="Switch color theme">
                <i class="bi bi-moon-stars" data-theme-icon aria-hidden="true"></i>
            </button>

            <div class="dropdown">
                <button class="icon-button" type="button" data-bs-toggle="dropdown" aria-expanded="false" aria-label="Notifications">
                    <span class="notification-dot"></span>
                    <i class="bi bi-bell" aria-hidden="true"></i>
                </button>
                <div class="dropdown-menu dropdown-menu-end notification-menu">
                    <div class="dropdown-header fw-bold text-body">Notifications</div>
                    <a class="dropdown-item" href="${ctx}/admin/orders">
                        <span class="notification-title">New order received</span>
                        <span class="notification-time">Just now</span>
                    </a>
                    <a class="dropdown-item" href="${ctx}/admin/products">
                        <span class="notification-title">Product stock check</span>
                        <span class="notification-time">12 minutes ago</span>
                    </a>
                    <a class="dropdown-item" href="${ctx}/admin/settings">
                        <span class="notification-title">System settings updated</span>
                        <span class="notification-time">1 hour ago</span>
                    </a>
                </div>
            </div>

            <div class="dropdown">
                <button class="profile-button dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                    <img class="avatar-img avatar-sm" src="${ctx}/admin/template/assets/images/avatar/avatar.jpg" alt="Admin avatar">
                    <span class="profile-name d-none d-sm-inline">Admin</span>
                </button>
                <ul class="dropdown-menu dropdown-menu-end">
                    <li><a class="dropdown-item" href="${ctx}/admin/settings">Profile</a></li>
                    <li><a class="dropdown-item" href="${ctx}/admin/settings">Account settings</a></li>
                    <li><hr class="dropdown-divider"></li>
                    <li><a class="dropdown-item" href="${ctx}/logout">Sign out</a></li>
                </ul>
            </div>
        </div>
    </div>
</nav>
