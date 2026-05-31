<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý danh mục - Admin</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-dashboard.css">
    <style>
        .modal { display: none; position: fixed; z-index: 1000; left: 0; top: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); }
        .modal-content { background: white; margin: 100px auto; padding: 0; width: 500px; border-radius: 8px; }
        .modal-header { padding: 20px; border-bottom: 1px solid #e5e7eb; display: flex; justify-content: space-between; align-items: center; }
        .modal-body { padding: 20px; }
        .close-btn { background: none; border: none; font-size: 24px; cursor: pointer; color: #6b7280; }
        .form-group { margin-bottom: 16px; }
        .form-group label { display: block; margin-bottom: 6px; font-weight: 500; }
        .form-control { width: 100%; padding: 8px 12px; border: 1px solid #d1d5db; border-radius: 6px; }
        .btn-submit { background: #3b82f6; color: white; padding: 10px 20px; border: none; border-radius: 6px; cursor: pointer; width: 100%; }
        .alert { padding: 12px; border-radius: 6px; margin-bottom: 16px; }
        .alert-success { background: #d1fae5; color: #065f46; }
        .alert-error { background: #fee2e2; color: #991b1b; }
        .category-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 20px; padding: 20px; }
        .category-card { border: 1px solid #e5e7eb; border-radius: 8px; overflow: hidden; transition: box-shadow 0.2s; }
        .category-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
        .category-image { width: 100%; height: 150px; object-fit: cover; }
        .category-info { padding: 16px; }
        .category-name { font-weight: 600; font-size: 16px; margin-bottom: 8px; }
    </style>
</head>
<body>
<div class="admin-container">
    <jsp:include page="common/admin-sidebar.jsp"/>

    <div class="admin-main">
        <jsp:include page="common/admin-topbar.jsp"/>

        <div class="admin-content">
            <div class="content-header">
                <h1>Quản lý danh mục</h1>
                <c:if test="${canCreateCategory}">
                    <button class="btn-primary" onclick="openAddModal()">
                        <i class="fas fa-plus"></i> Thêm danh mục mới
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
                    <h3>Danh sách danh mục (${categories.size()} danh mục)</h3>
                </div>

                <div class="category-grid">
                    <c:forEach items="${categories}" var="cat">
                        <div class="category-card">
                            <img src="${pageContext.request.contextPath}/${cat.image}"
                                 alt="${cat.name}"
                                 class="category-image"
                                 onerror="this.src='https://placehold.co/250x150?text=${cat.name}'">
                            <div class="category-info">
                                <div class="category-name">${cat.name}</div>
                                <div class="action-buttons">
                                    <c:if test="${canUpdateCategory}">
                                    <button class="btn-icon btn-edit" onclick="openEditModal(${cat.id}, '${cat.name}', '${cat.image}')" title="Sửa">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                </c:if>
                                <c:if test="${canDeleteCategory}">
                                    <form action="${pageContext.request.contextPath}/admin/categories" method="post" style="display: inline;" onsubmit="return confirm('Bạn có chắc muốn xóa danh mục này?');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${cat.id}">
                                        <button class="btn-icon btn-delete" title="Xóa">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </form>
                                </c:if>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modal Add/Edit -->
<div id="categoryModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 id="modalTitle">Thêm danh mục mới</h3>
            <button class="close-btn" onclick="closeModal()">&times;</button>
        </div>
        <div class="modal-body">
            <form action="${pageContext.request.contextPath}/admin/categories" method="post">
                <input type="hidden" name="action" id="formAction" value="add">
                <input type="hidden" name="id" id="categoryId">

                <div class="form-group">
                    <label>Tên danh mục *</label>
                    <input type="text" name="name" id="categoryName" class="form-control" required>
                </div>

                <div class="form-group">
                    <label>URL hình ảnh *</label>
                    <input type="text" name="imageUrl" id="categoryImage" class="form-control" required placeholder="assets/images/categories/...">
                </div>

                <button type="submit" class="btn-submit">Lưu danh mục</button>
            </form>
        </div>
    </div>
</div>

<script>
    const modal = document.getElementById('categoryModal');

    function openAddModal() {
        document.getElementById('modalTitle').innerText = 'Thêm danh mục mới';
        document.getElementById('formAction').value = 'add';
        document.getElementById('categoryId').value = '';
        document.getElementById('categoryName').value = '';
        document.getElementById('categoryImage').value = '';
        modal.style.display = 'block';
    }

    function openEditModal(id, name, image) {
        document.getElementById('modalTitle').innerText = 'Cập nhật danh mục';
        document.getElementById('formAction').value = 'edit';
        document.getElementById('categoryId').value = id;
        document.getElementById('categoryName').value = name;
        document.getElementById('categoryImage').value = image;
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
