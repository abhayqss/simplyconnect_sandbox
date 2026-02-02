<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="patient.info.ccd.procedures.procedure" var="procedureLabel"/>
<spring:message code="patient.info.ccd.start.date" var="startDateLabel"/>
<spring:message code="patient.info.ccd.end.date" var="endDateLabel"/>

<c:set value="/patient-info" var="patientInfoUrl"/>

<c:url value="${patientInfoUrl}/${residentId}/ccd/procedures/${aggregated}/results?hashKey=${hashKey}" var="proceduresDataUrl"/>

<spring:message code="patient.info.ccd.datasource" var="dataSourceLabel"/>
<c:choose>
    <c:when test="${aggregated}">
        <c:set value="type,startDate,endDate,dataSource" var="columnIds"/>
        <c:set value="${procedureLabel},${startDateLabel},${endDateLabel},${dataSourceLabel}" var="columnNames"/>
        <c:set value="string,string,string,custom" var="columnFormats"/>
    </c:when>
    <c:otherwise>
        <c:set value="type,startDate,endDate" var="columnIds"/>
        <c:set value="${procedureLabel},${startDateLabel},${endDateLabel}" var="columnNames"/>
        <c:set value="string,string,string" var="columnFormats"/>
    </c:otherwise>
</c:choose>

<lt:layout cssClass="proceduresTable">
    <wg:link name="procedures"/>
    <lt:layout>
        <wg:grid id="proceduresList"
                 colIds="${columnIds}"
                 colNames="${columnNames}"
                 colFormats="${columnFormats}"
                 dataUrl="${proceduresDataUrl}"/>
    </lt:layout>
</lt:layout>