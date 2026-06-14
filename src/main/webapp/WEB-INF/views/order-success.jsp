<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <c:choose>
            <c:when test="${signaturePaymentSuccess}">Thanh toán thành công - INOLA</c:when>
            <c:otherwise>Đơn hàng chờ ký - INOLA</c:otherwise>
        </c:choose>
    </title>
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
        <section class="payment-panel payment-result payment-result--success">
            <div class="payment-result__icon">
                <i class="fa-solid fa-check" aria-hidden="true"></i>
            </div>
            <h1>
                <c:choose>
                    <c:when test="${signaturePaymentSuccess}">Ký hợp lệ, thanh toán thành công</c:when>
                    <c:otherwise>Đơn hàng đã được tạo</c:otherwise>
                </c:choose>
            </h1>
            <p class="payment-result__message">
                <c:choose>
                    <c:when test="${signaturePaymentSuccess}">
                        Chữ ký số đã được xác thực. Đơn hàng của bạn đã được xác nhận và sẵn sàng xử lý.
                    </c:when>
                    <c:otherwise>
                        Đơn hàng đã được tạo và đang chờ ký xác nhận. Vui lòng ký đơn hàng để hệ thống tiếp tục xử lý hoặc thanh toán.
                    </c:otherwise>
                </c:choose>
            </p>
            <c:if test="${not empty orderCode}">
                <dl class="payment-result__details">
                    <div><dt>Mã đơn hàng</dt><dd><c:out value="${orderCode}"/></dd></div>
                    <div><dt>Phương thức</dt><dd>Thanh toán khi nhận hàng</dd></div>
                </dl>
            </c:if>
            <div class="payment-result__actions">
                <c:if test="${not signaturePaymentSuccess and not empty orderId}">
                    <a class="payment-action payment-action--primary"
                       href="${pageContext.request.contextPath}/orders/sign?id=${orderId}">
                        Ký đơn hàng
                    </a>
                </c:if>
                <a class="payment-action payment-action--primary" href="${pageContext.request.contextPath}/user/orders">
                    Xem đơn hàng
                </a>
                <a class="payment-action" href="${pageContext.request.contextPath}/home">Tiếp tục mua sắm</a>
            </div>
        </section>
    </main>
    <jsp:include page="/views/common/footer.jsp"/>
</div>
</body>
</html>
