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
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/send-adt-39.js"></script>
</head>

<body class="panel-body">
<jsp:include page="header.jsp"/>

<H1> ITI-8 Patient Identity Feed (Merge) </H1>

<div class="panel panel-primary">
    <div class="panel-heading" id="requestHead">Request</div>

    <div class="panel-body" id="requestBody"
        <c:if test="${not empty responseDto}">style="display:none"</c:if>
            >
        <form:form method="POST" commandName="requestDto" class="container" id="iti8form">
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


                <label for="messageDate-input" class="col-xs-1 col-form-label">Message Date</label>
                <div class="col-xs-2">
                    <form:input path="messageDate" type="text" class="form-control" id="messageDate-input"/>
                </div>

                <label for="eventDate-input" class="col-xs-1 col-form-label">Event Date</label>
                <div class="col-xs-2">
                    <form:input path="eventDate" type="text" class="form-control" id="eventDate-input"/>
                </div>

            </div>


            <div class="form-group row">


                <label for="pid-input" class="col-xs-1 col-form-label">Patient Identifier</label>
                <div class="col-xs-4">
                    <form:input path="pid" type="text" class="form-control" id="pid-input"/>
                </div>

                <label for="lastName-input" class="col-xs-1 col-form-label">Last Name</label>
                <div class="col-xs-2">
                    <form:input path="lastName" type="text" class="form-control" id="lastName-input"/>
                </div>

                <label for="firstName-input" class="col-xs-1 col-form-label">First Name</label>
                <div class="col-xs-2">
                    <form:input path="firstName" type="text" class="form-control" id="firstName-input"/>
                </div>

            </div>

            <div class="form-group row">

                <label for="birthDate-input" class="col-xs-1 col-form-label">Birth Date</label>
                <div class="col-xs-2">
                    <form:input path="birthDate" type="text" class="form-control" id="birthDate-input"/>
                </div>

                <label for="sex-input" class="col-xs-1 col-form-label">Sex</label>
                <div class="col-xs-2">
                    <form:input path="sex" type="text" class="form-control" id="sex-input"/>
                </div>

            </div>

            <div class="form-group row">
                <label for="mergPid-input" class="col-xs-1 col-form-label">Merge Patient Identifier</label>
                <div class="col-xs-4">
                    <form:input path="mergePid" type="text" class="form-control" id="mergPid-input"/>
                </div>

            </div>


            <div class="form-group row">
                <div class="col-xs-2 col-xs-offset-2">
                    <button id="apply-values-8" type="button" class="btn btn-primary">Apply Values to Template</button>
                </div>
            </div>


            <div class="form-group row">
                <label for="template-input"  class="col-xs-2 col-form-label">TCP MESSAGE</label>
                <div class="col-xs-10">
                    <form:textarea path="template" type="textarea" rows="25" class="form-control" id="template-input"/>
                </div>


            </div>


            <div class="form-group row">
                <div class="col-xs-2 col-xs-offset-2">
                    <button id="send-iti8" type="submit" class="btn btn-primary">Send</button>
                </div>
            </div>
        </form:form>
    </div>
</div>

<c:if test="${not empty responseDto}">

    <div id="adt-server-response" class="panel ${(responseDto.status) ? 'panel-primary' : 'panel-success'}">
        <div class="panel-heading">Response</div>
        <div class="panel-body">
            <label>Status</label> : <span class="${(responseDto.status) ? 'ok' : 'nok'}"> ${responseDto.status}</span>
            <p/>
            <label>Text</label> : ${responseDto.text}

        </div>
    </div>

</c:if>

</body>
</html>