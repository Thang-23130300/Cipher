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

                    <c:set var="quickPriceRange"
                           value="${empty data.minPrice and data.maxPrice == 100000
                                   or data.minPrice == 100000 and data.maxPrice == 300000
                                   or data.minPrice == 300000 and data.maxPrice == 500000
                                   or data.minPrice == 500000 and empty data.maxPrice}"/>

                    <div class="filter-group filter-group--price">
                        <div class="filter-group__title">Khoảng giá</div>
                        <div class="filter-choice-list">
                            <label class="filter-choice">
                                <input type="radio" name="priceRange" value="under-100"
                                       data-min-price="" data-max-price="100000"
                                       ${empty data.minPrice and data.maxPrice == 100000 ? 'checked' : ''}/>
                                <span>Dưới 100.000đ</span>
                            </label>
                            <label class="filter-choice">
                                <input type="radio" name="priceRange" value="100-300"
                                       data-min-price="100000" data-max-price="300000"
                                       ${data.minPrice == 100000 and data.maxPrice == 300000 ? 'checked' : ''}/>
                                <span>100.000đ - 300.000đ</span>
                            </label>
                            <label class="filter-choice">
                                <input type="radio" name="priceRange" value="300-500"
                                       data-min-price="300000" data-max-price="500000"
                                       ${data.minPrice == 300000 and data.maxPrice == 500000 ? 'checked' : ''}/>
                                <span>300.000đ - 500.000đ</span>
                            </label>
                            <label class="filter-choice">
                                <input type="radio" name="priceRange" value="over-500"
                                       data-min-price="500000" data-max-price=""
                                       ${data.minPrice == 500000 and empty data.maxPrice ? 'checked' : ''}/>
                                <span>Trên 500.000đ</span>
                            </label>
                        </div>

                        <div class="filter-price-row">
                            <label>
                                <span>Từ</span>
                                <input type="number" min="0" name="minPrice"
                                       value="${quickPriceRange ? '' : data.minPrice}"
                                       placeholder="Tối thiểu"/>
                            </label>
                            <span class="filter-price-separator">-</span>
                            <label>
                                <span>Đến</span>
                                <input type="number" min="0" name="maxPrice"
                                       value="${quickPriceRange ? '' : data.maxPrice}"
                                       placeholder="Tối đa"/>
                            </label>
                        </div>
                    </div>

                    <div class="filter-group">
                        <div class="filter-group__title">Đánh giá</div>
                        <div class="filter-choice-list">
                            <label class="filter-choice">
                                <input type="radio" name="rating" value="" ${empty data.rating ? 'checked' : ''}/>
                                <span>Tất cả</span>
                            </label>
                            <label class="filter-choice">
                                <input type="radio" name="rating" value="5" ${data.rating == 5 ? 'checked' : ''}/>
                                <span>Từ 5 sao</span>
                            </label>
                            <label class="filter-choice">
                                <input type="radio" name="rating" value="4" ${data.rating == 4 ? 'checked' : ''}/>
                                <span>Từ 4 sao</span>
                            </label>
                            <label class="filter-choice">
                                <input type="radio" name="rating" value="3" ${data.rating == 3 ? 'checked' : ''}/>
                                <span>Từ 3 sao</span>
                            </label>
                        </div>
                    </div>

                    <div class="filter-group">
                        <label class="filter-group__title" for="productSort">Sắp xếp</label>
                        <select id="productSort" name="sort">
                            <option value="popular" ${data.sortParam == 'popular' ? 'selected' : ''}>Bán chạy</option>
                            <option value="price_asc" ${data.sortParam == 'price_asc' ? 'selected' : ''}>Giá thấp đến cao</option>
                            <option value="price_desc" ${data.sortParam == 'price_desc' ? 'selected' : ''}>Giá cao đến thấp</option>
                            <option value="newest" ${data.sortParam == 'newest' ? 'selected' : ''}>Mới nhất</option>
                        </select>
                    </div>

                    <button class="filter-apply-button" type="submit">Áp dụng</button>
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
