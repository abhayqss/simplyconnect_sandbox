<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
</head>

<body>
    <div id="header">
        <h1>ADT Messages Sender</h1>
        <div id="navigation">
            <a href="<c:url value="/j_spring_security_logout"/>">Log Out</a>
        </div>
    </div>
    <h3>Access Denied</h3>
    <p>You do not have enough permission to access the selected resource.</p>
</body>
</html>