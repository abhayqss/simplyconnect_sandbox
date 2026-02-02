<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags" %>

<html>
<head>
    <link rel="stylesheet" href="<c:url value="/resources/css/standard.css"/>" type="text/css"/>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.9.1/themes/smoothness/jquery-ui.css">
    <script src="//code.jquery.com/jquery-1.10.2.js"></script>
    <script src="//code.jquery.com/ui/1.11.2/jquery-ui.js"></script>
    <script src="https://ajax.aspnetcdn.com/ajax/jquery.validate/1.13.1/jquery.validate.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/hightlight.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/syncReport.js"></script>
    <link rel="shortcut icon" type="image/x-icon"  href="<c:url value="/resources/images/favicon.ico"/>"/>
</head>

<body>
    <div id="header">
        <h1>DataSync Log Report</h1>
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
                <label for="from"> Date: </label>
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
                    <th>log_id</th>
                    <th>log_date</th>
                    <th>log_type</th>
                    <th>description</th>
                    <th>table_name</th>
                    <th>stack_trace</th>
                    <th>iteration_number</th>
                    <th>database_name</th>
                </tr>
                <c:choose>
                    <c:when test="${not empty report.content}">
                        <c:forEach var="entry" items="${report.content}">
                            <tr>
                                <td>${entry.id}</td>
                                <td>${entry.date}</td>
                                <td>${entry.type}</td>
                                <td>${entry.description}</td>
                                <td>${entry.tableName}</td>
                                <td>${entry.stackTrace}</td>
                                <td>${entry.iterationNumber}</td>
                                <td>${entry.databaseName}</td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <c:forEach begin="0" end="7">
                                <td></td>
                            </c:forEach>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </table>

            <div class="left">
                <c:if test="${not empty range}">
                    Iteration: <b>${range.diff}</b>.
                    (<fmt:formatDate pattern="MM/dd/yyyy HH:mm:ss" value="${range.from}"/> –
                    <fmt:formatDate pattern="MM/dd/yyyy HH:mm:ss" value="${range.to}"/>)
                </c:if>
            </div>

            <div class="right">
                <wg:pagination baseUrl="${reportUrl}" totalPages="${report.totalPages}" currentIndex="${currentIndex}"/>
            </div>
        </div>
    </form:form>
</body>
</html>