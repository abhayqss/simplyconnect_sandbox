<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="patient.info.ccd.advanceDirectives.type" var="typeLabel"/>
<spring:message code="patient.info.ccd.advanceDirectives.verification" var="verificationLabel"/>
<spring:message code="patient.info.ccd.advanceDirectives.supportingDocuments" var="supportingDocumentsLabel"/>

<c:set value="/patient-info" var="patientInfoUrl"/>

<c:url value="${patientInfoUrl}/${residentId}/ccd/advanceDirectives/${aggregated}/results?hashKey=${hashKey}" var="advanceDirectivesDataUrl"/>

<spring:message code="patient.info.ccd.datasource" var="dataSourceLabel"/>
<c:choose>
    <c:when test="${aggregated}">
        <c:set value="type,verification,supportingDocuments,dataSource" var="columnIds"/>
        <c:set value="${typeLabel},${verificationLabel},${supportingDocumentsLabel},${dataSourceLabel}" var="columnNames"/>
        <c:set value="string,string,custom,custom" var="columnFormats"/>
    </c:when>
    <c:otherwise>
        <c:set value="type,verification,supportingDocuments" var="columnIds"/>
        <c:set value="${typeLabel},${verificationLabel},${supportingDocumentsLabel}" var="columnNames"/>
        <c:set value="string,string,custom" var="columnFormats"/>
    </c:otherwise>
</c:choose>

<lt:layout cssClass="advanceDirectivesTable">
    <wg:link name="advanceDirectives"/>
    <lt:layout>
        <wg:grid id="advanceDirectivesList"
                 colIds="${columnIds}"
                 colNames="${columnNames}"
                 colFormats="${columnFormats}"
                 dataUrl="${advanceDirectivesDataUrl}"/>
    </lt:layout>
</lt:layout>

