<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="currentUser" value="${sessionScope.currentUser}"/>

<div class="account-heading">
    <div>
        <c:choose>
            <c:when test="${not empty requestScope.order}">
                <h1>Chi tiết đơn hàng #${requestScope.order.id}</h1>
            </c:when>
            <c:otherwise>
                <h1>Đơn hàng của tôi</h1>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<c:if test="${not empty sessionScope.success}">
    <div class="alert alert-success"
         style="margin-bottom: 16px; padding: 12px 16px; border-radius: 8px; background: #e8f7ee; color: #176c35; border: 1px solid #b9e6c9;">
        <c:out value="${sessionScope.success}"/>
    </div>
    <c:remove var="success" scope="session"/>
</c:if>

<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-error"
         style="margin-bottom: 16px; padding: 12px 16px; border-radius: 8px; background: #fdecec; color: #9f1c1c; border: 1px solid #f3b8b8;">
        <c:out value="${sessionScope.error}"/>
    </div>
    <c:remove var="error" scope="session"/>
</c:if>

<!-- Order Detail Section -->
<c:if test="${not empty requestScope.order}">
    <section class="profile-panel">
        <div class="order-detail-header">
            <div class="order-detail-info">
                <div class="info-item">
                    <label>Mã đơn hàng</label>
                    <p>#${requestScope.order.id}</p>
                </div>
                <div class="info-item">
                    <label>Ngày đặt hàng</label>
                    <p><fmt:formatDate value="${requestScope.order.createdAt}" pattern="dd/MM/yyyy HH:mm"/></p>
                </div>
                <div class="info-item">
                    <label>Trạng thái</label>
                    <p>
                        <span class="order-status order-status--${requestScope.order.status}">
                            <c:choose>
                                <c:when test="${requestScope.order.status eq 'PENDING'}">Chờ xác nhận</c:when>
                                <c:when test="${requestScope.order.status eq 'CONFIRMED'}">Đã xác nhận</c:when>
                                <c:when test="${requestScope.order.status eq 'SHIPPED'}">Đang gửi</c:when>
                                <c:when test="${requestScope.order.status eq 'DELIVERED'}">Đã giao</c:when>
                                <c:when test="${requestScope.order.status eq 'CANCELLED'}">Đã hủy</c:when>
                                <c:otherwise>${requestScope.order.status}</c:otherwise>
                            </c:choose>
                        </span>
                    </p>
                </div>
            </div>
            <div class="order-detail-total">
                <span>Tổng tiền:</span>
                <strong>
                    <fmt:formatNumber value="${requestScope.order.totalAmount}" type="currency" currencySymbol="₫"/>
                </strong>
            </div>
        </div>

        <h3 style="margin-top: 24px; margin-bottom: 16px;">Sản phẩm</h3>
        <div class="order-items">
            <c:forEach items="${requestScope.orderItems}" var="item">
                <div class="order-item">
                    <div class="item-name">
                        <strong>${item.productName}</strong>
                    </div>
                    <div class="item-details">
                        <span>x${item.quantity}</span>
                        <span class="item-price">
                            <fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol="₫"/>
                        </span>
                    </div>
                </div>
            </c:forEach>
        </div>
    </section>

    <div style="margin-top: 16px;">
        <a href="${pageContext.request.contextPath}/user/orders" class="text-button">
            <i class="fa-solid fa-arrow-left"></i>
            <span>Quay lại danh sách đơn hàng</span>
        </a>
    </div>
</c:if>

<!-- Order List Section -->
<c:if test="${empty requestScope.order}">
    <c:choose>
        <c:when test="${not empty requestScope.orderList}">
            <section class="profile-panel orders-section">
                <div class="orders-list">
                    <c:forEach items="${requestScope.orderList}" var="order">
                        <article class="order-card">
                            <div class="order-card__header">
                                <div class="order-card__info">
                                    <h3>Đơn hàng #${order.id}</h3>
                                    <p class="order-date">
                                        <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                    </p>
                                </div>
                                <div class="order-card__status">
                                    <span class="order-status order-status--${order.status}">
                                        <c:choose>
                                            <c:when test="${order.status eq 'PENDING'}">Chờ xác nhận</c:when>
                                            <c:when test="${order.status eq 'CONFIRMED'}">Đã xác nhận</c:when>
                                            <c:when test="${order.status eq 'SHIPPED'}">Đang gửi</c:when>
                                            <c:when test="${order.status eq 'DELIVERED'}">Đã giao</c:when>
                                            <c:when test="${order.status eq 'CANCELLED'}">Đã hủy</c:when>
                                            <c:otherwise>${order.status}</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                            </div>

                            <div class="order-card__body">
                                <div class="order-total">
                                    <span>Tổng tiền:</span>
                                    <strong>
                                        <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫"/>
                                    </strong>
                                </div>
                            </div>

                            <div class="order-card__footer">
                                <a href="${pageContext.request.contextPath}/user/orders?action=detail&id=${order.id}"
                                   class="primary-button">
                                    <i class="fa-solid fa-eye"></i>
                                    <span>Xem chi tiết</span>
                                </a>
                            </div>
                        </article>
                    </c:forEach>
                </div>
            </section>
        </c:when>
        <c:otherwise>
            <section class="profile-panel">
                <div class="empty-state">
                    <i class="fa-regular fa-box-open"></i>
                    <p>Bạn chưa có đơn hàng nào.</p>
                </div>
            </section>
        </c:otherwise>
    </c:choose>
</c:if>


