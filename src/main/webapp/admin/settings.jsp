<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Cài đặt - Admin</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-dashboard.css">
    <style>
        .settings-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 24px;
            margin-top: 24px;
        }

        @media (max-width: 968px) {
            .settings-grid {
                grid-template-columns: 1fr;
            }
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
            color: #374151;
        }

        .form-control {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #d1d5db;
            border-radius: 6px;
            font-size: 14px;
            transition: border-color 0.2s;
        }

        .form-control:focus {
            outline: none;
            border-color: #3b82f6;
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
        }

        .btn-save {
            background: #3b82f6;
            color: white;
            padding: 10px 24px;
            border: none;
            border-radius: 6px;
            font-weight: 500;
            cursor: pointer;
            transition: background 0.2s;
        }

        .btn-save:hover {
            background: #2563eb;
        }

        .alert {
            padding: 12px 16px;
            border-radius: 6px;
            margin-bottom: 20px;
        }

        .alert-success {
            background: #d1fae5;
            color: #065f46;
            border: 1px solid #6ee7b7;
        }

        .alert-error {
            background: #fee2e2;
            color: #991b1b;
            border: 1px solid #fca5a5;
        }

        .profile-avatar {
            width: 100px;
            height: 100px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 40px;
            font-weight: 600;
            margin-bottom: 16px;
        }

        .settings-section {
            margin-bottom: 32px;
        }

        .settings-section h3 {
            font-size: 16px;
            font-weight: 600;
            color: #111827;
            margin-bottom: 16px;
            padding-bottom: 8px;
            border-bottom: 2px solid #e5e7eb;
        }
    </style>
</head>
<body>
<c:set var="currentAdminUser" value="${not empty sessionScope.user ? sessionScope.user : sessionScope.userInSession}" />
<div class="admin-container">
    <jsp:include page="common/admin-sidebar.jsp"/>

    <div class="admin-main">
        <jsp:include page="common/admin-topbar.jsp"/>

        <div class="admin-content">
            <div class="content-header">
                <h1>Cài đặt</h1>
            </div>

            <c:if test="${not empty message}">
                <div class="alert alert-${messageType}">
                    <i class="fas fa-check-circle"></i> ${message}
                </div>
            </c:if>

            <div class="settings-grid">
                <!-- Profile Settings -->
                <div class="card">
                    <div class="card-header">
                        <h3>Thông tin cá nhân</h3>
                    </div>
                    <div style="padding: 24px;">
                        <div style="text-align: center;">
                            <div class="profile-avatar">
                                <c:choose>
                                    <c:when test="${not empty currentAdminUser.fullName}">
                                        ${currentAdminUser.fullName.substring(0,1).toUpperCase()}
                                    </c:when>
                                    <c:otherwise>A</c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <form action="${pageContext.request.contextPath}/admin/settings" method="post">
                            <input type="hidden" name="action" value="updateProfile">

                            <div class="form-group">
                                <label>Họ và tên</label>
                                <input type="text" name="fullName" class="form-control"
                                       value="${currentAdminUser.fullName}" placeholder="Nhập họ tên" required>
                            </div>

                            <div class="form-group">
                                <label>Email</label>
                                <input type="email" name="email" class="form-control"
                                       value="${currentAdminUser.email}" placeholder="Nhập email" required>
                            </div>

                            <div class="form-group">
                                <label>Số điện thoại</label>
                                <input type="tel" name="phone" class="form-control"
                                       value="${currentAdminUser.phone}" placeholder="Nhập số điện thoại">
                            </div>

                            <c:if test="${canUpdateSettings}">
                                <button type="submit" class="btn-save">
                                    <i class="fas fa-save"></i> Lưu thay đổi
                                </button>
                            </c:if>
                        </form>
                    </div>
                </div>

                <!-- Password Settings -->
                <div class="card">
                    <div class="card-header">
                        <h3>Đổi mật khẩu</h3>
                    </div>
                    <div style="padding: 24px;">
                        <form action="${pageContext.request.contextPath}/admin/settings" method="post">
                            <input type="hidden" name="action" value="changePassword">

                            <div class="form-group">
                                <label>Mật khẩu hiện tại</label>
                                <input type="password" name="currentPassword" class="form-control"
                                       placeholder="Nhập mật khẩu hiện tại" required>
                            </div>

                            <div class="form-group">
                                <label>Mật khẩu mới</label>
                                <input type="password" name="newPassword" class="form-control"
                                       placeholder="Nhập mật khẩu mới" required>
                            </div>

                            <div class="form-group">
                                <label>Xác nhận mật khẩu mới</label>
                                <input type="password" name="confirmPassword" class="form-control"
                                       placeholder="Nhập lại mật khẩu mới" required>
                            </div>

                            <c:if test="${canUpdateSettings}">
                                <button type="submit" class="btn-save">
                                    <i class="fas fa-key"></i> Đổi mật khẩu
                                </button>
                            </c:if>
                        </form>
                    </div>
                </div>
            </div>

            <!-- System Settings -->
            <div class="card" style="margin-top: 24px;">
                <div class="card-header">
                    <h3>Cài đặt hệ thống</h3>
                </div>
                <div style="padding: 24px;">
                    <form action="${pageContext.request.contextPath}/admin/settings" method="post">
                        <input type="hidden" name="action" value="updateSystem">

                        <div class="settings-section">
                            <h3>Thông tin website</h3>
                            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
                                <div class="form-group">
                                    <label>Tên website</label>
                                    <input type="text" name="siteName" class="form-control" value="${settings.site_name}">
                                </div>
                                <div class="form-group">
                                    <label>Email liên hệ</label>
                                    <input type="email" name="siteEmail" class="form-control" value="${settings.site_email}">
                                </div>
                                <div class="form-group">
                                    <label>Số điện thoại</label>
                                    <input type="tel" name="sitePhone" class="form-control" value="${settings.site_phone}">
                                </div>
                                <div class="form-group">
                                    <label>Địa chỉ</label>
                                    <input type="text" name="siteAddress" class="form-control" value="${settings.site_address}">
                                </div>
                            </div>
                        </div>

                        <div class="settings-section">
                            <h3>Cài đặt thanh toán</h3>
                            <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px;">
                                <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
                                    <input type="checkbox" name="paymentCod" ${settings.payment_cod == 'true' ? 'checked' : ''}> COD
                                </label>
                                <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
                                    <input type="checkbox" name="paymentVnpay" ${settings.payment_vnpay == 'true' ? 'checked' : ''}> VNPay
                                </label>
                                <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
                                    <input type="checkbox" name="paymentMomo" ${settings.payment_momo == 'true' ? 'checked' : ''}> MoMo
                                </label>
                                <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
                                    <input type="checkbox" name="paymentCard" ${settings.payment_card == 'true' ? 'checked' : ''}> Visa/Mastercard
                                </label>
                            </div>
                        </div>

                        <div class="settings-section">
                            <h3>Cài đặt vận chuyển</h3>
                            <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px;">
                                <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
                                    <input type="checkbox" name="shippingGhn" ${settings.shipping_ghn == 'true' ? 'checked' : ''}> Giao Hàng Nhanh
                                </label>
                                <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
                                    <input type="checkbox" name="shippingGhtk" ${settings.shipping_ghtk == 'true' ? 'checked' : ''}> Giao Hàng Tiết Kiệm
                                </label>
                                <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
                                    <input type="checkbox" name="shippingJnt" ${settings.shipping_jnt == 'true' ? 'checked' : ''}> J&T Express
                                </label>
                            </div>
                        </div>

                        <c:if test="${canUpdateSettings}">
                            <button type="submit" class="btn-save">
                                <i class="fas fa-save"></i> Lưu cài đặt hệ thống
                            </button>
                        </c:if>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
