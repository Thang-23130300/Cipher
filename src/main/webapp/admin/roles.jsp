<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Phân quyền - Admin</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-dashboard.css">
    <style>
        .permission-layout {
            display: grid;
            grid-template-columns: 1.15fr 0.85fr;
            gap: 24px;
            margin-top: 24px;
        }

        @media (max-width: 1100px) {
            .permission-layout {
                grid-template-columns: 1fr;
            }
        }

        .form-group {
            margin-bottom: 16px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #1f2937;
        }

        .form-control {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #d1d5db;
            border-radius: 8px;
            font-size: 14px;
        }

        .permission-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 12px;
        }

        @media (max-width: 768px) {
            .permission-grid {
                grid-template-columns: 1fr;
            }
        }

        .permission-pill {
            display: flex;
            align-items: flex-start;
            gap: 10px;
            padding: 12px;
            border: 1px solid #e5e7eb;
            border-radius: 10px;
            background: #fafafa;
        }

        .permission-pill input {
            margin-top: 3px;
        }

        .permission-pill strong {
            display: block;
            color: #111827;
        }

        .permission-pill span {
            display: block;
            color: #6b7280;
            font-size: 12px;
            margin-top: 2px;
        }

        .tag-list {
            display: flex;
            flex-wrap: wrap;
            gap: 6px;
        }

        .tag {
            padding: 4px 10px;
            border-radius: 999px;
            background: #eff6ff;
            color: #1d4ed8;
            font-size: 12px;
            font-weight: 600;
        }

        .tag-muted {
            background: #f3f4f6;
            color: #374151;
        }

        .alert {
            padding: 12px 16px;
            border-radius: 8px;
            margin-bottom: 20px;
        }

        .alert-success {
            background: #dcfce7;
            color: #166534;
        }

        .alert-error {
            background: #fee2e2;
            color: #991b1b;
        }

        .inline-actions {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;
        }

        .inline-actions .btn-icon {
            border: none;
            border-radius: 8px;
            padding: 8px 10px;
            cursor: pointer;
        }
    </style>
</head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<div class="admin-container">
    <jsp:include page="common/admin-sidebar.jsp">
        <jsp:param name="activePage" value="roles" />
    </jsp:include>

    <div class="admin-main">
        <jsp:include page="common/admin-topbar.jsp" />

        <main class="admin-content">
            <div class="content-header">
                <h1 class="content-title">Phân quyền</h1>
            </div>

            <c:if test="${not empty message}">
                <div class="alert alert-${messageType}">
                    ${message}
                </div>
            </c:if>

            <div class="permission-layout">
                <div>
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">
                                <c:choose>
                                    <c:when test="${not empty editRole}">Cập nhật nhóm quyền</c:when>
                                    <c:otherwise>Tạo nhóm quyền</c:otherwise>
                                </c:choose>
                            </h3>
                            <p class="card-description">Nhóm quyền được gán các quyền CRUD cố định cho từng tài nguyên.</p>
                        </div>
                        <div class="card-content">
                            <form action="${ctx}/admin/roles" method="post">
                                <input type="hidden" name="action" value="${not empty editRole ? 'edit' : 'add'}">
                                <c:if test="${not empty editRole}">
                                    <input type="hidden" name="id" value="${editRole.id}">
                                </c:if>

                                <div class="form-group">
                                    <label>Tên nhóm quyền</label>
                                    <input type="text" name="name" class="form-control"
                                           value="${not empty editRole ? editRole.name : ''}"
                                           placeholder="Ví dụ: Content Manager" required>
                                </div>

                                <div class="form-group">
                                    <label>Mô tả</label>
                                    <textarea name="description" class="form-control" rows="3"
                                              placeholder="Mô tả ngắn về nhóm quyền">${not empty editRole ? editRole.description : ''}</textarea>
                                </div>

                                <div class="form-group">
                                    <label>Quyền truy cập</label>
                                    <div class="permission-grid">
                                        <c:forEach items="${permissions}" var="permission">
                                            <label class="permission-pill">
                                                <input type="checkbox" name="permissionIds" value="${permission.id}"
                                                       <c:if test="${not empty editPermissionIds and editPermissionIds.contains(permission.id)}">checked</c:if>>
                                                <div>
                                                    <strong>${permission.resource}.${permission.action}</strong>
                                                    <span>${permission.description}</span>
                                                </div>
                                            </label>
                                        </c:forEach>
                                    </div>
                                </div>

                                <div class="inline-actions">
                                    <button type="submit" class="btn-primary">
                                        <i class="fas fa-save"></i>
                                        <c:choose>
                                            <c:when test="${not empty editRole}">Lưu thay đổi</c:when>
                                            <c:otherwise>Tạo nhóm</c:otherwise>
                                        </c:choose>
                                    </button>
                                    <c:if test="${not empty editRole}">
                                        <a href="${ctx}/admin/roles" class="btn-secondary" style="text-decoration:none; display:inline-flex; align-items:center; gap:8px;">
                                            Hủy
                                        </a>
                                    </c:if>
                                </div>
                            </form>
                        </div>
                    </div>

                    <div class="card" style="margin-top: 24px;">
                        <div class="card-header">
                            <h3 class="card-title">Các nhóm quyền hiện có</h3>
                            <p class="card-description">Mỗi nhóm có thể được gán cho nhiều người dùng.</p>
                        </div>
                        <div class="table-container">
                            <table class="data-table">
                                <thead>
                                <tr>
                                    <th>Tên nhóm</th>
                                    <th>Quyền</th>
                                    <th>Người dùng</th>
                                    <th>Hệ thống</th>
                                    <th>Thao tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${roles}" var="role">
                                    <tr>
                                        <td>
                                            <strong>${role.name}</strong>
                                            <div style="color:#6b7280; font-size:12px; margin-top:4px;">
                                                ${role.description}
                                            </div>
                                        </td>
                                        <td>
                                            <div class="tag-list">
                                                <c:choose>
                                                    <c:when test="${empty role.permissions}">
                                                        <span class="tag tag-muted">Chưa có quyền</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:forEach items="${role.permissions}" var="perm">
                                                            <span class="tag">${perm.resource}.${perm.action}</span>
                                                        </c:forEach>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </td>
                                        <td>${role.userCount}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${role.system}">
                                                    <span class="tag tag-muted">System</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="tag">Custom</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div class="inline-actions">
                                                <a href="${ctx}/admin/roles?editId=${role.id}" class="btn-icon" style="background:#3b82f6; color:white; text-decoration:none;">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <form action="${ctx}/admin/roles" method="post" onsubmit="return confirm('Xóa nhóm quyền này?');">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="id" value="${role.id}">
                                                    <button type="submit" class="btn-icon" style="background:#ef4444; color:white;" <c:if test="${role.system}">disabled</c:if>>
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <div>
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">Gán người dùng vào nhóm</h3>
                            <p class="card-description">Chọn một nhóm quyền và danh sách người dùng cần gán.</p>
                        </div>
                        <div class="card-content">
                            <form action="${ctx}/admin/roles" method="post">
                                <input type="hidden" name="action" value="assignUsers">

                                <div class="form-group">
                                    <label>Nhóm quyền</label>
                                    <select name="roleId" class="form-control" required>
                                        <option value="">-- Chọn nhóm quyền --</option>
                                        <c:forEach items="${roles}" var="role">
                                            <option value="${role.id}"
                                                    <c:if test="${not empty editRole and editRole.id == role.id}">selected</c:if>>
                                                ${role.name}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label>Người dùng</label>
                                    <select name="userIds" class="form-control" multiple size="12" required>
                                        <c:forEach items="${users}" var="user">
                                            <option value="${user.id}"
                                                    <c:if test="${not empty editUserIds and editUserIds.contains(user.id.toString())}">selected</c:if>>
                                                ${user.fullName} - ${user.email} (${user.role})
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <small style="color:#6b7280; display:block; margin-top:8px;">
                                        Giữ `Ctrl` hoặc `Cmd` để chọn nhiều người dùng.
                                    </small>
                                </div>

                                <button type="submit" class="btn-primary" style="width:100%;">
                                    <i class="fas fa-users-cog"></i> Lưu phân nhóm
                                </button>
                            </form>
                        </div>
                    </div>

                    <div class="card" style="margin-top:24px;">
                        <div class="card-header">
                            <h3 class="card-title">Người dùng trong nhóm đang chọn</h3>
                            <p class="card-description">Hiển thị nhanh thành viên đã được gán cho từng nhóm.</p>
                        </div>
                        <div class="card-content">
                            <c:choose>
                                <c:when test="${empty roles}">
                                    <p style="color:#6b7280;">Chưa có nhóm quyền nào.</p>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${roles}" var="role">
                                        <div style="padding: 12px 0; border-bottom: 1px solid #e5e7eb;">
                                            <strong>${role.name}</strong>
                                            <div class="tag-list" style="margin-top:8px;">
                                                <c:choose>
                                                    <c:when test="${empty role.users}">
                                                        <span class="tag tag-muted">Chưa gán người dùng</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:forEach items="${role.users}" var="member">
                                                            <span class="tag tag-muted">${member.fullName}</span>
                                                        </c:forEach>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>
</body>
</html>
