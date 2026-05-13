<%--
  Created by IntelliJ IDEA.
  User: vthiet
  Date: 5/13/26
  Time: 8:27 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>${pageTitle} | Souvenir Shop</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/header-base.css">
    <%-- CSS, meta... --%>
</head>
<body>
<%@ include file="/WEB-INF/layout/header.jsp" %>

<main id="main-content">
    <jsp:include page="${contentPage}"/>
</main>

<%@ include file="/WEB-INF/layout/footer.jsp" %>
<%-- jQuery, scripts chung --%>

</body>
</html>
