<%@ page contentType="text/html;charset=UTF-8" language="java" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="wgForm" uri="http://www.springframework.org/tags/form" %>

<spring:message code="patient.search.table.header.label" var="patientSearchTableHeaderLabel"/>

<spring:message code="patient.search.table.header.residentNumber" var="residentNumber"/>
<spring:message code="patient.search.table.header.firstName" var="firstName"/>
<spring:message code="patient.search.table.header.lastName" var="lastName"/>
<spring:message code="patient.search.table.header.gender" var="gender"/>
<spring:message code="patient.search.table.header.dateOfBirth" var="dateOfBirth"/>
<spring:message code="patient.search.table.header.community" var="community"/>
<spring:message code="patient.search.table.header.sourceCompany" var="sourceCompany"/>

<!- Patient Search Results -->

<lt:layout cssClass="patientListBox panel panel-primary">
    <lt:layout cssClass="boxHeader panel panel-heading">
        ${patientSearchTableHeaderLabel}
    </lt:layout>
    <lt:layout cssClass="boxBody">
        <%--<lt:layout id="searchMergedCheckLayout" style="padding:5px 0;" cssClass="col-md-6  hidden">--%>
            <%--<wg:label><input type="checkbox" id="searchMergedCheck"  class="showDeactivatedRecords">Show Merged Records</input>--%>
                <%--</wg:label>--%>
        <%--</lt:layout>--%>
        <wg:grid id="patientList" cssClass="patientList"
                 colIds="firstName,lastName,genderDisplayName,dateOfBirth,organizationName,databaseName,dateCreated,actions"
                 colNames="${firstName},${lastName},${gender},${dateOfBirth},${community},${sourceCompany},Date Created, "
                 colFormats="string,string,string,string,string,string,string,fake"
                 dataUrl="patient-search/results-scope"
                 deferLoading="true"/>
    </lt:layout>
</lt:layout>

<!- Patient Preview -->

<spring:message code="patient.detail.field.dateOfBirth" var="poDateOfBirth"/>
<spring:message code="patient.detail.field.name" var="poName"/>
<spring:message code="patient.detail.field.gender" var="poGender"/>
<spring:message code="patient.detail.field.ssn" var="poSsn"/>
<spring:message code="patient.detail.field.contactInfo" var="poContactInfo"/>
<spring:message code="patient.detail.field.contactInfo.primaryHome" var="poPrimaryHome"/>
<spring:message code="patient.detail.field.patientPreview" var="patientPreview"/>
<spring:message code="patient.detail.field.viewHealthRecord" var="viewHealthRecord"/>
<spring:message code="patient.detail.field.showAggregatedRecord" var="showAggregatedRecord"/>

<c:set var="patientInfoUrl" value="patient-info"/>
<lt:layout id="patientPreviewTemplate" cssClass="patientPreview hidden">
    <lt:layout cssClass="head">
        <wg:label cssClass="name">${patientPreview}</wg:label>
        <wg:link href="${patientInfoUrl}" cssClass="recordLink">${viewHealthRecord}</wg:link>
    </lt:layout>
    <lt:layout>
        <wg:label cssClass="text">${poName}</wg:label>
        <wg:label id="fullName" cssClass="value"></wg:label>
    </lt:layout>
    <lt:layout>
        <wg:label cssClass="text">${poGender}</wg:label>
        <wg:label id="genderDisplayName" cssClass="value"></wg:label>
    </lt:layout>
    <lt:layout>
        <wg:label cssClass="text">${poSsn}</wg:label>
        <wg:label id="ssn" cssClass="value"></wg:label>
    </lt:layout>
    <lt:layout>
        <wg:label cssClass="text">${poDateOfBirth}</wg:label>
        <wg:label id="dateOfBirth" cssClass="value"></wg:label>
    </lt:layout>
    <lt:layout>
        <wg:label id="contactInfoLabel" cssClass="text">${poContactInfo}</wg:label>
        <wg:label>
            <span class="value">${poPrimaryHome}</span>
            <br/>
            <span id="streetAddress" class="value"></span>
            <br/>
            <span id="cityStateAndPostalCode" class="value"></span>
        </wg:label>
    </lt:layout>

</lt:layout>
