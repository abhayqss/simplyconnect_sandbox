<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <link rel="stylesheet" href="<c:url value="/resources/css/standard.css"/>" type="text/css"/>
    <link rel="shortcut icon" type="image/x-icon"  href="<c:url value="/resources/images/favicon.ico"/>"/>
</head>

<body>
    <div id="header">
        <h1>Exchange Reports</h1>
        <div id="navigation">
            <a href="<c:url value="/j_spring_security_logout"/>">Log Out</a>
        </div>
    </div>

    <ul>
        <li><a href="<c:url value="/logs"/>">Audit Log</a></li>
        <li><a href="<c:url value="/facilities"/>">Trading Organizations</a></li>
        <li><a href="<c:url value="/mpiReport"/>">MPI Report</a></li>
        <li><a href="<c:url value="/dataSyncReport"/>">DataSync Report</a></li>
        <li><a href="<c:url value="/dataSyncLogReport"/>">DataSync Log Report</a></li>
        <li><a href="<c:url value="/dataSyncStats"/>">DataSync Stats Report</a></li>
    </ul>
</body>
</html>