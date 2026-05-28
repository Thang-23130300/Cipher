<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard | INOLA Admin</title>
    <link rel="stylesheet" href="${ctx}/assets/css/admin-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
</head>
<body>
<div class="admin-container">
    <jsp:include page="common/admin-sidebar.jsp">
        <jsp:param name="activePage" value="dashboard" />
    </jsp:include>

    <div class="admin-main">
        <jsp:include page="common/admin-topbar.jsp" />

        <main class="admin-content">
            <div class="content-header">
                <h1 class="content-title">Dashboard</h1>
                <div class="content-actions">
                    <div style="position: relative; display: inline-block;">
                        <button class="btn btn-secondary" onclick="toggleReportMenu(event)">
                            <svg class="btn-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                                <polyline points="7 10 12 15 17 10"/>
                                <line x1="12" x2="12" y1="15" y2="3"/>
                            </svg>
                            Xuất báo cáo
                        </button>
                        <div id="reportMenu" style="display: none; position: absolute; top: 100%; right: 0; margin-top: 8px; background: white; border: 1px solid #e5e7eb; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); min-width: 200px; z-index: 1000;">
                            <a href="${ctx}/admin/export-report?type=summary" style="display: block; padding: 12px 16px; text-decoration: none; color: #374151; border-bottom: 1px solid #e5e7eb;">
                                <i class="fas fa-chart-line" style="margin-right: 8px;"></i> Báo cáo tổng quan
                            </a>
                            <a href="${ctx}/admin/export-report?type=products" style="display: block; padding: 12px 16px; text-decoration: none; color: #374151; border-bottom: 1px solid #e5e7eb;">
                                <i class="fas fa-box" style="margin-right: 8px;"></i> Báo cáo sản phẩm
                            </a>
                            <a href="${ctx}/admin/export-report?type=orders" style="display: block; padding: 12px 16px; text-decoration: none; color: #374151; border-bottom: 1px solid #e5e7eb;">
                                <i class="fas fa-shopping-cart" style="margin-right: 8px;"></i> Báo cáo đơn hàng
                            </a>
                            <a href="${ctx}/admin/export-report?type=customers" style="display: block; padding: 12px 16px; text-decoration: none; color: #374151;">
                                <i class="fas fa-users" style="margin-right: 8px;"></i> Báo cáo khách hàng
                            </a>
                        </div>
                    </div>
                    <c:if test="${canCreateProduct}">
                        <button class="btn btn-primary" onclick="window.location.href='${ctx}/admin/products?action=add'">
                            <svg class="btn-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M5 12h14"/>
                                <path d="M12 5v14"/>
                            </svg>
                            Thêm sản phẩm
                        </button>
                    </c:if>
                </div>
            </div>

            <!-- Stats Cards -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-card-header">
                        <span class="stat-card-title">Doanh thu tháng</span>
                        <svg class="stat-card-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <line x1="12" x2="12" y1="2" y2="22"/>
                            <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
                        </svg>
                    </div>
                    <div class="stat-card-value">
                        <fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                    </div>
                    <p class="stat-card-description">+12.5% so với tháng trước</p>
                </div>

                <div class="stat-card">
                    <div class="stat-card-header">
                        <span class="stat-card-title">Đơn hàng mới</span>
                        <svg class="stat-card-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <circle cx="8" cy="21" r="1"/>
                            <circle cx="19" cy="21" r="1"/>
                            <path d="M2.05 2.05h2l2.66 12.42a2 2 0 0 0 2 1.58h9.78a2 2 0 0 0 1.95-1.57l1.65-7.43H5.12"/>
                        </svg>
                    </div>
                    <div class="stat-card-value">${totalOrders}</div>
                    <p class="stat-card-description">+3 đơn so với tháng trước</p>
                </div>

                <div class="stat-card">
                    <div class="stat-card-header">
                        <span class="stat-card-title">Sản phẩm</span>
                        <svg class="stat-card-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z"/>
                            <path d="M3 6h18"/>
                            <path d="M16 10a4 4 0 0 1-8 0"/>
                        </svg>
                    </div>
                    <div class="stat-card-value">${totalProducts}</div>
                    <p class="stat-card-description">Tổng số sản phẩm</p>
                </div>

                <div class="stat-card">
                    <div class="stat-card-header">
                        <span class="stat-card-title">Khách hàng</span>
                        <svg class="stat-card-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
                            <circle cx="9" cy="7" r="4"/>
                            <path d="M22 21v-2a4 4 0 0 0-3-3.87"/>
                            <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                        </svg>
                    </div>
                    <div class="stat-card-value">${totalCustomers}</div>
                    <p class="stat-card-description">Tổng số khách hàng</p>
                </div>
            </div>

            <!-- Tabs -->
            <div class="tabs">
                <div class="tabs-list">
                    <button class="tabs-trigger active" data-tab="overview">Tổng quan</button>
                    <button class="tabs-trigger" data-tab="orders">Đơn hàng gần đây</button>
                    <button class="tabs-trigger" data-tab="products">Sản phẩm bán chạy</button>
                </div>

                <!-- Overview Tab -->
                <div class="tabs-content active" id="overview">
                    <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 1rem;">
                        <div class="card">
                            <div class="card-header">
                                <h3 class="card-title">Doanh thu theo tháng</h3>
                                <p class="card-description">Biểu đồ doanh thu 6 tháng gần đây</p>
                            </div>
                            <div class="card-content">
                                <canvas id="revenueChart" style="height: 300px;"></canvas>
                            </div>
                        </div>

                        <div class="card">
                            <div class="card-header">
                                <h3 class="card-title">Đơn hàng gần đây</h3>
                                <p class="card-description">5 đơn hàng mới nhất</p>
                            </div>
                            <div class="card-content">
                                <c:choose>
                                    <c:when test="${empty recentOrders}">
                                        <div style="padding: 2rem; text-align: center; color: var(--muted-foreground);">
                                            <p>Chưa có đơn hàng nào</p>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach items="${recentOrders}" var="order" varStatus="status">
                                            <div style="display: flex; align-items: center; gap: 1rem; padding: 0.75rem 0; border-bottom: 1px solid var(--border);">
                                                <div style="flex: 1;">
                                                    <p style="font-weight: 500; font-size: 0.875rem;">#${order.id}</p>
                                                    <p style="font-size: 0.75rem; color: var(--muted-foreground);">${order.customerName}</p>
                                                </div>
                                                <div style="text-align: right;">
                                                    <p style="font-weight: 600; font-size: 0.875rem;">
                                                        <fmt:formatNumber value="${order.totalAmount}" pattern="#,###"/>₫
                                                    </p>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Orders Tab -->
                <div class="tabs-content" id="orders">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">Đơn hàng gần đây</h3>
                            <p class="card-description">Danh sách đơn hàng mới nhất</p>
                        </div>
                        <div class="card-content">
                            <c:choose>
                                <c:when test="${empty recentOrders}">
                                    <div style="padding: 3rem; text-align: center; color: var(--muted-foreground);">
                                        <p>Chưa có đơn hàng nào</p>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <table class="data-table">
                                        <thead>
                                        <tr>
                                            <th>Mã đơn</th>
                                            <th>Khách hàng</th>
                                            <th>Ngày đặt</th>
                                            <th>Tổng tiền</th>
                                            <th>Trạng thái</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach items="${recentOrders}" var="order">
                                            <tr>
                                                <td>#${order.id}</td>
                                                <td>${order.customerName}</td>
                                                <td><fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                                <td><fmt:formatNumber value="${order.totalAmount}" pattern="#,###"/>₫</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${order.status == 'Đang xử lý'}">
                                                            <span class="badge badge-warning">${order.status}</span>
                                                        </c:when>
                                                        <c:when test="${order.status == 'Đang giao'}">
                                                            <span class="badge badge-info">${order.status}</span>
                                                        </c:when>
                                                        <c:when test="${order.status == 'Hoàn thành'}">
                                                            <span class="badge badge-success">${order.status}</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge badge-secondary">${order.status}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <!-- Products Tab -->
                <div class="tabs-content" id="products">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">Sản phẩm bán chạy</h3>
                            <p class="card-description">Top 10 sản phẩm bán chạy nhất</p>
                        </div>
                        <div class="card-content">
                            <table class="data-table">
                                <thead>
                                <tr>
                                    <th>Sản phẩm</th>
                                    <th>Danh mục</th>
                                    <th>Giá</th>
                                    <th>Đã bán</th>
                                    <th>Tồn kho</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${topProducts}" var="product">
                                    <tr>
                                        <td>${product.name}</td>
                                        <td>${product.categoryName}</td>
                                        <td><fmt:formatNumber value="${product.originalPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                        <td>${product.totalSold}</td>
                                        <td>${product.stockQuantity}</td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>

<script>
    // Tab switching
    document.querySelectorAll('.tabs-trigger').forEach(trigger => {
        trigger.addEventListener('click', function() {
            const tabId = this.getAttribute('data-tab');

            // Remove active class from all triggers and contents
            document.querySelectorAll('.tabs-trigger').forEach(t => t.classList.remove('active'));
            document.querySelectorAll('.tabs-content').forEach(c => c.classList.remove('active'));

            // Add active class to clicked trigger and corresponding content
            this.classList.add('active');
            document.getElementById(tabId).classList.add('active');
        });
    });

    // Report menu toggle
    function toggleReportMenu(event) {
        event.stopPropagation();
        const menu = document.getElementById('reportMenu');
        menu.style.display = menu.style.display === 'none' ? 'block' : 'none';
    }

    // Close menu when clicking outside
    document.addEventListener('click', function(event) {
        const menu = document.getElementById('reportMenu');
        if (menu.style.display === 'block') {
            menu.style.display = 'none';
        }
    });

    // Prevent menu from closing when clicking inside
    document.getElementById('reportMenu')?.addEventListener('click', function(event) {
        event.stopPropagation();
    });
</script>

<!-- Chart.js -->
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<script>
    // Revenue Chart
    const ctx = document.getElementById('revenueChart');
    if (ctx) {
        // Get data from JSP
        const revenueData = [
            <c:forEach items="${monthlyRevenues}" var="revenue" varStatus="status">
            ${revenue}<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];

        // Get month labels (last 6 months)
        const monthNames = ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6',
            'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12'];
        const currentMonth = new Date().getMonth();
        const labels = [];
        for (let i = 5; i >= 0; i--) {
            const monthIndex = (currentMonth - i + 12) % 12;
            labels.push(monthNames[monthIndex]);
        }

        new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Doanh thu (₫)',
                    data: revenueData,
                    borderColor: 'rgb(52, 152, 219)',
                    backgroundColor: 'rgba(52, 152, 219, 0.1)',
                    tension: 0.4,
                    fill: true,
                    pointRadius: 5,
                    pointHoverRadius: 7,
                    pointBackgroundColor: 'rgb(52, 152, 219)',
                    pointBorderColor: '#fff',
                    pointBorderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: true,
                        position: 'top',
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                let label = context.dataset.label || '';
                                if (label) {
                                    label += ': ';
                                }
                                label += new Intl.NumberFormat('vi-VN').format(context.parsed.y) + '₫';
                                return label;
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return new Intl.NumberFormat('vi-VN', {
                                    notation: 'compact',
                                    compactDisplay: 'short'
                                }).format(value) + '₫';
                            }
                        }
                    }
                }
            }
        });
    }
</script>
</body>
</html>
