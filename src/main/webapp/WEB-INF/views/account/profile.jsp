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
