<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <title>Thanh toán - INOLA</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/Base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/Payment.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <jsp:include page="/WEB-INF/layout/dark-mode.jsp"/>
</head>
<body>
<div class="page-container">
    <jsp:include page="/views/common/header-renew.jsp"/>

    <main id="main-content" class="payment-page">
        <header class="payment-page__header">
            <a class="payment-back-link" href="${pageContext.request.contextPath}/cart">
                <i class="fa-solid fa-arrow-left" aria-hidden="true"></i>
                Quay lại giỏ hàng
            </a>
            <p class="payment-page__eyebrow">Bước cuối cùng</p>
            <h1>Thanh toán đơn hàng</h1>
            <p>Kiểm tra thông tin nhận hàng và chọn phương thức thanh toán phù hợp.</p>
        </header>

        <c:if test="${not empty error}">
            <div class="payment-alert payment-alert--danger" role="alert">
                <i class="fa-solid fa-circle-exclamation" aria-hidden="true"></i>
                <span><c:out value="${error}"/></span>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/checkout" method="post" id="checkoutForm">
            <div class="checkout-layout">
                <div class="checkout-main">
                    <section class="payment-panel" aria-labelledby="shipping-title">
                        <div class="payment-panel__heading">
                            <span class="payment-step">1</span>
                            <div>
                                <h2 id="shipping-title">Thông tin giao hàng</h2>
                                <p>Chọn địa chỉ đã lưu hoặc nhập địa chỉ mới.</p>
                            </div>
                        </div>

                        <c:if test="${not empty savedAddresses}">
                            <fieldset class="address-list">
                                <legend>Địa chỉ đã lưu</legend>
                                <label class="address-choice">
                                    <input type="radio" name="savedAddressId" value="" checked>
                                    <span class="address-choice__content">
                                        <strong>Giao đến địa chỉ mới</strong>
                                        <span>Nhập thông tin người nhận bên dưới</span>
                                    </span>
                                    <i class="fa-solid fa-circle-check" aria-hidden="true"></i>
                                </label>
                                <c:forEach items="${savedAddresses}" var="addr">
                                    <label class="address-choice">
                                        <input type="radio" name="savedAddressId" value="${addr.id}">
                                        <span class="address-choice__content">
                                            <strong><c:out value="${addr.receiverName}"/> · <c:out value="${addr.receiverPhone}"/></strong>
                                            <span><c:out value="${addr.addressDetail}"/>, <c:out value="${addr.ward}"/>, <c:out value="${addr.province}"/></span>
                                            <c:if test="${addr.isDefault()}"><small>Địa chỉ mặc định</small></c:if>
                                        </span>
                                        <i class="fa-solid fa-circle-check" aria-hidden="true"></i>
                                    </label>
                                </c:forEach>
                            </fieldset>
                        </c:if>

                        <div id="newAddressFields" class="address-form">
                            <div class="payment-form-row">
                                <div class="payment-field">
                                    <label for="receiverName">Họ và tên người nhận <span aria-hidden="true">*</span></label>
                                    <input id="receiverName" type="text" name="receiverName"
                                           value="<c:out value="${currentUser.fullName}"/>" autocomplete="name" required>
                                </div>
                                <div class="payment-field">
                                    <label for="receiverPhone">Số điện thoại <span aria-hidden="true">*</span></label>
                                    <input id="receiverPhone" type="tel" name="receiverPhone"
                                           value="<c:out value="${currentUser.phone}"/>" autocomplete="tel"
                                           inputmode="tel" required>
                                </div>
                            </div>

                            <div class="payment-field">
                                <label for="addressDetail">Địa chỉ chi tiết <span aria-hidden="true">*</span></label>
                                <input id="addressDetail" type="text" name="addressDetail"
                                       placeholder="Số nhà, tên đường, khu vực" autocomplete="street-address" required>
                            </div>

                            <div class="payment-form-row">
                                <div class="payment-field">
                                    <label for="provinceCode">Tỉnh/Thành phố <span aria-hidden="true">*</span></label>
                                    <select name="provinceCode" id="provinceCode" required>
                                        <option value="">Chọn Tỉnh/Thành phố</option>
                                        <c:forEach items="${provinceOptions}" var="province">
                                            <option value="${province.code}">${empty province.fullName ? province.name : province.fullName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="payment-field">
                                    <label for="wardCode">Phường/Xã <span aria-hidden="true">*</span></label>
                                    <select name="wardCode" id="wardCode" required>
                                        <option value="">Chọn Phường/Xã</option>
                                    </select>
                                    <small id="wardStatus" class="payment-field__status" aria-live="polite"></small>
                                </div>
                            </div>
                        </div>

                        <div class="payment-field">
                            <label for="note">Ghi chú đơn hàng</label>
                            <textarea id="note" name="note" placeholder="Thời gian giao hàng hoặc chỉ dẫn địa điểm giao hàng."></textarea>
                        </div>
                    </section>

                    <section class="payment-panel" aria-labelledby="method-title">
                        <div class="payment-panel__heading">
                            <span class="payment-step">2</span>
                            <div>
                                <h2 id="method-title">Phương thức thanh toán</h2>
                                <p>Thông tin giao dịch được bảo vệ và xác thực an toàn.</p>
                            </div>
                        </div>

                        <div class="payment-method-list">
                            <label class="payment-method">
                                <input type="radio" name="paymentMethod" value="COD" checked>
                                <span class="payment-method__icon"><i class="fa-solid fa-money-bill-wave"></i></span>
                                <span class="payment-method__content">
                                    <strong>Thanh toán khi nhận hàng</strong>
                                    <span>Thanh toán tiền mặt sau khi kiểm tra đơn hàng</span>
                                </span>
                                <i class="fa-solid fa-circle-check payment-method__check" aria-hidden="true"></i>
                            </label>

                            <c:choose>
                                <c:when test="${vnpayAvailable}">
                                    <label class="payment-method">
                                        <input type="radio" name="paymentMethod" value="VNPAY_QR">
                                        <span class="payment-method__icon payment-method__icon--vnpay">
                                            <img src="${pageContext.request.contextPath}/assets/images/Payment/vnpay.webp" alt="">
                                        </span>
                                        <span class="payment-method__content">
                                            <strong>VNPay QR</strong>
                                            <span>Thanh toán qua ứng dụng ngân hàng hoặc ví hỗ trợ VNPay</span>
                                        </span>
                                        <i class="fa-solid fa-circle-check payment-method__check" aria-hidden="true"></i>
                                    </label>
                                </c:when>
                                <c:otherwise>
                                    <div class="payment-method payment-method--disabled" aria-disabled="true">
                                        <span class="payment-method__icon payment-method__icon--vnpay">
                                            <img src="${pageContext.request.contextPath}/assets/images/Payment/vnpay.webp" alt="">
                                        </span>
                                        <span class="payment-method__content">
                                            <strong>VNPay QR</strong>
                                            <span>Tạm thời chưa khả dụng</span>
                                        </span>
                                        <i class="fa-solid fa-lock payment-method__check" aria-hidden="true"></i>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </section>
                </div>

                <aside class="payment-panel order-summary" aria-labelledby="summary-title">
                    <div class="order-summary__heading">
                        <h2 id="summary-title">Đơn hàng của bạn</h2>
                        <span>${cart.totalQuantity()} sản phẩm</span>
                    </div>

                    <div class="order-summary__items">
                        <c:forEach items="${cart.items}" var="item">
                            <div class="order-summary__item">
                                <div>
                                    <strong><c:out value="${item.product.name}"/></strong>
                                    <span>Số lượng: ${item.quantity}</span>
                                </div>
                                <b><fmt:formatNumber value="${item.subTotal}" groupingUsed="true"/>₫</b>
                            </div>
                        </c:forEach>
                    </div>

                    <dl class="order-summary__totals">
                        <div><dt>Tạm tính</dt><dd><fmt:formatNumber value="${cart.total()}" groupingUsed="true"/>₫</dd></div>
                        <div><dt>Phí vận chuyển</dt><dd class="order-summary__free">Miễn phí</dd></div>
                        <div class="order-summary__grand-total">
                            <dt>Tổng cộng</dt>
                            <dd><fmt:formatNumber value="${cart.total()}" groupingUsed="true"/>₫</dd>
                        </div>
                    </dl>

                    <button type="submit" class="payment-submit" id="submitOrder">
                        <i class="fa-solid fa-shield-halved" aria-hidden="true"></i>
                        <span>Đặt hàng</span>
                    </button>
                    <p class="payment-secure-note">
                        <i class="fa-solid fa-lock" aria-hidden="true"></i>
                        Bằng việc đặt hàng, bạn đồng ý với điều khoản mua hàng của INOLA.
                    </p>
                </aside>
            </div>
        </form>
    </main>

    <jsp:include page="/views/common/footer.jsp"/>
</div>
<script src="${pageContext.request.contextPath}/assets/js/payment.js"></script>
</body>
</html>
