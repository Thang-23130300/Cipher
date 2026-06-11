<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activePage" value="logs" scope="request" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Logs | INOLA Admin</title>
    <link rel="stylesheet" href="${ctx}/admin/template/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/admin/template/assets/vendors/bootstrap-icons/bootstrap-icons.css">
    <link rel="stylesheet" href="${ctx}/admin/template/assets/css/style.css">
    <link rel="stylesheet" href="${ctx}/assets/css/admin-dashboard.css">
    <link rel="stylesheet" href="${ctx}/assets/css/admin-pages.css">
    <style>
        .log-toolbar {
            display: grid;
            gap: 12px;
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
        }
        .log-row {
            font-family: "SFMono-Regular", Consolas, "Liberation Mono", monospace;
            font-size: 0.88rem;
            white-space: nowrap;
        }
        .log-message {
            white-space: normal;
            word-break: break-word;
        }
        .badge-log-info { background: #0d6efd; }
        .badge-log-warn { background: #fd7e14; }
        .badge-log-error { background: #dc3545; }
        .badge-log-debug { background: #6c757d; }
    </style>
</head>
<body>
<div class="admin-shell">
    <div class="sidebar-backdrop" data-sidebar-close></div>
    <jsp:include page="/admin/common/admin-sidebar.jsp" />
    <div class="admin-main">
        <jsp:include page="/admin/common/admin-topbar.jsp" />
        <main class="admin-content container-fluid py-4">
            <div class="page-heading mb-4">
                <h1 class="h3 mb-1">System Logs</h1>
                <p class="text-muted mb-0">Search by level, keyword, or request metadata.</p>
            </div>

            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <form method="get" action="${ctx}/admin/logs" class="log-toolbar">
                        <div>
                            <label class="form-label" for="level">Level</label>
                            <select class="form-select" id="level" name="level">
                                <option value="" ${empty selectedLevel ? 'selected' : ''}>All levels</option>
                                <option value="INFO" ${selectedLevel == 'INFO' ? 'selected' : ''}>INFO</option>
                                <option value="WARN" ${selectedLevel == 'WARN' ? 'selected' : ''}>WARN</option>
                                <option value="ERROR" ${selectedLevel == 'ERROR' ? 'selected' : ''}>ERROR</option>
                                <option value="DEBUG" ${selectedLevel == 'DEBUG' ? 'selected' : ''}>DEBUG</option>
                            </select>
                        </div>
                        <div>
                            <label class="form-label" for="q">Search</label>
                            <input type="search" class="form-control" id="q" name="q" value="${query}" placeholder="Order, user, controller, message">
                        </div>
                        <div>
                            <label class="form-label" for="limit">Limit</label>
                            <input type="number" class="form-control" id="limit" name="limit" value="${limit}" min="25" max="500">
                        </div>
                        <div class="d-flex align-items-end">
                            <button type="submit" class="btn btn-primary w-100">Filter</button>
                        </div>
                    </form>
                </div>
            </div>

            <div class="d-flex justify-content-between align-items-center mb-3">
                <div class="text-muted">Showing ${logCount} matching log entries</div>
            </div>

            <div class="card shadow-sm">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                        <tr>
                            <th>Time</th>
                            <th>Level</th>
                            <th>Logger</th>
                            <th>User</th>
                            <th>Message</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${entries}" var="entry">
                            <tr>
                                <td class="log-row">${entry.timestamp}</td>
                                <td>
                                    <c:set var="levelClass" value="badge-log-info" />
                                    <c:if test="${entry.level == 'WARN'}"><c:set var="levelClass" value="badge-log-warn" /></c:if>
                                    <c:if test="${entry.level == 'ERROR'}"><c:set var="levelClass" value="badge-log-error" /></c:if>
                                    <c:if test="${entry.level == 'DEBUG'}"><c:set var="levelClass" value="badge-log-debug" /></c:if>
                                    <span class="badge ${levelClass}">${entry.level}</span>
                                </td>
                                <td class="log-row">${entry.logger}</td>
                                <td class="log-row">${entry.user}</td>
                                <td class="log-message">${entry.message}</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty entries}">
                            <tr>
                                <td colspan="5" class="text-center text-muted py-5">No log entries matched your filters.</td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </main>
    </div>
</div>
<script src="${ctx}/admin/template/assets/vendors/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${ctx}/admin/template/assets/js/main.js"></script>
</body>
</html>
