<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="patient.info.ccd.socialHistory.element" var="elementLabel"/>
<spring:message code="patient.info.ccd.socialHistory.description" var="descriptionLabel"/>

<c:set value="/patient-info" var="patientInfoUrl"/>

<c:url value="${patientInfoUrl}/${residentId}/ccd/socialHistory/${aggregated}/results?hashKey=${hashKey}" var="socialHistoryDataUrl"/>

<spring:message code="patient.info.ccd.datasource" var="dataSourceLabel"/>
<c:choose>
    <c:when test="${aggregated}">
        <c:set value="element,description,dataSource" var="columnIds"/>
        <c:set value="${elementLabel},${descriptionLabel},${dataSourceLabel}" var="columnNames"/>
        <c:set value="string,string,custom" var="columnFormats"/>
    </c:when>
    <c:otherwise>
        <c:set value="element,description" var="columnIds"/>
        <c:set value="${elementLabel},${descriptionLabel}" var="columnNames"/>
        <c:set value="string,string" var="columnFormats"/>
    </c:otherwise>
</c:choose>

<lt:layout cssClass="socialHistoryTable">
    <wg:link name="socialHistory"/>
    <lt:layout>
        <wg:grid id="socialHistoryList"
                 colIds="${columnIds}"
                 colNames="${columnNames}"
                 colFormats="${columnFormats}"
                 dataUrl="${socialHistoryDataUrl}"/>
    </lt:layout>
</lt:layout>