<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết đơn hàng - Admin</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendors/bootstrap-icons/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-pages.css">
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
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb" style="background: none; padding: 0;">
                            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/orders" style="color: #6c757d; text-decoration: none;">Quản lý đơn hàng</a></li>
                            <li class="breadcrumb-item active" aria-current="page" style="color: #212529;">Chi tiết đơn hàng #${order.id}</li>
                        </ol>
                    </nav>
                    <h1 class="h3 mb-0 text-gray-800">Chi tiết đơn hàng #${order.id}</h1>
                </div>

                <c:set var="adminFlashSuccess" value="${sessionScope.success}"/>
                <c:if test="${not empty adminFlashSuccess}">
                    <div class="alert alert-success"><c:out value="${adminFlashSuccess}"/></div>
                    <c:remove var="success" scope="session"/>
                </c:if>
                <c:set var="adminFlashError" value="${sessionScope.error}"/>
                <c:if test="${not empty adminFlashError}">
                    <div class="alert alert-danger"><c:out value="${adminFlashError}"/></div>
                    <c:remove var="error" scope="session"/>
                </c:if>

                <c:choose>
                    <c:when test="${empty order}">
                        <div class="alert alert-danger">
                            Không tìm thấy thông tin đơn hàng này trong hệ thống.
                        </div>
                        <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-secondary">
                            <i class="fas fa-arrow-left me-2"></i>Quay lại danh sách
                        </a>
                    </c:when>
                    <c:otherwise>
                        <div class="row">
                            <!-- Cột bên trái: Chi tiết sản phẩm & Cảnh báo chữ ký -->
                            <div class="col-lg-8 mb-4">
                                <!-- Báo cáo chữ ký / Cảnh báo bảo mật -->
                                <!-- Báo cáo chữ ký / Cảnh báo bảo mật -->
                                <c:choose>
                                    <c:when test="${order.signatureStatus == 'KEY_COMPROMISED_REVIEW'}">
                                        <div class="alert alert-warning mb-4" style="border-left: 5px solid #f97316; background-color: #fffbeb; border-radius: 8px; padding: 20px;">
                                            <h4 class="alert-heading text-warning" style="font-weight: 700; font-size: 1.15rem; display: flex; align-items: center; gap: 10px; color: #d97706 !important;">
                                                <i class="fas fa-exclamation-triangle" style="font-size: 1.3rem;"></i>
                                                CẢNH BÁO BẢO MẬT: NGHI VẤN LỘ KHÓA
                                            </h4>
                                            <p class="mb-0 mt-2" style="color: #7c2d12; font-size: 0.95rem; line-height: 1.6;">
                                                Đơn hàng này được ký bằng khóa bảo mật đã được báo cáo lộ/rò rỉ sau thời điểm ký, hoặc khóa đã bị vô hiệu hóa trước khi ký đơn hàng.
                                                <br/><strong>Khuyến nghị:</strong> Admin cần chủ động liên hệ với khách hàng <strong>${order.customerName}</strong> qua email <strong>${order.customerEmail}</strong> để xác thực giao dịch trước khi xử lý đơn hàng.
                                            </p>
                                        </div>
                                    </c:when>
                                    <c:when test="${order.signatureStatus == 'SIGNATURE_INVALID'}">
                                        <div class="alert alert-danger mb-4" style="border-left: 5px solid #dc2626; background-color: #fef2f2; border-radius: 8px; padding: 20px;">
                                            <h4 class="alert-heading text-danger" style="font-weight: 700; font-size: 1.15rem; display: flex; align-items: center; gap: 10px; color: #dc2626 !important;">
                                                <i class="fas fa-times-circle" style="font-size: 1.3rem;"></i>
                                                CẢNH BÁO NGUY HIỂM: CHỮ KÝ LỖI (INVALID)
                                            </h4>
                                            <p class="mb-0 mt-2" style="color: #7f1d1d; font-size: 0.95rem; line-height: 1.6;">
                                                Chữ ký số của đơn hàng không trùng khớp hoặc khóa công khai của người dùng đã bị thay đổi trái phép.
                                                <br/><strong>Hệ thống bảo mật:</strong> Đã vô hiệu hóa chức năng cập nhật trạng thái đơn hàng để đảm bảo an toàn tài chính.
                                            </p>
                                        </div>
                                    </c:when>
                                    <c:when test="${order.signatureStatus == 'DATA_TAMPERED'}">
                                        <div class="alert alert-danger mb-4" style="border-left: 5px solid #ef4444; background-color: #fef2f2; border-radius: 8px; padding: 20px;">
                                            <h4 class="alert-heading text-danger" style="font-weight: 700; font-size: 1.15rem; display: flex; align-items: center; gap: 10px; color: #ef4444 !important;">
                                                <i class="fas fa-radiation" style="font-size: 1.3rem;"></i>
                                                CẢNH BÁO KHẨN CẤP: DỮ LIỆU ĐÃ KÝ BỊ GIẢ MẠO / SAI LỆCH TRONG DATABASE
                                            </h4>
                                            <p class="mb-0 mt-2" style="color: #991b1b; font-size: 0.95rem; line-height: 1.6;">
                                                Giá trị tổng tiền hoặc các sản phẩm trong đơn hàng hiện tại không trùng khớp với bản ghi Snapshot đã ký số tại thời điểm checkout.
                                                <br/><strong>Hệ thống bảo mật:</strong> Đã chặn hoàn toàn khả năng cập nhật trạng thái đơn hàng này. Vui lòng kiểm tra bảng Nhật ký thay đổi bên dưới để xác định chính xác các trường dữ liệu bị sửa đổi.
                                            </p>
                                        </div>
                                    </c:when>
                                    <c:when test="${order.signatureStatus != 'SIGNED'}">
                                        <div class="alert alert-warning mb-4" style="border-left: 5px solid #f59e0b; background-color: #fffbeb; border-radius: 8px; padding: 20px;">
                                            <h4 class="alert-heading text-warning" style="font-weight: 700; font-size: 1.05rem; display: flex; align-items: center; gap: 10px; color: #b45309 !important;">
                                                <i class="fas fa-lock" style="font-size: 1.2rem;"></i>
                                                Đơn hàng chưa có chữ ký hợp lệ
                                            </h4>
                                            <p class="mb-0 mt-2" style="color: #78350f; font-size: 0.95rem; line-height: 1.6;">
                                                Đơn hàng chưa có chữ ký hợp lệ, không thể xử lý.
                                            </p>
                                        </div>
                                    </c:when>
                                </c:choose>

                                <!-- Danh sách sản phẩm trong đơn hàng -->
                                <div class="card shadow-sm border-0 mb-4" style="border-radius: 10px; overflow: hidden;">
                                    <div class="card-header bg-white py-3 border-bottom">
                                        <h5 class="mb-0" style="font-weight: 600; color: #333;"><i class="fas fa-box-open me-2 text-primary"></i>Danh sách sản phẩm</h5>
                                    </div>
                                    <div class="table-responsive">
                                        <table class="table align-middle mb-0" style="min-width: 600px;">
                                            <thead class="table-light text-secondary" style="font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">
                                                <tr>
                                                    <th style="padding-left: 20px;">Sản phẩm</th>
                                                    <th class="text-end">Đơn giá</th>
                                                    <th class="text-center">Số lượng</th>
                                                    <th class="text-end" style="padding-right: 20px;">Thành tiền</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="item" items="${orderItems}">
                                                    <tr>
                                                        <td style="padding-left: 20px; padding-top: 15px; padding-bottom: 15px;">
                                                            <div class="d-flex align-items-center">
                                                                <img src="${pageContext.request.contextPath}/${item.productImage}" alt="${item.productName}"
                                                                     class="rounded" style="width: 50px; height: 50px; object-fit: cover; border: 1px solid #eee; margin-right: 15px;">
                                                                <div>
                                                                    <h6 class="mb-0" style="font-weight: 600; color: #2d3748;">${item.productName}</h6>
                                                                    <small class="text-muted">Mã SP: #${item.productId}</small>
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td class="text-end font-monospace" style="font-weight: 500; color: #4a5568;">
                                                            <fmt:formatNumber value="${item.priceAtPurchase}" pattern="#,###"/>₫
                                                        </td>
                                                        <td class="text-center" style="font-weight: 600; color: #2d3748;">
                                                            ${item.quantity}
                                                        </td>
                                                        <td class="text-end font-monospace text-primary" style="font-weight: 600; padding-right: 20px;">
                                                            <fmt:formatNumber value="${item.subTotal}" pattern="#,###"/>₫
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                    <div class="card-footer bg-light py-3 border-top d-flex justify-content-between align-items-center" style="padding-left: 20px; padding-right: 20px;">
                                        <span style="font-size: 1rem; color: #4a5568; font-weight: 500;">Tổng cộng:</span>
                                        <span class="fs-4 text-danger font-monospace" style="font-weight: 700;">
                                            <fmt:formatNumber value="${order.totalAmount}" pattern="#,###"/>₫
                                        </span>
                                    </div>
                                </div>

                                <!-- Khối thông tin chữ ký số -->
                                <div class="card shadow-sm border-0 mb-4" style="border-radius: 10px; overflow: hidden;">
                                    <div class="card-header bg-white py-3 border-bottom">
                                        <h5 class="mb-0" style="font-weight: 600; color: #333;">
                                            <i class="fas fa-signature me-2 text-primary"></i>Thông tin chữ ký số
                                        </h5>
                                    </div>
                                    <div class="card-body p-4">
                                        <c:choose>
                                            <c:when test="${not empty signatureInfo}">
                                                <div class="row mb-3">
                                                    <div class="col-md-4">
                                                        <div class="text-muted small">Mã khóa đã dùng (Key ID)</div>
                                                        <div style="font-weight: 600; color: #2d3748;">#${signatureInfo.key_id}</div>
                                                    </div>
                                                    <div class="col-md-4">
                                                        <div class="text-muted small">Thời điểm ký</div>
                                                        <div style="font-weight: 600; color: #2d3748;">
                                                            <fmt:formatDate value="${signatureInfo.signed_at}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-4">
                                                        <div class="text-muted small">Trạng thái chữ ký trong DB</div>
                                                        <div style="font-weight: 600; color: #2d3748;">
                                                            <c:choose>
                                                                <c:when test="${signatureInfo.verify_status == 'VALID'}">
                                                                    <span class="text-success"><i class="fas fa-check-circle"></i> VALID</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="text-danger"><i class="fas fa-times-circle"></i> ${signatureInfo.verify_status}</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="form-group mb-0">
                                                    <label class="text-muted small mb-1">Giá trị chữ ký (Base64)</label>
                                                    <textarea class="form-control font-monospace" rows="3" readonly style="font-size: 0.8rem; background-color: #f8fafc; color: #475569; resize: none; word-break: break-all;">${signatureInfo.signature_value}</textarea>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="text-center py-3 text-muted">
                                                    <i class="fas fa-info-circle me-1"></i>Đơn hàng này chưa có thông tin chữ ký số được lưu trữ.
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>

                                <!-- Nhật ký thay đổi (Audit Logs) -->
                                <div class="card shadow-sm border-0 mb-4" style="border-radius: 10px; overflow: hidden;">
                                    <div class="card-header bg-white py-3 border-bottom">
                                        <h5 class="mb-0" style="font-weight: 600; color: #333;">
                                            <i class="fas fa-history me-2 text-primary"></i>Nhật ký thay đổi dữ liệu (Audit Logs)
                                        </h5>
                                    </div>
                                    <div class="table-responsive">
                                        <table class="table align-middle mb-0" style="min-width: 600px;">
                                            <thead class="table-light text-secondary" style="font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">
                                            <tr>
                                                <th style="padding-left: 20px;">Thời điểm</th>
                                                <th>Tác nhân</th>
                                                <th>Thao tác</th>
                                                <th>Trường thay đổi</th>
                                                <th>Giá trị cũ</th>
                                                <th>Giá trị mới</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:choose>
                                                <c:when test="${empty auditLogs}">
                                                    <tr>
                                                        <td colspan="6" class="text-center py-4 text-muted">
                                                            <i class="fas fa-info-circle me-1"></i>Chưa có lịch sử thay đổi dữ liệu cho đơn hàng này.
                                                        </td>
                                                    </tr>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:forEach var="log" items="${auditLogs}">
                                                        <tr class="${log.signedField ? 'table-danger' : ''}">
                                                            <td style="padding-left: 20px;">
                                                                    ${log.createdAt}
                                                            </td>
                                                            <td>
                                                                <div style="font-weight: 600; color: #2d3748;">
                                                                    <c:choose>
                                                                        <c:when test="${not empty log.actorId}">
                                                                            ID: #${log.actorId}
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            Hệ thống / Khách hàng
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </div>
                                                                <small class="text-muted">${log.actorRole}</small>
                                                            </td>
                                                            <td>
                                                                <span class="badge bg-light text-dark border">${log.actionType}</span>
                                                            </td>
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${log.signedField}">
                                                                            <span class="text-danger fw-bold" title="Trường dữ liệu đã ký bị thay đổi trái phép!">
                                                                                <i class="fas fa-exclamation-triangle"></i> ${log.fieldName} *
                                                                            </span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        ${log.fieldName}
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td class="font-monospace text-muted" style="font-size: 0.85rem; max-width: 150px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="${log.oldValue}">
                                                                    ${log.oldValue}
                                                            </td>
                                                            <td class="font-monospace text-dark" style="font-size: 0.85rem; max-width: 150px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="${log.newValue}">
                                                                    ${log.newValue}
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:otherwise>
                                            </c:choose>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>

                                
                                <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-secondary">
                                    <i class="fas fa-arrow-left me-2"></i>Quay lại danh sách
                                </a>
                            </div>

                            <!-- Cột bên phải: Thông tin đơn hàng & Trạng thái -->
                            <div class="col-lg-4">
                                <!-- Thông tin khách hàng & Giao hàng -->
                                <div class="card shadow-sm border-0 mb-4" style="border-radius: 10px; overflow: hidden;">
                                    <div class="card-header bg-white py-3 border-bottom">
                                        <h5 class="mb-0" style="font-weight: 600; color: #333;"><i class="fas fa-user-circle me-2 text-primary"></i>Thông tin đơn hàng</h5>
                                    </div>
                                    <div class="card-body p-4">
                                        <div class="mb-3 pb-3 border-bottom">
                                            <div class="text-muted small mb-1">Khách hàng</div>
                                            <div style="font-weight: 600; color: #2d3748;">${order.customerName}</div>
                                            <div class="text-muted small mt-1"><i class="far fa-envelope me-1"></i>${order.customerEmail}</div>
                                        </div>
                                        
                                        <div class="mb-3 pb-3 border-bottom">
                                            <div class="text-muted small mb-1">Địa chỉ giao hàng</div>
                                            <div style="font-weight: 500; color: #4a5568; font-size: 0.95rem; line-height: 1.5;">
                                                ${order.shippingAddress}
                                            </div>
                                        </div>

                                        <div class="mb-3 pb-3 border-bottom">
                                            <div class="text-muted small mb-1">Thời gian đặt hàng</div>
                                            <div style="font-weight: 500; color: #4a5568;">
                                                <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                            </div>
                                        </div>

                                        <div class="mb-3 pb-3 border-bottom">
                                            <div class="text-muted small mb-1">Phương thức thanh toán</div>
                                            <div>
                                                <span class="badge bg-light text-dark border px-2.5 py-1.5" style="font-weight: 500; color: #333;">
                                                    ${not empty order.paymentMethod ? order.paymentMethod : 'COD'}
                                                </span>
                                            </div>
                                        </div>

                                        <div>
                                            <div class="text-muted small mb-1">Thời gian ký số</div>
                                            <div style="font-weight: 500; color: #4a5568;">
                                                <c:choose>
                                                    <c:when test="${not empty order.signedAt}">
                                                        <fmt:formatDate value="${order.signedAt}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted font-italic">Chưa có chữ ký</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                        <div class="mt-3 pt-3 border-top">
                                            <div class="text-muted small mb-1">Verify status</div>
                                            <div style="font-weight: 500; color: #4a5568;">
                                                <c:out value="${empty order.verifyStatus ? 'Chưa có chữ ký để verify' : order.verifyStatus}"/>
                                            </div>
                                        </div>
                                        <div class="mt-3 pt-3 border-top">
                                            <div class="text-muted small mb-1">Đánh giá rủi ro khóa</div>
                                            <div style="font-weight: 500; color: #4a5568;">
                                                <c:choose>
                                                    <c:when test="${order.signatureStatus == 'KEY_COMPROMISED_REVIEW'}">Khóa cần xem xét</c:when>
                                                    <c:when test="${order.signatureStatus == 'SIGNED'}">Không phát hiện rủi ro</c:when>
                                                    <c:otherwise>Chưa đủ điều kiện đánh giá</c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Trạng thái chữ ký số & Cập nhật trạng thái đơn hàng -->
                                <div class="card shadow-sm border-0 mb-4" style="border-radius: 10px; overflow: hidden;">
                                    <div class="card-header bg-white py-3 border-bottom">
                                        <h5 class="mb-0" style="font-weight: 600; color: #333;"><i class="fas fa-shield-alt me-2 text-primary"></i>Xác thực & Trạng thái</h5>
                                    </div>
                                    <div class="card-body p-4">
                                        <!-- Trạng thái chữ ký -->
                                        <div class="mb-4">
                                            <label class="form-label text-muted small mb-2 d-block">Trạng thái chữ ký số</label>
                                            <c:choose>
                                                <c:when test="${order.signatureStatus == 'SIGNED'}">
                                                    <span class="badge bg-success text-white py-2 px-3 fs-6 d-inline-flex align-items-center gap-2" style="border-radius: 20px; font-weight: normal; font-size: 0.85rem !important;">
                                                        <i class="fas fa-check-circle"></i> Đã ký số hợp lệ
                                                    </span>
                                                </c:when>
                                                <c:when test="${order.signatureStatus == 'KEY_COMPROMISED_REVIEW'}">
                                                    <span class="badge py-2 px-3 fs-6 d-inline-flex align-items-center gap-2" style="border-radius: 20px; font-weight: normal; font-size: 0.85rem !important; background-color: #f97316; color: white;">
                                                        <i class="fas fa-exclamation-triangle"></i> Nghi vấn lộ khóa
                                                    </span>
                                                </c:when>
                                                <c:when test="${order.signatureStatus == 'SIGNATURE_INVALID'}">
                                                    <span class="badge bg-danger text-white py-2 px-3 fs-6 d-inline-flex align-items-center gap-2" style="border-radius: 20px; font-weight: normal; font-size: 0.85rem !important;">
                                                        <i class="fas fa-times-circle"></i> Chữ ký lỗi (Invalid)
                                                    </span>
                                                </c:when>
                                                <c:when test="${order.signatureStatus == 'DATA_TAMPERED'}">
                                                    <span class="badge bg-danger text-white py-2 px-3 fs-6 d-inline-flex align-items-center gap-2" style="border-radius: 20px; font-weight: normal; font-size: 0.85rem !important;">
                                                        <i class="fas fa-radiation"></i> Dữ liệu bị giả mạo
                                                    </span>
                                                </c:when>
                                                <c:when test="${order.signatureStatus == 'WAITING_SIGNATURE'}">
                                                    <span class="badge bg-warning text-dark py-2 px-3 fs-6 d-inline-flex align-items-center gap-2" style="border-radius: 20px; font-weight: normal; font-size: 0.85rem !important;">
                                                        <i class="fas fa-clock"></i> Chờ ký số
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary text-white py-2 px-3 fs-6 d-inline-flex align-items-center gap-2" style="border-radius: 20px; font-weight: normal; font-size: 0.85rem !important;">
                                                        <i class="fas fa-question-circle"></i> <c:out value="${empty order.signatureStatus ? 'Chưa ký số' : order.signatureStatus}"/>
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                            <div class="text-muted mt-2" style="font-size: 0.82rem;">
                                                Verify: <c:out value="${empty order.verifyStatus ? 'N/A' : order.verifyStatus}"/>
                                            </div>
                                        </div>

                                        <!-- Trạng thái đơn hàng hiện tại -->
                                        <div class="mb-4">
                                            <label class="form-label text-muted small mb-2 d-block">Trạng thái đơn hàng hiện tại</label>
                                            <c:choose>
                                                <c:when test="${order.status == 'Chờ ký số' || order.status == 'Chờ ký xác nhận' || order.status == 'Chờ xử lý'}">
                                                    <span class="badge badge-warning py-1.5 px-3" style="font-size: 0.9rem;">${order.status}</span>
                                                </c:when>
                                                <c:when test="${order.status == 'Đã xác nhận' || order.status == 'Đang xử lý'}">
                                                    <span class="badge badge-info py-1.5 px-3" style="font-size: 0.9rem;">${order.status}</span>
                                                </c:when>
                                                <c:when test="${order.status == 'Đang giao hàng'}">
                                                    <span class="badge badge-info py-1.5 px-3" style="font-size: 0.9rem;">${order.status}</span>
                                                </c:when>
                                                <c:when test="${order.status == 'Đã giao hàng' || order.status == 'Hoàn thành'}">
                                                    <span class="badge badge-success py-1.5 px-3" style="font-size: 0.9rem;">${order.status}</span>
                                                </c:when>
                                                <c:when test="${order.status == 'Đã hủy'}">
                                                    <span class="badge badge-danger py-1.5 px-3" style="font-size: 0.9rem;">${order.status}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-secondary py-1.5 px-3" style="font-size: 0.9rem;">${order.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <div class="d-grid gap-2">
                                            <c:if test="${canUpdateOrder and canAcceptOrder}">
                                                <form method="post" action="${pageContext.request.contextPath}/admin/orders">
                                                    <input type="hidden" name="action" value="accept">
                                                    <input type="hidden" name="orderId" value="${order.id}">
                                                    <input type="hidden" name="returnTo" value="detail">
                                                    <button type="submit" class="btn btn-success w-100 py-2.5">
                                                        <i class="fas fa-check me-2"></i>Chấp nhận đơn
                                                    </button>
                                                </form>
                                            </c:if>
                                            <c:if test="${canUpdateOrder and canCancelOrder}">
                                                <button type="button" class="btn btn-danger w-100 py-2.5"
                                                        onclick="showCancelOrderModal(${order.id})">
                                                    <i class="fas fa-times me-2"></i>Hủy đơn
                                                </button>
                                            </c:if>
                                            <c:if test="${canManageOrderStatuses and order.signatureStatus == 'SIGNED'}">
                                                <button type="button" class="btn btn-primary w-100 py-2.5"
                                                        onclick="showUpdateStatusModal(${order.id}, '${order.status}')">
                                                    <i class="fas fa-edit me-2"></i>Cập nhật trạng thái
                                                </button>
                                            </c:if>
                                            <c:if test="${!canCancelOrder and !canAcceptOrder and !canManageOrderStatuses}">
                                                <button class="btn btn-secondary w-100 py-2.5" disabled>
                                                    <i class="fas fa-lock me-2"></i>Không có thao tác phù hợp
                                                </button>
                                            </c:if>
                                            <c:if test="${canAcceptOrder == false and order.signatureStatus != 'SIGNED' and order.status != 'Đã hủy'}">
                                                <small class="text-danger d-block mt-2 text-center">
                                                    Đơn hàng chưa có chữ ký hợp lệ, không thể chấp nhận xử lý.
                                                </small>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>

        <jsp:include page="common/admin-footer.jsp"/>
    </div>
</div>

<!-- Modal cập nhật trạng thái -->
<div id="updateStatusModal" class="modal orders-modal">
    <div class="modal-content orders-modal-content">
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
                    <p id="currentStatus" class="orders-current-status"></p>
                </div>
                <div class="form-group">
                    <label>Chọn trạng thái mới</label>
                    <select name="status" class="form-control" required>
                        <option value="">-- Chọn trạng thái --</option>
                        <option value="Chờ ký xác nhận">Chờ ký xác nhận</option>
                        <option value="Chờ xử lý">Chờ xử lý</option>
                        <option value="Đã xác nhận">Đã xác nhận</option>
                        <option value="Đang xử lý">Đang xử lý</option>
                        <option value="Đang giao hàng">Đang giao hàng</option>
                        <option value="Đã giao hàng">Đã giao hàng</option>
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

<div id="cancelOrderModal" class="modal orders-modal">
    <div class="modal-content orders-modal-content">
        <div class="modal-header">
            <h3>Hủy đơn hàng</h3>
            <button type="button" class="close-btn" onclick="closeCancelOrderModal()">&times;</button>
        </div>
        <form id="cancelOrderForm" method="post" action="${pageContext.request.contextPath}/admin/orders">
            <input type="hidden" name="action" value="cancel">
            <input type="hidden" name="orderId" id="cancelOrderId">
            <input type="hidden" name="returnTo" value="detail">
            <div class="modal-body">
                <div class="form-group">
                    <label for="cancelReason">Lý do hủy</label>
                    <textarea id="cancelReason" name="reason" class="form-control" maxlength="500" required></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="closeCancelOrderModal()">Đóng</button>
                <button type="submit" class="btn btn-danger">Xác nhận hủy</button>
            </div>
        </form>
    </div>
</div>

<script>
    function filterByStatus(status) {
        window.location.href = '${pageContext.request.contextPath}/admin/orders?status=' + status;
    }

    function showUpdateStatusModal(orderId, currentStatus) {
        document.getElementById('updateOrderId').value = orderId;
        document.getElementById('currentStatus').textContent = currentStatus;
        document.getElementById('updateStatusModal').classList.add('show');
    }

    function closeUpdateStatusModal() {
        document.getElementById('updateStatusModal').classList.remove('show');
        document.getElementById('updateStatusForm').reset();
    }

    function showCancelOrderModal(orderId) {
        document.getElementById('cancelOrderId').value = orderId;
        document.getElementById('cancelOrderModal').classList.add('show');
    }

    function closeCancelOrderModal() {
        document.getElementById('cancelOrderModal').classList.remove('show');
        document.getElementById('cancelOrderForm').reset();
    }

    // Close modal when clicking outside
    window.onclick = function(event) {
        const modal = document.getElementById('updateStatusModal');
        if (event.target === modal) {
            closeUpdateStatusModal();
        }
        const cancelModal = document.getElementById('cancelOrderModal');
        if (event.target === cancelModal) {
            closeCancelOrderModal();
        }
    }
</script>

<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/admin-main.js"></script>
</body>
</html>
