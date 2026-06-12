<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý khóa công khai</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f6f7fb;
            margin: 0;
            padding: 30px;
        }

        .key-page {
            max-width: 1100px;
            margin: 0 auto;
            background: #fff;
            padding: 28px;
            border-radius: 14px;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
        }

        h1 {
            margin-top: 0;
            color: #222;
        }

        .description {
            color: #666;
            margin-bottom: 24px;
            line-height: 1.6;
        }

        .alert {
            padding: 12px 16px;
            border-radius: 8px;
            margin-bottom: 16px;
        }

        .alert-success {
            background: #e8f7ee;
            color: #176c35;
            border: 1px solid #b9e6c9;
        }

        .alert-error {
            background: #fdecec;
            color: #9f1c1c;
            border: 1px solid #f3b8b8;
        }

        .section {
            margin-top: 28px;
            padding-top: 22px;
            border-top: 1px solid #e6e6e6;
        }

        label {
            display: block;
            font-weight: bold;
            margin-bottom: 8px;
        }

        textarea {
            width: 100%;
            min-height: 180px;
            padding: 12px;
            font-family: Consolas, monospace;
            font-size: 14px;
            border: 1px solid #ccc;
            border-radius: 8px;
            resize: vertical;
            box-sizing: border-box;
        }

        .btn {
            display: inline-block;
            border: none;
            border-radius: 8px;
            padding: 10px 16px;
            cursor: pointer;
            text-decoration: none;
            font-weight: bold;
        }

        .btn-primary {
            background: #2563eb;
            color: white;
            margin-top: 12px;
        }

        .btn-danger {
            background: #dc2626;
            color: white;
        }

        .key-box {
            background: #f8fafc;
            border: 1px solid #dbe3ef;
            padding: 14px;
            border-radius: 8px;
            white-space: pre-wrap;
            word-break: break-all;
            font-family: Consolas, monospace;
            font-size: 13px;
            max-height: 220px;
            overflow: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 14px;
        }

        th, td {
            border: 1px solid #e5e7eb;
            padding: 10px;
            text-align: left;
            vertical-align: top;
        }

        th {
            background: #f3f4f6;
        }

        .status-active {
            color: #15803d;
            font-weight: bold;
        }

        .status-revoked {
            color: #b91c1c;
            font-weight: bold;
        }

        .empty {
            color: #777;
            font-style: italic;
        }
    </style>
</head>

<body>
<div class="key-page">
    <h1>Quản lý khóa công khai</h1>

    <p class="description">
        Trang này dùng để lưu public key của bạn trên hệ thống.
        Private key không được nhập vào web và phải được giữ trong công cụ ký trên máy cá nhân.
    </p>

    <c:if test="${not empty sessionScope.success}">
        <div class="alert alert-success">
            <c:out value="${sessionScope.success}"/>
        </div>
        <c:remove var="success" scope="session"/>
    </c:if>

    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-error">
            <c:out value="${sessionScope.error}"/>
        </div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <div class="section">
        <h2>Thêm / cập nhật public key</h2>

        <form method="post" action="${pageContext.request.contextPath}/signature/keys/save">
            <label for="publicKey">Public key PEM</label>

            <textarea id="publicKey" name="publicKey" placeholder="-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A...
-----END PUBLIC KEY-----"></textarea>

            <button type="submit" class="btn btn-primary">Lưu public key</button>
        </form>
    </div>

    <div class="section">
        <h2>Public key đang hoạt động</h2>

        <c:choose>
            <c:when test="${not empty activeKey}">
                <p>
                    Trạng thái:
                    <span class="status-active">
                        <c:out value="${activeKey.keyStatus}"/>
                    </span>
                </p>

                <p>
                    Thuật toán:
                    <strong><c:out value="${activeKey.keyAlgorithm}"/></strong>
                    |
                    Kích thước:
                    <strong><c:out value="${activeKey.keySize}"/></strong>
                    |
                    Ký:
                    <strong><c:out value="${activeKey.signatureAlgorithm}"/></strong>
                </p>

                <div class="key-box"><c:out value="${activeKey.publicKey}"/></div>

                <form method="post" action="${pageContext.request.contextPath}/signature/keys/revoke" style="margin-top: 12px;">
                    <input type="hidden" name="keyId" value="${activeKey.id}">
                    <button type="submit" class="btn btn-danger"
                            onclick="return confirm('Bạn chắc chắn muốn thu hồi public key này?');">
                        Thu hồi key
                    </button>
                </form>
            </c:when>

            <c:otherwise>
                <p class="empty">Hiện chưa có public key ACTIVE.</p>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="section">
        <h2>Lịch sử public key</h2>

        <c:choose>
            <c:when test="${not empty keyHistory}">
                <table>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Trạng thái</th>
                        <th>Thuật toán</th>
                        <th>Ngày tạo</th>
                        <th>Ngày thu hồi</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="key" items="${keyHistory}">
                        <tr>
                            <td><c:out value="${key.id}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${key.keyStatus == 'ACTIVE'}">
                                        <span class="status-active">ACTIVE</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status-revoked">
                                            <c:out value="${key.keyStatus}"/>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:out value="${key.keyAlgorithm}"/>
                                /
                                <c:out value="${key.keySize}"/>
                            </td>
                            <td><c:out value="${key.createdAt}"/></td>
                            <td><c:out value="${key.revokedAt}"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:when>

            <c:otherwise>
                <p class="empty">Chưa có lịch sử public key.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>