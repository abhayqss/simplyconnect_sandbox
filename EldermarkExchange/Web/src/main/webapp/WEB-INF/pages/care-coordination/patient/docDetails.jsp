<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: nsauchanka
  Date: 18-Jul-18
  Time: 17:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${title}</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/ccdFreeText.css"/>" media="screen"/>
</head>
<body>
    <header>${fileName}</header>
    <c:out value="${content}" escapeXml="false"/>
</body>
</html>
