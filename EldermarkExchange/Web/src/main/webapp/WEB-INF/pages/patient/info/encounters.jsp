<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="patient.info.ccd.encounters.encounter" var="encounterLabel"/>
<spring:message code="patient.info.ccd.encounters.performer" var="performerLabel"/>
<spring:message code="patient.info.ccd.encounters.location" var="locationLabel"/>
<spring:message code="patient.info.ccd.encounters.date" var="dateLabel"/>

<c:set value="/patient-info" var="patientInfoUrl"/>

<c:url value="${patientInfoUrl}/${residentId}/ccd/encounters/${aggregated}/results?hashKey=${hashKey}" var="encountersDataUrl"/>

<spring:message code="patient.info.ccd.datasource" var="dataSourceLabel"/>
<c:choose>
    <c:when test="${aggregated}">
        <c:set value="type,providerCodes,serviceDeliveryLocations,date,dataSource" var="columnIds"/>
        <c:set value="${encounterLabel},${performerLabel},${locationLabel},${dateLabel},${dataSourceLabel}" var="columnNames"/>
        <c:set value="string,string,string,string,custom" var="columnFormats"/>
    </c:when>
    <c:otherwise>
        <c:set value="type,providerCodes,serviceDeliveryLocations,date" var="columnIds"/>
        <c:set value="${encounterLabel},${performerLabel},${locationLabel},${dateLabel}" var="columnNames"/>
        <c:set value="string,string,string,string" var="columnFormats"/>
    </c:otherwise>
</c:choose>

<lt:layout cssClass="encountersTable">
    <wg:link name="encounters"/>
    <lt:layout>
        <wg:grid id="encountersList"
                 colIds="${columnIds}"
                 colNames="${columnNames}"
                 colFormats="${columnFormats}"
                 dataUrl="${encountersDataUrl}"/>
    </lt:layout>
</lt:layout>

