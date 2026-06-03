<%@ page contentType="text/html;charset=UTF-8" language="java" import="nlu.fit.web.souvenirecommerce.common.utils.PermissionHelper" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    request.setAttribute("canViewDashboard", true);
    request.setAttribute("canViewProducts", PermissionHelper.hasAnyPermission(request, "product"));
    request.setAttribute("canViewOrders", PermissionHelper.hasAnyPermission(request, "order"));
    request.setAttribute("canUpdateOrder", PermissionHelper.hasPermission(request, "order", "update"));
    request.setAttribute("canViewCustomers", PermissionHelper.hasAnyPermission(request, "customer"));
    request.setAttribute("canViewCategories", PermissionHelper.hasAnyPermission(request, "category"));
    request.setAttribute("canViewBanners", PermissionHelper.hasAnyPermission(request, "banner"));
    request.setAttribute("canViewSettings", PermissionHelper.hasAnyPermission(request, "settings"));
    request.setAttribute("canViewRoles", PermissionHelper.hasAnyPermission(request, "role"));

    request.setAttribute("canCreateProduct", PermissionHelper.hasPermission(request, "product", "create"));
    request.setAttribute("canUpdateProduct", PermissionHelper.hasPermission(request, "product", "update"));
    request.setAttribute("canDeleteProduct", PermissionHelper.hasPermission(request, "product", "delete"));
    request.setAttribute("canCreateCategory", PermissionHelper.hasPermission(request, "category", "create"));
    request.setAttribute("canUpdateCategory", PermissionHelper.hasPermission(request, "category", "update"));
    request.setAttribute("canDeleteCategory", PermissionHelper.hasPermission(request, "category", "delete"));
    request.setAttribute("canCreateBanner", PermissionHelper.hasPermission(request, "banner", "create"));
    request.setAttribute("canUpdateBanner", PermissionHelper.hasPermission(request, "banner", "update"));
    request.setAttribute("canDeleteBanner", PermissionHelper.hasPermission(request, "banner", "delete"));
    request.setAttribute("canCreateCustomer", PermissionHelper.hasPermission(request, "customer", "create"));
    request.setAttribute("canUpdateCustomer", PermissionHelper.hasPermission(request, "customer", "update"));
    request.setAttribute("canDeleteCustomer", PermissionHelper.hasPermission(request, "customer", "delete"));
    request.setAttribute("canUpdateSettings", PermissionHelper.hasPermission(request, "settings", "update"));
    request.setAttribute("canCreateRole", PermissionHelper.hasPermission(request, "role", "create"));
    request.setAttribute("canUpdateRole", PermissionHelper.hasPermission(request, "role", "update"));
    request.setAttribute("canDeleteRole", PermissionHelper.hasPermission(request, "role", "delete"));
%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="currentAdminUser" value="${not empty sessionScope.user ? sessionScope.user : sessionScope.userInSession}" />

<aside class="admin-sidebar">
    <div class="sidebar-header">
        <a href="${ctx}/admin/dashboard" class="sidebar-logo">
            <div class="sidebar-logo-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M3 3v18h18"/>
                    <path d="m19 9-5 5-4-4-3 3"/>
                </svg>
            </div>
            <span class="sidebar-logo-text">INOLA Admin</span>
        </a>
    </div>

    <div class="sidebar-content">
        <ul class="sidebar-menu">
            <li class="sidebar-menu-item">
                <a href="${ctx}/admin/dashboard" class="sidebar-menu-button ${param.activePage == 'dashboard' ? 'active' : ''}">
                    <svg class="sidebar-menu-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <rect width="7" height="9" x="3" y="3" rx="1"/>
                        <rect width="7" height="5" x="14" y="3" rx="1"/>
                        <rect width="7" height="9" x="14" y="12" rx="1"/>
                        <rect width="7" height="5" x="3" y="16" rx="1"/>
                    </svg>
                    <span>Dashboard</span>
                </a>
            </li>
            <c:if test="${canViewProducts}">
                <li class="sidebar-menu-item">
                    <a href="${ctx}/admin/products" class="sidebar-menu-button ${param.activePage == 'products' ? 'active' : ''}">
                    <svg class="sidebar-menu-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z"/>
                        <path d="M3 6h18"/>
                        <path d="M16 10a4 4 0 0 1-8 0"/>
                    </svg>
                    <span>Sản phẩm</span>
                </a>
            </li>
            </c:if>
            <c:if test="${canViewOrders}">
                <li class="sidebar-menu-item">
                    <a href="${ctx}/admin/orders" class="sidebar-menu-button ${param.activePage == 'orders' ? 'active' : ''}">
                    <svg class="sidebar-menu-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="8" cy="21" r="1"/>
                        <circle cx="19" cy="21" r="1"/>
                        <path d="M2.05 2.05h2l2.66 12.42a2 2 0 0 0 2 1.58h9.78a2 2 0 0 0 1.95-1.57l1.65-7.43H5.12"/>
                    </svg>
                    <span>Đơn hàng</span>
                </a>
            </li>
            </c:if>
            <c:if test="${canViewCustomers}">
                <li class="sidebar-menu-item">
                    <a href="${ctx}/admin/customers" class="sidebar-menu-button ${param.activePage == 'customers' ? 'active' : ''}">
                    <svg class="sidebar-menu-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
                        <circle cx="9" cy="7" r="4"/>
                        <path d="M22 21v-2a4 4 0 0 0-3-3.87"/>
                        <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                    </svg>
                    <span>Khách hàng</span>
                </a>
            </li>
            </c:if>
            <c:if test="${canViewCategories}">
                <li class="sidebar-menu-item">
                    <a href="${ctx}/admin/categories" class="sidebar-menu-button ${param.activePage == 'categories' ? 'active' : ''}">
                    <svg class="sidebar-menu-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <rect width="7" height="7" x="3" y="3" rx="1"/>
                        <rect width="7" height="7" x="14" y="3" rx="1"/>
                        <rect width="7" height="7" x="14" y="14" rx="1"/>
                        <rect width="7" height="7" x="3" y="14" rx="1"/>
                    </svg>
                    <span>Danh mục</span>
                </a>
            </li>
            </c:if>
            <c:if test="${canViewBanners}">
                <li class="sidebar-menu-item">
                    <a href="${ctx}/admin/banner" class="sidebar-menu-button ${param.activePage == 'banner' ? 'active' : ''}">
                    <svg class="sidebar-menu-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <rect width="18" height="18" x="3" y="3" rx="2"/>
                        <path d="M7 7h10"/>
                        <path d="M7 12h10"/>
                        <path d="M7 17h10"/>
                    </svg>
                    <span>Banner</span>
                </a>
            </li>
            </c:if>
            <c:if test="${canViewSettings}">
                <li class="sidebar-menu-item">
                    <a href="${ctx}/admin/settings" class="sidebar-menu-button ${param.activePage == 'settings' ? 'active' : ''}">
                    <svg class="sidebar-menu-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"/>
                        <circle cx="12" cy="12" r="3"/>
                    </svg>
                    <span>Cài đặt</span>
                </a>
            </li>
            </c:if>
            <c:if test="${canViewRoles}">
                <li class="sidebar-menu-item">
                    <a href="${ctx}/admin/roles" class="sidebar-menu-button ${param.activePage == 'roles' ? 'active' : ''}">
                    <svg class="sidebar-menu-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                        <circle cx="9" cy="7" r="4"/>
                        <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                        <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                    </svg>
                    <span>Phân quyền</span>
                </a>
            </li>
            </c:if>
        </ul>
    </div>

    <div class="sidebar-footer">
        <div class="sidebar-user">
            <div class="sidebar-user-avatar">
                <c:choose>
                    <c:when test="${not empty currentAdminUser.fullName}">
                        <c:out value="${fn:toUpperCase(fn:substring(currentAdminUser.fullName, 0, 1))}"/>
                    </c:when>
                    <c:otherwise>A</c:otherwise>
                </c:choose>
            </div>
            <div class="sidebar-user-info">
                <span class="sidebar-user-name"><c:out value="${currentAdminUser.fullName}"/></span>
                <span class="sidebar-user-role">
                    <c:choose>
                        <c:when test="${not empty currentAdminUser.roles}">
                            <c:forEach items="${currentAdminUser.roles}" var="r" varStatus="st">
                                <c:out value="${r.name}"/>
                                <c:if test="${!st.last}">, </c:if>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>—</c:otherwise>
                    </c:choose>
                </span>
            </div>
        </div>
    </div>
</aside>
