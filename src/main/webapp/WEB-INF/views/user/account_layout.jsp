<%--
  Created by IntelliJ IDEA.
  User: vthiet
  Date: 5/21/26
  Time: 7:46 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="currentUser" value="${sessionScope.currentUser}"/>
<c:set var="profileMessage" value="${sessionScope.profileMessage}"/>
<c:set var="profileMessageType" value="${sessionScope.profileMessageType}"/>
<c:remove var="profileMessage" scope="session"/>
<c:remove var="profileMessageType" scope="session"/>

<section class="account-page">
  <div class="layout-shell account-shell">
    <aside class="account-sidebar" aria-label="Tài khoản">
      <div class="account-user">
        <div class="account-avatar">
          <c:choose>
            <c:when test="${not empty currentUser.avatar && currentUser.avatar ne 'default-avatar.png' && fn:startsWith(currentUser.avatar, 'http')}">
              <img src="${currentUser.avatar}" alt="Ảnh đại diện của ${currentUser.fullName}">
            </c:when>
            <c:when test="${not empty currentUser.avatar && currentUser.avatar ne 'default-avatar.png'}">
              <img src="${pageContext.request.contextPath}/assets/images/Avatar/${currentUser.avatar}"
                   alt="Ảnh đại diện của ${currentUser.fullName}">
            </c:when>
            <c:otherwise>
              <span aria-hidden="true"><i class="fa-solid fa-user"></i></span>
            </c:otherwise>
          </c:choose>
        </div>

        <div class="account-user__text">
          <strong><c:out value="${currentUser.fullName}"/></strong>
          <span><c:out value="${currentUser.email}"/></span>
        </div>

        <%--Change avatar--%>
        <form id="avatarForm"
              class="avatar-form"
              action="${pageContext.request.contextPath}/user/account/profile"
              method="post"
              enctype="multipart/form-data">
          <input type="hidden" name="action" value="change_avatar">
          <input id="avatarInput" class="sr-only" type="file" name="avatarFile" accept="image/*">
          <button id="avatarButton" class="avatar-button" type="button">
            <i class="fa-solid fa-camera"></i>
            <span>Đổi ảnh</span>
          </button>
        </form>
      </div>

      <nav class="account-nav" aria-label="Menu tài khoản">
        <a class="is-active" href="${pageContext.request.contextPath}/user/account/profile">
          <i class="fa-solid fa-user"></i>
          <span>Hồ sơ</span>
        </a>
        <a href="${pageContext.request.contextPath}/user/orders">
          <i class="fa-solid fa-receipt"></i>
          <span>Đơn hàng</span>
        </a>
        <a href="${pageContext.request.contextPath}/user/review">
          <i class="fa-solid fa-star"></i>
          <span>Đánh giá</span>
        </a>
        <a href="${pageContext.request.contextPath}/user/change-password">
          <i class="fa-solid fa-key"></i>
          <span>Đổi mật khẩu</span>
        </a>
        <a href="${pageContext.request.contextPath}/logout">
          <i class="fa-solid fa-arrow-right-from-bracket"></i>
          <span>Đăng xuất</span>
        </a>
      </nav>
    </aside>

    <div class="account-content">
      <jsp:include page="profile.jsp"/>
    </div>
  </div>
</section>
