<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="patient.info.ccd.immunizations.vaccine" var="vaccineLabel"/>
<spring:message code="patient.info.ccd.immunizations.startDate" var="startDateLabel"/>
<spring:message code="patient.info.ccd.immunizations.endDate" var="endDateLabel"/>
<spring:message code="patient.info.ccd.immunizations.status" var="statusLabel"/>

<c:set value="/patient-info" var="patientInfoUrl"/>

<c:url value="${patientInfoUrl}/${residentId}/ccd/immunizations/${aggregated}/results?hashKey=${hashKey}" var="immunizationsDataUrl"/>

<spring:message code="patient.info.ccd.datasource" var="dataSourceLabel"/>
<c:choose>
    <c:when test="${aggregated}">
        <c:set value="vaccine,startDate,endDate,status,dataSource" var="columnIds"/>
        <c:set value="${vaccineLabel},${startDateLabel},${endDateLabel},${statusLabel},${dataSourceLabel}" var="columnNames"/>
        <c:set value="string,string,string,string,custom" var="columnFormats"/>
    </c:when>
    <c:otherwise>
        <c:set value="vaccine,startDate,endDate,status" var="columnIds"/>
        <c:set value="${vaccineLabel},${startDateLabel},${endDateLabel},${statusLabel}" var="columnNames"/>
        <c:set value="string,string,string,string" var="columnFormats"/>
    </c:otherwise>
</c:choose>

<lt:layout cssClass="immunizationsTable">
    <wg:link name="immunizations"/>
    <lt:layout>
        <wg:grid id="immunizationsList"
                 colIds="${columnIds}"
                 colNames="${columnNames}"
                 colFormats="${columnFormats}"
                 dataUrl="${immunizationsDataUrl}"/>
    </lt:layout>
</lt:layout>

