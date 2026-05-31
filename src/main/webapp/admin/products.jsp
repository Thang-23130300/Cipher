<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý sản phẩm - Admin</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-dashboard.css">
    <style>
        .modal { display: none; position: fixed; z-index: 1000; left: 0; top: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); }
        .modal-content { background: white; margin: 50px auto; padding: 0; width: 600px; border-radius: 8px; max-height: 90vh; overflow-y: auto; }
        .modal-header { padding: 20px; border-bottom: 1px solid #e5e7eb; display: flex; justify-content: space-between; align-items: center; }
        .modal-body { padding: 20px; }
        .close-btn { background: none; border: none; font-size: 24px; cursor: pointer; color: #6b7280; }
        .form-group { margin-bottom: 16px; }
        .form-group label { display: block; margin-bottom: 6px; font-weight: 500; }
        .form-control { width: 100%; padding: 8px 12px; border: 1px solid #d1d5db; border-radius: 6px; }
        .form-control:focus { outline: none; border-color: #3b82f6; }
        .btn-submit { background: #3b82f6; color: white; padding: 10px 20px; border: none; border-radius: 6px; cursor: pointer; width: 100%; }
        .btn-submit:hover { background: #2563eb; }
        .alert { padding: 12px; border-radius: 6px; margin-bottom: 16px; }
        .alert-success { background: #d1fae5; color: #065f46; }
        .alert-error { background: #fee2e2; color: #991b1b; }
    </style>
</head>
<body>
<div class="admin-container">
    <jsp:include page="common/admin-sidebar.jsp"/>

    <div class="admin-main">
        <jsp:include page="common/admin-topbar.jsp"/>

        <div class="admin-content">
            <div class="content-header">
                <h1>Quản lý sản phẩm</h1>
                <c:if test="${canCreateProduct}">
                    <button class="btn-primary" onclick="openAddModal()">
                        <i class="fas fa-plus"></i> Thêm sản phẩm mới
                    </button>
                </c:if>
            </div>

            <c:if test="${not empty searchQuery}">
                <div style="padding: 12px; background: #f0f9ff; border: 1px solid #bae6fd; border-radius: 6px; margin-bottom: 16px;">
                    <span>Kết quả tìm kiếm cho: <strong>"${searchQuery}"</strong></span>
                    <a href="${pageContext.request.contextPath}/admin/products" style="margin-left: 12px; color: #3b82f6; text-decoration: none;">
                        <i class="fas fa-times"></i> Xóa bộ lọc
                    </a>
                </div>
            </c:if>

            <c:if test="${not empty message}">
                <div class="alert alert-${messageType}">
                        ${message}
                </div>
            </c:if>

            <div class="card">
                <div class="card-header">
                    <h3>Danh sách sản phẩm (${totalProducts} sản phẩm)</h3>
                </div>

                <div class="table-container">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th style="width: 60px;">ID</th>
                            <th style="width: 80px;">Hình ảnh</th>
                            <th>Tên sản phẩm</th>
                            <th style="width: 120px;">Giá</th>
                            <th style="width: 100px;">Tồn kho</th>
                            <th style="width: 100px;">Đã bán</th>
                            <th style="width: 100px;">Đánh giá</th>
                            <th style="width: 150px;">Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${products}" var="p">
                            <tr>
                                <td>${p.id}</td>
                                <td>
                                    <img src="${p.imageUrl}"
                                         alt="${p.name}"
                                         style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;"
                                         onerror="this.src='https://placehold.co/50x50?text=No+Image'">
                                </td>
                                <td style="font-weight: 500;">${p.name}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${p.discountPercent > 0 and p.salePrice != null}">
                                            <div style="display: flex; flex-direction: column; gap: 2px;">
                                                <span style="color: #EE4D2D; font-weight: 600;"><fmt:formatNumber value="${p.salePrice}" pattern="#,###"/>đ</span>
                                                <span style="text-decoration: line-through; color: #9ca3af; font-size: 12px;"><fmt:formatNumber value="${p.originalPrice}" pattern="#,###"/>đ</span>
                                                <span style="background: #FEF6F5; color: #EE4D2D; padding: 2px 6px; border-radius: 4px; font-size: 11px; width: fit-content;">-${p.discountPercent}%</span>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <fmt:formatNumber value="${p.originalPrice}" pattern="#,###"/>đ
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="text-align: center;">${p.stockQuantity}</td>
                                <td style="text-align: center;">${p.totalSold}</td>
                                <td style="text-align: center;">
                                    <i class="fas fa-star" style="color: #fbbf24;"></i> ${p.avgRating}
                                </td>
                                <td>
                                    <div class="action-buttons">
                                        <c:if test="${canUpdateProduct}">
                                            <button class="btn-icon btn-edit" onclick="openEditModal(${p.id}, '${p.name}', '${p.description}', ${p.categoryId}, ${p.originalPrice}, '${p.imageUrl}', ${p.stockQuantity}, ${p.discountPercent}, ${p.salePrice != null ? p.salePrice : 0})" title="Sửa">
                                                <i class="fas fa-edit"></i>
                                            </button>
                                        </c:if>
                                        <c:if test="${canDeleteProduct}">
                                            <form action="${pageContext.request.contextPath}/admin/products" method="post" style="display: inline;" onsubmit="return confirm('Bạn có chắc muốn xóa sản phẩm này?');">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="id" value="${p.id}">
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
<div id="productModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 id="modalTitle">Thêm sản phẩm mới</h3>
            <button class="close-btn" onclick="closeModal()">&times;</button>
        </div>
        <div class="modal-body">
            <form action="${pageContext.request.contextPath}/admin/products" method="post">
                <input type="hidden" name="action" id="formAction" value="add">
                <input type="hidden" name="id" id="productId">

                <div class="form-group">
                    <label>Tên sản phẩm *</label>
                    <input type="text" name="name" id="productName" class="form-control" required>
                </div>

                <div class="form-group">
                    <label>Danh mục *</label>
                    <select name="categoryId" id="categoryId" class="form-control" required>
                        <option value="">-- Chọn danh mục --</option>
                        <c:forEach items="${categories}" var="cat">
                            <option value="${cat.id}">${cat.name}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label>Mô tả</label>
                    <textarea name="description" id="productDesc" class="form-control" rows="3"></textarea>
                </div>

                <div class="form-group">
                    <label>Giá gốc (VNĐ) *</label>
                    <input type="number" name="price" id="productPrice" class="form-control" required min="0">
                </div>

                <div class="form-group">
                    <label>Giảm giá (%)</label>
                    <input type="number" name="discountPercent" id="discountPercent" class="form-control" min="0" max="100" value="0" onchange="calculateSalePrice()">
                    <small style="color: #6b7280;">Nhập 0 nếu không có giảm giá</small>
                </div>

                <div class="form-group">
                    <label>Giá sau giảm (VNĐ)</label>
                    <input type="number" name="salePrice" id="salePrice" class="form-control" min="0" readonly style="background: #f3f4f6;">
                    <small style="color: #6b7280;">Tự động tính khi nhập % giảm giá</small>
                </div>

                <div class="form-group">
                    <label>Số lượng tồn kho *</label>
                    <input type="number" name="stock" id="productStock" class="form-control" required min="0">
                </div>

                <div class="form-group">
                    <label>URL hình ảnh *</label>
                    <input type="text" name="imageUrl" id="productImage" class="form-control" required placeholder="assets/images/products/...">
                </div>

                <button type="submit" class="btn-submit">Lưu sản phẩm</button>
            </form>
        </div>
    </div>
</div>

<script>
    const modal = document.getElementById('productModal');

    function calculateSalePrice() {
        const price = parseFloat(document.getElementById('productPrice').value) || 0;
        const discount = parseFloat(document.getElementById('discountPercent').value) || 0;

        if (discount > 0 && price > 0) {
            const salePrice = price * (1 - discount / 100);
            document.getElementById('salePrice').value = Math.round(salePrice);
        } else {
            document.getElementById('salePrice').value = '';
        }
    }

    function openAddModal() {
        document.getElementById('modalTitle').innerText = 'Thêm sản phẩm mới';
        document.getElementById('formAction').value = 'add';
        document.getElementById('productId').value = '';
        document.getElementById('productName').value = '';
        document.getElementById('categoryId').value = '';
        document.getElementById('productDesc').value = '';
        document.getElementById('productPrice').value = '';
        document.getElementById('discountPercent').value = '0';
        document.getElementById('salePrice').value = '';
        document.getElementById('productStock').value = '';
        document.getElementById('productImage').value = '';
        modal.style.display = 'block';
    }

    function openEditModal(id, name, desc, catId, price, image, stock, discount, salePrice) {
        document.getElementById('modalTitle').innerText = 'Cập nhật sản phẩm';
        document.getElementById('formAction').value = 'edit';
        document.getElementById('productId').value = id;
        document.getElementById('productName').value = name;
        document.getElementById('categoryId').value = catId;
        document.getElementById('productDesc').value = desc || '';
        document.getElementById('productPrice').value = price;
        document.getElementById('discountPercent').value = discount || 0;
        document.getElementById('salePrice').value = salePrice || '';
        document.getElementById('productStock').value = stock;
        document.getElementById('productImage').value = image;
        modal.style.display = 'block';
    }

    function closeModal() {
        modal.style.display = 'none';
    }

    window.onclick = function(event) {
        if (event.target == modal) closeModal();
    }

    // Auto calculate when price changes
    document.getElementById('productPrice').addEventListener('input', calculateSalePrice);

    // Check if page loaded with action=add parameter
    window.addEventListener('DOMContentLoaded', function() {
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get('action') === 'add') {
            openAddModal();
            // Clean URL without reloading
            window.history.replaceState({}, document.title, window.location.pathname);
        }
    });
</script>
</body>
</html>