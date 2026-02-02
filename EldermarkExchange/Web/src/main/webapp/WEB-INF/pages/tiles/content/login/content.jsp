<%@ page contentType="text/html;charset=UTF-8" language="java" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="auditReportContextPath" value="${auditReportContext}"/>

<spring:message code="auditReportLoginUrl" var="loginUrl"/>
<spring:message code="auditReportLogoutUrl" var="logoutUrl"/>

<!DOCTYPE html>
<html class="html">
<head xmlns:tiles="http://tiles.apache.org/tags-tiles">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <sec:csrfMetaTags />
    <title></title>

    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/bootstrap.min.css"/>"
          media="screen"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/bootstrap-theme.min.css"/>"
          media="screen"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/eldermark-wgt.css"/>" media="screen"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/eldermark-theme.css"/>" media="screen"/>
    <%--<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/care-coordination.css"/>" media="screen"/>--%>
    <link rel="shortcut icon" type="image/x-icon"  href="<c:url value="/resources/images/favicon.ico"/>"/>

    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/jquery-1.12.4.min.js"/>"></script>

    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/bootstrap.min.js"/>"></script>

    <script language="JavaScript" type="text/javascript">
        window.auditReportLoginUrl='/${auditReportContextPath}/${loginUrl}';
        window.auditReportLogoutUrl='/${auditReportContextPath}/${logoutUrl}';
    </script>

    <tiles:insertAttribute name="resources"/>
    <tiles:insertAttribute name="errors"/>

</head>
<body class="ldr-ui">
<tiles:insertAttribute name="appLayout"/>
</body>
</html>
