<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="patient.info.ccd.results.date" var="date"/>
<spring:message code="patient.info.ccd.results.type" var="type"/>
<spring:message code="patient.info.ccd.results.statusCode" var="statusCode"/>
<spring:message code="patient.info.ccd.results.value" var="value"/>
<spring:message code="patient.info.ccd.results.interpretations" var="interpretations"/>
<spring:message code="patient.info.ccd.results.referenceRanges" var="referenceRanges"/>

<c:set value="/patient-info" var="patientInfoUrl"/>

<c:url value="${patientInfoUrl}/${residentId}/ccd/results/${aggregated}/results?hashKey=${hashKey}" var="resultsListDataUrl"/>

<spring:message code="patient.info.ccd.datasource" var="dataSourceLabel"/>
<c:choose>
    <c:when test="${aggregated}">
        <c:set value="date,type,statusCode,value,interpretations,referenceRanges,dataSource" var="columnIds"/>
        <c:set value="${date},${type},${statusCode},${value},${interpretations},${referenceRanges},${dataSourceLabel}" var="columnNames"/>
        <c:set value="string,string,string,string,string,string,custom" var="columnFormats"/>
    </c:when>
    <c:otherwise>
        <c:set value="date,type,statusCode,value,interpretations,referenceRanges" var="columnIds"/>
        <c:set value="${date},${type},${statusCode},${value},${interpretations},${referenceRanges}" var="columnNames"/>
        <c:set value="string,string,string,string,string,string" var="columnFormats"/>
    </c:otherwise>
</c:choose>

<lt:layout cssClass="resultsTable">
    <wg:link name="results"/>
    <lt:layout>
        <wg:grid id="resultsList"
                 colIds="${columnIds}"
                 colNames="${columnNames}"
                 colFormats="${columnFormats}"
                 dataUrl="${resultsListDataUrl}"/>
    </lt:layout>
</lt:layout>

