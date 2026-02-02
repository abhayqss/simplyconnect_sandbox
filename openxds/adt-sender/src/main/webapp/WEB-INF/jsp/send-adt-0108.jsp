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
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/send-adt-0108.js"></script>
</head>

<body class="panel-body">
<jsp:include page="header.jsp"/>

<H1> ITI-8 Patient Identity Feed (Admit/Register/Update) </H1>

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
                <label for="msgType-input" class="col-xs-2 col-form-label">Message Type</label>
                <div class="col-xs-2">
                    <form:select path="msgType" type="text" class="form-control" id="msgType-input">
                        <form:option value="A01">ADT-01</form:option>
                        <form:option value="A04">ADT-04</form:option>
                        <form:option value="A08">ADT-08</form:option>
                    </form:select>
                </div>

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

                <label for="race-input" class="col-xs-1 col-form-label">Race</label>
                <div class="col-xs-2">
                    <form:input path="race" type="text" class="form-control" id="race-input"/>
                </div>

                <label for="language-input" class="col-xs-1 col-form-label">Primary Language</label>
                <div class="col-xs-2">
                    <form:input path="language" type="text" class="form-control" id="language-input"/>
                </div>
            </div>

            <div class="form-group row">

                <label for="address-input" class="col-xs-1 col-form-label">Address</label>
                <div class="col-xs-3">
                    <form:input path="address" type="text" class="form-control" id="address-input"/>
                </div>

                <label for="city-input" class="col-xs-1 col-form-label">City</label>
                <div class="col-xs-2">
                    <form:input path="city" type="text" class="form-control" id="city-input"/>
                </div>

                <label for="state-input" class="col-xs-1 col-form-label">State</label>
                <div class="col-xs-1">
                    <form:input path="state" type="text" class="form-control" id="state-input"/>
                </div>

                <label for="zip-input" class="col-xs-1 col-form-label">Zip</label>
                <div class="col-xs-2">
                    <form:input path="zip" type="text" class="form-control" id="zip-input"/>
                </div>
            </div>

            <div class="form-group row">

                <label for="phoneHome-input" class="col-xs-1 col-form-label">Home Phone</label>
                <div class="col-xs-2">
                    <form:input path="phoneHome" type="text" class="form-control" id="phoneHome-input"/>
                </div>

                <label for="phoneBusiness-input" class="col-xs-1 col-form-label">Business phone</label>
                <div class="col-xs-2">
                    <form:input path="phoneBusiness" type="text" class="form-control" id="phoneBusiness-input"/>
                </div>

                <label for="maritalStatus-input" class="col-xs-1 col-form-label">Marital Status</label>
                <div class="col-xs-2">
                    <form:input path="maritalStatus" type="text" class="form-control" id="maritalStatus-input"/>
                </div>

                <label for="religion-input" class="col-xs-1 col-form-label">Religion</label>
                <div class="col-xs-2">
                    <form:input path="religion" type="text" class="form-control" id="religion-input"/>
                </div>
            </div>

            <div class="form-group row">

                <label for="accountNumber-input" class="col-xs-1 col-form-label">Account Number</label>
                <div class="col-xs-2">
                    <form:input path="accountNumber" type="text" class="form-control" id="accountNumber-input"/>
                </div>

                <label for="ssn-input" class="col-xs-1 col-form-label">SSN</label>
                <div class="col-xs-4">
                    <form:input path="ssn" type="text" class="form-control" id="ssn-input"/>
                </div>

                <label for="driverLicence-input" class="col-xs-1 col-form-label">Driver Licence</label>
                <div class="col-xs-2">
                    <form:input path="driverLicence" type="text" class="form-control" id="driverLicence-input"/>
                </div>

            </div>

            <div class="form-group row">

                <label for="ethnicity-input" class="col-xs-1 col-form-label">Ethnicity</label>
                <div class="col-xs-2">
                    <form:input path="ethnicity" type="text" class="form-control" id="ethnicity-input"/>
                </div>

                <label for="placeOfBirth-input" class="col-xs-1 col-form-label">Place Of Birth</label>
                <div class="col-xs-2">
                    <form:input path="placeOfBirth" type="text" class="form-control" id="placeOfBirth-input"/>
                </div>

                <label for="citizenship-input" class="col-xs-1 col-form-label">Citizenship</label>
                <div class="col-xs-2">
                    <form:input path="citizenship" type="text" class="form-control" id="citizenship-input"/>
                </div>

                <label for="veteranStatus-input" class="col-xs-1 col-form-label">Veteran Military Status</label>
                <div class="col-xs-2">
                    <form:input path="veteranStatus" type="text" class="form-control" id="veteranStatus-input"/>
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

    <div id="adt-server-response" class="panel ${(responseDto.status!=null) ? 'panel-primary' : 'panel-success'}">
        <div class="panel-heading">Response</div>
        <div class="panel-body">
            <label>Success</label> : <span class="${(responseDto.status!=null && responseDto.status) ? 'ok' : 'nok'}"> ${responseDto.status}</span>
            <p/>
            <label>Response Message</label> : ${responseDto.text}

        </div>
    </div>

</c:if>

</body>
</html>