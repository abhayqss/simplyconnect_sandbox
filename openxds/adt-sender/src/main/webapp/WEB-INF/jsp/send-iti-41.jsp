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
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/send-iti-41.js"></script>
</head>

<body class="panel-body">

<c:set var="rand"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>
<jsp:include page="header.jsp"/>


<H1> ITI-41 Provide And Register Document Set-b </H1>

<div class="panel panel-primary">
    <div class="panel-heading" id="requestHead">Request</div>

    <div class="panel-body" id="requestBody"
        <c:if test="${not empty responseDto}">style="display:none"</c:if>
            >

        <div class="form-group row" style="margin-left: auto;margin-right: auto; width:600px">

            <div class="col-xs-3">
                <input type="file" id="fileInput" />
            </div>

            <div class="col-xs-2 col-xs-offset-2">
                <button id="send-iti41upload" type="button" class="btn btn-primary" onclick="return false">Upload File</button>
            </div>

        </div>




        <form:form method="POST" commandName="requestDto" class="container" id="iti41form">

            <div style="padding: 10px; margin: 10px; border: 1px solid #A0A0D0;">

                <div class="form-group row">
                    <label for="hash-input" class="col-xs-1 col-form-label">Hash</label>
                    <div class="col-xs-3">
                        <form:input path="hash" type="text" class="form-control" id="hash-input"/>
                    </div>

                    <label for="size-input" class="col-xs-1 col-form-label">Size</label>
                    <div class="col-xs-3">
                        <form:input path="size" type="text" class="form-control" id="size-input"/>
                    </div>

                    <label for="fileName-input" class="col-xs-1 col-form-label">File Name</label>
                    <div class="col-xs-3">
                        <form:input path="fileName" type="text" class="form-control" id="fileName-input"/>
                    </div>
                </div>
                <div class="form-group row">
                    <label for="documentContentEncoded-input" class="col-xs-1 col-form-label">Document Content Encoded</label>
                    <div class="col-xs-8">
                        <form:textarea path="documentContentEncoded" rows="10" type="text" class="form-control" id="documentContentEncoded-input"/>
                    </div>
                </div>
            </div>

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

                <label for="mimeType-input" class="col-xs-1 col-form-label">Mime Type</label>
                <div class="col-xs-3">
                    <form:input path="mimeType" type="text" class="form-control" id="mimeType-input"/>
                </div>

                <label for="repositoryUniqueId-input" class="col-xs-1 col-form-label">Repository Unique Id</label>
                <div class="col-xs-3">
                    <form:input path="repositoryUniqueId" type="text" class="form-control" id="repositoryUniqueId-input"/>
                </div>

                <label for="creationTime-input" class="col-xs-1 col-form-label">Creation Time</label>
                <div class="col-xs-3">
                    <form:input path="creationTime" type="text" class="form-control" id="creationTime-input"/>
                </div>


            </div>

            <div class="form-group row">
                <label for="sourcePatientId-input" class="col-xs-1 col-form-label">Source Patient Id</label>
                <div class="col-xs-3">
                    <form:input path="sourcePatientId" type="text" class="form-control" id="sourcePatientId-input"/>
                </div>

                <label for="patientId-input" class="col-xs-1 col-form-label">Patient Id</label>
                <div class="col-xs-3">
                    <form:input path="patientId" type="text" class="form-control" id="patientId-input"/>
                </div>

                <label for="languageCode-input" class="col-xs-1 col-form-label">Language Code</label>
                <div class="col-xs-3">
                    <form:input path="languageCode" type="text" class="form-control" id="languageCode-input"/>
                </div>

            </div>

            <div class="form-group row">

                <label for="documentUniqueId-input" class="col-xs-1 col-form-label">Document Unique Id</label>
                <div class="col-xs-3">
                    <form:input path="documentUniqueId" type="text" class="form-control" id="documentUniqueId-input"/>
                </div>

                <label for="documentEntryUuid-input" class="col-xs-1 col-form-label">Document Entry Uuid</label>
                <div class="col-xs-3">
                    <form:input path="documentEntryUuid" type="text" class="form-control" id="documentEntryUuid-input"/>
                </div>


                <label for="description-input" class="col-xs-1 col-form-label">Description</label>
                <div class="col-xs-3">
                    <form:input path="description" type="text" class="form-control" id="description-input"/>
                </div>

            </div>

            <hr/>

            <div class="form-group row">

                <label for="submissionSetName-input" class="col-xs-1 col-form-label">Submission Set Name</label>
                <div class="col-xs-3">
                    <form:input path="submissionSetName" type="text" class="form-control" id="submissionSetName-input"/>
                </div>

                <label for="submissionSetUniqueId-input" class="col-xs-1 col-form-label">Submission Set Unique Id</label>
                <div class="col-xs-3">
                    <form:input path="submissionSetUniqueId" type="text" class="form-control" id="submissionSetUniqueId-input"/>
                </div>

                <label for="submissionSetSourceId-input" class="col-xs-1 col-form-label">Submission Set Source Id</label>
                <div class="col-xs-3">
                    <form:input path="submissionSetSourceId" type="text" class="form-control" id="submissionSetSourceId-input"/>
                </div>

            </div>



            <div class="form-group row">
                <label for="submissionTime-input" class="col-xs-1 col-form-label">Submission Time</label>
                <div class="col-xs-3">
                    <form:input path="submissionTime" type="text" class="form-control" id="submissionTime-input"/>
                </div>

                <label for="submissionSetDescription-input" class="col-xs-1 col-form-label">Submission Set Description</label>
                <div class="col-xs-6">
                    <form:input path="submissionSetDescription" type="text" class="form-control" id="submissionSetDescription-input"/>
                </div>


            </div>

            <hr/>

            <div class="form-group row">
                <label for="classCodeValue-input" class="col-xs-1 col-form-label">Class Code Value</label>
                <div class="col-xs-3">
                    <form:input path="classCodeValue" type="text" class="form-control" id="classCodeValue-input"/>
                </div>

                <label for="classCodeCodingScheme-input" class="col-xs-1 col-form-label">Class Code Coding Scheme</label>
                <div class="col-xs-3">
                    <form:input path="classCodeCodingScheme" type="text" class="form-control" id="classCodeCodingScheme-input"/>
                </div>

                <label for="classCodeValueLocalized-input" class="col-xs-1 col-form-label">Class Code Value Localized</label>
                <div class="col-xs-3">
                    <form:input path="classCodeValueLocalized" type="text" class="form-control" id="classCodeValueLocalized-input"/>
                </div>
            </div>

            <div class="form-group row">
                <label for="formatCodeValue-input" class="col-xs-1 col-form-label">Format Code Value</label>
                <div class="col-xs-3">
                    <form:input path="formatCodeValue" type="text" class="form-control" id="formatCodeValue-input"/>
                </div>

                <label for="formatCodeCodingScheme-input" class="col-xs-1 col-form-label">Format Code Coding Scheme</label>
                <div class="col-xs-3">
                    <form:input path="formatCodeCodingScheme" type="text" class="form-control" id="formatCodeCodingScheme-input"/>
                </div>

                <label for="formatCodeValueLocalized-input" class="col-xs-1 col-form-label">Format Code Value Localized</label>
                <div class="col-xs-3">
                    <form:input path="formatCodeValueLocalized" type="text" class="form-control" id="formatCodeValueLocalized-input"/>
                </div>
            </div>

            <div class="form-group row">
                <label for="practiceCodeValue-input" class="col-xs-1 col-form-label">Practice Code Value</label>
                <div class="col-xs-3">
                    <form:input path="practiceCodeValue" type="text" class="form-control" id="practiceCodeValue-input"/>
                </div>

                <label for="practiceCodeCodingScheme-input" class="col-xs-1 col-form-label">Practice Code Coding Scheme</label>
                <div class="col-xs-3">
                    <form:input path="practiceCodeCodingScheme" type="text" class="form-control" id="practiceCodeCodingScheme-input"/>
                </div>

                <label for="practiceCodeValueLocalized-input" class="col-xs-1 col-form-label">Practice Code Value Localized</label>
                <div class="col-xs-3">
                    <form:input path="practiceCodeValueLocalized" type="text" class="form-control" id="practiceCodeValueLocalized-input"/>
                </div>
            </div>

            <div class="form-group row">
                <label for="healthcareFacilityCodeValue-input" class="col-xs-1 col-form-label">Healthcare Facility Code Value</label>
                <div class="col-xs-3">
                    <form:input path="healthcareFacilityCodeValue" type="text" class="form-control" id="healthcareFacilityCodeValue-input"/>
                </div>

                <label for="healthcareFacilityCodingScheme-input" class="col-xs-1 col-form-label">Healthcare Facility Coding Scheme</label>
                <div class="col-xs-3">
                    <form:input path="healthcareFacilityCodingScheme" type="text" class="form-control" id="healthcareFacilityCodingScheme-input"/>
                </div>

                <label for="healthcareFacilityCodeValueLocalized-input" class="col-xs-1 col-form-label">Healthcare Facility Code Value Localized</label>
                <div class="col-xs-3">
                    <form:input path="healthcareFacilityCodeValueLocalized" type="text" class="form-control" id="healthcareFacilityCodeValueLocalized-input"/>
                </div>

            </div>

            <div class="form-group row">
                <label for="confidentialityCodeValue-input" class="col-xs-1 col-form-label">Confidentiality Code Value</label>
                <div class="col-xs-3">
                    <form:input path="confidentialityCodeValue" type="text" class="form-control" id="confidentialityCodeValue-input"/>
                </div>

                <label for="confidentialityCodeCodingScheme-input" class="col-xs-1 col-form-label">Confidentiality Code Coding Scheme</label>
                <div class="col-xs-3">
                    <form:input path="confidentialityCodeCodingScheme" type="text" class="form-control" id="confidentialityCodeCodingScheme-input"/>
                </div>

                <label for="confidentialityCodeValueLocalized-input" class="col-xs-1 col-form-label">Confidentiality Code Value Localized</label>
                <div class="col-xs-3">
                    <form:input path="confidentialityCodeValueLocalized" type="text" class="form-control" id="confidentialityCodeValueLocalized-input"/>
                </div>
            </div>

            <div class="form-group row">
                <label for="contentTypeCodeValue-input" class="col-xs-1 col-form-label">Content Type Code Value</label>
                <div class="col-xs-3">
                    <form:input path="contentTypeCodeValue" type="text" class="form-control" id="contentTypeCodeValue-input"/>
                </div>

                <label for="contentTypeCodeCodingScheme-input" class="col-xs-1 col-form-label">Content Type Code Coding Scheme</label>
                <div class="col-xs-3">
                    <form:input path="contentTypeCodeCodingScheme" type="text" class="form-control" id="contentTypeCodeCodingScheme-input"/>
                </div>

                <label for="contentTypeCodeValueLocalized-input" class="col-xs-1 col-form-label">Content Type Code Value Localized</label>
                <div class="col-xs-3">
                    <form:input path="contentTypeCodeValueLocalized" type="text" class="form-control" id="contentTypeCodeValueLocalized-input"/>
                </div>
            </div>

            <hr/>


            <hr/>

            <div class="form-group row">
                <div class="col-xs-2 col-xs-offset-2">
                    <button id="apply-values-41" type="button" class="btn btn-primary">Apply Values to Template</button>
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
           <%-- <form:input path="contentType" type="text" class="form-control" id="contentType-input"/>--%>

            <div class="form-group row">
                <label for="template-input"  class="col-xs-2 col-form-label">SOAP MESSAGE</label>
                <div class="col-xs-10">
                    <form:textarea path="template" type="textarea" rows="25" class="form-control" id="template-input"/>
                </div>


            </div>


            <div class="form-group row">
                <div class="col-xs-2 col-xs-offset-2">
                    <button id="send-iti41" type="submit" class="btn btn-primary">Send</button>
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