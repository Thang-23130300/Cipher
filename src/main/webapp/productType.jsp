<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<section class="section product-type-page">
    <div class="main-container">

        <div class="product-type-layout">

            <aside class="filter-sidebar">
                <h3>Bộ lọc</h3>

                <form method="get" action="${pageContext.request.contextPath}${data.listingAction}">
                    <c:choose>
                        <c:when test="${data.searchMode}">
                            <input type="hidden" name="keyword" value="${data.searchKeyword}"/>
                        </c:when>
                        <c:when test="${data.panelMode}">
                            <input type="hidden" name="panel" value="${data.panelSlug}"/>
                        </c:when>
                        <c:otherwise>
                            <input type="hidden" name="id" value="${data.category.id}"/>
                        </c:otherwise>
                    </c:choose>
                    <input type="hidden" name="page" value="1"/>

                    <div class="filter-group">
                        <label>Khoảng giá</label>
                        <select name="priceRange">
                            <option value="">Tất cả mức giá</option>
                            <option value="under-100" ${empty data.minPrice and data.maxPrice == 100000 ? 'selected' : ''}>Dưới 100.000đ</option>
                            <option value="100-300" ${data.minPrice == 100000 and data.maxPrice == 300000 ? 'selected' : ''}>100.000đ - 300.000đ</option>
                            <option value="300-500" ${data.minPrice == 300000 and data.maxPrice == 500000 ? 'selected' : ''}>300.000đ - 500.000đ</option>
                            <option value="over-500" ${data.minPrice == 500000 and empty data.maxPrice ? 'selected' : ''}>Trên 500.000đ</option>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label>Giá từ</label>
                        <input type="number" min="0" name="minPrice" value="${data.minPrice}"/>
                    </div>

                    <div class="filter-group">
                        <label>Đến</label>
                        <input type="number" min="0" name="maxPrice" value="${data.maxPrice}"/>
                    </div>

                    <div class="filter-group">
                        <label>Đánh giá</label>
                        <select name="rating">
                            <option value="">Tất cả</option>
                            <option value="5" ${data.rating == 5 ? 'selected' : ''}>⭐ 5 sao</option>
                            <option value="4" ${data.rating == 4 ? 'selected' : ''}>⭐ 4 sao trở lên</option>
                            <option value="3" ${data.rating == 3 ? 'selected' : ''}>⭐ 3 sao trở lên</option>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label>Sắp xếp</label>
                        <select name="sort">
                            <option value="popular" ${data.sortParam == 'popular' ? 'selected' : ''}>Bán chạy</option>
                            <option value="newest" ${data.sortParam == 'newest' ? 'selected' : ''}>Mới nhất</option>
                            <option value="price_asc" ${data.sortParam == 'price_asc' ? 'selected' : ''}>Giá tăng
                            </option>
                            <option value="price_desc" ${data.sortParam == 'price_desc' ? 'selected' : ''}>Giá giảm
                            </option>
                        </select>
                    </div>

                    <button type="submit">Áp dụng</button>
                </form>
            </aside>

            <main class="product-type-content">

                <c:if test="${not data.searchMode and not data.panelMode}">
                    <div class="category-banner">
                        <img src="${pageContext.request.contextPath}/assets/images/home_banner/${data.category.image}"
                             alt="${data.category.categoryName}">
                    </div>
                </c:if>

                <jsp:include page="/WEB-INF/views/product/product-type-results.jsp"/>

            </main>
        </div>
    </div>
</section>
