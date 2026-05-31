<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý khách hàng - Admin</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-dashboard.css">
    <style>
        .alert { padding: 12px; border-radius: 6px; margin-bottom: 16px; }
        .alert-success { background: #d1fae5; color: #065f46; }
        .alert-error { background: #fee2e2; color: #991b1b; }
        .badge { padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: 500; }
        .badge-success { background: #d1fae5; color: #065f46; }
        .badge-danger { background: #fee2e2; color: #991b1b; }
        .modal { display: none; position: fixed; z-index: 1000; left: 0; top: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); }
        .modal-content { background: white; margin: 80px auto; padding: 0; width: 500px; border-radius: 8px; }
        .modal-header { padding: 20px; border-bottom: 1px solid #e5e7eb; display: flex; justify-content: space-between; align-items: center; }
        .modal-body { padding: 20px; }
        .close-btn { background: none; border: none; font-size: 24px; cursor: pointer; color: #6b7280; }
        .form-group { margin-bottom: 16px; }
        .form-group label { display: block; margin-bottom: 6px; font-weight: 500; }
        .form-control { width: 100%; padding: 8px 12px; border: 1px solid #d1d5db; border-radius: 6px; }
        .btn-submit { background: #3b82f6; color: white; padding: 10px 20px; border: none; border-radius: 6px; cursor: pointer; width: 100%; }
    </style>
</head>
<body>
<div class="admin-container">
    <jsp:include page="common/admin-sidebar.jsp"/>

    <div class="admin-main">
        <jsp:include page="common/admin-topbar.jsp"/>

        <div class="admin-content">
            <div class="content-header">
                <h1>Quản lý khách hàng</h1>
                <c:if test="${canCreateCustomer}">
                    <button class="btn-primary" onclick="openAddModal()">
                        <i class="fas fa-plus"></i> Thêm khách hàng mới
                    </button>
                </c:if>
            </div>

            <c:if test="${not empty message}">
                <div class="alert alert-${messageType}">
                        ${message}
                </div>
            </c:if>

            <div class="card">
                <div class="card-header">
                    <h3>Danh sách khách hàng (${totalCustomers} khách hàng)</h3>
                </div>

                <div class="table-container">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th style="width: 60px;">ID</th>
                            <th>Họ tên</th>
                            <th>Email</th>
                            <th>Số điện thoại</th>
                            <th style="width: 120px;">Trạng thái</th>
                            <th style="width: 150px;">Ngày đăng ký</th>
                            <th style="width: 150px;">Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${customers}" var="customer">
                            <tr>
                                <td>${customer.id}</td>
                                <td style="font-weight: 500;">${customer.fullName}</td>
                                <td>${customer.email}</td>
                                <td>${customer.phone}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${customer.status == 'Active'}">
                                            <span class="badge badge-success">Hoạt động</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-danger">Bị cấm</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${customer.createdAt}</td>
                                <td>
                                    <div class="action-buttons">
                                        <c:if test="${canUpdateCustomer}">
                                            <button class="btn-icon btn-edit" onclick="openEditModal(${customer.id}, '${customer.fullName}', '${customer.email}', '${customer.phone}')" title="Sửa">
                                                <i class="fas fa-edit"></i>
                                            </button>
                                            <form action="${pageContext.request.contextPath}/admin/customers" method="post" style="display: inline;">
                                                <input type="hidden" name="action" value="toggleStatus">
                                                <input type="hidden" name="id" value="${customer.id}">
                                                <input type="hidden" name="currentStatus" value="${customer.status}">
                                                <button class="btn-icon" style="background: ${customer.status == 'Active' ? '#f59e0b' : '#10b981'};" title="${customer.status == 'Active' ? 'Cấm' : 'Mở cấm'}">
                                                    <i class="fas fa-${customer.status == 'Active' ? 'ban' : 'check'}"></i>
                                                </button>
                                            </form>
                                        </c:if>
                                        <c:if test="${canDeleteCustomer}">
                                            <form action="${pageContext.request.contextPath}/admin/customers" method="post" style="display: inline;" onsubmit="return confirm('Bạn có chắc muốn xóa khách hàng này?');">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="id" value="${customer.id}">
                                                <button class="btn-icon btn-delete" title="Xóa">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </form>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <div style="padding: 20px; display: flex; justify-content: center; align-items: center; gap: 8px;">
                        <c:if test="${currentPage > 1}">
                            <a href="?page=${currentPage - 1}" class="btn-icon" style="text-decoration: none;">
                                <i class="fas fa-chevron-left"></i>
                            </a>
                        </c:if>

                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <c:choose>
                                <c:when test="${i == currentPage}">
                                    <span style="padding: 8px 12px; background: #3b82f6; color: white; border-radius: 6px; font-weight: 500;">${i}</span>
                                </c:when>
                                <c:otherwise>
                                    <a href="?page=${i}" style="padding: 8px 12px; background: #f3f4f6; color: #374151; border-radius: 6px; text-decoration: none;">${i}</a>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">
                            <a href="?page=${currentPage + 1}" class="btn-icon" style="text-decoration: none;">
                                <i class="fas fa-chevron-right"></i>
                            </a>
                        </c:if>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<!-- Modal Add/Edit -->
<div id="customerModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 id="modalTitle">Thêm khách hàng mới</h3>
            <button class="close-btn" onclick="closeModal()">&times;</button>
        </div>
        <div class="modal-body">
            <form action="${pageContext.request.contextPath}/admin/customers" method="post">
                <input type="hidden" name="action" id="formAction" value="add">
                <input type="hidden" name="id" id="customerId">

                <div class="form-group">
                    <label>Họ tên *</label>
                    <input type="text" name="fullName" id="customerName" class="form-control" required>
                </div>

                <div class="form-group">
                    <label>Email *</label>
                    <input type="email" name="email" id="customerEmail" class="form-control" required>
                </div>

                <div class="form-group" id="passwordGroup">
                    <label>Mật khẩu *</label>
                    <input type="password" name="password" id="customerPassword" class="form-control">
                </div>

                <div class="form-group">
                    <label>Số điện thoại</label>
                    <input type="tel" name="phone" id="customerPhone" class="form-control">
                </div>

                <button type="submit" class="btn-submit">Lưu khách hàng</button>
            </form>
        </div>
    </div>
</div>

<script>
    const modal = document.getElementById('customerModal');

    function openAddModal() {
        document.getElementById('modalTitle').innerText = 'Thêm khách hàng mới';
        document.getElementById('formAction').value = 'add';
        document.getElementById('customerId').value = '';
        document.getElementById('customerName').value = '';
        document.getElementById('customerEmail').value = '';
        document.getElementById('customerPhone').value = '';
        document.getElementById('customerPassword').value = '';
        document.getElementById('customerPassword').required = true;
        document.getElementById('passwordGroup').style.display = 'block';
        modal.style.display = 'block';
    }

    function openEditModal(id, name, email, phone) {
        document.getElementById('modalTitle').innerText = 'Cập nhật khách hàng';
        document.getElementById('formAction').value = 'edit';
        document.getElementById('customerId').value = id;
        document.getElementById('customerName').value = name;
        document.getElementById('customerEmail').value = email;
        document.getElementById('customerPhone').value = phone;
        document.getElementById('customerPassword').required = false;
        document.getElementById('passwordGroup').style.display = 'none';
        modal.style.display = 'block';
    }

    function closeModal() {
        modal.style.display = 'none';
    }

    window.onclick = function(event) {
        if (event.target == modal) closeModal();
    }
</script>
</body>
</html>
