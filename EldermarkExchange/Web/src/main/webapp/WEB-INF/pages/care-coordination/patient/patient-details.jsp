<%@ page import="com.scnsoft.eldermark.entity.CareTeamRoleCode" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="cc" tagdir="/WEB-INF/tags/cc" %>
<%@taglib prefix="format" tagdir="/WEB-INF/tags/format" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- <c:set value="/patient-info" var="patientInfoUrl"/> --%>
<c:url value="/care-coordination/patients/patient" var="deleteDocumentUrl"/>

<c:url value="/patient-info" var="patientInfoUrl"/>
<c:url value="patient-info" var="patientInfoModuleUrl"/>

<c:url value="/care-coordination/patient/service-plans" var="servicePlansUrl"/>
<c:url value="care-coordination/service-plans" var="servicePlansModuleUrl"/>

<c:url value="/resources/images/video_call.png" var="videoCall"/>
<c:url value="/resources/images/add_note.png" var="addNote"/>
<c:url value="/resources/images/add_event.png" var="addEvent"/>
<c:url value="/resources/images/more_options.png" var="moreOptions"/>
<wg:link id="patientInfoUrl" href="${patientInfoUrl}/" cssClass="hidden"/>
<wg:link id="deleteDocumentUrl" href="${deleteDocumentUrl}/" cssClass="hidden"/>
<jsp:useBean id="affiliatedView" scope="request" type="java.lang.Boolean"/>

<%--@elvariable id="patient" type="com.scnsoft.eldermark.shared.carecoordination.PatientDto"--%>
<div class="ldr-ui-layout patientInfoNavigator whiteBackground ldr-center-block" style="padding:10px;">
    <ol class="breadcrumb">
        <li class="backToPatientList">
            <span class="ldr-ui-label">
                <c:url value="/resources/images/nav-arrow-left.png" var="backImg"/>
                <img src="${backImg}" class="back">
            </span>
            <span class="crumb">
                Patient List
            </span>
        </li>
        <li class="active">
            <span class="crumb">
                Details of ${patient.displayName}
            </span>
        </li>
    </ol>

    <wg:dropdown cssClass="icon-link-dropdown pull-right patientOptions">
        <wg:dropdown-head href="#" id="userOptionsHead">
            <lt:layout cssClass="display-inline-block text-right valign-middle head-wrp">
                <wg:link id="moreOptionsLink" cssClass="no-text-decoration">
                    <wg:img cssClass="icon-link-img" src="${moreOptions}"/><span class="icon-link-text">More Options</span>
                </wg:link>
            </lt:layout>
        </wg:dropdown-head>
        <wg:dropdown-body forHead="userOptionsHead" cssClass="dropdown-menu-right">
            <%-- phase 2 --%>
            <%-- <wg:dropdown-item
                    cssClass="option "
                    href="#">
                Call
            </wg:dropdown-item>
            <hr class="patientOptionsLine"/> --%>
            <sec:authorize access="<%=CareTeamRoleCode.IS_CAN_ADD_EDIT_ASSESSMENT_RESULTS %>">
                <wg:dropdown-item
                        id="addAssessmentResults"
                        cssClass="option "
                        href="#">
                    Add Assessment Results
                </wg:dropdown-item>
            </sec:authorize>
            <sec:authorize access="<%=CareTeamRoleCode.IS_CAN_ADD_VIEW_SERVICE_PLANS %>">
                <wg:dropdown-item
                        id="addServicePlan"
                        cssClass="option "
                        href="#url%5Btemplate%5D=care-coordination/patients/${patient.id}/create-service-plan">
                    Create Service Plan
                </wg:dropdown-item>
            </sec:authorize>
            <wg:dropdown-item
                    cssClass="option "
                    ajaxLoad="false"
                    href="transportation/${patient.id}/request-new-ride"
                    newWindow="true">
                Request a new ride
            </wg:dropdown-item>
            <wg:dropdown-item
                    cssClass="option"
                    ajaxLoad="false"
                    href="transportation/${patient.id}/history-ride"
                    newWindow="true">
                Ride History
            </wg:dropdown-item>
            <c:if test="${showTestIncomingCallLink}">
                <wg:dropdown-item
                        id="testIncomingCall"
                        cssClass="option"
                        ajaxLoad="false">
                    Test Incoming Call Notification
                </wg:dropdown-item>
            </c:if>
        </wg:dropdown-body>
    </wg:dropdown>

        <c:if test="${not affiliatedView}">
            <wg:link id="createNewEvent" cssClass="icon-link pull-right no-text-decoration">
                <wg:img cssClass="icon-link-img" src="${addEvent}"/><span class="icon-link-text">Add a New Event</span>
            </wg:link>
        </c:if>

    <wg:link id="addNote" cssClass="icon-link pull-right no-text-decoration">
        <wg:img cssClass="icon-link-img" src="${addNote}"/><span class="icon-link-text">Add a Note</span>
    </wg:link>

    <c:if test="${not (empty loggedInEmployeeNucleusUserId or empty patientNucleusUserId)}">
        <wg:link id="videoCallLink" cssClass="icon-link pull-right no-text-decoration">
            <wg:img cssClass="icon-link-img" src="${videoCall}"/><span class="icon-link-text">Video Call</span>
        </wg:link>
    </c:if>

</div>



<wg:tabs cssClass="whiteBackground">
    <wg:tab-header>
        <wg:tab-head-item id="patientDetailsTab"
                          cssClass="tab patientTabs"
                          href="#patientDetailsInfoContent"
                          active="true"
                >Patient Details</wg:tab-head-item>

        <wg:tab-head-item id="patientEventsTab"
                          cssClass="tab patientTabs"
                          href="#patientEventsContent"
                >Patient Events</wg:tab-head-item>

        <wg:tab-head-item id="ccdDetailsTab"
                          cssClass="tab patientTabs"
                          href="#patientCCDContent"
                >CCD Details</wg:tab-head-item>

        <wg:tab-head-item id="documentsTab"
                          cssClass="tab patientTabs"
                          href="#patientDocumentsContent">
            Documents List (<span id="documentsCountTabPanel"></span>)
        </wg:tab-head-item>

        <wg:tab-head-item id="notesTab"
                          cssClass="tab patientTabs"
                          href="#patientNotesContent">
            Notes (<span id="notesCountTabPanel"></span>)
        </wg:tab-head-item>

        <wg:tab-head-item id="assessmentsTab"
                          cssClass="tab patientTabs"
                          href="#patientAssessmentsContent">
            Assessments (<span id="assessmentsCountTabPanel"></span>)
        </wg:tab-head-item>

        <wg:tab-head-item id="servicePlansTab"
                          cssClass="tab patientTabs"
                          href="#patientServicePlansContent">
            Service Plans (<span id="servicePlanCountTabPanel"></span>)
        </wg:tab-head-item>
    </wg:tab-header>
    <wg:tab-content>
        <wg:tab-content-item id="patientDetailsInfoContent" active="true" cssClass="patientTabContent patientDetailsInfoContent">

            <lt:layout cssClass="col-md-12 patientDetailsInfoContent">
                <lt:layout style="padding:20px 0 0 5px">
                    <span style="display: inline-block; margin-top:7px;" class="sectionHead">${patient.displayName}</span>
                    <c:if test="${patient.editable}">
                        <a type="button" style="margin-left:10px" class="btn btn btn-primary pull-right"
                           id="editPatient">EDIT RECORD</a>
                        <a type="button" class="btn btn-default pull-right" id="deactivateRecord">
                            <c:if test="${patient.active}">DE</c:if>ACTIVATE RECORD</a>
                    </c:if>
                </lt:layout>
                <div id="newlyCreatedAlert" class="alert alert-success patientDetailsAlert">
                    Patient’s record has been created.
                    <c:if test="${canAddCtm}">
                        Please add patient care team members who will be notified about an event relating to that patient.
                    </c:if>
                </div>
                <div id="activationAlert" class="alert alert-success patientDetailsAlert"></div>
                <lt:layout cssClass="col-md-12 patientDetailsDetailsContent" style="margin-left:20px">

                    <input type="hidden" value="${patient.id}" id="currentPatientId"/>
                    <%--<input type="hidden" value="${patient.communityId}" id="communityId"/>--%>
                    <input type="hidden" value="${patient.organizationId}" id="organizationId"/>
                    <input type="hidden" value="${patient.hashKey}" id="hashKey"/>
                    <input type="hidden" value="${affiliatedView}" id="affiliatedView" >
                    <input type="hidden" value="${hasMerged}" id="hasMerged" >
                    <%--<cc:label-for-value label="Name :" value="${patient.displayName}"/>--%>

                    <div class="ldr-ui-layout col-md-12">
                        <p class="col-md-4 eventLabel">Name </p>
                        <p class="col-md-8">
                                ${patient.displayName}
                        </p>
                    </div>
                    <cc:label-for-value label="Social Security Number " value="${patient.ssn}"/>
                    <cc:label-for-value label="Date of Birth " value="${patient.birthDate}"/>
                    <cc:label-for-value label="Gender  " value="${patient.gender}"/>
                    <cc:label-for-value label="Marital Status " value="${patient.maritalStatus}"/>
                    <cc:label-for-value label="Status " value="${patient.status}"/>
                    <cc:label-for-value label="Address " value="${patient.address.displayAddress}"/>
                    <cc:label-for-value label="Organization " value="${patient.organization}"/>
                    <cc:label-for-value label="Community " value="${patient.community}"/>

                    <c:if test="${not empty patient.admitDates}">
                        <cc:label-for-value label="Admit " cssStyle="display: flex; flex-direction: row;">
                            <jsp:attribute name="value">
                                <c:forEach var="admitDate" items="${patient.admitDates}" varStatus="loop">
                                    <cc:local-date-format date="${admitDate}" pattern="MM/DD/YYYY hh:mm A" />
                                    <c:if test="${loop.index lt (fn:length(patient.admitDates)-1)}"><span>,&nbsp;</span></c:if>
                                </c:forEach>
                            </jsp:attribute>
                        </cc:label-for-value>
                    </c:if>
                    <c:if test="${not empty patient.dischargeDates}">
                        <cc:label-for-value label="Discharge " cssStyle="display: flex; flex-direction: row;">
                            <jsp:attribute name="value">
                                <c:forEach var="dischargeDate" items="${patient.dischargeDates}" varStatus="loop">
                                    <cc:local-date-format date="${dischargeDate}" pattern="MM/DD/YYYY hh:mm A" />
                                    <c:if test="${loop.index lt (fn:length(patient.admitDates)-1)}"><span>,&nbsp;</span></c:if>
                                </c:forEach>
                            </jsp:attribute>
                        </cc:label-for-value>
                    </c:if>
                    <c:if test="${not empty patient.deathDate}">
                        <cc:label-for-value label="Death date ">
                            <jsp:attribute name="value">
                                <cc:local-date-format date="${patient.deathDate}" pattern="MM/DD/YYYY hh:mm A" />
                            </jsp:attribute>
                        </cc:label-for-value>
                    </c:if>

                </lt:layout>
            </lt:layout>

            <c:if test="${(not empty patient.cellPhone)|| (not empty patient.homePhone) || (not empty patient.workPhone) ||
                          (not empty patient.primaryCarePhysicians) ||
                          (not empty patient.specialtyPhysicians) ||
                          (not empty patient.pharmacyDtos) ||
                          (not empty patient.medicareNumber)|| (not empty patient.medicaidNumber) || (not empty patient.healthPlans)
                                || (not empty patient.dentalPlan)

            }">
            <lt:layout cssClass="col-md-12 patientDetailsCareTeamContent">
                <lt:layout style="padding:20px 0;">
                    <wg:collapsed-panel id = "adTags"
                                        theme="gray"
                                        headerCssClass="patientDetailsCareTeamSectionHeader"
                                        cssClass="ccdDetailItem"
                                        clpHeaderText="Additional Details"
                                        expHeaderText="Additional Details">
                        <c:if test="${(not empty patient.cellPhone)|| (not empty patient.homePhone) || (not empty patient.workPhone)}">
                            <lt:layout id="phone-numbers" cssClass="col-md-12 noteSection no-border">
                                <span style = "font-weight: bold; padding-left: 20px;" class="table-title">Phone Numbers</span>
                                <lt:layout cssClass="col-md-12 patientDetailsDetailsContent" style="margin-left:20px">
                                    <cc:label-for-value label="Cell phone #" value="${patient.cellPhone}"/>
                                    <cc:label-for-value label="Home phone #" value="${patient.homePhone}"/>
                                    <cc:label-for-value label="Work phone #" value="${patient.workPhone}"/>
                                </lt:layout>
                            </lt:layout>
                        </c:if>

                        <c:if test="${not empty patient.primaryCarePhysicians}">
                            <lt:layout id="primary-care-physician" cssClass="col-md-12 noteSection no-border">
                                <span style = "font-weight: bold; padding-left: 20px;" class="table-title">Primary Care Physician</span>

                                <c:forEach var="entry" items="${patient.primaryCarePhysicians}">
                                    <lt:layout cssClass="col-md-12 patientDetailsDetailsContent" style="margin-left:20px">
                                        <cc:label-for-value label="Name" value="${entry.primaryCarePhysician}"/>
                                        <cc:label-for-value label="Phone #" value="${entry.primaryCarePhysicianPhone}"/>
                                        <cc:label-for-value label="Address" value="${entry.primaryCarePhysicianAddress}"/>
                                    </lt:layout>
                                </c:forEach>
                            </lt:layout>
                        </c:if>

                        <c:if test="${not empty patient.specialtyPhysicians}">
                            <lt:layout id="specialty-physicians" cssClass="col-md-12 noteSection no-border">
                                <span style = "font-weight: bold; padding-left: 20px;" class="table-title">Specialty Physician</span>
                                <c:forEach var="entry" items="${patient.specialtyPhysicians}">
                                    <lt:layout cssClass="col-md-12 patientDetailsDetailsContent" style="margin-left:20px">
                                        <cc:label-for-value label="Name " value="${entry.name}"/>
                                        <cc:label-for-value label="Specialty/Role " value="${entry.role}"/>
                                        <cc:label-for-value label="Phone #" value="${entry.phone}"/>
                                        <cc:label-for-value label="Address " value="${entry.address}"/>
                                    </lt:layout>
                                </c:forEach>
                            </lt:layout>
                        </c:if>

                        <c:if test="${not empty patient.pharmacyDtos}">
                            <lt:layout id="pharmacy" cssClass="col-md-12 noteSection no-border">
                                <span style = "font-weight: bold; padding-left: 20px;" class="table-title">Pharmacy</span>
                                <c:forEach var="entry" items="${patient.pharmacyDtos}">
                                    <lt:layout cssClass="col-md-12 patientDetailsDetailsContent" style="margin-left:20px">
                                        <cc:label-for-value label="Name" value="${entry.name}"/>
                                        <cc:label-for-value label="Phone #" value="${entry.phone}"/>
                                        <cc:label-for-value label="Address" value="${entry.address}"/>
                                    </lt:layout>
                                </c:forEach>
                            </lt:layout>
                        </c:if>

                        <c:if test="${(not empty patient.medicareNumber)|| (not empty patient.medicaidNumber) || (not empty patient.healthPlans)
                                || (not empty patient.dentalPlan)}">
                            <lt:layout id="billing" cssClass="col-md-12 noteSection no-border">
                                <span style = "font-weight: bold; padding-left: 20px;" class="table-title">Billing</span>

                                <lt:layout cssClass="col-md-12 patientDetailsDetailsContent" style="margin-left:20px">
                                    <cc:label-for-value label="Medicare # " value="${patient.medicareNumber}"/>
                                    <cc:label-for-value label="Medicaid  # " value="${patient.medicaidNumber}"/>
                                    <cc:label-for-value label="Health Plans " value="${patient.healthPlans}"/>
                                    <cc:label-for-value label="Dentail Plan " value="${patient.dentalPlan}"/>
                                </lt:layout>
                            </lt:layout>
                        </c:if>
                    </wg:collapsed-panel>
                    <%--<span style="display: inline-block; margin-top:7px;" class="sectionHead">Additional Details </span>--%>
                </lt:layout>
            </lt:layout>
            </c:if>

            <lt:layout cssClass="col-md-12 patientDetailsCareTeamContent">
                <lt:layout style="padding:20px 0;">
                    <span style="display: inline-block; margin-top:7px;" class="sectionHead">Patient Care Team </span>
                    <c:if test="${canAddCtm}">
                        <wg:button id="createCareTeamMember"
                                   domType="link"
                                   cssClass="btn-primary createCareTeamMember pull-right">ADD NEW MEMBER</wg:button>
                    </c:if>
                </lt:layout>

                <lt:layout cssClass="patientListBox panel panel-primary">
                    <lt:layout cssClass="boxHeader panel panel-heading">
                        Patient Care Team Members
                    </lt:layout>
                    <lt:layout cssClass="boxBody">
                        <c:choose>
                            <c:when test="${affiliatedView && not hasMerged}">
                                <wg:grid id="patientCareTeam"
                                         colIds="employee.label,role.label,description"
                                         colNames="Name, Role, Description"
                                         dataUrl="care-coordination/patients/patient/${patientId}/care-team/false"
                                         colFormats="string,string,string"/>
                            </c:when>
                            <c:when test="${affiliatedView && hasMerged}">
                                <wg:grid id="patientCareTeam"
                                         colIds="employee.label,role.label,description,employeeDatabaseName"
                                         colNames="Name, Role, Description, Organization"
                                         dataUrl="care-coordination/patients/patient/${patientId}/care-team/false"
                                         colFormats="string,string,string,string"/>
                            </c:when>
                            <c:when test="${not affiliatedView && hasMerged}">
                                <wg:grid id="patientCareTeam"
                                         colIds="employee.label,role.label,description,employeeDatabaseName,actions"
                                         colNames="Name, Role, Description, Organization, Actions"
                                         dataUrl="care-coordination/patients/patient/${patientId}/care-team/false"
                                         colFormats="string,string,string,string,fake"/>
                            </c:when>
                            <c:otherwise>
                                <wg:grid id="patientCareTeam"
                                         colIds="employee.label,role.label,description,actions"
                                         colNames="Name, Role, Description,Actions"
                                         dataUrl="care-coordination/patients/patient/${patientId}/care-team/false"
                                         colFormats="string,string,string,fake"/>
                            </c:otherwise>
                        </c:choose>

                    </lt:layout>
                </lt:layout>
            </lt:layout>
            <c:if test="${hasAffiliated || affiliatedView}">
                <lt:layout cssClass="col-md-12 patientDetailsCareTeamContent">
                    <lt:layout style="padding:20px 0;">
                        <span style="display: inline-block; margin-top:7px;" class="sectionHead">People Responsible for Handling Events Submitted for ${patient.displayName}</span>
                        <c:if test="${hasAddAffiliatedCtm}">
                            <a type="button" class="btn btn-primary createCareTeamMember pull-right" id="createAffiliatedCareTeamMember">ADD NEW MEMBER</a>
                        </c:if>

                    </lt:layout>

                    <lt:layout cssClass="patientListBox panel panel-primary">
                        <lt:layout cssClass="boxHeader panel panel-heading">
                            Patient Care Team Members
                        </lt:layout>
                        <lt:layout cssClass="boxBody">

                            <c:choose>
                                <c:when test="${not hasMerged}">
                                    <wg:grid id="affiliatedPatientCareTeam"
                                             colIds="employee.label,role.label,description,actions"
                                             colNames="Name, Role, Description,Actions"
                                             dataUrl="care-coordination/patients/patient/${patientId}/care-team/true"
                                             colFormats="string,string,string,fake"/>
                                </c:when>
                                <c:otherwise>
                                    <wg:grid id="affiliatedPatientCareTeam"
                                             colIds="employee.label,role.label,description,employeeDatabaseName,residentDatabaseName,actions"
                                             colNames="Name, Role, Description,Organization, Primary Organization, Actions"
                                             dataUrl="care-coordination/patients/patient/${patientId}/care-team/true"
                                             colFormats="string,string,string,string,string,fake"/>
                                </c:otherwise>
                            </c:choose>


                        </lt:layout>
                    </lt:layout>
                </lt:layout>
            </c:if>
        </wg:tab-content-item>

        <wg:tab-content-item id="patientEventsContent" cssClass="patientTabContent patientEventsContent">
             <jsp:include page="patient-events-log.jsp"/>
        </wg:tab-content-item>

        <wg:tab-content-item id="patientCCDContent" cssClass="patientTabContent patientCCDContent">
            <jsp:include page="../../patient/info/ccd/ccdSectionsDetails.jsp"/>
        </wg:tab-content-item>

        <wg:tab-content-item id="patientDocumentsContent" cssClass="patientTabContent patientDocumentsContent">
            <jsp:include page="../../patient/info/documentList.jsp">
                <jsp:param name="composeBtnName" value="composeMsgBtnCC"></jsp:param>
            </jsp:include>
        </wg:tab-content-item>

        <wg:tab-content-item id="patientNotesContent" cssClass="patientTabContent patientNotesContent">
            <jsp:include page="patient-notes-log.jsp"/>
        </wg:tab-content-item>

        <wg:tab-content-item id="patientAssessmentsContent" cssClass="patientTabContent patientAssessmentsContent">
            <jsp:include page="patient-assessments.jsp"/>
        </wg:tab-content-item>

        <wg:tab-content-item id="patientServicePlansContent" cssClass="patientTabContent patientServicePlansContent"/>

    </wg:tab-content>
</wg:tabs>

<lt:layout id="rowActions" cssClass="hidden rowActions">
    <a type="button" class="btn btn-default videoCallCareTeamMember hidden">
        <span class="glyphicon glyphicon-facetime-video" aria-hidden="true"></span>
    </a>
    <a type="button" class="btn btn-default editCareTeamMember hidden">
        <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
    </a>
    <a type="button"
       class="btn btn-default deleteCareTeamMember hidden"
       data-toggle="modal"
       data-target="#deleteCareTeamMemberModal">
        <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
    </a>
</lt:layout>

<lt:layout id="assessmentRowActions" cssClass="hidden rowActions">
    <a type="button" class="toolbar__item btn-default toolbar__edit-item editResidentAssessmentResult invisible">
        <img src="${pageContext.request.contextPath}/resources/images/pencil-edit-1.svg" aria-hidden="true">
    </a>
    <a type="button" class="toolbar__item btn-default toolbar__download-item downloadResidentAssessmentResult invisible">
        <img src="${pageContext.request.contextPath}/resources/images/download-arrow.svg" aria-hidden="true">
    </a>
</lt:layout>
<%-- =================== Create Care Team Member Modal ========================== --%>
<div id="createCareTeamContainer"></div>
<%-- =================== Delete CTM Confirmation =================== --%>
<div id="deleteCareTeamContainer"></div>
<div id="editPatientContainer"></div>
<jsp:include page="../../patient/info/uploadDocumentModal.jsp"/>
<%-- =================== Create New Event Modal ========================== --%>
<div id="createNewEventContainer"></div>
<%-- =================== Note Modal ========================== --%>
<div id="noteContainer"></div>
<%-- =================== Assesment Modal ========================== --%>
<div id="assessmentContainer"></div>
<div id="assessmentViewContainer"></div>
<div id="assessmentHistoryViewContainer"></div>
<%-- =================== Ccd Modal ========================== --%>
<div id="ccdContainer"></div>

<script language="JavaScript" type="text/javascript">
    <%-- startCall() is used in module.patients.js --%>
    function startCall(toId) {
        var FromID = "${loggedInEmployeeNucleusUserId}";
        var ToID = toId || "${patientNucleusUserId}";
        // for debug
        //var FromID = "11111111-1111-1111-1111-111111111111";
        //var ToID = "11111111-1111-1111-1111-111111111111";

        setFrameSrc("${nucleusHost}", "${nucleusAuthToken}", FromID, ToID);
    }
</script>