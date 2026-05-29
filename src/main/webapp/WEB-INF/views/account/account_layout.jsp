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
                <div class="account-avatarUrl">
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

                <div class="account-user__text">
                    <strong><c:out value="${currentUser.fullName}"/></strong>
                    <span><c:out value="${currentUser.email}"/></span>
                </div>

                <%--Change avatarUrl--%>
                <form id="avatarUrlForm"
                      class="avatarUrl-form"
                      action="${pageContext.request.contextPath}/user/profile"
                      method="post"
                      enctype="multipart/form-data">
                    <input type="hidden" name="action" value="change_avatar">
                    <input id="avatarUrlInput" class="sr-only" type="file" name="avatarFile" accept="image/*" required>
                    <button id="avatarUrlButton" class="avatarUrl-button" type="button">
                        <i class="fa-solid fa-camera"></i>
                        <span>Đổi ảnh</span>
                    </button>
                </form>
            </div>

            <nav class="account-nav" aria-label="Menu tài khoản">
                <a href="${pageContext.request.contextPath}/user/profile"
                   class="${requestScope.pageTitle eq 'Hồ sơ' ? 'is-active' : ''}">
                    <i class="fa-solid fa-user"></i>
                    <span>Hồ sơ</span>
                </a>
                <a href="${pageContext.request.contextPath}/user/orders"
                   class="${requestScope.pageTitle eq 'Đơn hàng' or requestScope.pageTitle eq 'Chi tiết đơn hàng' ? 'is-active' : ''}">
                    <i class="fa-solid fa-receipt"></i>
                    <span>Đơn hàng</span>
                </a>
                <a href="${pageContext.request.contextPath}/user/review"
                   class="${requestScope.pageTitle eq 'Đánh giá' ? 'is-active' : ''}">
                    <i class="fa-solid fa-star"></i>
                    <span>Đánh giá</span>
                </a>
                <a href="${pageContext.request.contextPath}/user/change-password"
                   class="${requestScope.pageTitle eq 'Đổi mật khẩu' ? 'is-active' : ''}">
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
             <jsp:include page="${requestScope.pageContent != null ? requestScope.pageContent : 'profile.jsp'}"/>
         </div>
     </div>
 </section>

 <!-- Avatar Upload Script -->
 <script>
     // Sidebar avatar upload
     const avatarButton = document.getElementById('avatarUrlButton');
     const avatarInput = document.getElementById('avatarUrlInput');
     const avatarForm = document.getElementById('avatarUrlForm');

     if (avatarButton && avatarInput) {
         avatarButton.addEventListener('click', function() {
             avatarInput.click();
         });

         avatarInput.addEventListener('change', function() {
             if (this.files.length > 0) {
                 const file = this.files[0];

                 // Validate file type
                 if (!file.type.startsWith('image/')) {
                     alert('Vui lòng chọn một tệp hình ảnh');
                     this.value = '';
                     return;
                 }

                 // Validate file size (5MB)
                 const maxSize = 5 * 1024 * 1024;
                 if (file.size > maxSize) {
                     alert('Hình ảnh không được vượt quá 5MB');
                     this.value = '';
                     return;
                 }

                 // Show preview
                 const reader = new FileReader();
                 reader.onload = function(e) {
                     const avatarImg = document.querySelector('.account-avatarUrl img');
                     if (avatarImg) {
                         avatarImg.src = e.target.result;
                     }
                 };
                 reader.readAsDataURL(file);

                 // Submit form after a short delay to ensure all processing is done
                 setTimeout(function() {
                     if (avatarForm) {
                         avatarForm.submit();
                     }
                 }, 100);
             }
         });
     }
 </script>
