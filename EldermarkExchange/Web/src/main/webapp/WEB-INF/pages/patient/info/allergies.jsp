<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="patient.info.ccd.allergies.product" var="productLabel"/>
<spring:message code="patient.info.ccd.allergies.status" var="statusLabel"/>
<spring:message code="patient.info.ccd.allergies.reactions" var="reactionsLabel"/>

<c:set value="/patient-info" var="patientInfoUrl"/>

<c:url value="${patientInfoUrl}/${residentId}/ccd/allergies/${aggregated}/results?hashKey=${hashKey}" var="allergiesDataUrl"/>

<spring:message code="patient.info.ccd.datasource" var="dataSourceLabel"/>
<c:choose>
    <c:when test="${aggregated}">
        <c:set value="product,status,reactions,dataSource" var="columnIds"/>
        <c:set value="${productLabel},${statusLabel},${reactionsLabel},${dataSourceLabel}" var="columnNames"/>
        <c:set value="string,string,string,custom" var="columnFormats"/>
    </c:when>
    <c:otherwise>
        <c:set value="product,status,reactions" var="columnIds"/>
        <c:set value="${productLabel},${statusLabel},${reactionsLabel}" var="columnNames"/>
        <c:set value="string,string,string" var="columnFormats"/>
    </c:otherwise>
</c:choose>

<lt:layout cssClass="allergiesTable">
    <wg:link name="allergies"/>
    <lt:layout>
        <wg:grid id="allergiesList"
                    colIds="${columnIds}"
                    colNames="${columnNames}"
                    colFormats="${columnFormats}"
                    dataUrl="${allergiesDataUrl}"/>
    </lt:layout>
</lt:layout>

