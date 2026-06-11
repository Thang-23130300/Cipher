<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán - INOLA</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/HomePage.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/HomePageFooter.css">
    <style>
        .checkout-container { max-width: 1200px; margin: 40px auto; padding: 0 20px; }
        .checkout-grid { display: grid; grid-template-columns: 2fr 1fr; gap: 30px; }
        .checkout-section { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
        .section-title { font-size: 24px; margin-bottom: 20px; color: #333; }
        .form-group { margin-bottom: 20px; }
        .form-group label { display: block; margin-bottom: 8px; font-weight: 500; color: #555; }
        .form-group input, .form-group select, .form-group textarea { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; box-sizing: border-box; }
        .form-group textarea { resize: vertical; min-height: 80px; }
        .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
        .alert-error { padding: 12px 14px; border-radius: 6px; background: #fee2e2; color: #991b1b; margin-bottom: 20px; }
        .address-option { display: block; border: 1px solid #e5e7eb; border-radius: 8px; padding: 14px; margin-bottom: 12px; cursor: pointer; }
        .address-option input { width: auto; margin-right: 8px; }
        .address-option strong { color: #111827; }
        .muted { color: #6b7280; font-size: 14px; }
        .order-summary { background: #f9f9f9; padding: 20px; border-radius: 8px; }
        .summary-item { display: flex; justify-content: space-between; gap: 16px; margin-bottom: 15px; padding-bottom: 15px; border-bottom: 1px solid #ddd; }
        .item-info { flex: 1; }
        .item-name { font-weight: 500; margin-bottom: 5px; }
        .item-quantity { color: #666; font-size: 14px; }
        .item-price { font-weight: 600; color: #e74c3c; white-space: nowrap; }
        .summary-total { display: flex; justify-content: space-between; padding-top: 20px; margin-top: 20px; border-top: 2px solid #333; font-size: 20px; font-weight: bold; }
        .payment-methods { display: grid; grid-template-columns: repeat(2, 1fr); gap: 15px; margin-top: 15px; }
        .payment-option { border: 2px solid #ddd; padding: 15px; border-radius: 8px; cursor: pointer; transition: all 0.3s; text-align: center; }
        .payment-option:hover { border-color: #e74c3c; }
        .payment-option input[type="radio"] { width: auto; margin-right: 8px; }
        .payment-option.disabled { opacity: .55; cursor: not-allowed; background: #f9fafb; }
        .payment-option.disabled:hover { border-color: #ddd; }
        .submit-btn { width: 100%; padding: 15px; background: #e74c3c; color: white; border: none; border-radius: 4px; font-size: 16px; font-weight: bold; cursor: pointer; margin-top: 20px; }
        .submit-btn:hover { background: #c0392b; }
        @media (max-width: 768px) { .checkout-grid, .form-row, .payment-methods { grid-template-columns: 1fr; } }
    </style>
</head>
<body>
<div class="page-container">
    <jsp:include page="/views/common/header-renew.jsp"/>

    <main class="checkout-container">
        <h1 style="margin-bottom: 30px;">Thanh toán đơn hàng</h1>

        <c:if test="${not empty error}">
            <div class="alert-error">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/checkout" method="post" id="checkoutForm">
            <div class="checkout-grid">
                <div>
                    <div class="checkout-section">
                        <h2 class="section-title">Thông tin giao hàng</h2>

                        <c:if test="${not empty savedAddresses}">
                            <div class="form-group">
                                <label>Địa chỉ đã lưu</label>
                                <label class="address-option">
                                    <input type="radio" name="savedAddressId" value="" checked>
                                    Nhập địa chỉ giao hàng mới
                                </label>
                                <c:forEach items="${savedAddresses}" var="addr">
                                    <label class="address-option">
                                        <input type="radio" name="savedAddressId" value="${addr.id}">
                                        <strong>${addr.receiverName}</strong> - ${addr.receiverPhone}
                                        <div class="muted">${addr.addressDetail}, ${addr.ward}, ${addr.province}</div>
                                        <c:if test="${addr.isDefault()}"><div class="muted">Địa chỉ mặc định</div></c:if>
                                    </label>
                                </c:forEach>
                            </div>
                        </c:if>

                        <div id="newAddressFields">
                            <div class="form-row">
                                <div class="form-group">
                                    <label>Họ và tên người nhận *</label>
                                    <input type="text" name="receiverName" value="${currentUser.fullName}" required>
                                </div>
                                <div class="form-group">
                                    <label>Số điện thoại *</label>
                                    <input type="tel" name="receiverPhone" value="${currentUser.phone}" required>
                                </div>
                            </div>

                            <div class="form-group">
                                <label>Địa chỉ chi tiết *</label>
                                <input type="text" name="addressDetail" placeholder="Số nhà, tên đường, khu vực" required>
                            </div>

                            <div class="form-row">
                                <div class="form-group">
                                    <label>Tỉnh/Thành phố *</label>
                                    <select name="provinceCode" id="provinceCode" required>
                                        <option value="">Chọn Tỉnh/Thành phố</option>
                                        <c:forEach items="${provinceOptions}" var="province">
                                            <option value="${province.code}">${empty province.fullName ? province.name : province.fullName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label>Phường/Xã *</label>
                                    <select name="wardCode" id="wardCode" required>
                                        <option value="">Chọn Phường/Xã</option>
                                    </select>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label>Ghi chú đơn hàng</label>
                            <textarea name="note" placeholder="Ví dụ: thời gian giao hàng hoặc chỉ dẫn địa điểm giao hàng."></textarea>
                        </div>
                    </div>

                    <div class="checkout-section" style="margin-top: 20px;">
                        <h2 class="section-title">Phương thức thanh toán</h2>
                        <div class="payment-methods">
                            <label class="payment-option">
                                <input type="radio" name="paymentMethod" value="COD" checked>
                                <div>
                                    <i class="fas fa-money-bill-wave" style="font-size: 24px; color: #27ae60;"></i>
                                    <div>Thanh toán khi nhận hàng (COD)</div>
                                </div>
                            </label>
                            <label class="payment-option disabled" title="Sẽ tích hợp VNPay sandbox ở bước tiếp theo">
                                <input type="radio" name="paymentMethod" value="VNPAY_QR" disabled>
                                <div>
                                    <i class="fas fa-qrcode" style="font-size: 24px; color: #2563eb;"></i>
                                    <div>VNPay QR</div>
                                    <div class="muted">Sẵn sàng tích hợp sandbox</div>
                                </div>
                            </label>
                        </div>
                    </div>
                </div>

                <div>
                    <div class="checkout-section">
                        <h2 class="section-title">Đơn hàng của bạn</h2>
                        <div class="order-summary">
                            <c:forEach items="${cart.items}" var="item">
                                <div class="summary-item">
                                    <div class="item-info">
                                        <div class="item-name">${item.product.name}</div>
                                        <div class="item-quantity">Số lượng: ${item.quantity}</div>
                                    </div>
                                    <div class="item-price"><fmt:formatNumber value="${item.subTotal}" groupingUsed="true"/>₫</div>
                                </div>
                            </c:forEach>

                            <div style="margin-top: 20px; padding-top: 20px; border-top: 1px solid #ddd;">
                                <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
                                    <span>Tạm tính:</span>
                                    <span><fmt:formatNumber value="${cart.total()}" groupingUsed="true"/>₫</span>
                                </div>
                                <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
                                    <span>Phí vận chuyển:</span>
                                    <span>Miễn phí</span>
                                </div>
                            </div>

                            <div class="summary-total">
                                <span>Tổng cộng:</span>
                                <span style="color: #e74c3c;"><fmt:formatNumber value="${cart.total()}" groupingUsed="true"/>₫</span>
                            </div>
                        </div>

                        <button type="submit" class="submit-btn">
                            <i class="fas fa-check-circle"></i> Đặt hàng
                        </button>

                        <a href="${pageContext.request.contextPath}/cart" style="display: block; text-align: center; margin-top: 15px; color: #666;">
                            <i class="fas fa-arrow-left"></i> Quay lại giỏ hàng
                        </a>
                    </div>
                </div>
            </div>
        </form>
    </main>

    <jsp:include page="/views/common/footer.jsp"/>
</div>

<script>
    const contextPath = '${pageContext.request.contextPath}';
    const newAddressFields = document.getElementById('newAddressFields');
    const addressInputs = newAddressFields.querySelectorAll('input, select');
    const savedAddressRadios = document.querySelectorAll('input[name="savedAddressId"]');

    function syncAddressMode() {
        const selected = document.querySelector('input[name="savedAddressId"]:checked');
        const usingSavedAddress = selected && selected.value !== '';
        addressInputs.forEach(input => {
            input.disabled = usingSavedAddress;
            input.required = !usingSavedAddress;
        });
        newAddressFields.style.opacity = usingSavedAddress ? '.55' : '1';
    }

    savedAddressRadios.forEach(radio => radio.addEventListener('change', syncAddressMode));
    syncAddressMode();

    document.getElementById('provinceCode').addEventListener('change', function () {
        const provinceCode = this.value;
        const wardSelect = document.getElementById('wardCode');
        wardSelect.innerHTML = '<option value="">Chọn Phường/Xã</option>';
        if (!provinceCode) return;

        fetch(contextPath + '/user/address/wards?provinceCode=' + encodeURIComponent(provinceCode))
            .then(response => response.json())
            .then(data => {
                (data.results || []).forEach(ward => {
                    const option = document.createElement('option');
                    option.value = ward.id;
                    option.textContent = ward.text;
                    wardSelect.appendChild(option);
                });
            });
    });
</script>
</body>
</html>
