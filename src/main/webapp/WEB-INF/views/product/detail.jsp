<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<input type="hidden" id="productId" value="${data.product.id}">
<input type="hidden" id="contextPath" value="${pageContext.request.contextPath}">

<main class="product-page">

    <section class="section">
        <div class="main-container">

            <div class="product-top">

                <div class="product-left-wrapper">
                    <div class="product-gallery">
                        <img src="${data.product.image}" alt="${data.product.name}">
                        <button type="button" class="btn-zoom">🔍</button>
                    </div>

                    <div class="store-note-block-left">
                        <p class="store-note-title">
                            <i class="fa-solid fa-box"></i> Lưu ý từ cửa hàng
                        </p>
                        <p class="store-note-text">
                            Với đơn hàng số lượng lớn (từ 20 sản phẩm trở lên),
                            vui lòng liên hệ hotline hoặc Zalo INOLA để nhận báo giá ưu đãi.
                        </p>
                    </div>
                </div>

                <!-- RIGHT -->
                <div class="product-right">

                    <h1 class="product-title">${data.product.name}</h1>

                    <div class="product-meta">
                        <span class="rating">⭐ ${data.avgRating}</span>
                        <span class="sold">Đã bán ${data.product.totalSold}</span>
                        <span class="stock">Còn ${data.product.stockQuantity} sản phẩm</span>
                    </div>

                    <div class="product-price">
                        <c:choose>
                            <c:when test="${data.promotion != null}">
                                <span class="old-price">
                                    <fmt:formatNumber value="${data.product.originalPrice}"/> ₫
                                </span>
                                <span class="sale-price">
                                    <fmt:formatNumber value="${data.discountedPrice}"/> ₫
                                </span>
                                <span class="discount">
                                    -${data.promotion.discountPercent}%
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span class="normal-price">
                                    <fmt:formatNumber value="${data.product.originalPrice}"/> ₫
                                </span>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <p class="short-description">
                        ${data.product.shortDescription}
                    </p>

                    <form action="${pageContext.request.contextPath}/cart/add"
                          method="post"
                          class="buy-form">
                        <input type="hidden" name="productId" value="${data.product.id}">

                        <div class="quantity-actions">
                            <div class="quantity-control">
                                <button type="button" class="qty-btn minus">-</button>
                                <input type="number"
                                       name="quantity"
                                       class="qty-input"
                                       value="1"
                                       min="1"
                                       max="${data.product.stockQuantity}">
                                <button type="button" class="qty-btn plus">+</button>
                            </div>

                            <button type="submit" class="add-cart">
                                <i class="fa-solid fa-cart-shopping"></i>
                                Thêm vào giỏ hàng
                            </button>
                        </div>

                        <button type="submit"
                                name="buyNow"
                                value="true"
                                class="buy-now-full">
                            MUA NGAY
                        </button>


                    </form>
                </div>

            </div>
        </div>
    </section>

    <div class="divider"></div>

    <section class="section product-info-section">
        <div class="main-container">

            <div class="info-header">
                <h2 class="info-title">THÔNG TIN CHI TIẾT</h2>
            </div>

            <div class="description-content">
                ${data.product.description}
            </div>

            <c:if test="${not empty data.specifications}">
                <table class="spec-table">
                    <c:forEach var="spec" items="${data.specifications}">
                        <tr>
                            <td class="spec-name">${spec.specName}</td>
                            <td>${spec.specValue}</td>
                        </tr>
                    </c:forEach>
                </table>
            </c:if>

            <c:if test="${empty data.specifications}">
                <p class="text-muted text-center">Chưa có thông tin.</p>
            </c:if>

        </div>
    </section>

    <div class="divider"></div>

    <section class="section product-reviews">
        <div class="main-container">

            <h2 class="reviews-main-title">Đánh giá</h2>

            <div class="review-content-wrap">

                <div class="review-mleft">
                    <div class="review-summary-block">
                        <p class="average-rating">${data.avgRating}</p>
                        <p class="review-count">
                            Dựa trên ${data.totalReviews} đánh giá
                        </p>

                        <div class="rating-breakdown">
                            <c:forEach begin="1" end="5" var="i">
                                <c:set var="star" value="${6 - i}"/>
                                <c:set var="count" value="${data.ratingCount[star]}"/>
                                <c:set var="percent"
                                       value="${data.totalReviews > 0
                                               ? (count * 100 / data.totalReviews)
                                               : 0}"/>
                                <div class="rating-row">
                                    <span>${star}</span>
                                    <i class="fa-solid fa-star"></i>
                                    <div class="rating-bar">
                                        <span style="width:${percent}%"></span>
                                    </div>
                                    <span>${percent}%</span>
                                </div>
                            </c:forEach>
                        </div>
                    </div>

                    <button class="review-action-btn">
                        Đánh giá sản phẩm
                    </button>
                </div>

                <div class="review-mright">

                    <div class="review-filter-bar">
                        <button class="filter-btn active" data-rating="">Tất cả</button>
                        <button class="filter-btn" data-rating="5">5 ★</button>
                        <button class="filter-btn" data-rating="4">4 ★</button>
                        <button class="filter-btn" data-rating="3">3 ★</button>
                        <button class="filter-btn" data-rating="2">2 ★</button>
                        <button class="filter-btn" data-rating="1">1 ★</button>

                        <select class="sort-select">
                            <option>Mới nhất</option>
                            <option>Cũ nhất</option>
                        </select>
                    </div>

                    <div id="reviewContainer"></div>
                    <button id="loadMoreReview" type="button">Xem thêm</button>

                </div>

            </div>

        </div>
    </section>

</main>

<section class="section related-products">
    <div class="main-container">

        <h2 class="related-title">Sản phẩm liên quan</h2>

        <div class="related-grid">
            <c:forEach var="p" items="${data.relatedProductCards}">
                <%@ include file="product-card.jsp" %>
            </c:forEach>
        </div>

    </div>
</section>

<div class="image-modal" id="imageModal">
    <div class="modal-overlay"></div>
    <div class="modal-content">
        <img id="zoomImage" src="" alt="Zoom image">
        <button class="modal-close">&times;</button>
    </div>
</div>

<div class="review-modal" id="reviewModal" >
    <div class="review-overlay"></div>

    <div class="review-box">
        <button class="review-close">&times;</button>

        <h3>Đánh giá sản phẩm</h3>

        <div class="rating-stars" data-rating="0">
            <i data-value="1">★</i>
            <i data-value="2">★</i>
            <i data-value="3">★</i>
            <i data-value="4">★</i>
            <i data-value="5">★</i>
        </div>

        <textarea id="reviewText"
                  maxlength="700"
                  placeholder="Viết nhận xét của bạn (tối đa 700 ký tự)">
        </textarea>

        <button type="button" class="submit-review">
            Gửi đánh giá
        </button>
    </div>
</div>
