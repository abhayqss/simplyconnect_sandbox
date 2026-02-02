<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <link href="<c:url value="/resources/css/standard.css"/>" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/send-iti-43.js"></script>
</head>

<body class="panel-body">

<c:set var="rand"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<jsp:include page="header.jsp"/>


<H1> ITI-43 Retrieve Document Set </H1>

<div class="panel panel-primary">
    <div class="panel-heading" id="requestHead">Request</div>

    <div class="panel-body" id="requestBody"
        <c:if test="${not empty responseDto}">style="display:none"</c:if>
            >
        <form:form method="POST" commandName="requestDto" class="container">
            <div class="form-group row">
                <label for="host-input" class="col-xs-2 col-form-label">Host</label>
                <div class="col-xs-4">
                    <form:input path="host" type="text" class="form-control" id="host-input"/>
                </div>

                <label for="port-input" class="col-xs-2 col-form-label">Port</label>
                <div class="col-xs-4">
                    <form:input path="port" type="text" class="form-control" id="port-input"/>
                </div>
            </div>
            <div class="form-group row">
                <label for="repositoryUniqueId-input" class="col-xs-2 col-form-label">Repository Unique Id</label>
                <div class="col-xs-4">
                    <form:input path="repositoryUniqueId" type="text" class="form-control" id="repositoryUniqueId-input"/>
                </div>

                <label for="documentId-input" class="col-xs-2 col-form-label">Document Unique Id</label>
                <div class="col-xs-4">
                    <form:input path="documentUniqueId" type="text" class="form-control" id="documentId-input"/>
                </div>

            </div>

            <div class="form-group row">
                <div class="col-xs-2 col-xs-offset-2">
                    <button id="apply-values-43" type="button" class="btn btn-primary">Apply Values to Template</button>
                </div>
            </div>

            <div class="form-group row">
                <h2> SOAP Request </h2>
                <h4>Headers</h4>
                <c:forEach var="head" items="${itiHeaders}">
                <div class="form-group row">
                    <div class="col-xs-2">
                        <input type="text" class="form-control" readonly="readonly" value="${head.key}"/>
                    </div>
                    <div class="col-xs-4">
                        <input type="text" class="form-control" readonly="readonly" value="${head.value}"/>
                    </div>
                </div>
                </c:forEach>
            </div>


            <div class="form-group row">
                <label for="template-input"  class="col-xs-2 col-form-label">SOAP MESSAGE</label>
                <div class="col-xs-10">
                    <form:textarea path="template" type="textarea" rows="25" class="form-control" id="template-input"/>
                </div>


            </div>


            <div class="form-group row">
                <div class="col-xs-2 col-xs-offset-2">
                    <button id="send-iti43" type="submit" class="btn btn-primary">Send</button>
                </div>
            </div>
        </form:form>
    </div>
</div>

<c:if test="${not empty responseDto}">
    <div id="adt-server-response" class="panel ${(responseDto.statusCode!=200) ? 'panel-primary' : 'panel-success'}">
        <div class="panel-heading">Response</div>
        <div class="panel-body">
            <label>Status</label> : <span class="${(responseDto.statusCode==200) ? 'ok' : 'nok'}">${responseDto.statusCode}: ${responseDto.status}</span>
            <p/>
            <label>Response Text</label> :
            <form:textarea  path="responseDto.responseBody" id="responseTextarea" type="textarea" rows="25" class="form-control" ></form:textarea>

        </div>
    </div>
</c:if>

</body>
</html>