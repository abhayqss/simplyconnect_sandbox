<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="patient.info.ccd.medicalEquipment.device" var="deviceLabel"/>
<spring:message code="patient.info.ccd.medicalEquipment.suppliedDate" var="suppliedDateLabel"/>

<c:set value="/patient-info" var="patientInfoUrl"/>

<c:url value="${patientInfoUrl}/${residentId}/ccd/medicalEquipment/${aggregated}/results?hashKey=${hashKey}" var="medicalEquipmentDataUrl"/>

<spring:message code="patient.info.ccd.datasource" var="dataSourceLabel"/>
<c:choose>
    <c:when test="${aggregated}">
        <c:set value="device,suppliedDate,dataSource" var="columnIds"/>
        <c:set value="${deviceLabel},${suppliedDateLabel},${dataSourceLabel}" var="columnNames"/>
        <c:set value="string,string,custom" var="columnFormats"/>
    </c:when>
    <c:otherwise>
        <c:set value="device,suppliedDate" var="columnIds"/>
        <c:set value="${deviceLabel},${suppliedDateLabel}" var="columnNames"/>
        <c:set value="string,string" var="columnFormats"/>
    </c:otherwise>
</c:choose>

<lt:layout cssClass="medicalEquipmentTable">
    <wg:link name="medicalEquipment"/>
    <lt:layout>
        <wg:grid id="medicalEquipmentList"
                 colIds="${columnIds}"
                 colNames="${columnNames}"
                 colFormats="${columnFormats}"
                 dataUrl="${medicalEquipmentDataUrl}"/>
    </lt:layout>
</lt:layout>

