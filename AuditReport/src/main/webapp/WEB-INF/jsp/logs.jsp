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
    <script src="https://ajax.aspnetcdn.com/ajax/jquery.validate/1.13.1/jquery.validate.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/hightlight.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/datepickers.js"></script>
    <link rel="shortcut icon" type="image/x-icon"  href="<c:url value="/resources/images/favicon.ico"/>"/>
</head>

<body>
    <div id="header">
        <h1>Audit Log Report</h1>
        <div id="navigation">
            <sec:authorize access="hasRole('ROLE_SUPER_MANAGER')">
                <a href="<c:url value="/home"/>">Home</a> |
            </sec:authorize>
            <a href="<c:url value="/j_spring_security_logout"/>">Log Out</a>
        </div>
    </div>

    <form:form method="GET" commandName="reportFilter" id="filterForm">
        <div id="container">

            <c:url var="firstUrl" value="/${reportUrl}/1"/>

            <div class="left">
                <label for="from">Start date: </label>
                <form:input path="from" name="from" id ="from"/>
                <form:errors path="from" cssClass="error"/>

                <label for="to">End date: </label>
                <form:input path="to" name="to"/>
                <form:errors path="to" cssClass="error"/>

                <sec:authorize access="hasRole('ROLE_SUPER_MANAGER')">
                    <label for="companies">Company: </label>
                    <form:select id="companies" path="company">
                        <form:option value="" label="All Companies"/>
                        <form:options items="${companies}" itemValue="id" itemLabel="name"/>
                    </form:select>
                </sec:authorize>

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
                    <th>id</th>
                    <th>time</th>
                    <th>action</th>
                    <th>employee_id</th>
                    <th>employee_login</th>
                    <th>employee_ip_address</th>
                    <th>resident_id</th>
                    <th>resident_first_name</th>
                    <th>resident_last_name</th>
                    <th>document_id</th>
                    <th>document_title</th>
                    <th>company_name</th>
                </tr>
                <c:choose>
                    <c:when test="${not empty report.content}">
                        <c:forEach var="entry" items="${report.content}">
                            <tr>
                                <td>${entry.id}</td>
                                <td>${entry.date}</td>
                                <td>${entry.action}</td>
                                <td>${entry.employeeId}</td>
                                <td>${entry.employeeLogin}</td>
                                <td>${entry.ipAddress}</td>
                                <td>${entry.residentId}</td>
                                <td>${entry.residentFirstName}</td>
                                <td>${entry.residentLastName}</td>
                                <td>${entry.documentId}</td>
                                <td>${entry.documentTitle}</td>
                                <td>${entry.databaseName}</td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <c:forEach begin="0" end="11">
                                <td></td>
                            </c:forEach>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </table>

            <div class="left">
                <c:url var="exportForPrevMonth" value="/${reportUrl}/exportToExcel/for-prev-month"/>
                <c:url var="exportForAllTime" value="/${reportUrl}/exportToExcel/for-all-time"/>

                <form:button id="exportForPrevMonth" class="link" onclick="form.action='${exportForPrevMonth}'"></form:button>
                <form:button class="link" onclick="form.action='${exportForAllTime}'">For all time</form:button>
            </div>

            <div class="right">
                <wg:pagination baseUrl="${reportUrl}" totalPages="${report.totalPages}" currentIndex="${currentIndex}"/>
            </div>
        </div>
    </form:form>
</body>
</html>