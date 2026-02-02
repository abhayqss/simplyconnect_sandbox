<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags" %>

<html>
<head>
    <link rel="stylesheet" href="<c:url value="/resources/css/standard.css"/>" type="text/css"/>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.9.1/themes/smoothness/jquery-ui.css">
    <script src="//code.jquery.com/jquery-1.10.2.js"></script>
    <script src="//code.jquery.com/ui/1.11.2/jquery-ui.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/hightlight.js"></script>
    <link rel="shortcut icon" type="image/x-icon"  href="<c:url value="/resources/images/favicon.ico"/>"/>
</head>

<body>
    <div id="header">
        <h1>Trading Organizations</h1>
        <div id="navigation">
            <sec:authorize access="hasRole('ROLE_SUPER_MANAGER')">
                <a href="<c:url value="/home"/>">Home</a> |
            </sec:authorize>
            <a href="<c:url value="/j_spring_security_logout"/>">Log Out</a>
        </div>
    </div>

    <form:form method="GET" commandName="reportFilter">
        <div id="container">

            <c:url var="firstUrl" value="/${reportUrl}/1"/>

            <div class="left">
                <label for="states">State: </label>
                <form:select id="states" path="state">
                    <form:option value="all" label="All States"/>
                    <form:options items="${states}" itemValue="name" itemLabel="name"/>
                </form:select>

                <label for="companies">Company: </label>
                <form:select id="companies" path="company">
                    <form:option value="" label="All Companies"/>
                    <form:options items="${companies}" itemValue="id" itemLabel="name"/>
                </form:select>

                <c:url var="refreshIcon" value="/resources/images/refresh.png"/>
                <form:button id="refresh" onclick="form.action='${firstUrl}';" class="transparent">
                    <img src="${refreshIcon}"/>
                </form:button>
            </div>

            <div class="right">
                <c:url var="exportUrl" value="/${reportUrl}/exportToExcel"/>
                <form:button id="export" onclick="form.action='${exportUrl}';">
                    Export to Excel
                </form:button>
            </div>

            <table id="result_set">
                <tr>
                    <th>company_name</th>
                    <th>facility_name</th>
                    <th>facility_state</th>
                    <th>facility_sales_region</th>
                    <th>resident_count</th>
                    <th>last_success_sync_date</th>
                </tr>
                <c:choose>
                    <c:when test="${not empty report.content}">
                        <c:forEach var="facility" items="${report.content}">
                            <tr>
                                <td>${facility.companyName}</td>
                                <td>${facility.name}</td>
                                <td>${facility.state}</td>
                                <td>${facility.salesRegion}</td>
                                <td>${facility.residentNumber}</td>
                                <td>${facility.lastSyncDate}</td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <c:forEach begin="0" end="6">
                                <td></td>
                            </c:forEach>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </table>

            <div class="left">
            </div>

            <div class="right">
                <wg:pagination baseUrl="${reportUrl}" totalPages="${report.totalPages}" currentIndex="${currentIndex}"/>
            </div>
        </div>
    </form:form>
</body>
</html>