<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${empty pageTitle ? 'INOLA' : pageTitle}"/></title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/images/logo.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/Base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout/footer.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <c:if test="${not empty pageCss}">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/${pageCss}">
    </c:if>
    <c:if test="${not empty contentCss}">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/${contentCss}">
    </c:if>
</head>
<body>
<jsp:include page="/WEB-INF/layout/header.jsp"/>

<main id="main-content">
    <jsp:include page="${contentPage}"/>
</main>

<jsp:include page="/WEB-INF/layout/footer.jsp"/>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/common.js"></script>
<c:if test="${not empty pageJs}">
    <script src="${pageContext.request.contextPath}/assets/js/${pageJs}"></script>
</c:if>
</body>
</html>
