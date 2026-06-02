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

<section class="account-page">
    <div class="layout-shell account-shell">
        <jsp:include page="account_sidebar.jsp"/>

         <div class="account-content">
             <jsp:include page="${requestScope.pageContent != null ? requestScope.pageContent : 'profile.jsp'}"/>
         </div>
     </div>
 </section>
