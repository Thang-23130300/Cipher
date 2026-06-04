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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-roles.css">
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
                <div class="content-actions">
                    <c:if test="${canCreateRole}">
                        <a href="${ctx}/admin/roles" class="btn btn-secondary" style="text-decoration:none; display:inline-flex; align-items:center; gap:8px;">
                            <i class="fas fa-plus"></i>
                            Tạo nhóm mới
                        </a>
                    </c:if>
                </div>
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
                                    <c:if test="${canCreateRole or canUpdateRole}">
                                        <button type="submit" class="btn-primary">
                                            <i class="fas fa-save"></i>
                                            <c:choose>
                                                <c:when test="${not empty editRole}">Lưu thay đổi</c:when>
                                                <c:otherwise>Tạo nhóm</c:otherwise>
                                            </c:choose>
                                        </button>
                                    </c:if>
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
                                                <c:if test="${canUpdateRole}">
                                                    <a href="${ctx}/admin/roles?editId=${role.id}" class="btn-icon" style="background:#3b82f6; color:white; text-decoration:none;">
                                                        <i class="fas fa-edit"></i>
                                                    </a>
                                                </c:if>
                                                <c:if test="${canDeleteRole}">
                                                    <form action="${ctx}/admin/roles" method="post" onsubmit="return confirm('Xóa nhóm quyền này?');">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="id" value="${role.id}">
                                                    <button type="submit" class="btn-icon" style="background:#ef4444; color:white;" <c:if test="${role.system}">disabled</c:if>>
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
                                                ${user.fullName} - ${user.email} (
                                                <c:choose>
                                                    <c:when test="${not empty user.roles}">
                                                        <c:forEach items="${user.roles}" var="r" varStatus="st">
                                                            <c:out value="${r.name}"/>
                                                            <c:if test="${!st.last}">, </c:if>
                                                        </c:forEach>
                                                    </c:when>
                                                    <c:otherwise>—</c:otherwise>
                                                </c:choose>
                                                )
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <small style="color:#6b7280; display:block; margin-top:8px;">
                                        Giữ `Ctrl` hoặc `Cmd` để chọn nhiều người dùng.
                                    </small>
                                </div>

                                <c:if test="${canUpdateRole}">
                                    <button type="submit" class="btn-primary" style="width:100%;">
                                        <i class="fas fa-users-cog"></i> Lưu phân nhóm
                                    </button>
                                </c:if>
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
