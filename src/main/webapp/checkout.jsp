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
        .checkout-container {
            max-width: 1200px;
            margin: 40px auto;
            padding: 0 20px;
        }

        .checkout-grid {
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: 30px;
        }

        .checkout-section {
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .section-title {
            font-size: 24px;
            margin-bottom: 20px;
            color: #333;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
            color: #555;
        }

        .form-group input,
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }

        .form-group textarea {
            resize: vertical;
            min-height: 80px;
        }

        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
        }

        .order-summary {
            background: #f9f9f9;
            padding: 20px;
            border-radius: 8px;
        }

        .summary-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 15px;
            padding-bottom: 15px;
            border-bottom: 1px solid #ddd;
        }

        .summary-item:last-child {
            border-bottom: none;
        }

        .item-info {
            flex: 1;
        }

        .item-name {
            font-weight: 500;
            margin-bottom: 5px;
        }

        .item-quantity {
            color: #666;
            font-size: 14px;
        }

        .item-price {
            font-weight: 600;
            color: #e74c3c;
        }

        .summary-total {
            display: flex;
            justify-content: space-between;
            padding-top: 20px;
            margin-top: 20px;
            border-top: 2px solid #333;
            font-size: 20px;
            font-weight: bold;
        }

        .payment-methods {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 15px;
            margin-top: 15px;
        }

        .payment-option {
            border: 2px solid #ddd;
            padding: 15px;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s;
            text-align: center;
        }

        .payment-option:hover {
            border-color: #e74c3c;
        }

        .payment-option input[type="radio"] {
            margin-right: 8px;
        }

        .submit-btn {
            width: 100%;
            padding: 15px;
            background: #e74c3c;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            margin-top: 20px;
        }

        .submit-btn:hover {
            background: #c0392b;
        }

        @media (max-width: 768px) {
            .checkout-grid {
                grid-template-columns: 1fr;
            }

            .form-row {
                grid-template-columns: 1fr;
            }

            .payment-methods {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
<div class="page-container">
    <jsp:include page="/views/common/header-renew.jsp"/>

    <main class="checkout-container">
        <h1 style="margin-bottom: 30px;">Thanh toán đơn hàng</h1>

        <form action="${pageContext.request.contextPath}/checkout" method="post">
            <div class="checkout-grid">
                <!-- Shipping Information -->
                <div>
                    <div class="checkout-section">
                        <h2 class="section-title">Thông tin giao hàng</h2>

                        <div class="form-row">
                            <div class="form-group">
                                <label>Họ và tên *</label>
                                <input type="text" name="fullName" required>
                            </div>
                            <div class="form-group">
                                <label>Số điện thoại *</label>
                                <input type="tel" name="phone" required>
                            </div>
                        </div>

                        <div class="form-group">
                            <label>Email</label>
                            <input type="email" name="email">
                        </div>

                        <div class="form-group">
                            <label>Địa chỉ giao hàng *</label>
                            <input type="text" name="address" required>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label>Tỉnh/Thành phố *</label>
                                <select name="province" required>
                                    <option value="">Chọn Tỉnh/Thành phố</option>
                                    <option value="HCM">TP. Hồ Chí Minh</option>
                                    <option value="HN">Hà Nội</option>
                                    <option value="DN">Đà Nẵng</option>
                                    <option value="CT">Cần Thơ</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label>Quận/Huyện *</label>
                                <select name="district" required>
                                    <option value="">Chọn Quận/Huyện</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label>Ghi chú đơn hàng</label>
                            <textarea name="note" placeholder="Ghi chú về đơn hàng, ví dụ: thời gian hay chỉ dẫn địa điểm giao hàng chi tiết hơn."></textarea>
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

                            <label class="payment-option">
                                <input type="radio" name="paymentMethod" value="BANK">
                                <div>
                                    <i class="fas fa-university" style="font-size: 24px; color: #3498db;"></i>
                                    <div>Chuyển khoản ngân hàng</div>
                                </div>
                            </label>

                            <label class="payment-option">
                                <input type="radio" name="paymentMethod" value="MOMO">
                                <div>
                                    <i class="fas fa-mobile-alt" style="font-size: 24px; color: #e91e63;"></i>
                                    <div>Ví MoMo</div>
                                </div>
                            </label>

                            <label class="payment-option">
                                <input type="radio" name="paymentMethod" value="VNPAY">
                                <div>
                                    <i class="fas fa-credit-card" style="font-size: 24px; color: #f39c12;"></i>
                                    <div>VNPay</div>
                                </div>
                            </label>
                        </div>
                    </div>
                </div>

                <!-- Order Summary -->
                <div>
                    <div class="checkout-section">
                        <h2 class="section-title">Đơn hàng của bạn</h2>

                        <div class="order-summary">
                            <c:forEach items="${sessionScope.cart.getItems()}" var="item">
                                <div class="summary-item">
                                    <div class="item-info">
                                        <div class="item-name">${item.product.name}</div>
                                        <div class="item-quantity">Số lượng: ${item.quantity}</div>
                                    </div>
                                    <div class="item-price">
                                        <fmt:formatNumber value="${item.subTotal}" pattern="#,###"/>₫
                                    </div>
                                </div>
                            </c:forEach>

                            <div style="margin-top: 20px; padding-top: 20px; border-top: 1px solid #ddd;">
                                <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
                                    <span>Tạm tính:</span>
                                    <span><fmt:formatNumber value="${sessionScope.cart.total()}" pattern="#,###"/>₫</span>
                                </div>
                                <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
                                    <span>Phí vận chuyển:</span>
                                    <span>Miễn phí</span>
                                </div>
                            </div>

                            <div class="summary-total">
                                <span>Tổng cộng:</span>
                                <span style="color: #e74c3c;">
                                    <fmt:formatNumber value="${sessionScope.cart.total()}" pattern="#,###"/>₫
                                </span>
                            </div>
                        </div>

                        <button type="submit" class="submit-btn">
                            <i class="fas fa-check-circle"></i> Đặt hàng
                        </button>

                        <a href="${pageContext.request.contextPath}/cart"
                           style="display: block; text-align: center; margin-top: 15px; color: #666;">
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
    // Dữ liệu quận/huyện theo tỉnh/thành phố
    const districts = {
        'HCM': [
            'Quận 1', 'Quận 2', 'Quận 3', 'Quận 4', 'Quận 5', 'Quận 6', 'Quận 7', 'Quận 8',
            'Quận 9', 'Quận 10', 'Quận 11', 'Quận 12', 'Quận Bình Thạnh', 'Quận Tân Bình',
            'Quận Tân Phú', 'Quận Phú Nhuận', 'Quận Gò Vấp', 'Quận Bình Tân', 'Quận Thủ Đức',
            'Huyện Củ Chi', 'Huyện Hóc Môn', 'Huyện Bình Chánh', 'Huyện Nhà Bè', 'Huyện Cần Giờ'
        ],
        'HN': [
            'Quận Ba Đình', 'Quận Hoàn Kiếm', 'Quận Tây Hồ', 'Quận Long Biên', 'Quận Cầu Giấy',
            'Quận Đống Đa', 'Quận Hai Bà Trưng', 'Quận Hoàng Mai', 'Quận Thanh Xuân', 'Quận Hà Đông',
            'Huyện Sóc Sơn', 'Huyện Đông Anh', 'Huyện Gia Lâm', 'Huyện Từ Liêm', 'Huyện Thanh Trì'
        ],
        'DN': [
            'Quận Hải Châu', 'Quận Thanh Khê', 'Quận Sơn Trà', 'Quận Ngũ Hành Sơn',
            'Quận Liên Chiểu', 'Quận Cẩm Lệ', 'Huyện Hòa Vang', 'Huyện Hoàng Sa'
        ],
        'CT': [
            'Quận Ninh Kiều', 'Quận Ô Môn', 'Quận Bình Thuỷ', 'Quận Cái Răng', 'Quận Thốt Nốt',
            'Huyện Vĩnh Thạnh', 'Huyện Cờ Đỏ', 'Huyện Phong Điền', 'Huyện Thới Lai'
        ]
    };

    // Lắng nghe sự kiện thay đổi tỉnh/thành phố
    document.querySelector('select[name="province"]').addEventListener('change', function() {
        const provinceCode = this.value;
        const districtSelect = document.querySelector('select[name="district"]');

        // Xóa các option cũ
        districtSelect.innerHTML = '<option value="">Chọn Quận/Huyện</option>';

        // Thêm các option mới
        if (provinceCode && districts[provinceCode]) {
            districts[provinceCode].forEach(district => {
                const option = document.createElement('option');
                option.value = district;
                option.textContent = district;
                districtSelect.appendChild(option);
            });
        }
    });
</script>
</body>
</html>
