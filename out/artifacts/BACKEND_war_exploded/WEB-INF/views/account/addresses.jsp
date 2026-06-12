<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="currentUser" value="${sessionScope.currentUser}"/>
<c:set var="profileMessage" value="${sessionScope.profileMessage}"/>
<c:set var="profileMessageType" value="${sessionScope.profileMessageType}"/>
<c:remove var="profileMessage" scope="session"/>
<c:remove var="profileMessageType" scope="session"/>

<div class="account-heading">
    <div>
        <h1>Địa chỉ nhận hàng</h1>
    </div>
</div>

<c:if test="${not empty profileMessage}">
    <div class="profile-alert ${profileMessageType == 'error' ? 'profile-alert--error' : 'profile-alert--success'}">
        <c:out value="${profileMessage}"/>
    </div>
</c:if>

<section class="profile-panel" aria-labelledby="address-title">
    <div class="panel-header panel-header--split">
        <div>
            <h2 id="address-title">Sổ địa chỉ</h2>
            <p>Địa chỉ mặc định sẽ được ưu tiên khi thanh toán.</p>
        </div>

        <c:if test="${requestScope.addressMode ne 'add'}">
            <a class="secondary-button" href="${pageContext.request.contextPath}/user/address/add">
                <i class="fa-solid fa-plus"></i>
                <span>Thêm địa chỉ</span>
            </a>
        </c:if>
    </div>

    <c:choose>
        <c:when test="${requestScope.addressMode == 'add'}">
            <form class="profile-form address-form"
                  action="${pageContext.request.contextPath}/user/address/add"
                  method="post"
                  data-address-form
                  data-wards-url="${pageContext.request.contextPath}/user/address/wards">
                <div class="form-grid">
                    <label class="field">
                        <span>Tỉnh / Thành phố</span>
                        <select id="provinceSelect" name="provinceCode" required>
                            <option value="">Chọn tỉnh/thành phố</option>
                            <c:forEach items="${provinceOptions}" var="province">
                                <option value="${province.code}">
                                    <c:out value="${empty province.fullName ? province.name : province.fullName}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </label>
                    <label class="field">
                        <span>Phường / Xã</span>
                        <select id="wardSelect" name="wardCode" required disabled>
                            <option value="">Chọn phường/xã</option>
                        </select>
                    </label>
                    <label class="field field--full">
                        <span>Địa chỉ chi tiết</span>
                        <input type="text" name="addressDetail" placeholder="Số nhà, tên đường..." required
                               maxlength="255">
                    </label>
                </div>

                <div class="form-actions">
                    <button class="primary-button" type="submit">
                        <i class="fa-solid fa-floppy-disk"></i>
                        <span>Lưu địa chỉ</span>
                    </button>
                    <a class="text-button" href="${pageContext.request.contextPath}/user/address">Hủy</a>
                </div>
            </form>
        </c:when>

        <c:when test="${not empty listAddr}">
            <div class="address-list">
                <c:forEach items="${listAddr}" var="addr">
                    <article class="address-card">
                        <div class="address-card__main">
                            <div class="address-card__title">
                                <strong><c:out value="${currentUser.fullName}"/></strong>
                                <c:if test="${addr.isDefault()}">
                                    <span>Mặc định</span>
                                </c:if>
                            </div>
                            <p>
                                <c:out value="${addr.addressDetail}"/>,
                                <c:out value="${addr.ward}"/>,
                                <c:out value="${addr.district}"/>,
                                <c:out value="${addr.province}"/>
                            </p>
                        </div>

                        <div class="address-card__actions">
                            <c:if test="${not addr.isDefault()}">
                                <a href="${pageContext.request.contextPath}/user/address/default?id=${addr.id}">
                                    Đặt mặc định
                                </a>
                            </c:if>
                            <a class="danger-link"
                               href="${pageContext.request.contextPath}/user/address/delete?id=${addr.id}"
                               data-confirm="Bạn có chắc muốn xóa địa chỉ này?">Xóa</a>
                        </div>
                    </article>
                </c:forEach>
            </div>
        </c:when>

        <c:otherwise>
            <div class="empty-state">
                <i class="fa-regular fa-map"></i>
                <p>Bạn chưa có địa chỉ nào.</p>
                <a class="secondary-button" href="${pageContext.request.contextPath}/user/address/add">
                    <i class="fa-solid fa-plus"></i>
                    <span>Thêm địa chỉ</span>
                </a>
            </div>
        </c:otherwise>
    </c:choose>
</section>
