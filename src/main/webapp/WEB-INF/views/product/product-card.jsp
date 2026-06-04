<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:if test="${not empty p}">
    <article class="product-card">

        <a class="product-card__link"
           href="${pageContext.request.contextPath}/product?id=${p.id}"
           aria-label="Xem chi tiết ${p.name}">

            <div class="product-card__media">
                <c:url var="productImage" value="${p.image}"/>

                <img src="${productImage}"
                     alt="${p.name}"
                     loading="lazy"
                     decoding="async"/>

                <c:if test="${p.discountPercent != null && p.discountPercent > 0}">
                    <span class="product-card__badge">
                        SALE
                    </span>
                </c:if>
            </div>

            <div class="product-card__body">

                <h3 class="product-card__name">
                        ${p.name}
                </h3>

                <div class="product-card__price">
                    <c:choose>

                        <c:when test="${p.discountPercent != null && p.discountPercent > 0}">
                            <span class="current-price">
                                <fmt:formatNumber value="${p.discountedPrice}" groupingUsed="true"/> ₫
                            </span>

                            <span class="old-price">
                                <fmt:formatNumber value="${p.originalPrice}" groupingUsed="true"/> ₫
                            </span>

                            <span class="discount-tag">
                                ${p.discountPercent}%
                            </span>
                        </c:when>

                        <c:otherwise>
                            <span class="current-price">
                                <fmt:formatNumber value="${p.originalPrice}" groupingUsed="true"/> ₫
                            </span>
                        </c:otherwise>

                    </c:choose>
                </div>

                <div class="product-card__meta">
                    <span>
                        Đã bán ${p.totalSold}
                    </span>

                    <span class="rating" aria-label="Đánh giá trung bình ${p.avgRating}">
                        <i class="fa-solid fa-star" aria-hidden="true"></i>

                        <fmt:formatNumber value="${p.avgRating}"
                                          minFractionDigits="1"
                                          maxFractionDigits="1"/>
                    </span>
                </div>

            </div>

        </a>

    </article>
</c:if>