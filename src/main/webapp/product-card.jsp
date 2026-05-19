<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:if test="${not empty p}">
    <article class="product-card">
        <a class="product-card__link" href="${pageContext.request.contextPath}/product?id=${p.id}"
           aria-label="Xem chi tiết ${p.name}">

            <div class="product-card__media img-box">
                <c:url var="productImage" value="${p.image}"/>
                <img src="${productImage}" alt="${p.name}" loading="lazy" decoding="async"/>

                <c:if test="${p.discountPercent != null && p.discountPercent > 0}">
                    <span class="product-card__badge badge-sale">-${p.discountPercent}%</span>
                </c:if>
            </div>

            <div class="product-card__body">
                <h3 class="product-card__name product-name">${p.name}</h3>

                <div class="product-card__meta product-sold">
                    <span>Đã bán ${p.totalSold}</span>
                    <span class="rating" aria-label="Đánh giá trung bình ${p.avgRating}">
                        <i class="fa-solid fa-star" aria-hidden="true"></i>
                        <fmt:formatNumber value="${p.avgRating}" minFractionDigits="1" maxFractionDigits="1"/>
                        <c:if test="${p.reviewCount > 0}">
                            <span class="review-count">(${p.reviewCount})</span>
                        </c:if>
                </span>
                </div>

                <div class="product-card__price price-container">
                    <c:choose>
                        <c:when test="${p.discountPercent != null && p.discountPercent > 0}">
                        <span class="old-price">
                            <fmt:formatNumber value="${p.originalPrice}" groupingUsed="true"/> ₫
                        </span>
                            <span class="current-price">
                            <fmt:formatNumber value="${p.discountedPrice}" groupingUsed="true"/> ₫
                        </span>
                        </c:when>

                        <c:otherwise>
                        <span class="current-price">
                            <fmt:formatNumber value="${p.originalPrice}" groupingUsed="true"/> ₫
                        </span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </a>
    </article>
</c:if>
