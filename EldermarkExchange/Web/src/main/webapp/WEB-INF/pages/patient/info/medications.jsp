<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="patient.info.ccd.medications.medication" var="medicationLabel"/>
<spring:message code="patient.info.ccd.medications.direction" var="directionLabel"/>
<spring:message code="patient.info.ccd.start.date" var="startDateLabel"/>
<spring:message code="patient.info.ccd.end.date" var="endDateLabel"/>
<spring:message code="patient.info.ccd.medications.indications" var="indicationsLabel"/>
<spring:message code="patient.info.ccd.medications.instruction" var="instructionLabel"/>
<spring:message code="patient.info.ccd.medications.status" var="statusLabel"/>

<c:set value="/patient-info" var="patientInfoUrl"/>

<c:url value="${patientInfoUrl}/${residentId}/ccd/medications/${aggregated}/results?hashKey=${hashKey}" var="medicationsDataUrl"/>

<spring:message code="patient.info.ccd.datasource" var="dataSourceLabel"/>
<c:choose>
    <c:when test="${aggregated}">
        <c:set value="productNameText,direction,startDate,endDate,status,indications,dataSource" var="columnIds"/>
        <c:set value="${medicationLabel},${directionLabel},${startDateLabel},${endDateLabel},${statusLabel},${indicationsLabel},${dataSourceLabel}" var="columnNames"/>
        <c:set value="string,string,string,string,string,string,custom" var="columnFormats"/>
    </c:when>
    <c:otherwise>
        <c:set value="productNameText,direction,startDate,endDate,status,indications" var="columnIds"/>
        <c:set value="${medicationLabel},${directionLabel},${startDateLabel},${endDateLabel},${statusLabel},${indicationsLabel}" var="columnNames"/>
        <c:set value="string,string,string,string,string,string" var="columnFormats"/>
    </c:otherwise>
</c:choose>

<lt:layout cssClass="medicationsTable">
    <wg:link name="medications"/>
    <lt:layout>
        <wg:grid id="medicationsList"
                 colIds="${columnIds}"
                 colNames="${columnNames}"
                 colFormats="${columnFormats}"
                 dataUrl="${medicationsDataUrl}"/>
    </lt:layout>
</lt:layout>