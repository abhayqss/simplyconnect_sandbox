<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="patient.info.ccd.vitalSigns.date" var="dateLabel"/>
<spring:message code="patient.info.ccd.vitalSigns.type" var="typeLabel"/>
<spring:message code="patient.info.ccd.vitalSigns.value" var="valueLabel"/>

<c:set value="/patient-info" var="patientInfoUrl"/>

<c:url value="${patientInfoUrl}/${residentId}/ccd/vitalSigns/${aggregated}/results?hashKey=${hashKey}" var="vitalSignsDataUrl"/>

<spring:message code="patient.info.ccd.datasource" var="dataSourceLabel"/>
<c:choose>
    <c:when test="${aggregated}">
        <c:set value="date,type,value,dataSource" var="columnIds"/>
        <c:set value="${dateLabel},${typeLabel},${valueLabel},${dataSourceLabel}" var="columnNames"/>
        <c:set value="string,string,string,custom" var="columnFormats"/>
    </c:when>
    <c:otherwise>
        <c:set value="date,type,value" var="columnIds"/>
        <c:set value="${dateLabel},${typeLabel},${valueLabel}" var="columnNames"/>
        <c:set value="string,string,string" var="columnFormats"/>
    </c:otherwise>
</c:choose>

<lt:layout cssClass="vitalSignsTable">
    <wg:link name="vitalSigns"/>
    <lt:layout>
        <wg:grid id="vitalSignsList"
                 colIds="${columnIds}"
                 colNames="${columnNames}"
                 colFormats="${columnFormats}"
                 dataUrl="${vitalSignsDataUrl}"/>
    </lt:layout>
</lt:layout>