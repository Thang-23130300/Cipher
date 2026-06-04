<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt hàng thành công - INOLA</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout/footer.css">
    <style>
        .success-wrap { max-width: 760px; margin: 70px auto; padding: 0 20px; text-align: center; }
        .success-card { background: #fff; border-radius: 14px; box-shadow: 0 10px 30px rgba(15, 23, 42, .12); padding: 44px 32px; }
        .success-icon { width: 74px; height: 74px; border-radius: 999px; margin: 0 auto 18px; display: grid; place-items: center; background: #dcfce7; color: #15803d; font-size: 38px; }
        .success-title { margin: 0 0 10px; color: #111827; }
        .success-text { color: #4b5563; line-height: 1.6; margin-bottom: 24px; }
        .order-code { display: inline-block; background: #f3f4f6; border-radius: 8px; padding: 10px 14px; font-weight: 700; color: #111827; margin-bottom: 26px; }
        .actions { display: flex; justify-content: center; gap: 12px; flex-wrap: wrap; }
        .btn { display: inline-block; padding: 12px 18px; border-radius: 8px; text-decoration: none; font-weight: 700; }
        .btn-primary { background: #e74c3c; color: #fff; }
        .btn-secondary { background: #f3f4f6; color: #374151; }
    </style>
</head>
<body>
<div class="page-container">
    <jsp:include page="/views/common/header-renew.jsp"/>
    <main class="success-wrap">
        <section class="success-card">
            <div class="success-icon">✓</div>
            <h1 class="success-title">Đặt hàng thành công</h1>
            <p class="success-text">Đơn hàng của bạn đã được ghi nhận. Chúng tôi sẽ liên hệ xác nhận và xử lý giao hàng trong thời gian sớm nhất.</p>
            <c:if test="${not empty orderCode}">
                <div class="order-code">Mã đơn hàng: ${orderCode}</div>
            </c:if>
            <div class="actions">
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/user/orders">Xem đơn hàng</a>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/home">Tiếp tục mua sắm</a>
            </div>
        </section>
    </main>
    <jsp:include page="/views/common/footer.jsp"/>
</div>
</body>
</html>
