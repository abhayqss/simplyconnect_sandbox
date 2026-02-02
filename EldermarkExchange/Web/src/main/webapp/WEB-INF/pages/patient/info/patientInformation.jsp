<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="ccd" tagdir="/WEB-INF/tags/ccd" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:message code="patient.info.patientInfoHeaderLabel" var="patientInfoHeaderLabel"/>
<spring:message code="patient.info.ccd.tab" var="ccdTab"/>
<spring:message code="patient.info.documents.tab" var="documentsTab"/>
<spring:message code="patient.info.button.downloadPatientRecord" var="downloadPatientRecord"/>
<spring:message code="patient.info.button.attachFile" var="attachFile"/>
<spring:message code="patient.detail.field.viewHealthRecord" var="viewHealthRecord"/>

<spring:message code="patient.detail.field.patientDetails" var="patientDetails"/>
<spring:message code="patient.detail.field.dateOfBirth" var="dateOfBirthLabel"/>
<spring:message code="patient.detail.field.ssn" var="ssnLabel"/>
<spring:message code="patient.detail.field.gender" var="genderLabel"/>
<spring:message code="patient.detail.field.ssn" var="ssnLabel"/>
<spring:message code="patient.detail.field.relationship" var="relationshipLabel"/>
<spring:message code="patient.detail.field.guardiangs" var="guardiangsLabel"/>
<spring:message code="patient.detail.field.languages" var="languagesLabel"/>
<spring:message code="patient.detail.field.maritalStatus" var="maritalStatusLabel"/>
<spring:message code="patient.detail.field.ethnicGroup" var="ethnicGroupLabel"/>
<spring:message code="patient.detail.field.religion" var="religionLabel"/>
<spring:message code="patient.detail.field.race" var="raceLabel"/>
<spring:message code="patient.detail.field.providerOrganization" var="providerOrganizationLabel"/>

<c:set value="patient-search" var="patientSearchUrl"/>

<%-- <c:url value="/employee/company" var="companyNameUrl"/> --%>
<c:url value="/patient-info" var="patientInfoUrl"/>
<c:url value="/resources/images/nav-arrow-left.png" var="navArrowLeftImgUrl"/>

<c:set value="${patientInfoUrl}/${residentId}/ccd/headerDetails/${aggregated}?hashKey=${hashKey}" var="ccdHeaderDetailsUrl"/>


<wg:link id="patientInfoUrl" href="${patientInfoUrl}/" cssClass="hidden"/>
<input type="hidden" value="${aggregated}" id="aggregatedRecord"/>


<lt:layout cssClass="patientInfoNavigator ldr-center-block">
    <wg:link href="#${patientSearchUrl}" ajaxLoad="true" ajaxUrl="${patientSearchUrl}" cssClass="backToPatientList">
        <wg:img src="${navArrowLeftImgUrl}" cssClass="back"/>
    </wg:link>
    <wg:bread-crumbs>
        <%--<c:choose>--%>
            <%--<c:when test="${ccdLink}">--%>
                <%--<wg:crumb href="#${patientSearchUrl}" ajaxLoad="true" ajaxUrl="${patientSearchUrl}"--%>
                          <%--ajaxUrlParams="firstName=${patient.firstName}&lastName=${patient.lastName}&birthDate=${patient.birthDate}&gender=${patient.gender}&ssn=${fn:substring(patient.socialSecurity, 5, 9)}"--%>
                        <%-->--%>
                    <%--Search Results11</wg:crumb>--%>
            <%--</c:when>--%>
            <%--<c:otherwise>--%>
                <%--<wg:crumb href="#${patientSearchUrl}" ajaxLoad="true" ajaxUrl="${patientSearchUrl}">Search Results</wg:crumb>--%>
            <%--</c:otherwise>--%>
        <%--</c:choose>--%>


        <li><a href="#${patientSearchUrl}"
                        data-ajax-load="true"
                        data-ajax-url-tmpl="${patientSearchUrl}"
                        <c:if test="${ccdLink}">
                            data-ajax-url-params="firstName=${patient.firstName}&lastName=${patient.lastName}&birthDate=${patient.birthDate}&gender=${patient.gender}&ssn=${fn:substring(patient.socialSecurity, 7, 11)}"
                        </c:if>
                        class="crumb">Search Results</a>
        </li>
        <wg:crumb href="#" cssClass="active">${patient.fullName}</wg:crumb>
    </wg:bread-crumbs>
</lt:layout>

<lt:layout cssClass="patientInformation ldr-center-block">
    <lt:layout cssClass="patientInfoHeader panel panel-primary">
        <lt:layout cssClass="panel-heading">
            ${patientInfoHeaderLabel}
        </lt:layout>
    </lt:layout>

    <lt:layout cssClass="patientInfoBody">
        <lt:layout cssClass="downloadAttachBtnBox text-right">
            <a type="button"
                       class="btn btn-lg  btn-mid btn-default"
                       style="margin-right: 10px;"
                       target="_blank"
                       href = "transportation/${residentId}/history-ride"
                       id="rideHistoryBtn">
                <lt:layout cssClass="button-icon history-icon"/>
                RIDE HISTORY
            </a>
            <a type="button"
               class="btn btn-lg  btn-mid btn-primary"
               style="margin-right: 50px;"
               target="_blank"
               href = "transportation/${residentId}/request-new-ride"
               id="requestNewRide">
                <lt:layout cssClass="button-icon car-frontal-view-icon"/>
                REQUEST NEW RIDE
            </a>
            <wg:button domType="button"
                       cssClass="btn btn-lg btn-default downloadPatientRecordBtn"
                       name="downloadPatientRecordBtn"
                       id="downloadPatientRecordBtn">
                <lt:layout cssClass="glyphicon glyphicon-download-alt"/>
                ${downloadPatientRecord}
            </wg:button>
            <wg:button domType="button"
                       cssClass="btn btn-lg attachFileBtn btn-primary"
                       name="attachFile"
                       dataToggle="modal"
                       dataTarget="#uploadDocumentModal"
                       id="attachFile">
                <lt:layout cssClass="glyphicon glyphicon-paperclip"/>
                ${attachFile}
            </wg:button>
        </lt:layout>

        <wg:head size="1" cssClass="patientFullName">${patient.fullName}</wg:head>

        <lt:layout cssClass="ccdHeader">
            <c:if test="${not empty patient}">
                <lt:layout cssClass="ccdHeaderItem">
                    <lt:layout cssClass="commonInfo">

                        <lt:layout cssClass="item">
                            <lt:layout cssClass="row">
                                <wg:label cssClass="name">${patientDetails}</wg:label>
                            </lt:layout>
                            <ccd:person person="${patient.person}"/>
                            <ccd:label-for-value label="${ssnLabel}" value="${patient.socialSecurity}"/>
                            <ccd:label-for-value label="${dateOfBirthLabel}" value="${patient.birthDate}"/>
                            <ccd:label-for-value label="${genderLabel}" value="${patient.gender}"/>
                            <ccd:label-for-value label="${maritalStatusLabel}" value="${patient.maritalStatus}"/>
                            <ccd:label-for-value label="${ethnicGroupLabel}" value="${patient.ethnicGroup}"/>
                            <ccd:label-for-value label="${religionLabel}" value="${patient.religion}"/>
                            <ccd:label-for-value label="${raceLabel}" value="${patient.race}"/>

                            <c:if test="${not empty patient.languages}">
                                <lt:layout cssClass="row">
                                    <wg:label cssClass="text">${languagesLabel}</wg:label>
                                    <lt:layout cssClass="table-cell-box">
                                        <c:forEach var="language" items="${patient.languages}">
                                            <wg:label cssClass="value display-block">${language.code}</wg:label>
                                        </c:forEach>
                                    </lt:layout>
                                </lt:layout>
                            </c:if>

                            <lt:layout cssClass="row splitter"/>

                        </lt:layout>

                        <c:if test="${not empty patient.providerOrganization}">
                            <lt:layout cssClass="item">
                                <lt:layout cssClass="row">
                                    <wg:label cssClass="name">${providerOrganizationLabel}</wg:label>
                                </lt:layout>
                                <ccd:organization organization="${patient.providerOrganization}"/>
                            </lt:layout>
                        </c:if>

                    </lt:layout>

                    <c:if test="${not empty patient.guardians}">
                        <lt:layout cssClass="row">
                            <wg:label cssClass="name">${guardiangsLabel}</wg:label>
                        </lt:layout>
                        <lt:layout cssClass="guardians">
                            <c:forEach var="guardian" items="${patient.guardians}">
                                <lt:layout cssClass="item">
                                    <lt:layout cssClass="content">
                                        <ccd:label-for-value label="${relationshipLabel}"
                                                             value="${guardian.relationship}"/>
                                        <ccd:person person="${guardian.person}"/>
                                    </lt:layout>
                                </lt:layout>
                            </c:forEach>
                        </lt:layout>
                    </c:if>
                </lt:layout>
            </c:if>
            <wg:collapsed-panel id="ccdHeaderDetails"
                                cssClass="ccdHeaderDetails"
                                clpHeaderText="Additional Details"
                                ajax="true"
                                href="${ccdHeaderDetailsUrl}"
                                expHeaderText="Additional Details">
            </wg:collapsed-panel>
        </lt:layout>

        <wg:tabs cssClass="medicalDetails">
            <wg:tab-header>
                <wg:tab-head-item href="#ccdDetails" active="${searchScope == 'ELDERMARK'}">
                    ${ccdTab}
                </wg:tab-head-item>
                <wg:tab-head-item id="documentsTab"
                                  href="${patientInfoUrl}/${residentId}/documents/init/${aggregated}?hashKey=${hashKey}&databaseId=${databaseId}"
                                  active="${searchScope == 'NWHIN'}"
                                  ajax="true"
                                  target="#documents"
                                  badge="true">
                    ${documentsTab}
                </wg:tab-head-item>
            </wg:tab-header>
            <wg:tab-content>
                <wg:tab-content-item id="ccdDetails" active="${searchScope == 'ELDERMARK'}">
                    <%@include file="ccd/ccdSectionsDetails.jsp" %>
                </wg:tab-content-item>
                <wg:tab-content-item id="documents" active="${searchScope == 'NWHIN'}">
                    <c:if test="${searchScope == 'NWHIN'}">
                        <%@include file="documentList.jsp" %>
                    </c:if>
                </wg:tab-content-item>
            </wg:tab-content>
        </wg:tabs>
    </lt:layout>

</lt:layout>

<!-- Attach File Modal -->
<jsp:include page="uploadDocumentModal.jsp"/>