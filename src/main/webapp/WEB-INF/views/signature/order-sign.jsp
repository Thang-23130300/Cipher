<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Ký đơn hàng</title>
    <style>
        body {
            margin: 0;
            padding: 30px;
            background: #f6f7fb;
            color: #1f2937;
            font-family: Arial, sans-serif;
        }

        .sign-page {
            max-width: 980px;
            margin: 0 auto;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
            padding: 28px;
        }

        h1 {
            margin: 0 0 8px;
            font-size: 28px;
        }

        .muted {
            color: #6b7280;
            margin: 0 0 22px;
        }

        .field {
            margin-bottom: 18px;
        }

        label {
            display: block;
            font-weight: 700;
            margin-bottom: 8px;
        }

        textarea,
        input[type="text"] {
            width: 100%;
            box-sizing: border-box;
            border: 1px solid #d1d5db;
            border-radius: 8px;
            padding: 12px;
            font: 14px Consolas, monospace;
        }

        textarea {
            min-height: 150px;
            resize: vertical;
        }

        .hash-box {
            background: #f8fafc;
        }

        .actions {
            display: flex;
            flex-wrap: wrap;
            gap: 12px;
            align-items: center;
        }

        .btn {
            border: none;
            border-radius: 8px;
            padding: 10px 16px;
            font-weight: 700;
            cursor: pointer;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            justify-content: center;
        }

        .btn-primary {
            background: #2563eb;
            color: #fff;
        }

        .btn-secondary {
            background: #fff;
            color: #374151;
            border: 1px solid #d1d5db;
        }

        .alert-error {
            margin-bottom: 16px;
            padding: 12px 16px;
            border-radius: 8px;
            background: #fdecec;
            color: #9f1c1c;
            border: 1px solid #f3b8b8;
        }

        .alert-warning {
            margin-bottom: 16px;
            padding: 12px 16px;
            border-radius: 8px;
            background: #fff8e1;
            color: #7a4d00;
            border: 1px solid #f6d365;
        }

        .alert-warning a {
            color: #1d4ed8;
            font-weight: 700;
        }
    </style>
</head>
<body>
<main class="sign-page">
    <c:url var="keyManagementUrl" value="/key-management">
        <c:param name="returnUrl" value="/orders/sign?id=${requestScope.order.id}"/>
    </c:url>

    <h1>Ký đơn hàng <c:out value="${orderCode}"/></h1>
    <p class="muted">Copy hash_value bên dưới sang Java Signing Tool, ký bằng private key, rồi dán signature Base64 vào form.</p>

    <c:if test="${not empty sessionScope.error}">
        <div class="alert-error">
            <c:out value="${sessionScope.error}"/>
        </div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <c:if test="${hasActivePublicKey eq false}">
        <div class="alert-warning">
            Bạn chưa có public key ACTIVE. Vui lòng
            <a href="${keyManagementUrl}">quản lý khóa công khai</a>
            trước khi gửi chữ ký.
        </div>
    </c:if>

    <div class="field">
        <label for="hashValue">hash_value</label>
        <textarea id="hashValue" class="hash-box" readonly><c:out value="${hashValue}"/></textarea>
    </div>

    <form id="signatureSubmitForm"
          method="post"
          action="${pageContext.request.contextPath}/orders/submit-signature"
          accept-charset="UTF-8">
        <input type="hidden" name="orderId" value="${requestScope.order.id}">

        <div class="field">
            <label for="signatureValue">Signature Base64</label>
            <textarea id="signatureValue" name="signatureValue"
                      placeholder="Dán chữ ký Base64 từ Java Signing Tool vào đây"></textarea>
        </div>

        <div class="actions">
            <button type="submit" form="signatureSubmitForm" class="btn btn-primary">Gửi chữ ký</button>
            <a href="${pageContext.request.contextPath}/user/orders" class="btn btn-secondary">Quay lại đơn hàng</a>
        </div>
    </form>
</main>
</body>
</html>
