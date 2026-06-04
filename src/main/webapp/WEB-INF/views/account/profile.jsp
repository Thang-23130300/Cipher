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
        <p>Quản lý thông tin hồ sơ để bảo mật tài khoản</p>
    </div>
</div>

<c:if test="${not empty profileMessage}">
    <div class="profile-alert ${profileMessageType == 'error' ? 'profile-alert--error' : 'profile-alert--success'}">
        <c:out value="${profileMessage}"/>
    </div>
</c:if>

<section class="profile-panel profile-panel--split" aria-labelledby="profile-info-title">
    <div class="panel-header">
        <h2 id="profile-info-title">Thông tin cá nhân</h2>
    </div>

    <div class="profile-grid">
        <div class="profile-info">
            <div class="profile-static">
                <span class="profile-static__label">Tên đăng nhập</span>
                <span class="profile-static__value"><c:out value="${currentUser.email}"/></span>
            </div>

            <form class="profile-form profile-form--list" action="${pageContext.request.contextPath}/user/profile" method="post">
                <input type="hidden" name="action" value="update_profile">

                <div class="form-grid">
                    <label class="field">
                        <span>Họ và tên đệm</span>
                        <input type="text" id="lastName" name="lastName" value="${currentUser.lastName}" required maxlength="50">
                    </label>
                    <label class="field">
                        <span>Tên</span>
                        <input type="text" id="firstName" name="firstName" value="${currentUser.firstName}" required maxlength="50">
                    </label>
                    <label class="field">
                        <span>Email</span>
                        <input type="email" value="${currentUser.email}" disabled>
                    </label>
                    <label class="field">
                        <span>Số điện thoại</span>
                        <input type="tel" id="phone" name="phone" value="${fn:escapeXml(currentUser.phone)}" required maxlength="20">
                    </label>
                    <label class="field">
                        <span>Ngày sinh</span>
                        <input type="date" id="dob" name="dob" value="${currentUser.dateOfBirth}">
                    </label>
                    <fieldset class="gender-field field--full">
                        <legend>Giới tính</legend>
                        <label>
                            <input type="radio" name="gender" value="Nam" ${currentUser.gender == 'MALE' ? 'checked' : ''}>
                            <span>Nam</span>
                        </label>
                        <label>
                            <input type="radio" name="gender" value="Nữ" ${currentUser.gender == 'FEMALE' ? 'checked' : ''}>
                            <span>Nữ</span>
                        </label>
                        <label>
                            <input type="radio" name="gender" value="Khác" ${currentUser.gender == 'OTHER' ? 'checked' : ''}>
                            <span>Khác</span>
                        </label>
                    </fieldset>
                </div>

                <div class="form-actions">
                    <button class="primary-button" type="submit">
                        <span>Lưu</span>
                    </button>
                </div>
            </form>
        </div>

        <aside class="profile-avatar" aria-label="Ảnh đại diện">
            <div class="profile-avatar__image">
                <c:choose>
                    <c:when test="${not empty currentUser.avatarUrl && currentUser.avatarUrl ne 'default-avatarUrl.png' && fn:startsWith(currentUser.avatarUrl, 'http')}">
                        <img src="${currentUser.avatarUrl}" alt="Ảnh đại diện của ${currentUser.fullName}">
                    </c:when>
                    <c:when test="${not empty currentUser.avatarUrl && currentUser.avatarUrl ne 'default-avatarUrl.png'}">
                        <img src="${pageContext.request.contextPath}/assets/images/avatarUrl/${currentUser.avatarUrl}"
                             alt="Ảnh đại diện của ${currentUser.fullName}">
                    </c:when>
                    <c:otherwise>
                        <span aria-hidden="true"><i class="fa-solid fa-user"></i></span>
                    </c:otherwise>
                </c:choose>
            </div>

            <form class="profile-avatar__form"
                  action="${pageContext.request.contextPath}/user/upload-avatar"
                  method="post"
                  enctype="multipart/form-data">
                <label class="profile-avatar__file">
                    <span>Chọn ảnh</span>
                    <input type="file" name="avatarFile" accept="image/*" required>
                </label>
                <button class="secondary-button" type="submit">Tải lên</button>
            </form>

            <p class="profile-avatar__hint">Dung lượng file tối đa 5 MB<br>Định dạng: JPEG, PNG</p>
        </aside>
    </div>
</section>
