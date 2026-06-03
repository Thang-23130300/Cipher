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
        /* MODAL STYLES */
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            animation: fadeIn 0.3s ease-in-out;
        }

        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }

        .modal.show { display: flex !important; align-items: center; justify-content: center; }

        .modal-content {
            background: white;
            padding: 0;
            width: 90%;
            max-width: 500px;
            border-radius: 8px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
            animation: slideUp 0.3s ease-out;
        }

        @keyframes slideUp {
            from { transform: translateY(50px); opacity: 0; }
            to { transform: translateY(0); opacity: 1; }
        }

        .modal-header {
            padding: 20px;
            border-bottom: 1px solid #e5e7eb;
            display: flex;
            justify-content: space-between;
            align-items: center;
            background: #f9fafb;
        }

        .modal-header h3 {
            margin: 0;
            color: #1f2937;
            font-size: 18px;
        }

        .close-btn {
            background: none;
            border: none;
            font-size: 24px;
            cursor: pointer;
            color: #6b7280;
            transition: color 0.2s;
        }

        .close-btn:hover { color: #1f2937; }

        .modal-body {
            padding: 20px;
        }

        .form-group {
            margin-bottom: 16px;
        }

        .form-group label {
            display: block;
            margin-bottom: 6px;
            font-weight: 500;
            color: #374151;
        }

        .form-group label .required {
            color: #ef4444;
        }

        .form-control {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #d1d5db;
            border-radius: 6px;
            font-size: 14px;
            transition: border-color 0.2s;
            box-sizing: border-box;
        }

        .form-control:focus {
            outline: none;
            border-color: #3b82f6;
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
        }

        .form-group .error-message {
            color: #ef4444;
            font-size: 12px;
            margin-top: 4px;
            display: none;
        }

        .form-group.has-error .form-control {
            border-color: #ef4444;
        }

        .form-group.has-error .error-message {
            display: block;
        }

        .modal-footer {
            padding: 16px 20px;
            border-top: 1px solid #e5e7eb;
            display: flex;
            gap: 10px;
            justify-content: flex-end;
            background: #f9fafb;
        }

        .btn-submit, .btn-cancel {
            padding: 10px 20px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-weight: 500;
            transition: all 0.2s;
        }
                        <!-- ALERT CONTAINER -->
                        <div id="alertContainer"></div>

                        <!-- HEADER -->
                        <div class="content-header">
                            <h1><i class="fas fa-folder"></i> Quản lý danh mục</h1>
                            <c:if test="${canCreateCategory}">
                                <button class="btn-primary" onclick="openAddModal()">
                                    <i class="fas fa-plus"></i> Thêm danh mục
                                </button>
                            </c:if>
                        </div>

                        <!-- CARD -->
                        <div class="card">
                            <div class="card-header">
                                <h3><i class="fas fa-list"></i> Danh sách danh mục (<span id="categoryCount">${categories.size()}</span>)</h3>
                            </div>

                            <!-- CATEGORY GRID -->
                            <div id="categoryGrid" class="category-grid">
                                <c:choose>
                                    <c:when test="${empty categories}">
                                        <div class="empty-state" style="grid-column: 1/-1;">
                                            <i class="fas fa-inbox"></i>
                                            <p>Chưa có danh mục nào. <a href="javascript:openAddModal()" style="color: #3b82f6;">Thêm danh mục đầu tiên</a></p>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach items="${categories}" var="cat">
                                            <div class="category-card" data-category-id="${cat.id}">
                                                <img src="${pageContext.request.contextPath}/${cat.image}"
                                                     alt="${cat.categoryName}"
                                                     class="category-image"
                                                     onerror="this.src='https://placehold.co/250x150?text=${cat.categoryName}'">
                                                <div class="category-info">
                                                    <div class="category-name">${cat.categoryName}</div>
                                                    <div class="category-meta">
                                                        <i class="fas fa-box"></i> ${cat.productCount} sản phẩm
                                                    </div>
                                                    <div class="action-buttons">
                                                        <c:if test="${canUpdateCategory}">
                                                            <button class="btn-icon btn-edit" onclick="openEditModal(${cat.id}, '${cat.categoryName}', '${cat.image}')" title="Sửa">
                                                                <i class="fas fa-edit"></i> Sửa
                                                            </button>
                                                        </c:if>
                                                        <c:if test="${canDeleteCategory}">
                                                            <button class="btn-icon btn-delete" onclick="deleteCategory(${cat.id}, '${cat.categoryName}', ${cat.productCount})" title="Xóa" ${cat.productCount > 0 ? 'disabled' : ''}>
                                                                <i class="fas fa-trash"></i> Xóa
                                                            </button>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- MODAL ADD/EDIT -->
            <div id="categoryModal" class="modal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3 id="modalTitle">Thêm danh mục mới</h3>
                        <button class="close-btn" onclick="closeModal()">&times;</button>
                    </div>
                    <div class="modal-body">
                        <form id="categoryForm" onsubmit="handleFormSubmit(event)">
                            <input type="hidden" id="formAction" value="add">
                            <input type="hidden" id="categoryId">

                            <div class="form-group">
                                <label>Tên danh mục <span class="required">*</span></label>
                                <input type="text" id="categoryName" class="form-control" placeholder="Nhập tên danh mục" required>
                                <div class="error-message"></div>
                            </div>

                            <div class="form-group">
                                <label>URL hình ảnh <span class="required">*</span></label>
                                <input type="text" id="categoryImage" class="form-control" placeholder="assets/images/categories/..." required>
                                <div class="error-message"></div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button class="btn-cancel" onclick="closeModal()">Hủy</button>
                        <button class="btn-submit" id="submitBtn" onclick="submitForm()" disabled>
                            <span id="submitBtnText">Lưu danh mục</span>
                        </button>
                    </div>
                </div>
            </div>

            <script>
                // CONSTANTS
                const CONTEXT_PATH = '${pageContext.request.contextPath}';
                const MODAL = document.getElementById('categoryModal');
                const FORM = document.getElementById('categoryForm');
                const SUBMIT_BTN = document.getElementById('submitBtn');

                // ==================== MODAL MANAGEMENT ====================

                function openAddModal() {
                    document.getElementById('modalTitle').innerText = 'Thêm danh mục mới';
                    document.getElementById('formAction').value = 'add';
                    document.getElementById('categoryId').value = '';
                    document.getElementById('categoryName').value = '';
                    document.getElementById('categoryImage').value = '';
                    clearErrors();
                    SUBMIT_BTN.disabled = false;
                    MODAL.classList.add('show');
                }

                function openEditModal(id, name, image) {
                    document.getElementById('modalTitle').innerText = 'Cập nhật danh mục';
                    document.getElementById('formAction').value = 'edit';
                    document.getElementById('categoryId').value = id;
                    document.getElementById('categoryName').value = name;
                    document.getElementById('categoryImage').value = image;
                    clearErrors();
                    SUBMIT_BTN.disabled = false;
                    MODAL.classList.add('show');
                }

                function closeModal() {
                    MODAL.classList.remove('show');
                    FORM.reset();
                    clearErrors();
                }

                // CLOSE MODAL ON ESC KEY
                document.addEventListener('keydown', (e) => {
                    if (e.key === 'Escape') closeModal();
                });

                // CLOSE MODAL ON OUTSIDE CLICK
                MODAL.addEventListener('click', (e) => {
                    if (e.target === MODAL) closeModal();
                });

                // ==================== FORM HANDLING ====================

                function handleFormSubmit(e) {
                    e.preventDefault();
                    submitForm();
                }

                function submitForm() {
                    if (!validateForm()) return;

                    const formData = new FormData();
                    formData.append('action', document.getElementById('formAction').value);
                    formData.append('id', document.getElementById('categoryId').value);
                    formData.append('name', document.getElementById('categoryName').value.trim());
                    formData.append('imageUrl', document.getElementById('categoryImage').value.trim());

                    setSubmitButtonLoading(true);

                    fetch(CONTEXT_PATH + '/admin/categories', {
                        method: 'POST',
                        body: formData
                    })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            showAlert('success', data.message);
                            closeModal();
                            setTimeout(() => location.reload(), 1500);
                        } else {
                            showAlert('error', data.message);
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        showAlert('error', 'Lỗi kết nối: ' + error.message);
                    })
                    .finally(() => setSubmitButtonLoading(false));
                }

                // ==================== DELETE HANDLING ====================

                function deleteCategory(id, name, productCount) {
                    if (productCount > 0) {
                        showAlert('warning', `Không thể xóa danh mục "${name}" vì đang có ${productCount} sản phẩm`);
                        return;
                    }

                    if (!confirm(`Bạn có chắc muốn xóa danh mục "${name}"? Hành động này không thể hoàn tác.`)) {
                        return;
                    }

                    const formData = new FormData();
                    formData.append('action', 'delete');
                    formData.append('id', id);

                    fetch(CONTEXT_PATH + '/admin/categories', {
                        method: 'POST',
                        body: formData
                    })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            showAlert('success', data.message);
                            const card = document.querySelector(`[data-category-id="${id}"]`);
                            if (card) {
                                card.style.animation = 'slideUp 0.3s ease-out';
                                setTimeout(() => {
                                    card.remove();
                                    updateCategoryCount();
                                }, 300);
                            }
                        } else {
                            showAlert('error', data.message);
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        showAlert('error', 'Lỗi xóa danh mục: ' + error.message);
                    });
                }

                // ==================== VALIDATION ====================

                function validateForm() {
                    clearErrors();
                    let isValid = true;

                    const name = document.getElementById('categoryName').value.trim();
                    if (!name) {
                        showFieldError('categoryName', 'Tên danh mục không được để trống');
                        isValid = false;
                    } else if (name.length < 2) {
                        showFieldError('categoryName', 'Tên danh mục phải có ít nhất 2 ký tự');
                        isValid = false;
                    } else if (name.length > 100) {
                        showFieldError('categoryName', 'Tên danh mục không được vượt quá 100 ký tự');
                        isValid = false;
                    }

                    const image = document.getElementById('categoryImage').value.trim();
                    if (!image) {
                        showFieldError('categoryImage', 'URL hình ảnh không được để trống');
                        isValid = false;
                    }

                    return isValid;
                }

                function showFieldError(fieldId, message) {
                    const field = document.getElementById(fieldId);
                    const group = field.closest('.form-group');
                    const errorDiv = group.querySelector('.error-message');
                    group.classList.add('has-error');
                    errorDiv.textContent = message;
                }

                function clearErrors() {
                    document.querySelectorAll('.form-group').forEach(group => {
                        group.classList.remove('has-error');
                        group.querySelector('.error-message').textContent = '';
                    });
                }

                // ==================== ALERT MANAGEMENT ====================

                function showAlert(type, message) {
                    const container = document.getElementById('alertContainer');
                    const alert = document.createElement('div');
                    alert.className = `alert alert-${type} show`;
                    alert.innerHTML = `
                        ${message}
                        <span class="alert-close" onclick="this.parentElement.remove();">&times;</span>
                    `;
                    container.appendChild(alert);

                    setTimeout(() => alert.remove(), 5000);
                }

                // ==================== UI HELPERS ====================

                function setSubmitButtonLoading(isLoading) {
                    const btn = document.getElementById('submitBtn');
                    if (isLoading) {
                        btn.disabled = true;
                        btn.innerHTML = '<span class="spinner"></span> Đang lưu...';
                    } else {
                        btn.disabled = false;
                        btn.innerHTML = '<span id="submitBtnText">Lưu danh mục</span>';
                    }
                }

                function updateCategoryCount() {
                    const count = document.querySelectorAll('[data-category-id]').length;
                    document.getElementById('categoryCount').textContent = count;
                    if (count === 0) {
                        location.reload();
                    }
                }

                // ENABLE SUBMIT BUTTON ON INPUT CHANGE
                document.getElementById('categoryName').addEventListener('input', () => {
                    SUBMIT_BTN.disabled = !document.getElementById('categoryName').value.trim();
                });

                document.getElementById('categoryImage').addEventListener('input', () => {
                    SUBMIT_BTN.disabled = !document.getElementById('categoryImage').value.trim();
                });

        .btn-submit {
            background: #3b82f6;
            color: white;
        }

        .btn-submit:hover { background: #2563eb; }
        .btn-submit:disabled { background: #9ca3af; cursor: not-allowed; }

        .btn-cancel {
            background: #e5e7eb;
            color: #374151;
        }

        .btn-cancel:hover { background: #d1d5db; }

        /* ALERT STYLES */
        .alert {
            padding: 12px 16px;
            border-radius: 6px;
            margin-bottom: 16px;
            display: none;
            animation: slideDown 0.3s ease-out;
        }

        @keyframes slideDown {
            from { transform: translateY(-20px); opacity: 0; }
            to { transform: translateY(0); opacity: 1; }
        }

        .alert.show { display: block; }

        .alert-success {
            background: #d1fae5;
            color: #065f46;
            border-left: 4px solid #10b981;
        }

        .alert-error {
            background: #fee2e2;
            color: #991b1b;
            border-left: 4px solid #ef4444;
        }

        .alert-warning {
            background: #fef3c7;
            color: #92400e;
            border-left: 4px solid #f59e0b;
        }

        .alert-info {
            background: #dbeafe;
            color: #1e40af;
            border-left: 4px solid #3b82f6;
        }

        .alert-close {
            float: right;
            cursor: pointer;
            color: inherit;
            font-weight: bold;
        }

        /* CATEGORY GRID */
        .category-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
            gap: 20px;
            padding: 20px;
        }

        .category-card {
            border: 1px solid #e5e7eb;
            border-radius: 8px;
            overflow: hidden;
            transition: all 0.2s;
            background: white;
        }

        .category-card:hover {
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
            transform: translateY(-2px);
        }

        .category-image {
            width: 100%;
            height: 150px;
            object-fit: cover;
            background: #f3f4f6;
        }

        .category-info {
            padding: 16px;
        }

        .category-name {
            font-weight: 600;
            font-size: 16px;
            margin-bottom: 8px;
            color: #1f2937;
        }

        .category-meta {
            font-size: 13px;
            color: #6b7280;
            margin-bottom: 12px;
        }

        .action-buttons {
            display: flex;
            gap: 8px;
        }

        .btn-icon {
            flex: 1;
            padding: 8px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            transition: all 0.2s;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 14px;
        }

        .btn-edit {
            background: #dbeafe;
            color: #1e40af;
        }

        .btn-edit:hover { background: #bfdbfe; }

        .btn-delete {
            background: #fee2e2;
            color: #991b1b;
        }

        .btn-delete:hover { background: #fecaca; }

        /* CONTENT HEADER */
        .content-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .content-header h1 {
            margin: 0;
            font-size: 28px;
            color: #1f2937;
        }

        .btn-primary {
            background: #10b981;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-weight: 500;
            transition: all 0.2s;
            display: flex;
            gap: 8px;
            align-items: center;
        }

        .btn-primary:hover { background: #059669; }

        /* EMPTY STATE */
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #6b7280;
        }

        .empty-state i {
            font-size: 48px;
            margin-bottom: 16px;
            opacity: 0.5;
        }

        .empty-state p {
            margin: 0;
            font-size: 16px;
        }

        /* LOADING SPINNER */
        .spinner {
            display: inline-block;
            width: 20px;
            height: 20px;
            border: 3px solid rgba(59, 130, 246, 0.3);
            border-radius: 50%;
            border-top-color: #3b82f6;
            animation: spin 1s ease-in-out infinite;
        }

        @keyframes spin {
            to { transform: rotate(360deg); }
        }

        /* CARD STYLES */
        .card {
            background: white;
            border-radius: 8px;
            border: 1px solid #e5e7eb;
            overflow: hidden;
        }

        .card-header {
            padding: 20px;
            border-bottom: 1px solid #e5e7eb;
            background: #f9fafb;
        }

        .card-header h3 {
            margin: 0;
            color: #1f2937;
        }
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
