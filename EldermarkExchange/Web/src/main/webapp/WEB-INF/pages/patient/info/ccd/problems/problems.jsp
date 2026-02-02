<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="patient.info.ccd.problems.name" var="nameLabel"/>
<spring:message code="patient.info.ccd.start.date" var="startDateLabel"/>
<spring:message code="patient.info.ccd.end.date" var="endDateLabel"/>
<spring:message code="patient.info.ccd.actions" var="actionsLabel"/>
<spring:message code="patient.info.ccd.problems.type" var="typeLabel"/>
<spring:message code="patient.info.ccd.problems.status" var="statusLabel"/>
<spring:message code="patient.info.ccd.problems.diagnosisCode" var="diagnosisCodeLabel"/>
<spring:message code="patient.info.ccd.problems.diagnosisCodeSet" var="diagnosisCodeSetLabel"/>


<c:set value="/patient-info" var="patientInfoUrl"/>
<c:url value="/resources/images/round-plus.svg" var="addCcdIcon"/>

<c:url value="${patientInfoUrl}/${residentId}/ccd/problems/${aggregated}/results?hashKey=${hashKey}" var="problemsDataUrl"/>

<spring:message code="patient.info.ccd.datasource" var="dataSourceLabel"/>
<c:choose>
    <c:when test="${aggregated}">
        <c:set value="name, problemValueCode, problemValueCodeSet, type,  status, startDate, endDate, dataSource" var="columnIds"/>
        <c:set value="${nameLabel},${diagnosisCodeLabel},${diagnosisCodeSetLabel},${typeLabel},${statusLabel},${startDateLabel},${endDateLabel},${dataSourceLabel}" var="columnNames"/>
        <c:set value="custom, string, string, string, string, string, string, custom" var="columnFormats"/>
    </c:when>
    <c:otherwise>
        <c:set value="name, problemValueCode, problemValueCodeSet, type, status, startDate, endDate" var="columnIds"/>
        <c:set value="${nameLabel},${diagnosisCodeLabel},${diagnosisCodeSetLabel},${typeLabel},${statusLabel},${startDateLabel},${endDateLabel}" var="columnNames"/>
        <c:set value="custom, string, string, string, string, string, string" var="columnFormats"/>
    </c:otherwise>
</c:choose>

<lt:layout cssClass="col-md-12 addCcdLine">
    <wg:link id="addProblemCcdLink" cssClass="no-text-decoration pull-right addCcdLink">
        <wg:img cssClass="icon-link-img" src="${addCcdIcon}"/><span class="icon-link-text">Add a New ${nameLabel}</span>
    </wg:link>
</lt:layout>

<lt:layout cssClass="problemsTable">
    <wg:link name="problems"/>
    <lt:layout>
        <wg:grid id="problemsList"
                      colIds="${columnIds}"
                      colNames="${columnNames}"
                      colFormats="${columnFormats}"
                      dataUrl="${problemsDataUrl}"/>
    </lt:layout>
</lt:layout>

