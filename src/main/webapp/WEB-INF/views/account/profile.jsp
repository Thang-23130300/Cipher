<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="currentUser" value="${sessionScope.currentUser}"/>
<c:set var="profileMessage" value="${sessionScope.profileMessage}"/>
<c:set var="profileMessageType" value="${sessionScope.profileMessageType}"/>
<c:remove var="profileMessage" scope="session"/>
<c:remove var="profileMessageType" scope="session"/>

<div class="account-heading">
    <div>
        <h1>Hồ sơ của tôi</h1>
    </div>
</div>

<c:if test="${not empty profileMessage}">
    <div class="profile-alert ${profileMessageType == 'error' ? 'profile-alert--error' : 'profile-alert--success'}">
        <c:out value="${profileMessage}"/>
    </div>
</c:if>

<section class="profile-panel" aria-labelledby="profile-info-title">
    <div class="panel-header">
        <h2 id="profile-info-title">Thông tin cá nhân</h2>
    </div>

    <form class="profile-form" action="${pageContext.request.contextPath}/user/profile" method="post">
        <input type="hidden" name="action" value="update_profile">

        <table class="form-table">
            <tbody>
            <tr>
                <th><label for="lastName">Họ và tên đệm</label></th>
                <td>
                    <input type="text" id="lastName" name="lastName" value="${currentUser.lastName}" required maxlength="50">
                </td>
            </tr>
            <tr>
                <th><label for="firstName">Tên</label></th>
                <td>
                    <input type="text" id="firstName" name="firstName" value="${currentUser.firstName}" required maxlength="50">
                </td>
            </tr>
            <tr>
                <th><label id="email-label">Email</label></th>
                <td>
                    <input type="email" aria-labelledby="email-label" value="${currentUser.email}" disabled>
                </td>
            </tr>
            <tr>
                <th><label for="phone">Số điện thoại</label></th>
                <td>
                    <input type="tel" id="phone" name="phone" value="${fn:escapeXml(currentUser.phone)}" required maxlength="20">
                </td>
            </tr>
            <tr>
                <th><label for="dob">Ngày sinh</label></th>
                <td>
                    <input type="date" id="dob" name="dob" value="${currentUser.dateOfBirth}">
                </td>
            </tr>
            <tr>
                <th><span>Giới tính</span></th>
                <td>
                    <div class="gender-options">
                        <label>
                            <input type="radio" name="gender" value="Nam" ${currentUser.gender == "MALE" ? 'checked' : ''}>
                            <span>Nam</span>
                        </label>
                        <label>
                            <input type="radio" name="gender" value="Nữ" ${currentUser.gender == "FEMALE" ? 'checked' : ''}>
                            <span>Nữ</span>
                        </label>
                        <label>
                            <input type="radio" name="gender" value="Khác" ${currentUser.gender == "OTHER" ? 'checked' : ''}>
                            <span>Khác</span>
                        </label>
                    </div>
                </td>
            </tr>
            </tbody>
        </table>

        <div class="form-actions">
            <button class="primary-button" type="submit">
                <i class="fa-solid fa-floppy-disk"></i>
                <span>Lưu</span>
            </button>
        </div>
    </form>
</section>

<section class="profile-panel" aria-labelledby="address-title">
    <div class="panel-header panel-header--split">
        <div>
            <h2 id="address-title">Địa chỉ nhận hàng</h2>
            <p>Địa chỉ mặc định sẽ được ưu tiên khi thanh toán.</p>
        </div>

        <c:if test="${param.view ne 'add-address'}">
            <a class="secondary-button" href="${pageContext.request.contextPath}/user/profile?view=add-address">
                <i class="fa-solid fa-plus"></i>
                <span>Thêm địa chỉ</span>
            </a>
        </c:if>
    </div>

    <c:choose>
        <c:when test="${param.view == 'add-address'}">
            <form class="profile-form address-form"
                  action="${pageContext.request.contextPath}/user/address/add"
                  method="post">
                <div class="form-grid">
                    <label class="field field--full">
                        <span>Địa chỉ chi tiết</span>
                        <input type="text" name="addressDetail" placeholder="Số nhà, tên đường..." required
                               maxlength="255">
                    </label>
                    <label class="field">
                        <span>Phường / Xã</span>
                        <input type="text" name="ward" required maxlength="255">
                    </label>
                    <label class="field">
                        <span>Quận / Huyện</span>
                        <input type="text" name="district" required maxlength="255">
                    </label>
                    <label class="field">
                        <span>Tỉnh / Thành phố</span>
                        <input type="text" name="city" required maxlength="255">
                    </label>
                </div>

                <div class="form-actions">
                    <button class="primary-button" type="submit">
                        <i class="fa-solid fa-floppy-disk"></i>
                        <span>Lưu địa chỉ</span>
                    </button>
                    <a class="text-button" href="${pageContext.request.contextPath}/user/profile">Hủy</a>
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
                                <c:if test="${addr.isDefault == 1}">
                                    <span>Mặc định</span>
                                </c:if>
                            </div>
                            <p>
                                <c:out value="${addr.addressDetail}"/>,
                                <c:out value="${addr.ward}"/>,
                                <c:out value="${addr.district}"/>,
                                <c:out value="${addr.city}"/>
                            </p>
                        </div>

                        <div class="address-card__actions">
                            <c:if test="${addr.isDefault == 0}">
                                <a href="${pageContext.request.contextPath}/user/address/default?id=${addr.id}">Đặt mặc
                                    định</a>
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
            </div>
        </c:otherwise>
    </c:choose>
</section>
