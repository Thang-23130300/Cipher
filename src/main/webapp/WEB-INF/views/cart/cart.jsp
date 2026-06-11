<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<div class="page-wrapper cart-page">
    <div class="cart-layout">
        <div class="cart-items-section">

            <div class="continue-shopping">
                <a href="${pageContext.request.contextPath}/home">
                    <i class="fa-solid fa-arrow-left"></i> Tiếp tục mua sắm
                </a>
            </div>

            <div class="section-header">
                <h2>Giỏ hàng của bạn</h2>
                <span id="cart-total-qty">Tổng cộng: ${cart.totalQuantity()} sản phẩm</span>
            </div>

            <c:if test="${cart.totalQuantity() == 0}">
                <div class="empty-cart" style="text-align: center; padding: 40px 0;">
                    <p style="margin-bottom: 20px; font-size: 16px; color: #666;">Giỏ hàng của bạn đang trống.</p>
                    <a class="checkout-btn"
                       href="${pageContext.request.contextPath}/home"
                       style="width: auto; display: inline-block !important; padding: 10px 30px;">
                        Mua sắm ngay
                    </a>
                </div>
            </c:if>

            <c:forEach items="${cart.items}" var="item">
                <div class="cart-item-card" data-product-id="${item.product.id}">

                    <div class="item-image">
                        <img src="${item.product.imageUrl}" alt="${item.product.name}">
                    </div>

                    <div class="item-details">
                        <h3>${item.product.name}</h3>
                        <p>
                            Đơn giá:
                            <fmt:formatNumber value="${item.price}" groupingUsed="true"/>₫
                        </p>
                    </div>

                    <div class="item-price-quantity">
                        <span class="item-price">
                            <fmt:formatNumber value="${item.subTotal}" groupingUsed="true"/>₫
                        </span>

                        <div class="quantity-selector">
                            <button type="button" class="qty-btn minus-btn">-</button>

                            <input type="number"
                                   class="qty-input"
                                   value="${item.quantity}"
                                   min="1">

                            <button type="button" class="qty-btn plus-btn">+</button>
                        </div>
                    </div>

                    <button type="button"
                            class="remove-item-btn"
                            data-product-id="${item.product.id}">
                        <i class="fa-solid fa-trash-can"></i> Xóa
                    </button>

                </div>
            </c:forEach>

        </div>

        <div class="order-summary-section">
            <div class="summary-card">
                <h2 class="summary-card-title">Tóm tắt đơn hàng</h2>

                <div class="summary-row">
                    <span>Tạm tính</span>
                    <span id="cart-subtotal"><fmt:formatNumber value="${cart.total()}" groupingUsed="true"/>₫</span>
                </div>

                <div class="summary-total-row"
                     style="border-top: none; margin-top: 0; padding-top: 5px; color: #666; font-size: 14px;">
                    <span>Phí vận chuyển</span>
                    <span>Liên hệ sau</span>
                </div>

                <div class="summary-total-row">
                    <span>Tổng thanh toán</span>
                    <span id="cart-total-pay" class="final-total">
                        <fmt:formatNumber value="${cart.total()}" groupingUsed="true"/>₫
                    </span>
                </div>

                <a class="checkout-btn"
                   href="${pageContext.request.contextPath}/checkout"
                   onclick="return confirm('Xác nhận thanh toán?')">
                    XÁC NHẬN THANH TOÁN
                </a>
            </div>
        </div>

    </div>
</div>
