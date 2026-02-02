<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="patient.info.ccd.planOfCare.plannedActivity" var="plannedActivityLabel"/>
<spring:message code="patient.info.ccd.planOfCare.plannedDate" var="plannedDateLabel"/>

<c:set value="/patient-info" var="patientInfoUrl"/>

<c:url value="${patientInfoUrl}/${residentId}/ccd/planOfCare/${aggregated}/results?hashKey=${hashKey}" var="planOfCareDataUrl"/>

<spring:message code="patient.info.ccd.datasource" var="dataSourceLabel"/>
<c:choose>
    <c:when test="${aggregated}">
        <c:set value="plannedActivity,plannedDate,dataSource" var="columnIds"/>
        <c:set value="${plannedActivityLabel},${plannedDateLabel},${dataSourceLabel}" var="columnNames"/>
        <c:set value="custom,string,custom" var="columnFormats"/>
    </c:when>
    <c:otherwise>
        <c:set value="plannedActivity,plannedDate" var="columnIds"/>
        <c:set value="${plannedActivityLabel},${plannedDateLabel}" var="columnNames"/>
        <c:set value="custom,string" var="columnFormats"/>
    </c:otherwise>
</c:choose>

<lt:layout cssClass="planOfCareTable">
    <wg:link name="planOfCare"/>
    <lt:layout>
        <wg:grid id="planOfCareList"
                 colIds="${columnIds}"
                 colNames="${columnNames}"
                 colFormats="${columnFormats}"
                 dataUrl="${planOfCareDataUrl}"/>
    </lt:layout>
</lt:layout>