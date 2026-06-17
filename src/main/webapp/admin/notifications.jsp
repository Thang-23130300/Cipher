<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Cảnh báo bảo mật - Admin</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendors/bootstrap-icons/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-pages.css">
    <style>
        .unread-row {
            background-color: #f8fafc !important;
            font-weight: 600;
        }
        .notification-card {
            border-radius: 10px;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
            border: none;
        }
    </style>
</head>
<body>
<div class="admin-shell">
    <div class="sidebar-backdrop" data-sidebar-close></div>
    <jsp:include page="common/admin-sidebar.jsp"/>

    <div class="admin-main">
        <jsp:include page="common/admin-topbar.jsp"/>

        <main class="dashboard-content">
            <div class="container-fluid px-3 px-lg-4 py-4">
                <div class="content-header mb-4">
                    <h1 class="h3 mb-1 text-gray-800">Cảnh báo bảo mật</h1>
                    <p class="text-muted">Danh sách các cảnh báo bảo mật, lộ khóa và chữ ký lỗi tự động ghi nhận từ hệ thống.</p>
                </div>

                <div class="card notification-card mb-4">
                    <div class="card-header bg-white py-3 border-bottom d-flex justify-content-between align-items-center">
                        <h5 class="mb-0" style="font-weight: 600; color: #333;">
                            <i class="fas fa-shield-alt text-primary me-2"></i>Danh sách cảnh báo
                        </h5>
                    </div>
                    <div class="table-container">
                        <table class="data-table mb-0">
                            <thead>
                            <tr>
                                <th style="width: 80px;">Mã</th>
                                <th style="width: 150px;">Phân loại</th>
                                <th>Tiêu đề & Nội dung cảnh báo</th>
                                <th style="width: 180px;">Đơn hàng liên quan</th>
                                <th style="width: 180px;">Thời gian</th>
                                <th style="width: 120px;">Trạng thái</th>
                                <th style="width: 120px; text-align: center;">Thao tác</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${empty notifications}">
                                    <tr>
                                        <td colspan="7" class="text-center py-5 text-muted">
                                            <i class="fas fa-bell-slash fa-3x mb-3 text-secondary" style="opacity: 0.5;"></i>
                                            <p class="mb-0" style="font-size: 1.1rem; font-weight: 500;">Không có cảnh báo bảo mật nào</p>
                                            <p class="text-muted small">Hệ thống đang hoạt động an toàn và không phát hiện sự cố.</p>
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${notifications}" var="noti">
                                        <%
                                            nlu.fit.web.souvenirecommerce.features.notification.dto.NotificationDTO noti = 
                                                (nlu.fit.web.souvenirecommerce.features.notification.dto.NotificationDTO) pageContext.getAttribute("noti");
                                            String formattedDate = noti.getCreatedAt() != null ? 
                                                noti.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "";
                                            pageContext.setAttribute("formattedDate", formattedDate);
                                        %>
                                        <tr class="${noti.read ? '' : 'unread-row'}">
                                            <td>#${noti.id}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${noti.type == 'KEY_COMPROMISED'}">
                                                        <span class="badge" style="background-color: #f97316; color: white; padding: 4px 8px; border-radius: 4px; font-weight: 500;">
                                                            <i class="fas fa-exclamation-triangle"></i> Lộ khóa
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${noti.type == 'SIGNATURE_INVALID'}">
                                                        <span class="badge bg-danger text-white" style="padding: 4px 8px; border-radius: 4px; font-weight: 500;">
                                                            <i class="fas fa-times-circle"></i> Lỗi chữ ký
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${noti.type == 'ORDER_PROCESSING_BLOCKED'}">
                                                        <span class="badge bg-warning text-dark" style="padding: 4px 8px; border-radius: 4px; font-weight: 500;">
                                                            <i class="fas fa-lock"></i> Bị chặn
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${noti.type == 'KEY_LOST'}">
                                                        <span class="badge bg-secondary text-white" style="padding: 4px 8px; border-radius: 4px; font-weight: 500;">
                                                            <i class="fas fa-key"></i> Mất khóa
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${noti.type == 'KEY_RISK'}">
                                                        <span class="badge bg-warning text-dark" style="padding: 4px 8px; border-radius: 4px; font-weight: 500;">
                                                            <i class="fas fa-shield-halved"></i> Rủi ro khóa
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-info text-white" style="padding: 4px 8px; border-radius: 4px; font-weight: 500;">
                                                            <i class="fas fa-info-circle"></i> Hệ thống
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div style="font-weight: 600; color: #2d3748;">${noti.title}</div>
                                                <div class="text-muted small mt-1" style="font-weight: 400; line-height: 1.4;">${noti.message}</div>
                                            </td>
                                            <td>
                                                <c:if test="${not empty noti.orderId}">
                                                    <a href="${pageContext.request.contextPath}/admin/orders?action=view&id=${noti.orderId}" 
                                                       style="font-weight: 600; text-decoration: none;">
                                                        <i class="fas fa-receipt me-1"></i>Đơn #${noti.orderId}
                                                    </a>
                                                </c:if>
                                            </td>
                                            <td>
                                                <span class="text-muted small font-monospace">${formattedDate}</span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${noti.read}">
                                                        <span class="badge bg-light text-secondary border">Đã xem</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-primary text-white">Mới</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td style="text-align: center;">
                                                <c:if test="${!noti.read}">
                                                    <form method="post" action="${pageContext.request.contextPath}/admin/notifications" style="display: inline;">
                                                        <input type="hidden" name="action" value="markAsRead">
                                                        <input type="hidden" name="id" value="${noti.id}">
                                                        <button type="submit" class="btn btn-sm btn-outline-primary py-1 px-2" style="font-size: 0.8rem; border-radius: 4px;" title="Đánh dấu đã xem">
                                                            <i class="fas fa-check"></i> Đã đọc
                                                        </button>
                                                    </form>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </main>

        <jsp:include page="common/admin-footer.jsp"/>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/admin-main.js"></script>
</body>
</html>
