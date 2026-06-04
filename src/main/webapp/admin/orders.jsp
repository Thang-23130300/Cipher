<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý đơn hàng - Admin</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-dashboard.css">
    <style>
        .filter-group {
            display: flex;
            gap: 16px;
            flex-wrap: wrap;
            align-items: center;
        }
        .filter-group select,
        .filter-group input {
            min-width: 220px;
            padding: 10px 12px;
            border: 1px solid #d1d5db;
            border-radius: 8px;
            background: #ffffff;
            color: #111827;
        }
        .table-container {
            overflow-x: auto;
        }
        .data-table {
            width: 100%;
            border-collapse: collapse;
            min-width: 900px;
        }
        .data-table th,
        .data-table td {
            padding: 14px 16px;
            border-bottom: 1px solid #e5e7eb;
            text-align: left;
            vertical-align: middle;
        }
        .data-table th {
            font-weight: 600;
            font-size: 14px;
            color: #374151;
            background: #f8fafc;
        }
        .data-table tbody tr:hover {
            background: #f9fafb;
        }
        .action-buttons {
            display: flex;
            gap: 8px;
            align-items: center;
        }
        .btn-icon {
            border: none;
            background: #eef2ff;
            color: #4338ca;
            border-radius: 8px;
            width: 36px;
            height: 36px;
            display: inline-flex;
            justify-content: center;
            align-items: center;
            cursor: pointer;
            transition: background 0.2s ease;
        }
        .btn-icon:hover {
            background: #dbe4ff;
        }
        .badge {
            padding: 6px 12px;
            border-radius: 999px;
            font-size: 12px;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
        }
        .alert {
            padding: 16px 20px;
            border-radius: 12px;
            margin-bottom: 24px;
            font-size: 14px;
        }
        .alert-success {
            background: #ecfdf5;
            color: #166534;
            border: 1px solid #d1fae5;
        }
        .alert-danger {
            background: #fef2f2;
            color: #991b1b;
            border: 1px solid #fecaca;
        }
        .pagination {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
            justify-content: flex-end;
            padding: 18px 0 0;
        }
        .pagination-link {
            display: inline-flex;
            min-width: 38px;
            height: 38px;
            align-items: center;
            justify-content: center;
            border-radius: 8px;
            border: 1px solid #d1d5db;
            background: #ffffff;
            color: #374151;
            text-decoration: none;
            font-weight: 500;
        }
        .pagination-link.active {
            background: #4338ca;
            color: #ffffff;
            border-color: #4338ca;
        }
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(15, 23, 42, 0.6);
            align-items: center;
            justify-content: center;
            padding: 24px;
        }
        .modal-content {
            width: 100%;
            max-width: 560px;
            background-color: #ffffff;
            border-radius: 18px;
            overflow: hidden;
            box-shadow: 0 24px 80px rgba(15, 23, 42, 0.12);
        }
        .modal-header {
            padding: 24px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px solid #e5e7eb;
        }
        .modal-header h3 {
            margin: 0;
            font-size: 18px;
        }
        .modal-body {
            padding: 24px;
        }
        .close-btn {
            border: none;
            background: transparent;
            color: #6b7280;
            cursor: pointer;
            font-size: 24px;
            line-height: 1;
        }
        .form-group {
            margin-bottom: 18px;
        }
        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #1f2937;
        }
        .form-control {
            width: 100%;
            padding: 12px 14px;
            border-radius: 12px;
            border: 1px solid #d1d5db;
            background: #ffffff;
            color: #111827;
        }
        .modal-footer {
            display: flex;
            gap: 12px;
            justify-content: flex-end;
            padding: 24px;
            border-top: 1px solid #e5e7eb;
        }
        .btn {
            border: none;
            border-radius: 10px;
            padding: 12px 18px;
            cursor: pointer;
            font-weight: 600;
        }
        .btn-primary {
            background: #4338ca;
            color: white;
        }
        .btn-secondary {
            background: #f3f4f6;
            color: #111827;
        }
    </style>
</head>
<body>
<div class="admin-container">
    <jsp:include page="common/admin-sidebar.jsp"/>
    <div class="admin-main">
        <jsp:include page="common/admin-topbar.jsp"/>
        <div class="admin-content">
            <div class="content-header">
                <h1>Quản lý đơn hàng</h1>
            </div>

            <c:if test="${not empty param.success}">
                <div class="alert alert-success">Cập nhật trạng thái đơn hàng thành công.</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="alert alert-danger">Cập nhật trạng thái đơn hàng thất bại. Vui lòng thử lại.</div>
            </c:if>

            <div class="stats-grid" style="margin-bottom: 30px;">
                <div class="stat-card" style="background: #3498db;">
                    <div class="stat-icon" >
                        <i class="fas fa-clock"></i>
                    </div>
                    <div class="stat-info">
                        <h3>Chờ xác nhận</h3>
                        <p class="stat-value">${pendingCount}</p>
                    </div>
                </div>
                <div class="stat-card" style="background: #f39c12;">
                    <div class="stat-icon" >
                        <i class="fas fa-box"></i>
                    </div>
                    <div class="stat-info">
                        <h3>Đang xử lý</h3>
                        <p class="stat-value">${processingCount}</p>
                    </div>
                </div>
                <div class="stat-card" style="background: #9b59b6;">
                    <div class="stat-icon" >
                        <i class="fas fa-shipping-fast"></i>
                    </div>
                    <div class="stat-info">
                        <h3>Đang giao</h3>
                        <p class="stat-value">${shippingCount}</p>
                    </div>
                </div>
                <div class="stat-card" style="background: #27ae60;">
                    <div class="stat-icon" >
                        <i class="fas fa-check-circle"></i>
                    </div>
                    <div class="stat-info">
                        <h3>Hoàn thành</h3>
                        <p class="stat-value">${completedCount}</p>
                    </div>
                </div>
            </div>

            <div class="card">
                <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; gap: 16px; flex-wrap: wrap;">
                    <div>
                        <h3>Danh sách đơn hàng</h3>
                        <p style="margin: 6px 0 0; color: #6b7280; font-size: 14px;">Tổng đơn: ${totalOrders}</p>
                    </div>
                    <div class="filter-group">
                        <select onchange="filterByStatus(this.value)">
                            <option value="all" ${empty statusFilter || statusFilter == 'all' ? 'selected' : ''}>Tất cả trạng thái</option>
                            <option value="Chờ xác nhận" ${statusFilter == 'Chờ xác nhận' ? 'selected' : ''}>Chờ xác nhận</option>
                            <option value="Đang xử lý" ${statusFilter == 'Đang xử lý' ? 'selected' : ''}>Đang xử lý</option>
                            <option value="Đang giao" ${statusFilter == 'Đang giao' ? 'selected' : ''}>Đang giao</option>
                            <option value="Hoàn thành" ${statusFilter == 'Hoàn thành' ? 'selected' : ''}>Hoàn thành</option>
                            <option value="Đã hủy" ${statusFilter == 'Đã hủy' ? 'selected' : ''}>Đã hủy</option>
                        </select>
                    </div>
                </div>
                <div class="table-container">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th>Mã đơn</th>
                            <th>Khách hàng</th>
                            <th>Ngày đặt</th>
                            <th>Tổng tiền</th>
                            <th>Trạng thái</th>
                            <th>Phương thức</th>
                            <th>Hành động</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty orders}">
                                <tr>
                                    <td colspan="7" style="text-align: center; padding: 40px; color: #9ca3af;">
                                        <i class="fas fa-inbox" style="font-size: 42px; margin-bottom: 10px;"></i>
                                        <p>Chưa có đơn hàng nào.</p>
                                        <p style="font-size: 13px; margin-top: 4px;">Đơn hàng sẽ xuất hiện ở đây khi khách hàng đặt hàng.</p>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${orders}" var="order">
                                    <tr>
                                        <td>#${order.id}</td>
                                        <td>
                                            <div style="font-weight: 600;">${order.customerName}</div>
                                            <div style="font-size: 12px; color: #6b7280;">${order.customerEmail}</div>
                                        </td>
                                        <td><fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                        <td style="font-weight: 700; color: #b91c1c;"><fmt:formatNumber value="${order.totalAmount}" pattern="#,#00"/>₫</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${order.status == 'Chờ xác nhận'}">
                                                    <span class="badge badge-warning">${order.status}</span>
                                                </c:when>
                                                <c:when test="${order.status == 'Đang xử lý'}">
                                                    <span class="badge badge-info">${order.status}</span>
                                                </c:when>
                                                <c:when test="${order.status == 'Đang giao'}">
                                                    <span class="badge badge-info">${order.status}</span>
                                                </c:when>
                                                <c:when test="${order.status == 'Hoàn thành'}">
                                                    <span class="badge badge-success">${order.status}</span>
                                                </c:when>
                                                <c:when test="${order.status == 'Đã hủy'}">
                                                    <span class="badge badge-danger">${order.status}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-secondary">${order.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${not empty order.paymentMethod ? order.paymentMethod : 'COD'}</td>
                                        <td>
                                            <div class="action-buttons">
                                                <a href="${pageContext.request.contextPath}/admin/orders?action=view&id=${order.id}" class="btn-icon" title="Xem chi tiết">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <c:if test="${canUpdateOrder}">
                                                    <button type="button" class="btn-icon" title="Cập nhật trạng thái" onclick="showUpdateStatusModal(${order.id}, '${order.status}')">
                                                        <i class="fas fa-edit"></i>
                                                    </button>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>
                <div class="pagination">
                    <c:if test="${totalPages > 1}">
                        <c:forEach var="pageIndex" begin="1" end="${totalPages}">
                            <a class="pagination-link ${currentPage == pageIndex ? 'active' : ''}"
                               href="${pageContext.request.contextPath}/admin/orders?page=${pageIndex}${not empty statusFilter && statusFilter != 'all' ? '&status=' + statusFilter : ''}">
                                ${pageIndex}
                            </a>
                        </c:forEach>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<div id="updateStatusModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3>Cập nhật trạng thái đơn hàng</h3>
            <button type="button" class="close-btn" onclick="closeUpdateStatusModal()">&times;</button>
        </div>
        <form id="updateStatusForm" method="post" action="${pageContext.request.contextPath}/admin/orders">
            <input type="hidden" name="action" value="updateStatus">
            <input type="hidden" name="orderId" id="updateOrderId">
            <div class="modal-body">
                <div class="form-group">
                    <label>Trạng thái hiện tại</label>
                    <p id="currentStatus" style="margin: 0; color: #374151; font-weight: 600;"></p>
                </div>
                <div class="form-group">
                    <label>Chọn trạng thái mới</label>
                    <select name="status" class="form-control" required>
                        <option value="">-- Chọn trạng thái --</option>
                        <option value="Chờ xác nhận">Chờ xác nhận</option>
                        <option value="Đang xử lý">Đang xử lý</option>
                        <option value="Đang giao">Đang giao</option>
                        <option value="Hoàn thành">Hoàn thành</option>
                        <option value="Đã hủy">Đã hủy</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="closeUpdateStatusModal()">Hủy</button>
                <button type="submit" class="btn btn-primary">Cập nhật</button>
            </div>
        </form>
    </div>
</div>

<script>
    function filterByStatus(status) {
        let url = '${pageContext.request.contextPath}/admin/orders?status=' + encodeURIComponent(status);
        if (status === 'all') {
            url = '${pageContext.request.contextPath}/admin/orders';
        }
        window.location.href = url;
    }

    function showUpdateStatusModal(orderId, currentStatus) {
        document.getElementById('updateOrderId').value = orderId;
        document.getElementById('currentStatus').textContent = currentStatus;
        document.getElementById('updateStatusModal').style.display = 'flex';
    }

    function closeUpdateStatusModal() {
        document.getElementById('updateStatusModal').style.display = 'none';
        document.getElementById('updateStatusForm').reset();
    }

    window.onclick = function(event) {
        const modal = document.getElementById('updateStatusModal');
        if (event.target === modal) {
            closeUpdateStatusModal();
        }
    }
</script>
</body>
</html>
