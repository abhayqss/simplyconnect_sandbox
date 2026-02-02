<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="patient.info.ccd.familyHistory.diagnosis" var="diagnosisLabel"/>
<spring:message code="patient.info.ccd.familyHistory.ageAtOnset" var="ageAtOnsetLabel"/>

<c:set value="/patient-info" var="patientInfoUrl"/>

<c:url value="${patientInfoUrl}/${residentId}/ccd/familyHistory/${aggregated}/results?hashKey=${hashKey}" var="familyHistoryDataUrl"/>

<spring:message code="patient.info.ccd.datasource" var="dataSourceLabel"/>
<c:choose>
    <c:when test="${aggregated}">
        <c:set value="diagnosis,ageAtOnset,dataSource" var="columnIds"/>
        <c:set value="${diagnosisLabel},${ageAtOnsetLabel},${dataSourceLabel}" var="columnNames"/>
        <c:set value="string,string,custom" var="columnFormats"/>
    </c:when>
    <c:otherwise>
        <c:set value="diagnosis,ageAtOnset" var="columnIds"/>
        <c:set value="${diagnosisLabel},${ageAtOnsetLabel}" var="columnNames"/>
        <c:set value="string,string" var="columnFormats"/>
    </c:otherwise>
</c:choose>

<lt:layout cssClass="familyHistoryTable">
    <wg:link name="familyHistory"/>
    <lt:layout>
        <wg:grid id="familyHistoryList"
                 colIds="${columnIds}"
                 colNames="${columnNames}"
                 colFormats="${columnFormats}"
                 dataUrl="${familyHistoryDataUrl}"/>
    </lt:layout>
</lt:layout>

