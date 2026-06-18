<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý khóa công khai</title>

    <style>
        :root {
            --page-bg: #f7faf8;
            --card-bg: #ffffff;

            --primary: #1f8f5f;
            --primary-dark: #14784e;
            --primary-soft: #e7f6ef;
            --primary-border: #b7e4cf;

            --text-main: #1f2937;
            --text-muted: #6b7280;

            --border: #e5e7eb;

            --danger: #dc2626;
            --danger-dark: #b91c1c;
            --danger-soft: #fee2e2;
            --danger-border: #fecaca;

            --success: #15803d;
            --success-soft: #dcfce7;

            --warning: #b45309;
            --warning-soft: #fef3c7;

            --gray-soft: #f3f4f6;
            --shadow: 0 10px 28px rgba(15, 118, 80, 0.08);
        }

        * {
            box-sizing: border-box;
        }

        body {
            font-family: Arial, sans-serif;
            background: var(--page-bg);
            margin: 0;
            padding: 34px 18px;
            color: var(--text-main);
        }

        .key-page {
            max-width: 1160px;
            margin: 0 auto;
        }

        .page-header,
        .section {
            background: var(--card-bg);
            border: 1px solid var(--border);
            border-radius: 18px;
            box-shadow: var(--shadow);
        }

        .page-header {
            padding: 26px 28px;
            display: flex;
            justify-content: space-between;
            gap: 18px;
            align-items: flex-start;
            margin-bottom: 18px;
        }

        .page-title-group h1 {
            margin: 0 0 8px;
            font-size: 30px;
            color: var(--primary-dark);
        }

        .page-title-group p {
            margin: 0;
            color: var(--text-muted);
            line-height: 1.6;
            max-width: 760px;
        }

        .page-actions {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
            justify-content: flex-end;
        }

        .section {
            padding: 24px 26px;
            margin-top: 18px;
        }

        .section-title {
            margin-bottom: 16px;
        }

        .section-title h2 {
            margin: 0;
            font-size: 22px;
            color: var(--primary-dark);
        }

        .description {
            color: var(--text-muted);
            line-height: 1.6;
            margin: 8px 0 18px;
        }

        .alert {
            padding: 13px 16px;
            border-radius: 12px;
            margin-bottom: 16px;
            font-weight: 600;
        }

        .alert-success {
            background: var(--success-soft);
            color: #166534;
            border: 1px solid #bbf7d0;
        }

        .alert-error {
            background: var(--danger-soft);
            color: #991b1b;
            border: 1px solid var(--danger-border);
        }

        .alert-warning {
            background: var(--warning-soft);
            color: #92400e;
            border: 1px solid #fcd34d;
        }

        label {
            display: block;
            font-weight: 700;
            color: #374151;
            margin: 12px 0 8px;
        }

        textarea,
        select,
        input[type="datetime-local"] {
            width: 100%;
            border: 1px solid #d1d5db;
            border-radius: 12px;
            padding: 12px 14px;
            font-size: 14px;
            background: #ffffff;
            color: var(--text-main);
            outline: none;
            transition: border-color 0.18s ease, box-shadow 0.18s ease;
        }

        textarea:focus,
        select:focus,
        input[type="datetime-local"]:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 4px rgba(31, 143, 95, 0.12);
        }

        .public-key-textarea {
            min-height: 190px;
            resize: vertical;
            font-family: Consolas, monospace;
            line-height: 1.45;
        }

        .note-textarea {
            min-height: 92px;
            resize: vertical;
            font-family: Arial, sans-serif;
            line-height: 1.5;
        }

        .btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border: none;
            border-radius: 999px;
            padding: 11px 18px;
            cursor: pointer;
            text-decoration: none;
            font-weight: 700;
            font-size: 14px;
            transition: transform 0.15s ease, box-shadow 0.15s ease, background 0.15s ease, border-color 0.15s ease;
            white-space: nowrap;
        }

        .btn:hover {
            transform: translateY(-1px);
        }

        .btn-primary {
            background: var(--primary);
            color: white;
            box-shadow: 0 8px 18px rgba(31, 143, 95, 0.22);
        }

        .btn-primary:hover {
            background: var(--primary-dark);
        }

        .btn-danger {
            background: var(--danger);
            color: white;
            box-shadow: 0 8px 18px rgba(220, 38, 38, 0.18);
        }

        .btn-danger:hover {
            background: var(--danger-dark);
        }

        .btn-secondary {
            background: #ffffff;
            color: var(--primary-dark);
            border: 1px solid #d1d5db;
        }

        .btn-secondary:hover {
            border-color: var(--primary);
            background: var(--primary-soft);
        }

        .active-key-card {
            background: #f8fffb;
            border: 1px solid var(--primary-border);
            border-radius: 16px;
            padding: 18px;
        }

        .status-line {
            margin: 0 0 12px;
        }

        .key-meta {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            margin: 12px 0 16px;
        }

        .meta-pill {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            background: #ffffff;
            border: 1px solid var(--primary-border);
            color: var(--primary-dark);
            border-radius: 999px;
            padding: 8px 12px;
            font-size: 13px;
            font-weight: 700;
        }

        .key-box {
            background: #0f172a;
            color: #f9fafb;
            border: 1px solid #111827;
            padding: 16px;
            border-radius: 14px;
            white-space: pre-wrap;
            word-break: break-all;
            font-family: Consolas, monospace;
            font-size: 13px;
            line-height: 1.55;
            max-height: 240px;
            overflow: auto;
        }

        .key-actions {
            margin-top: 14px;
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        .danger-panel {
            margin-top: 18px;
            padding: 18px;
            border: 1px solid var(--danger-border);
            background: #fff7f7;
            border-radius: 16px;
        }

        .danger-panel h3 {
            margin: 0 0 8px;
            color: #991b1b;
        }

        .danger-panel .description {
            margin-bottom: 14px;
        }

        .form-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px;
        }

        .form-full {
            grid-column: 1 / -1;
        }

        .small-muted {
            color: var(--text-muted);
            font-size: 13px;
            line-height: 1.5;
            margin-top: 6px;
        }

        .table-wrap {
            width: 100%;
            overflow-x: auto;
            border: 1px solid var(--border);
            border-radius: 16px;
            background: #ffffff;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            min-width: 980px;
            background: #ffffff;
        }

        th,
        td {
            border-bottom: 1px solid #edf0f2;
            padding: 14px 12px;
            text-align: left;
            vertical-align: top;
            font-size: 14px;
            line-height: 1.45;
        }

        th {
            background: #f0fdf6;
            color: var(--primary-dark);
            font-weight: 800;
            white-space: nowrap;
        }

        tr:last-child td {
            border-bottom: none;
        }

        tr:hover td {
            background: #f8fffb;
        }

        .status-badge {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border-radius: 999px;
            padding: 5px 10px;
            font-size: 12px;
            font-weight: 800;
            letter-spacing: 0.02em;
            white-space: nowrap;
        }

        .status-active {
            background: var(--success-soft);
            color: var(--success);
        }

        .status-revoked {
            background: var(--gray-soft);
            color: #4b5563;
        }

        .status-lost {
            background: var(--warning-soft);
            color: var(--warning);
        }

        .status-compromised {
            background: var(--danger-soft);
            color: var(--danger);
        }

        .empty {
            color: var(--text-muted);
            font-style: italic;
            background: #f9fafb;
            border: 1px dashed #d1d5db;
            border-radius: 14px;
            padding: 16px;
        }

        .note-cell {
            max-width: 260px;
            min-width: 220px;
            white-space: normal;
            word-break: break-word;
            overflow-wrap: anywhere;
            line-height: 1.5;
            color: var(--text-main);
            text-align: left;
            vertical-align: top;
        }

        @media (max-width: 768px) {
            body {
                padding: 18px 10px;
            }

            .page-header {
                flex-direction: column;
                padding: 20px;
            }

            .page-actions {
                justify-content: flex-start;
            }

            .section {
                padding: 20px;
            }

            .form-grid {
                grid-template-columns: 1fr;
            }

            table {
                min-width: 900px;
            }
        }
        .table-wrap tbody tr {
            min-height: unset;
        }

        .table-wrap td {
            height: auto;
            vertical-align: top;
        }

        .table-wrap td:not(.note-cell) {
            white-space: nowrap;
        }
        .status-lost {
            color: #d97706;
            font-weight: bold;
        }

        .status-compromised {
            color: #dc2626;
            font-weight: bold;
        }
    </style>
</head>

<body>
<div class="key-page">
    <div class="page-header">
        <div class="page-title-group">
            <h1>Quản lý khóa công khai</h1>
            <p>
                Lưu và quản lý public key dùng để xác thực chữ ký đơn hàng.
                Private key chỉ được giữ trong công cụ ký trên máy cá nhân, không nhập lên website.
            </p>
        </div>

        <div class="page-actions">
            <c:if test="${not empty returnUrl}">
                <a href="${pageContext.request.contextPath}${returnUrl}" class="btn btn-primary">
                    Tiếp tục ký đơn
                </a>
            </c:if>
            <a href="${pageContext.request.contextPath}/user/orders" class="btn btn-secondary">
                Quay lại đơn hàng
            </a>
        </div>
    </div>

    <c:if test="${not empty sessionScope.success}">
        <div id="keySaveStatus" class="alert alert-success">
            <c:out value="${sessionScope.success}"/>
        </div>
        <c:remove var="success" scope="session"/>
    </c:if>

    <c:if test="${toolSyncPending}">
        <div id="toolSyncPayload"
             hidden
             data-tool-port="<c:out value="${signingToolPort}"/>"
             data-key-id="<c:out value="${toolSyncKeyId}"/>"
             data-fingerprint="<c:out value="${toolSyncFingerprint}"/>"
             data-created-at="<c:out value="${toolSyncCreatedAt}"/>">
            <textarea id="toolSyncPublicKey"><c:out value="${toolSyncPublicKey}"/></textarea>
        </div>
    </c:if>

    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-error">
            <c:out value="${sessionScope.error}"/>
        </div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <div class="section">
        <div class="section-title">
            <h2>Tải công cụ ký đơn hàng</h2>
        </div>

        <p class="description">
            INOLA Signing Tool là ứng dụng chạy trên máy cá nhân của bạn để tạo khóa và ký đơn hàng.
            Website chỉ lưu Public Key, không lưu Private Key.
        </p>

        <div style="display: flex; gap: 12px; flex-wrap: wrap; margin: 16px 0;">
            <a href="${pageContext.request.contextPath}/downloads/INOLA-Signing-Tool.zip"
               class="btn btn-primary"
               download>
                <i class="fa-solid fa-download"></i>
                Tải INOLA Signing Tool
            </a>
        </div>

        <div class="description" style="line-height: 1.7;">
            <strong>Hướng dẫn sử dụng nhanh:</strong><br>
            1. Tải file ZIP và giải nén INOLA Signing Tool trên máy cá nhân.<br>
            2. Mở file <strong>INOLA-Signing-Tool.exe</strong> trong thư mục vừa giải nén.<br>
            3. Bấm <strong>Tạo cặp khóa mới</strong> trong Tool.<br>
            3. Bấm <strong>Lưu Private Key</strong> để lưu khóa bí mật trên máy của bạn.<br>
            4. Copy <strong>Public Key</strong> từ Tool và dán vào ô bên dưới.<br>
            5. Khi ký đơn hàng, mở Tool, tải Private Key và bấm <strong>Bật kết nối với website</strong>.
        </div>
    </div>

    <!-- Bảng cảnh báo an toàn Private Key -->
    <div style="background: #fffbeb; color: #b45309; border: 1px solid #fef3c7; border-radius: 12px; padding: 18px; margin-top: 18px; line-height: 1.6;">
        <h3 style="margin: 0 0 8px; color: #b45309; display: flex; align-items: center; gap: 8px; font-size: 17px; font-weight: bold;">
            <i class="fa-solid fa-triangle-exclamation"></i> CẢNH BÁO BẢO MẬT QUAN TRỌNG
        </h3>
        <ul style="margin: 0; padding-left: 20px; font-size: 14px;">
            <li><strong>TUYỆT ĐỐI KHÔNG</strong> dán hoặc chia sẻ <strong>Khóa riêng tư (Private Key)</strong> của bạn lên website.</li>
            <li>Chỉ sao chép và dán <strong>Khóa công khai (Public Key)</strong> (bắt đầu bằng <code>-----BEGIN PUBLIC KEY-----</code> và kết thúc bằng <code>-----END PUBLIC KEY-----</code>).</li>
            <li>Khóa riêng tư (Private Key) của bạn phải được lưu trữ tuyệt mật và an toàn trong thiết bị cá nhân của bạn.</li>
        </ul>
    </div>

    <div class="section">
        <c:choose>
            <c:when test="${sessionScope.keyChangePending == true}">
                <!-- BƯỚC 2: Form nhập mã OTP xác thực email -->
                <div class="section-title">
                    <h2>Xác thực thay đổi khóa công khai</h2>
                </div>
                <p class="description">
                    Một mã OTP 6 chữ số đã được gửi đến email đăng ký của bạn. Vui lòng nhập mã OTP để hoàn thành việc cập nhật Public Key.
                </p>
                <form method="post" action="${pageContext.request.contextPath}/signature/keys/verify-otp">
                    <c:if test="${not empty returnUrl}">
                        <input type="hidden" name="returnUrl" value="${returnUrl}">
                    </c:if>

                    <label for="otpCode">Mã OTP (6 chữ số)</label>
                    <input type="text" id="otpCode" name="otpCode" maxlength="6" required
                           placeholder="Nhập 6 chữ số OTP"
                           style="width: 100%; max-width: 250px; font-size: 18px; font-weight: bold; text-align: center; letter-spacing: 5px; padding: 10px; margin-bottom: 15px; border-radius: 8px; border: 1px solid #d1d5db;">

                    <!-- Đếm ngược 5 phút -->
                    <div id="otp-timer" style="color: #ef4444; font-weight: bold; margin-bottom: 15px; font-size: 14px;">
                        Mã OTP hết hạn sau: <span id="countdown">05:00</span>
                    </div>

                    <div style="display: flex; gap: 10px;">
                        <button type="submit" class="btn btn-primary">Xác nhận và lưu khóa</button>
                        <button type="submit" name="action" value="cancel" class="btn btn-secondary" onclick="return confirm('Bạn muốn hủy bỏ yêu cầu đổi khóa?')">Hủy bỏ</button>
                    </div>
                </form>

                <script>
                    (function() {
                        let duration = 300; // 5 phút bằng giây
                        const display = document.getElementById('countdown');
                        const timerInterval = setInterval(function () {
                            let minutes = Math.floor(duration / 60);
                            let seconds = duration % 60;

                            minutes = minutes < 10 ? "0" + minutes : minutes;
                            seconds = seconds < 10 ? "0" + seconds : seconds;

                            display.textContent = minutes + ":" + seconds;

                            if (--duration < 0) {
                                clearInterval(timerInterval);
                                display.textContent = "Hết hạn";
                                document.getElementById('otpCode').disabled = true;
                                document.getElementById('otp-timer').innerHTML = "Mã OTP đã hết hạn. Vui lòng nhấn Hủy bỏ để gửi lại yêu cầu mới.";
                            }
                        }, 1000);
                    })();
                </script>
            </c:when>

            <c:otherwise>
                <!-- BƯỚC 1: Form nhập Public Key ban đầu -->
                <div class="section-title">
                    <h2>Thêm / cập nhật public key</h2>
                </div>

                <p class="description">
                    Dán public key dạng PEM từ công cụ ký của bạn vào ô bên dưới. Hệ thống sẽ gửi email mã OTP để xác nhận trước khi cập nhật.
                </p>

                <form method="post" action="${pageContext.request.contextPath}/signature/keys/save">
                    <c:if test="${not empty returnUrl}">
                        <input type="hidden" name="returnUrl" value="${returnUrl}">
                    </c:if>

                    <label for="publicKey">Public key PEM</label>
                    <textarea id="publicKey" class="public-key-textarea" name="publicKey" required placeholder="-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A...
-----END PUBLIC KEY-----"></textarea>

            <div style="display: flex; gap: 10px; margin-top: 14px; flex-wrap: wrap;">
                <button type="button" class="btn btn-secondary" id="btnLoadPublicKey" onclick="loadPublicKeyFromTool()">
                    <i class="fa-solid fa-rotate"></i>
                    Tải Public Key từ Tool
                </button>
                <button type="submit" class="btn btn-primary">
                    Gửi mã OTP xác nhận
                </button>
            </div>
            <p id="publicKeyToolStatus" class="description" style="display: none; margin-top: 10px;"></p>
        </form>
            </c:otherwise>
        </c:choose>
    </div>


    <div class="section">
        <div class="section-title">
            <h2>Public key đang hoạt động</h2>
        </div>

        <c:choose>
            <c:when test="${not empty activeKey}">
                <div class="active-key-card">
                    <p class="status-line">
                        Trạng thái:
                        <span class="status-badge status-active">
                            <c:out value="${activeKey.keyStatus}"/>
                        </span>
                    </p>

                    <div class="key-meta">
                        <span class="meta-pill">
                            Thuật toán: <c:out value="${activeKey.keyAlgorithm}"/>
                        </span>
                        <span class="meta-pill">
                            Kích thước: <c:out value="${activeKey.keySize}"/>
                        </span>
                        <span class="meta-pill">
                            Ký: <c:out value="${activeKey.signatureAlgorithm}"/>
                        </span>
                    </div>

                    <div class="key-box"><c:out value="${activeKey.publicKey}"/></div>

                <div style="display: flex; gap: 20px; margin-top: 20px; flex-wrap: wrap;">
                    <!-- Form thu hồi thường -->
                    <div style="flex: 1; min-width: 280px; border: 1px solid #e2e8f0; padding: 16px; border-radius: 8px; background: #fff;">
                        <h4 style="margin-top:0; color: #374151;">Thu hồi khóa thông thường</h4>
                        <p style="color:#64748b; font-size:0.85rem; margin-bottom:12px;">Các đơn hàng cũ vẫn giữ trạng thái hợp lệ.</p>
                        <form method="post" action="${pageContext.request.contextPath}/signature/keys/revoke">
                            <input type="hidden" name="keyId" value="${activeKey.id}">
                            <button type="submit" class="btn btn-secondary" onclick="return confirm('Xác nhận thu hồi?');">Thu hồi thông thường</button>
                        </form>
                    </div>
                    <!-- Form báo cáo sự cố bảo mật -->
                    <div style="flex: 1; min-width: 280px; border: 1px solid #fee2e2; padding: 16px; border-radius: 8px; background: #fff5f5;">
                        <h4 style="margin-top: 0; color: #991b1b;">Báo cáo sự cố bảo mật</h4>
                        <form method="post" action="${pageContext.request.contextPath}/signature/keys/report">
                            <input type="hidden" name="keyId" value="${activeKey.id}">
                            <div style="margin-bottom: 10px;">
                                <label style="font-weight:normal; font-size:0.9rem; display: block; margin-bottom: 4px;">Loại sự cố:</label>
                                <select name="reportType" id="reportType" style="width:100%; padding:8px; border-radius:6px; border:1px solid #cbd5e1;" onchange="toggleCompromisedTime(this.value)">
                                    <option value="LOST">Báo mất khóa (LOST)</option>
                                    <option value="COMPROMISED">Báo lộ khóa (COMPROMISED)</option>
                                </select>
                            </div>
                            <div id="compromisedTimeGroup" style="margin-bottom: 10px; display: none;">
                                <label style="font-weight:normal; font-size:0.9rem; color:#b91c1c; display: block; margin-bottom: 4px;">Mốc thời gian bị lộ:</label>
                                <input type="datetime-local" name="compromisedFrom" id="compromisedFrom" style="width:100%; padding:8px; border-radius:6px; border:1px solid #cbd5e1;">
                            </div>
                            <div style="margin-bottom: 10px;">
                                <label style="font-weight:normal; font-size:0.9rem; display: block; margin-bottom: 4px;">Mô tả chi tiết:</label>
                                <textarea name="description" style="width: 100%; min-height: 70px; border-radius:6px; border:1px solid #cbd5e1; font-family: inherit; font-size: inherit;"></textarea>
                            </div>
                            <button type="submit" class="btn btn-danger" style="width: 100%;" onclick="return confirmIncidentReport();">Gửi báo cáo</button>
                        </form>
                    </div>
                </div>
            </c:when>

            <c:otherwise>
                <p class="empty">Hiện chưa có public key ACTIVE. Vui lòng thêm public key mới để có thể ký đơn hàng.</p>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="section">
        <div class="section-title">
            <h2>Lịch sử public key</h2>
        </div>

        <c:choose>
            <c:when test="${not empty keyHistory}">
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Trạng thái</th>
                            <th>Thuật toán</th>
                            <th>Ngày tạo</th>
                            <th>Ngày thu hồi</th>
                            <th>Thời điểm lộ</th>
                            <th>Ghi chú</th>
                        </tr>
                        </thead>

                        <tbody>
                        <c:forEach var="key" items="${keyHistory}">
                            <tr>
                                <td><c:out value="${key.id}"/></td>

                                <td>
                                    <c:choose>
                                        <c:when test="${key.keyStatus == 'ACTIVE'}">
                                            <span class="status-badge status-active">ACTIVE</span>
                                        </c:when>
                                        <c:when test="${key.keyStatus == 'LOST'}">
                                            <span class="status-badge status-lost">LOST</span>
                                        </c:when>
                                        <c:when test="${key.keyStatus == 'COMPROMISED'}">
                                            <span class="status-badge status-compromised">COMPROMISED</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-badge status-revoked">
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

                                <td>
                                    <c:out value="${key.createdAt}"/>
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${not empty key.revokedAt}">
                                            <c:out value="${key.revokedAt}"/>
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${not empty key.compromisedFrom}">
                                            <c:out value="${key.compromisedFrom}"/>
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>

                                <td class="note-cell"><c:choose><c:when test="${not empty key.note}"><c:out
                                        value="${key.note}"/></c:when><c:otherwise>-</c:otherwise></c:choose></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>

            <c:otherwise>
                <p class="empty">Chưa có lịch sử public key.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<script>
    const SIGNING_TOOL_API_BASE = 'http://127.0.0.1:9090';

    async function notifySigningToolAfterKeySave() {
        const payloadElement = document.getElementById('toolSyncPayload');
        if (!payloadElement) {
            return;
        }

        const statusElement = document.getElementById('keySaveStatus');
        const toolPort = payloadElement.dataset.toolPort || '9090';
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 5000);

        try {
            const response = await fetch('http://localhost:' + toolPort + '/public-key/saved', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    success: true,
                    keyId: payloadElement.dataset.keyId,
                    publicKey: document.getElementById('toolSyncPublicKey').value,
                    fingerprint: payloadElement.dataset.fingerprint,
                    createdAt: payloadElement.dataset.createdAt
                }),
                signal: controller.signal
            });
            const data = await response.json();
            if (!response.ok || data.success !== true || data.keyPairMatched !== true) {
                throw new Error(data.message || 'Signing Tool chưa xác nhận public key khớp.');
            }

            if (statusElement) {
                statusElement.className = 'alert alert-success';
                statusElement.textContent = 'Đã lưu public key trên web và đã đồng bộ với Signing Tool.';
            }
        } catch (error) {
            console.warn('Không thể thông báo public key đã lưu cho Signing Tool:', error);
            if (statusElement) {
                statusElement.className = 'alert alert-warning';
                statusElement.textContent = 'Đã lưu public key trên web, nhưng chưa thông báo được cho Signing Tool. '
                    + 'Hãy bấm Load Public Key From Web trên tool để đồng bộ lại.';
            }
        } finally {
            clearTimeout(timeoutId);
        }
    }

    async function loadPublicKeyFromTool() {
        const button = document.getElementById('btnLoadPublicKey');
        const publicKeyArea = document.getElementById('publicKey');
        const status = document.getElementById('publicKeyToolStatus');
        const originalHtml = button.innerHTML;

        button.disabled = true;
        button.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Đang tải...';
        status.style.display = 'block';
        status.style.color = '#64748b';
        status.textContent = 'Đang đọc Public Key hiện tại từ INOLA Signing Tool...';

        try {
            const response = await fetch(SIGNING_TOOL_API_BASE + '/api/public-key', {
                method: 'GET',
                cache: 'no-store'
            });
            const data = await response.json();

            if (!response.ok || data.success === false || !data.publicKey) {
                throw new Error(data.message || 'Tool chưa có Public Key.');
            }

            publicKeyArea.value = data.publicKey.trim();
            status.style.color = '#15803d';
            status.textContent = 'Đã tải Public Key mới nhất từ Tool. Hãy bấm “Lưu public key” để cập nhật trên website.';
        } catch (error) {
            status.style.color = '#b91c1c';
            status.textContent = 'Không tải được Public Key. Hãy mở Tool, tạo hoặc tải khóa, bật kết nối với website rồi thử lại. Chi tiết: ' + error.message;
        } finally {
            button.disabled = false;
            button.innerHTML = originalHtml;
        }
    }

    function toggleCompromisedTime(type) {
        var group = document.getElementById('compromisedTimeGroup');
        var input = document.getElementById('compromisedFrom');
        if (type === 'COMPROMISED') {
            group.style.display = 'block';
            input.required = true;
            var now = new Date();
            var offset = now.getTimezoneOffset() * 60000;
            input.value = (new Date(now - offset)).toISOString().slice(0, 16);
        } else {
            group.style.display = 'none';
            input.required = false;
            input.value = '';
        }
    }
    function confirmIncidentReport() {
        var type = document.getElementById('reportType').value;
        if (type === 'COMPROMISED') {
            return confirm('Cảnh báo: Báo cáo lộ khóa sẽ rà soát quét hồi tố toàn bộ đơn hàng! Bạn muốn tiếp tục?');
        }
        return confirm('Xác nhận báo mất khóa? Khóa sẽ bị hủy ngay.');
    }

    document.addEventListener("DOMContentLoaded", function () {
        notifySigningToolAfterKeySave();
        const reportType = document.getElementById("reportType");
        if (reportType) {
            toggleCompromisedTime(reportType.value);
        }
    });
</script>
</body>
</html>
