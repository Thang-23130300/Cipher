<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Kết nối Java Signing Tool</title>
    <style>
        * { box-sizing: border-box; }
        body {
            margin: 0;
            min-height: 100vh;
            display: grid;
            place-items: center;
            padding: 24px;
            background: #f3f7f5;
            color: #1f2937;
            font-family: Arial, sans-serif;
        }
        .card {
            width: min(760px, 100%);
            padding: 30px;
            border: 1px solid #dbe7e1;
            border-radius: 18px;
            background: #fff;
            box-shadow: 0 18px 45px rgba(15, 82, 58, .10);
        }
        h1 { margin: 0 0 8px; color: #146c4b; }
        .muted { color: #64748b; line-height: 1.55; }
        .section {
            margin-top: 20px;
            padding: 18px;
            border-radius: 14px;
            background: #f8faf9;
            border: 1px solid #e2e8e5;
        }
        .row { margin: 8px 0; line-height: 1.5; }
        .label { font-weight: 700; color: #334155; }
        .key {
            margin-top: 12px;
            max-height: 190px;
            overflow: auto;
            white-space: pre-wrap;
            word-break: break-all;
            padding: 14px;
            border-radius: 10px;
            background: #0f172a;
            color: #f8fafc;
            font: 12px/1.5 Consolas, monospace;
        }
        .warning { color: #92400e; background: #fffbeb; border-color: #fde68a; }
        button {
            margin-top: 22px;
            width: 100%;
            border: 0;
            border-radius: 999px;
            padding: 13px 20px;
            background: #16805a;
            color: #fff;
            font-size: 16px;
            font-weight: 700;
            cursor: pointer;
        }
        button:hover { background: #116b4a; }
        button:disabled { cursor: wait; opacity: .65; }
        #result {
            display: none;
            margin-top: 16px;
            padding: 13px 15px;
            border-radius: 10px;
            line-height: 1.5;
        }
        #result.success { display: block; color: #166534; background: #dcfce7; }
        #result.error { display: block; color: #991b1b; background: #fee2e2; }
    </style>
</head>
<body>
<main class="card">
    <h1>Kết nối Java Signing Tool</h1>
    <p class="muted">Xác nhận gửi public key ACTIVE của tài khoản hiện tại về Signing Tool trên máy này.</p>

    <section class="section">
        <div class="row"><span class="label">Người dùng:</span> <c:out value="${currentUser.fullName}"/></div>
        <div class="row"><span class="label">Email:</span> <c:out value="${currentUser.email}"/></div>
    </section>

    <c:choose>
        <c:when test="${not empty activeKey}">
            <section class="section">
                <div class="row"><span class="label">Trạng thái:</span> ACTIVE</div>
                <div class="row"><span class="label">Key ID:</span> <c:out value="${activeKey.id}"/></div>
                <div class="row"><span class="label">Fingerprint:</span> <c:out value="${fingerprint}"/></div>
                <div class="row"><span class="label">Ngày tạo:</span> <c:out value="${activeKey.createdAt}"/></div>
                <pre class="key"><c:out value="${activeKey.publicKey}"/></pre>
            </section>
        </c:when>
        <c:otherwise>
            <section class="section warning">
                Tài khoản chưa có public key ACTIVE. Signing Tool sẽ nhận được thông báo lỗi này.
            </section>
        </c:otherwise>
    </c:choose>

    <div id="connectData"
         data-callback-url="<c:out value='${callbackUrl}'/>"
         data-nonce="<c:out value='${nonce}'/>"
         data-has-active-key="${not empty activeKey}"
         data-key-id="<c:out value='${activeKey.id}'/>"
         data-fingerprint="<c:out value='${fingerprint}'/>"
         data-created-at="<c:out value='${activeKey.createdAt}'/>"></div>
    <textarea id="publicKeyValue" hidden><c:out value="${activeKey.publicKey}"/></textarea>

    <button id="connectButton" type="button">Kết nối Signing Tool</button>
    <div id="result" role="status" aria-live="polite"></div>
</main>

<script>
    (() => {
        const data = document.getElementById('connectData');
        const button = document.getElementById('connectButton');
        const result = document.getElementById('result');
        const hasActiveKey = data.dataset.hasActiveKey === 'true';

        function showResult(message, type) {
            result.textContent = message;
            result.className = type;
        }

        button.addEventListener('click', async () => {
            button.disabled = true;
            showResult('Đang kết nối với Signing Tool...', 'success');

            const payload = hasActiveKey
                ? {
                    success: true,
                    nonce: data.dataset.nonce,
                    keyId: data.dataset.keyId,
                    publicKey: document.getElementById('publicKeyValue').value,
                    fingerprint: data.dataset.fingerprint,
                    createdAt: data.dataset.createdAt
                }
                : {
                    success: false,
                    nonce: data.dataset.nonce,
                    keyId: '',
                    publicKey: '',
                    fingerprint: '',
                    createdAt: '',
                    message: 'Tài khoản chưa có public key ACTIVE.'
                };

            try {
                const response = await fetch(data.dataset.callbackUrl, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(payload)
                });
                const responseBody = await response.json().catch(() => ({}));

                if (!response.ok && payload.success) {
                    throw new Error(responseBody.message || 'Signing Tool từ chối kết nối.');
                }

                showResult(
                    payload.success
                        ? 'Đã kết nối và gửi public key ACTIVE tới Signing Tool.'
                        : 'Đã thông báo cho Signing Tool rằng tài khoản chưa có public key ACTIVE.',
                    payload.success ? 'success' : 'error'
                );
            } catch (error) {
                showResult('Không thể kết nối Signing Tool: ' + error.message, 'error');
            } finally {
                button.disabled = false;
            }
        });
    })();
</script>
</body>
</html>
