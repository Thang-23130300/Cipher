<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ký đơn hàng #${order.orderCode} - INOLA</title>

    <!-- ================= GLOBAL CSS ================= -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/Base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout/header.css?v=8">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout/footer.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/account/account-common.css">

    <style>
        .sign-container {
            max-width: 900px;
            margin: 40px auto;
            padding: 0 15px;
        }

        .sign-card {
            background: var(--surface-default);
            border-radius: var(--radius-lg);
            box-shadow: var(--shadow-md);
            padding: 32px;
            border: 1px solid var(--border-subtle);
        }

        .sign-header {
            border-bottom: 1px solid var(--border-subtle);
            padding-bottom: 20px;
            margin-bottom: 24px;
        }

        .sign-header h1 {
            font-size: 26px;
            color: var(--text-strong);
            margin: 0 0 8px 0;
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .sign-header h1 i {
            color: var(--brand-primary);
        }

        .sign-header p {
            color: var(--text-soft);
            margin: 0;
            font-size: 14px;
            line-height: 1.5;
        }

        /* Order Summary Grid */
        .order-summary-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            background: var(--surface-muted);
            padding: 20px;
            border-radius: var(--radius-md);
            margin-bottom: 28px;
            border: 1px solid var(--border-subtle);
        }

        .summary-item {
            display: flex;
            flex-direction: column;
            gap: 6px;
        }

        .summary-item label {
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
            color: var(--text-soft);
            letter-spacing: 0.5px;
        }

        .summary-item span {
            font-size: 16px;
            font-weight: 700;
            color: var(--text-strong);
        }

        .summary-item .badge {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 13px;
            font-weight: 600;
            width: fit-content;
        }

        .badge-waiting {
            background-color: var(--warning-bg);
            color: var(--warning);
            border: 1px solid var(--warning-border);
        }

        .badge-invalid {
            background-color: var(--danger-bg);
            color: var(--danger);
            border: 1px solid var(--danger-border);
        }

        .badge-signed {
            background-color: var(--success-bg);
            color: var(--success);
            border: 1px solid var(--success-border);
        }

        /* Form Controls */
        .form-group {
            margin-bottom: 24px;
        }

        .form-group label {
            display: block;
            font-size: 15px;
            font-weight: 700;
            color: var(--text-strong);
            margin-bottom: 8px;
        }

        .input-group-textarea {
            position: relative;
        }

        .textarea-custom {
            width: 100%;
            height: 110px;
            padding: 14px;
            border: 1.5px solid var(--border-default);
            border-radius: var(--radius-md);
            background: var(--surface-default);
            color: var(--text-strong);
            font-family: 'Consolas', 'Courier New', Courier, monospace;
            font-size: 14px;
            line-height: 1.5;
            resize: none;
            transition: border-color 0.2s, box-shadow 0.2s;
        }

        .textarea-custom:focus {
            outline: none;
            border-color: var(--brand-primary);
            box-shadow: var(--focus-ring);
        }

        .textarea-readonly {
            background: var(--surface-muted);
            border-color: var(--border-subtle);
            color: var(--text-soft);
            cursor: default;
        }

        .textarea-custom:disabled {
            background: var(--surface-muted);
            border-color: var(--border-subtle);
            color: var(--text-soft);
            cursor: not-allowed;
        }

        .textarea-signature {
            height: 140px;
        }

        /* Buttons and actions */
        .button-row {
            display: flex;
            align-items: center;
            justify-content: flex-end;
            margin-top: 12px;
        }

        .btn-copy {
            background: var(--surface-default);
            color: var(--brand-primary);
            border: 1.5px solid var(--brand-primary);
            border-radius: var(--radius-sm);
            padding: 6px 12px;
            font-size: 13px;
            font-weight: 700;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 6px;
            transition: all 0.2s;
        }

        .btn-copy:hover {
            background: var(--brand-primary-soft);
        }

        .btn-copy.btn-success {
            background: var(--success-bg);
            color: var(--success);
            border-color: var(--success-border);
        }

        .actions-bar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-top: 1px solid var(--border-subtle);
            padding-top: 24px;
            margin-top: 28px;
            gap: 16px;
            flex-wrap: wrap;
        }

        .btn-back {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            color: var(--text-soft);
            font-weight: 600;
            text-decoration: none;
            transition: color 0.2s;
            padding: 10px 16px;
            border-radius: var(--radius-md);
            border: 1px solid var(--border-default);
        }

        .btn-back:hover {
            color: var(--text-strong);
            background: var(--surface-muted);
        }

        .action-buttons {
            display: flex;
            gap: 12px;
        }

        .btn-sign-tool {
            background: var(--brand-secondary);
            color: #fff;
            border: none;
            border-radius: var(--radius-md);
            padding: 12px 20px;
            font-weight: 700;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: opacity 0.2s, background-color 0.2s;
        }

        .btn-sign-tool:hover {
            background-color: #2b5163;
        }

        .btn-submit {
            background: var(--brand-primary);
            color: #fff;
            border: none;
            border-radius: var(--radius-md);
            padding: 12px 24px;
            font-weight: 700;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: background-color 0.2s;
        }

        .btn-submit:hover {
            background-color: var(--brand-primary-hover);
        }

        .btn-disabled {
            background-color: var(--border-strong) !important;
            color: var(--text-subtle) !important;
            cursor: not-allowed !important;
        }

        /* Alerts & Info */
        .status-alert {
            padding: 16px;
            border-radius: var(--radius-md);
            font-size: 14px;
            line-height: 1.5;
            margin-bottom: 24px;
            display: none;
        }

        .status-alert-success {
            background-color: var(--success-bg);
            color: var(--success);
            border: 1px solid var(--success-border);
        }

        .status-alert-danger {
            background-color: var(--danger-bg);
            color: var(--danger);
            border: 1px solid var(--danger-border);
        }

        .warning-banner {
            background-color: var(--warning-bg);
            color: var(--warning);
            border: 1px solid var(--warning-border);
            padding: 16px;
            border-radius: var(--radius-md);
            font-size: 14px;
            line-height: 1.6;
            margin-bottom: 24px;
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .warning-banner a {
            color: var(--brand-primary);
            font-weight: 700;
            text-decoration: underline;
        }

        .warning-banner a:hover {
            color: var(--brand-primary-hover);
        }
    </style>
</head>
<body>

<!-- ================= HEADER ================= -->
<jsp:include page="/WEB-INF/layout/header.jsp"/>

<!-- ================= MAIN CONTENT ================= -->
<main class="sign-container">

    <c:url var="keyManagementUrl" value="/key-management">
        <c:param name="returnUrl" value="/orders/sign?id=${order.id}"/>
    </c:url>

    <div class="sign-card">
        <div class="sign-header">
            <h1><i class="fa-solid fa-file-signature"></i> Ký đơn hàng #${order.orderCode}</h1>
            <p>Xác thực giao dịch an toàn bằng chữ ký số. Bạn có thể sử dụng <strong>Signing Tool</strong> cục bộ để ký tự động hoặc dán thủ công chữ ký Base64 bên dưới.</p>
        </div>

        <!-- Session Alert Messages -->
        <c:if test="${not empty sessionScope.error}">
            <div class="status-alert status-alert-danger" style="display: block;">
                <i class="fa-solid fa-circle-exclamation"></i> <c:out value="${sessionScope.error}"/>
            </div>
            <c:remove var="error" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.success}">
            <div class="status-alert status-alert-success" style="display: block;">
                <i class="fa-solid fa-circle-check"></i> <c:out value="${sessionScope.success}"/>
            </div>
            <c:remove var="success" scope="session"/>
        </c:if>

        <!-- Warnings if public key inactive -->
        <c:if test="${hasActivePublicKey eq false}">
            <div class="warning-banner">
                <div>
                    <i class="fa-solid fa-triangle-exclamation"></i> <strong>Bạn chưa cấu hình hoặc không có khóa công khai (Public Key) nào ở trạng thái HOẠT ĐỘNG.</strong>
                </div>
                <div>
                    Vui lòng truy cập trang <a href="${keyManagementUrl}">Quản lý khóa công khai</a> để thêm và kích hoạt khóa của bạn trước khi thực hiện ký đơn hàng.
                </div>
            </div>
        </c:if>

        <!-- Order Details Grid -->
        <div class="order-summary-grid">
            <div class="summary-item">
                <label>Mã Đơn hàng</label>
                <span><c:out value="${order.orderCode}"/></span>
            </div>
            <div class="summary-item">
                <label>Ngày đặt</label>
                <span><fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm"/></span>
            </div>
            <div class="summary-item">
                <label>Tổng thanh toán</label>
                <span style="color: var(--brand-primary);">
                        <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫"/>
                    </span>
            </div>
            <div class="summary-item">
                <label>Trạng thái chữ ký</label>
                <span>
                        <c:choose>
                            <c:when test="${order.signatureStatus eq 'WAITING_SIGNATURE'}">
                                <span class="badge badge-waiting"><i class="fa-solid fa-clock"></i> Chờ ký số</span>
                            </c:when>
                            <c:when test="${order.signatureStatus eq 'SIGNATURE_INVALID'}">
                                <span class="badge badge-invalid"><i class="fa-solid fa-triangle-exclamation"></i> Chữ ký không hợp lệ</span>
                            </c:when>
                            <c:when test="${order.signatureStatus eq 'SIGNED'}">
                                <span class="badge badge-signed"><i class="fa-solid fa-circle-check"></i> Đã ký hợp lệ</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge badge-waiting">${order.signatureStatus}</span>
                            </c:otherwise>
                        </c:choose>
                    </span>
            </div>
        </div>

        <!-- Connection status from JS API fetch -->
        <div id="toolStatus" class="status-alert"></div>

        <!-- Hash Value Section -->
        <div class="form-group">
            <label for="hashValue">Mã băm đơn hàng (hash_value)</label>
            <div class="input-group-textarea">
                <textarea id="hashValue" class="textarea-custom textarea-readonly" readonly><c:out value="${hashValue}"/></textarea>
            </div>
            <div class="button-row">
                <button type="button" id="btnCopyHash" class="btn-copy" onclick="copyHash()">
                    <i class="fa-regular fa-copy"></i> Copy mã băm
                </button>
            </div>
        </div>

        <!-- Form submit signature -->
        <form id="signatureSubmitForm" method="post" action="${pageContext.request.contextPath}/orders/submit-signature">
            <input type="hidden" name="orderId" value="${order.id}">

            <div class="form-group">
                <label for="signatureValue">Chữ ký số (Base64 Signature)</label>
                <div class="input-group-textarea">
                        <textarea id="signatureValue" name="signatureValue" class="textarea-custom textarea-signature"
                                  placeholder="Dán chữ ký Base64 từ Signing Tool vào đây hoặc nhấn 'Ký bằng Signing Tool' để kết nối tự động..."
                                  <c:if test="${hasActivePublicKey eq false}">disabled</c:if>></textarea>
                </div>
            </div>

            <!-- Footer Actions -->
            <div class="actions-bar">
                <a href="${pageContext.request.contextPath}/user/orders" class="btn-back">
                    <i class="fa-solid fa-arrow-left"></i> Quay lại đơn hàng
                </a>

                <div class="action-buttons">
                    <button type="button" id="btnSignTool" class="btn-sign-tool <c:if test="${hasActivePublicKey eq false}">btn-disabled</c:if>"
                            onclick="signWithTool()" <c:if test="${hasActivePublicKey eq false}">disabled</c:if>>
                        <i class="fa-solid fa-key"></i> Ký bằng Signing Tool
                    </button>

                    <button type="submit" id="btnSubmitSign" class="btn-submit <c:if test="${hasActivePublicKey eq false}">btn-disabled</c:if>"
                            <c:if test="${hasActivePublicKey eq false}">disabled</c:if>>
                        <i class="fa-solid fa-paper-plane"></i> Gửi chữ ký
                    </button>
                </div>
            </div>
        </form>
    </div>
</main>

<!-- ================= FOOTER ================= -->
<jsp:include page="/WEB-INF/layout/footer.jsp"/>

<!-- ================= SCRIPTS ================= -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/common.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/header.js?v=8"></script>

<script>
    // Copy hash value to clipboard
    function copyHash() {
        const hashTextarea = document.getElementById('hashValue');
        hashTextarea.select();
        hashTextarea.setSelectionRange(0, 99999);

        navigator.clipboard.writeText(hashTextarea.value.trim()).then(() => {
            const copyBtn = document.getElementById('btnCopyHash');
            const originalHtml = copyBtn.innerHTML;
            copyBtn.innerHTML = '<i class="fa-solid fa-check"></i> Đã copy';
            copyBtn.classList.add('btn-success');
            setTimeout(() => {
                copyBtn.innerHTML = originalHtml;
                copyBtn.classList.remove('btn-success');
            }, 2000);
        }).catch(err => {
            console.error('Không thể copy: ', err);
            alert('Không thể sao chép tự động. Bạn hãy tự bôi đen mã băm và nhấn Ctrl+C.');
        });
    }

    // Call Local Signing Tool API
    async function signWithTool() {
        const hash = document.getElementById('hashValue').value.trim();
        const statusDiv = document.getElementById('toolStatus');
        const signatureTextarea = document.getElementById('signatureValue');
        const btnSign = document.getElementById('btnSignTool');

        // Clear status alert
        statusDiv.style.display = 'none';
        statusDiv.className = 'status-alert';
        statusDiv.innerHTML = '';

        btnSign.disabled = true;
        const originalBtnHtml = btnSign.innerHTML;
        btnSign.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Đang kết nối...';

        try {
            const response = await fetch('http://localhost:9090/api/sign', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    orderId: '${requestScope.order.id}',
                    merchantName: 'INOLA',
                    hashAlgorithm: 'SHA-256',
                    signatureAlgorithm: 'SHA256withRSA',
                    hashValue: hash
                })
            });

            if (!response.ok) {
                throw new Error('Signing Tool local trả về mã lỗi HTTP: ' + response.status);
            }

            const responseText = await response.text();
            let signature = responseText.trim();

            // Flexible parsing of plain text or JSON output
            try {
                const data = JSON.parse(responseText);
                if (data && data.signature) {
                    signature = data.signature.trim();
                } else if (data && data.signatureValue) {
                    signature = data.signatureValue.trim();
                }
            } catch (e) {
                // responseText is plain text, keep signature as responseText
            }

            if (!signature) {
                throw new Error('Chữ ký nhận được từ tool bị rỗng.');
            }

            signatureTextarea.value = signature;

            // Show success status
            statusDiv.style.display = 'block';
            statusDiv.className = 'status-alert status-alert-success';
            statusDiv.innerHTML = '<i class="fa-solid fa-circle-check"></i> Đã tự động ký thành công từ Local Signing Tool!';
        } catch (error) {
            console.error('Lỗi khi gọi API ký: ', error);
            statusDiv.style.display = 'block';
            statusDiv.className = 'status-alert status-alert-danger';
            statusDiv.innerHTML = `<i class="fa-solid fa-circle-exclamation"></i> <strong>Không thể kết nối tới Local Signing Tool!</strong><br>
                                       Vui lòng đảm bảo phần mềm Signing Tool đã được bật và đang lắng nghe cổng 9090 trên máy tính của bạn.<br>
                                       Hoặc bạn có thể sao chép mã băm ở trên, dán vào tool để ký thủ công, rồi sao chép chữ ký dán ngược lại vào ô bên dưới.`;
        } finally {
            btnSign.disabled = false;
            btnSign.innerHTML = originalBtnHtml;
        }
    }

    // Validate on submit
    document.getElementById('signatureSubmitForm').addEventListener('submit', function(e) {
        const signature = document.getElementById('signatureValue').value.trim();
        if (!signature) {
            e.preventDefault();
            alert('Chữ ký không được để trống. Vui lòng ký đơn hàng trước khi gửi.');
            return false;
        }
    });
</script>
</body>
</html>
