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
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/send-iti-18.js"></script>
</head>

<body class="panel-body">

<c:set var="rand"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>

<jsp:include page="header.jsp"/>

<H1> ITI-18 Registry Stored Query </H1>

<div class="panel panel-primary">
    <div class="panel-heading" id="requestHead">Request</div>

    <div class="panel-body" id="requestBody"
        <c:if test="${not empty responseDto}">style="display:none"</c:if>
            >
        <form:form method="POST" commandName="requestDto" class="container" id="iti18form">
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
                <label for="templateFor-input" class="col-xs-2 col-form-label">Template For</label>
                <div class="col-xs-4">
                    <form:select path="templateFor" type="text" class="form-control" id="templateFor-input">
                        <form:option value="Documents">Documents</form:option>
                        <form:option value="Folders">Folders</form:option>
                        <form:option value="Submission Sets">Submission Sets</form:option>
                    </form:select>
                </div>
            </div>

            <div class="form-group row">
                <label for="returnType-input" class="col-xs-2 col-form-label">Return Type</label>
                <div class="col-xs-4">
                    <form:select path="returnType" type="text" class="form-control" id="returnType-input">
                        <form:option value="LeafClass">LeafClass</form:option>
                        <form:option value="ObjectRef">ObjectRef</form:option>
                    </form:select>
                </div>

                <label for="storedQueryId-input" class="col-xs-2 col-form-label">Stored Query Id</label>
                <div class="col-xs-4">
                    <form:input path="storedQueryId" type="text" class="form-control" id="storedQueryId-input"/>
                </div>
            </div>
            <div class="panel panel-info">
                <div class="panel-heading" id="helpPanelHead" onclick="$('#helpPanelBody').toggle()">ValueList</div>
                <div class="panel-body" id="helpPanelBody" >
                    <div class="col-xs-6">
                        <h4>Stored Query Id Examples</h4>
                        <table class="table">
                            <tr>
                                <th width="80%">Code</th>
                                <th width="20%">DisplayName</th>
                            </tr>
                            <c:forEach var="entry" items="${QUERY_ID_MAP_SAMPLES}">
                                <tr>
                                    <td>${entry.key}</td>
                                    <td>${entry.value}</td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>

                    <div class="col-xs-6">
                        <h4>HelathcareFacilityTypeCode Examples</h4>
                        <table class="table">
                            <tr>
                                <th width="60%">Code</th>
                                <th width="40%">DisplayName</th>
                            </tr>
                            <c:forEach var="entry" items="${HEALTHCARE_TYPES}">
                                <tr>
                                    <td>${entry.key}</td>
                                    <td>${entry.value}</td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-xs-5">
                    <form:checkbox path="patientCriteria" label="Include PatientId in search"/>
                </div>
                <div class="col-xs-5">
                    <form:checkbox path="healthcareFacilityTypeCodeCriteria" label="Include HelathcareFacilityTypeCode in search"/>
                </div>
            </div>

            <div class="form-group row">
                <label for="patientId-input" class="col-xs-1 col-form-label">PatientId</label>
                <div class="col-xs-4">
                    <form:input path="patientId" type="text" class="form-control" id="patientId-input"/>
                </div>

                <label for="healthcareFacilityTypeCode-input" class="col-xs-2 col-form-label">Healthcare Facility Type Code</label>
                <div class="col-xs-3">
                    <form:input path="healthCareFacilityTypeCode" type="text" class="form-control" id="healthcareFacilityTypeCode-input"/>
                </div>
            </div>


            <div class="form-group row">
                <div class="col-xs-5">
                    <form:checkbox path="createTimeFromCriteria" label="Include CreateTimeFrom in search"/>
                </div>
                <div class="col-xs-5">
                    <form:checkbox path="createTimeToCriteria" label="Include CreateTimeTo in search"/>
                </div>
            </div>

            <div class="form-group row">
                <label for="createTimeFrom-input" class="col-xs-2 col-form-label">Create Time From (yyyyMMddhhmm)</label>
                <div class="col-xs-3">
                    <form:input path="createTimeFrom" type="text" class="form-control" id="createTimeFrom-input"/>
                </div>

                <label for="createTimeTo-input" class="col-xs-2 col-form-label">Create Time To (yyyyMMddhhmm)</label>
                <div class="col-xs-3">
                    <form:input path="createTimeTo" type="text" class="form-control" id="createTimeTo-input"/>
                </div>
            </div>

            <div class="form-group row">
                <div class="col-xs-5">
                    <form:checkbox path="documentEntryUniqueIdCriteria" label="Include Document/Folder/SubmissionSet Unique Id in search"/>
                </div>
                <div class="col-xs-5">
                    <form:checkbox path="documentEntryEntryUUIDCriteria" label="Include Document/Folder/SubmissionSet Entry UUID in search"/>
                </div>
            </div>

            <div class="form-group row">
                <label for="documentEntryUniqueId-input" class="col-xs-1 col-form-label">Document Unique Id</label>
                <div class="col-xs-4">
                    <form:input path="documentEntryUniqueId" type="text" class="form-control" id="documentEntryUniqueId-input"/>
                </div>

                <label for="documentEntryEntryUUID-input" class="col-xs-2 col-form-label">Document Entry UUID</label>
                <div class="col-xs-3">
                    <form:input path="documentEntryEntryUUID" type="text" class="form-control" id="documentEntryEntryUUID-input"/>
                </div>
            </div>



            <div class="form-group row">
                <div class="col-xs-2 col-xs-offset-2">
                    <button id="apply-values-18" type="button" class="btn btn-primary">Apply Values to Template</button>
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