<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="patient.info.ccd.payerProviders.insuranceInfo" var="insuranceInfoLabel"/>
<spring:message code="patient.info.ccd.payerProviders.insuranceType" var="insuranceTypeLabel"/>
<spring:message code="patient.info.ccd.payerProviders.coverageDateStart" var="coverageDateStartLabel"/>
<spring:message code="patient.info.ccd.payerProviders.coverageDateEnd" var="coverageDateEndLabel"/>

<c:set value="/patient-info" var="patientInfoUrl"/>

<c:url value="${patientInfoUrl}/${residentId}/ccd/payerProviders/${aggregated}/results?hashKey=${hashKey}" var="payerProvidersDataUrl"/>

<spring:message code="patient.info.ccd.datasource" var="dataSourceLabel"/>
<c:choose>
    <c:when test="${aggregated}">
        <c:set value="insuranceInfo,insuranceType,coverageDateStart,coverageDateEnd,dataSource" var="columnIds"/>
        <c:set value="${insuranceInfoLabel},${insuranceTypeLabel},${coverageDateStartLabel},${coverageDateEndLabel},${dataSourceLabel}" var="columnNames"/>
        <c:set value="string,string,string,string,custom" var="columnFormats"/>
    </c:when>
    <c:otherwise>
        <c:set value="insuranceInfo,insuranceType,coverageDateStart,coverageDateEnd" var="columnIds"/>
        <c:set value="${insuranceInfoLabel},${insuranceTypeLabel},${coverageDateStartLabel},${coverageDateEndLabel}" var="columnNames"/>
        <c:set value="string,string,string,string" var="columnFormats"/>
    </c:otherwise>
</c:choose>

<lt:layout cssClass="payerProvidersTable">
    <wg:link name="payerProviders"/>
    <lt:layout>
        <wg:grid id="payerProvidersList"
                 colIds="${columnIds}"
                 colNames="${columnNames}"
                 colFormats="${columnFormats}"
                 dataUrl="${payerProvidersDataUrl}"/>
    </lt:layout>
</lt:layout>