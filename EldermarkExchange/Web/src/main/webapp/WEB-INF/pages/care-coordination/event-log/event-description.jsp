<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="ccd" tagdir="/WEB-INF/tags/ccd" %>
<%@taglib prefix="cc" tagdir="/WEB-INF/tags/cc" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:url value="/resources/images/add_note.png" var="addNoteImg"/>
<c:url value="/resources/images/download.png" var="downloadImg"/>
<c:url value="/resources/images/search-icon.svg" var="incidentReportImg"/>

<%--@elvariable id="canCurrentUserCreateIr" type="java.lang.String"--%>
<%--@elvariable id="event" type="com.scnsoft.eldermark.shared.carecoordination.events.EventDto"--%>
<lt:layout cssClass="col-md-12" style="padding-top: 30px;padding-right:0px;">
    <lt:layout style="float:right; padding-bottom:25px;">
        <c:if test="${canCurrentUserCreateIr eq 'true'}">
            <c:if test="${event.isIrRequired && event.irId == null}">
                <wg:link id="createIncidentReport" cssClass="icon-link no-text-decoration">
                    <wg:img cssClass="icon-link-img" src="${incidentReportImg}"/>
                    <span class="icon-link-text">Create Incident Report</span>
                </wg:link>
            </c:if>
            <c:if test="${event.isIrRequired && event.irId != null}">
                <button id="incidentReport" class="icon-link incident-report-btn no-text-decoration">
                    <wg:img cssClass="icon-link-img" src="${incidentReportImg}"/>
                    <span class="icon-link-text">Incident Report</span>
                    <div id="incidentReportPopup" style="display: none" class="incident-report-popup">
                        <a id="editIncidentReport" data-report-id="${event.irId}" class="no-text-decoration">
                            <span class="icon-link-text">Edit Incident Report</span>
                        </a>
                        <a id="viewIncidentReport" data-report-id="${event.irId}" class="no-text-decoration">
                            <span class="icon-link-text">View Incident Report</span>
                        </a>
                    </div>
                </button>
            </c:if>
        </c:if>
        <wg:link id="addEventNote" cssClass="icon-link no-text-decoration">
            <wg:img cssClass="icon-link-img" src="${addNoteImg}"/><span class="icon-link-text">Add a Note</span>
        </wg:link>
        <wg:link id="downloadBtn" cssClass="icon-link no-text-decoration">
            <wg:img cssClass="icon-link-img" src="${downloadImg}"/><span class="icon-link-text">Download pdf</span>
        </wg:link>
    </lt:layout>
    <lt:layout>
        <lt:layout cssClass="jumpPanel" style="display:table;width:100%">
            <lt:layout cssClass="jumpToSection">
                <wg:label cssClass="jumpToLabel">Jump to:</wg:label>
                <wg:link href="#info_${param.tab}" ajaxLoad="true" ajaxAnchor="true"
                         cssClass="jumpLnk">Patient Info</wg:link>
                <wg:link href="#essentials_${param.tab}" ajaxLoad="true" ajaxAnchor="true"
                         cssClass="jumpLnk">Event Essentials</wg:link>
                <wg:link href="#description_${param.tab}" ajaxLoad="true" ajaxAnchor="true"
                         cssClass="jumpLnk">Event Description</wg:link>
                <c:if test="${event.includeTreatingPhysician || event.includeHospital}">
                    <wg:link href="#treatment_${param.tab}" ajaxLoad="true" ajaxAnchor="true"
                             cssClass="jumpLnk">Treatment Details</wg:link>
                </c:if>
                <c:if test="${event.includeManager}">
                    <wg:link href="#manager_${param.tab}" ajaxLoad="true" ajaxAnchor="true"
                             cssClass="jumpLnk">Details of Responsible Manager</wg:link>
                </c:if>
                <c:if test="${event.includeResponsible}">
                    <wg:link href="#nurse_${param.tab}" ajaxLoad="true" ajaxAnchor="true"
                             cssClass="jumpLnk">Details of Registered Nurse (RN)</wg:link>
                </c:if>
                <c:if test="${event.adtSegmentPV1 != null}">
                    <wg:link href="#adtSegmentPV1_${param.tab}" ajaxLoad="true" ajaxAnchor="true"
                             cssClass="jumpLnk">Patient Visit</wg:link>
                </c:if>
                <c:if test="${not empty event.adtSegmentsPR1}">
                    <wg:link href="#procedures_${param.tab}" ajaxLoad="true" ajaxAnchor="true"
                             cssClass="jumpLnk">Procedures</wg:link>
                </c:if>
                <c:if test="${not empty event.adtSegmentsIN1}">
                    <wg:link href="#insurance_${param.tab}" ajaxLoad="true" ajaxAnchor="true"
                             cssClass="jumpLnk">Insurance</wg:link>
                </c:if>
                <c:if test="${not empty event.adtSegmentsDG1}">
                    <wg:link href="#diagnosis_${param.tab}" ajaxLoad="true" ajaxAnchor="true"
                             cssClass="jumpLnk">Diagnosis</wg:link>
                </c:if>
                <c:if test="${not empty event.adtSegmentsGT1}">
                    <wg:link href="#guarantor_${param.tab}" ajaxLoad="true" ajaxAnchor="true"
                             cssClass="jumpLnk">Guarantor</wg:link>
                </c:if>
                <c:if test="${not empty event.adtSegmentsAL1}">
                    <wg:link href="#guarantor_${param.tab}" ajaxLoad="true" ajaxAnchor="true"
                             cssClass="jumpLnk">Allergies</wg:link>
                </c:if>
                <c:if test="${event.relatedNotes != null}">
                    <wg:link href="#relatedNotes_${param.tab}" ajaxLoad="true" ajaxAnchor="true"
                             cssClass="jumpLnk">Related Notes</wg:link>
                </c:if>
            </lt:layout>
        </lt:layout>
    </lt:layout>
    <lt:layout cssClass="col-md-12 eventNone eventSection no-border noPadding">
        You received this alert because you are assigned as the responsible party for event types of "<b>${event.eventDetails.eventType}</b>"
        occur for <b>${event.patient.displayName}</b>
    </lt:layout>
    <lt:layout cssClass="jumpToLinks">
    </lt:layout>
    <wgForm:form cssClass="form-inline">
        <fmt:formatDate pattern='MM/dd/yyyy' value='${event.patient.birthDate}' var="birthDate"/>
        <input type="hidden" value="${eventId}" id="currentEventId"/>
        <lt:layout id="info_${param.tab}" cssClass="noPadding noTopBorder col-md-12  eventSection">
            <span class="sectionHeadEvent col-md-12">Patient Info</span>
            <lt:layout id="patient_info_${param.tab}">
                <span class="sectionSubHeadEvent col-md-12 segmentContent"></span>
                <cc:label-for-value label="Patient Name" value="${event.patient.displayName}"/>
                <cc:label-for-value label="Social Security Number" value="${event.patient.ssn}"/>
                <cc:label-for-value label="Date of Birth" value="${birthDate}"/>
                <cc:label-for-value label="Gender" value="${event.patient.gender}"/>
                <cc:label-for-value label="Marital Status" value="${event.patient.maritalStatus}"/>
                <cc:label-for-value label="Address" value="${event.patient.address.displayAddress}"/>
                <cc:label-for-value label="Organization" value="${event.patient.organization}"/>
                <c:if test="${event.adtSegmentPID != null}">
                    <cc:label-for-list-value singularLabel="Patient Identifier"
                                             pluralLabel="Patient Identifiers"
                                             valueList="${event.adtSegmentPID.patientIdentifiers}"/>
                    <cc:label-for-list-value singularLabel="Mother's Maiden Name"
                                             pluralLabel="Mother's Maiden Names"
                                             valueList="${event.adtSegmentPID.mothersMaidenNames}"/>
                    <cc:label-for-list-value singularLabel="Patient Alias"
                                             pluralLabel="Patient Aliases"
                                             valueList="${event.adtSegmentPID.patientAliases}"/>

                    <c:forEach items="${event.adtSegmentPID.phoneNumbersHome}" var="phoneNumberHome" varStatus="varStatus">
                        <cc:label-for-xtn-value value="${phoneNumberHome}" label="Phone Number - Home #${varStatus.index + 1}"/>
                    </c:forEach>

                    <c:forEach items="${event.adtSegmentPID.phoneNumbersBusiness}" var="phoneNumberBusiness" varStatus="varStatus">
                        <cc:label-for-xtn-value value="${phoneNumberBusiness}" label="Phone Number - Business #${varStatus.index + 1}"/>
                    </c:forEach>

                    <cc:label-for-list-value singularLabel="Race"
                                             pluralLabel="Races"
                                             valueList="${event.adtSegmentPID.races}"/>
                    <cc:label-for-value label="Primary Language" value="${event.adtSegmentPID.primaryLanguage}"/>
                    <cc:label-for-value label="Religion" value="${event.adtSegmentPID.religion}"/>
                    <cc:label-for-value label="Patient Account Number" value="${event.adtSegmentPID.patientAccountNumber}"/>
                    <cc:label-for-dln-value label="Driver's License Number" value="${event.adtSegmentPID.driverLicenseNumber}"/>
                    <cc:label-for-list-value singularLabel="Mother Identifier"
                                             pluralLabel="Mother Identifiers"
                                             valueList="${event.adtSegmentPID.motherIdentifiers}"/>
                    <cc:label-for-list-value singularLabel="Ethnic Group"
                                             pluralLabel="Ethnic Groups"
                                             valueList="${event.adtSegmentPID.etnicGroups}"/>
                    <cc:label-for-value label="Birth Place" value="${event.adtSegmentPID.birthPlace}"/>
                    <cc:label-for-value label="Birth Order" value="${event.adtSegmentPID.birthOrder}"/>
                    <cc:label-for-list-value singularLabel="Citizenship"
                                             pluralLabel="Citizenships"
                                             valueList="${event.adtSegmentPID.citizenships}"/>
                    <cc:label-for-value label="Veterans Military Status"
                                        value="${event.adtSegmentPID.veteransMilitaryStatus}"/>
                    <cc:label-for-value label="Nationality" value="${event.adtSegmentPID.nationality}"/>
                </c:if>
                <fmt:formatDate type="BOTH" pattern='MM/dd/yyyy hh:mm a z XXX' value='${event.eventDetails.deathDateTime}' var="deathDateTime"/>
                <cc:label-for-value label="Death Date" value="${deathDateTime}"/>
                <c:if test="${event.eventDetails.deathIndicator}">
                    <cc:label-for-value label="Death Indicator" value="${event.eventDetails.deathIndicator}"/>
                </c:if>
            </lt:layout>
        </lt:layout>
        <lt:layout id="essentials_${param.tab}" cssClass="noPadding noTopBorder col-md-12  eventSection">
            <span class="sectionHeadEvent col-md-12">Event Essentials</span>
            <lt:layout id="event_essentials_${param.tab}">
                <span class="sectionSubHeadEvent col-md-12 segmentContent"></span>
                <cc:label-for-value label="Person Submitting Event" value="${event.employee.displayName}"/>
                <cc:label-for-value label="Care Team Role" value="${event.employee.role}"/>
                <cc:label-for-value label="Event Date and Time" id="eventDetailsDateTime">
                    <jsp:attribute name="value">
                        <cc:local-date-format date="${event.eventDetails.eventDatetime}" pattern="MM/DD/YYYY hh:mm A Z" />
                    </jsp:attribute>
                </cc:label-for-value>
                <b><cc:label-for-value label="Event Type" value="${event.eventDetails.eventType}"/></b>
                <cc:label-for-value label="Emergency Department Visit" value="${event.eventDetails.emergencyVisit}"/>
                <cc:label-for-value label="Overnight In-patient" value="${event.eventDetails.overnightPatient}"/>
                <cc:label-for-value label="Patient Device ID" value="${event.eventDetails.deviceId}"/>
                <c:if test="${not empty event.adtSegmentEVN}">
                    <cc:label-for-value label="Event Type Code" value="${event.adtSegmentEVN.eventTypeCode}"/>
                    <fmt:formatDate type="BOTH" pattern='MM/dd/yyyy hh:mm a z XXX' value='${event.adtSegmentEVN.recordedDateTime}' var="recordedDateTime"/>
                    <cc:label-for-value label="Recorded Date/Time" value="${recordedDateTime}"/>
                    <cc:label-for-value label="Event Reason Code" value="${event.adtSegmentEVN.eventReasonCode}"/>
                    <fmt:formatDate type="BOTH" pattern='MM/dd/yyyy hh:mm a z XXX' value='${event.adtSegmentEVN.eventOccured}' var="eventOccured" />
                    <cc:label-for-value label="Event Occurred" value="${eventOccured}"/>
                </c:if>
            </lt:layout>
        </lt:layout>
        <lt:layout id="description_${param.tab}" cssClass="noPadding noTopBorder col-md-12  eventSection">
            <span class="sectionHeadEvent col-md-12">Event Description</span>
            <lt:layout id="event_description_${param.tab}">
                <span class="sectionSubHeadEvent col-md-12 segmentContent"></span>
                <cc:label-for-value label="Location" value="${event.eventDetails.location}"/>
                <cc:label-for-value label="Injury" value="${event.eventDetails.injury}"/>
                <cc:label-for-multiline-value label="Situation" value="${event.eventDetails.situation}"/>
                <cc:label-for-value label="Background" value="${event.eventDetails.background}"/>
                <%-- <cc:label-for-value label="Assessment" value="${event.eventDetails.assessment}"/> --%>
                <c:forEach items="${event.eventDetails.assessment.split(';')}" var="assessment" varStatus="loop">
                    <c:if test="${loop.index == 0}">
                        <cc:label-for-value label="Assessment" value="${assessment}"/>
                    </c:if>
                    <c:if test="${loop.index == 1}">
                        <c:if test="${event.eventDetails.assessmentCompletedDate != null}">
                            <cc:label-for-value label="Event Date and Time" id="eventDetailsDateTime">
                                 <jsp:attribute name="value">
                                    Date Completed: <cc:local-date-format date="${event.eventDetails.assessmentCompletedDate}" pattern="MM/DD/YYYY hh:mm A Z"/>
                                </jsp:attribute>
                            </cc:label-for-value>
                        </c:if>
                        <c:if test="${event.eventDetails.assessmentCompletedDate == null}">
                            <cc:label-for-value label="" value="${assessment}"/>
                        </c:if>
                    </c:if>
                    <c:if test="${loop.index > 1}">
                        <cc:label-for-value label=""  value="${assessment}"/>
                    </c:if>
                </c:forEach>
                <cc:label-for-value label="Follow Up Expected" value="${event.eventDetails.followUpExpected}"/>
                <cc:label-for-value label="Follow Up Details" value="${event.eventDetails.followUpDetails}"/>
            </lt:layout>
        </lt:layout>
        <%--========================= Treating ====================================--%>
        <c:if test="${event.includeTreatingPhysician || event.includeHospital}">
            <lt:layout id="treatment_${param.tab}" cssClass="noPadding noTopBorder col-md-12  eventSection">
                <span class="sectionHeadEvent col-md-12">Treatment Details</span>
                <c:if test="${event.includeTreatingPhysician}">
                    <span class="sectionSubHeadEvent col-md-12 segmentContent">Details of Treating Physician</span>
                    <cc:label-for-value label="Physician Name" value="${event.treatingPhysician.displayName}"/>
                    <c:if test="${event.treatingPhysician.includeAddress}">
                        <cc:label-for-value label="Address"
                                            value="${event.treatingPhysician.address.displayAddress}"/>
                    </c:if>
                    <cc:label-for-value label="Phone" value="${event.treatingPhysician.phone}"/>
                </c:if>
                <c:if test="${event.includeHospital}">
                    <span class="sectionSubHeadEvent col-md-12 segmentContent">Details of Treating Hospital</span>
                    <cc:label-for-value label="Hospital/Clinic" value="${event.treatingHospital.name}"/>
                    <c:if test="${event.treatingHospital.includeAddress}">
                        <cc:label-for-value label="Address" value="${event.treatingHospital.address.displayAddress}"/>
                    </c:if>
                    <cc:label-for-value label="Phone" value="${event.treatingHospital.phone}"/>
                </c:if>
            </lt:layout>
        </c:if>
        <%--======================================================--%>
        <%--================= Manager =====================================--%>
        <c:if test="${event.includeManager}">
            <lt:layout id="manager_${param.tab}" cssClass="noPadding noTopBorder col-md-12 eventSection">
                <span class="sectionHeadEvent col-md-12">Details of Responsible Manager</span>
                <lt:layout id="manager_description_${param.tab}">
                    <span class="sectionSubHeadEvent col-md-12 segmentContent"></span>
                    <cc:label-for-value label="Manager Name" value="${event.manager.displayName}"/>
                    <cc:label-for-value label="Phone" value="${event.manager.phone}"/>
                    <cc:label-for-value label="Email" value="${event.manager.email}"/>
                </lt:layout>
            </lt:layout>
        </c:if>
        <c:if test="${event.includeResponsible}">
            <lt:layout id="nurse_${param.tab}" cssClass="noPadding noTopBorder col-md-12 eventSection">
                <span class="sectionHeadEvent col-md-12">Details of Registered Nurse (RN)</span>
                <lt:layout id="nurse_description_${param.tab}">
                    <span class="sectionSubHeadEvent col-md-12 segmentContent"></span>
                    <cc:label-for-value label="RN Name" value="${event.responsible.displayName}"/>
                    <c:if test="${event.responsible.includeAddress}">
                        <cc:label-for-value label="Address" value="${event.responsible.address.displayAddress}"/>
                    </c:if>
                </lt:layout>
            </lt:layout>
        </c:if>
        <c:if test="${event.adtSegmentPV1 != null}">
            <lt:layout id="adtSegmentPV1_${param.tab}" cssClass="noPadding noTopBorder col-md-12 eventSection">
                <span class="sectionHeadEvent col-md-12">Patient Visit</span>
                <lt:layout id="adtSegmentPV1_description_${param.tab}">
                    <span class="sectionSubHeadEvent col-md-12 segmentContent"></span>
                    <cc:label-for-value label="Patient Class" value="${event.adtSegmentPV1.patientClass}"/>
                    <cc:label-for-patient-location-value label="Assigned Patient Location"
                                                         value="${event.adtSegmentPV1.assignedPatientLocation}"/>
                    <cc:label-for-value label="Admission type" value="${event.adtSegmentPV1.admissionType}"/>
                    <cc:label-for-patient-location-value label="Prior Patient Location"
                                                         value="${event.adtSegmentPV1.priorPatientLocation}"/>
                    <cc:label-for-list-value singularLabel="Attending Doctor"
                                             pluralLabel="Attending Doctors"
                                             valueList="${event.adtSegmentPV1.attendingDoctors}"/>
                    <cc:label-for-list-value singularLabel="Referring Doctor"
                                             pluralLabel="Referring Doctors"
                                             valueList="${event.adtSegmentPV1.referringDoctors}"/>
                    <cc:label-for-list-value singularLabel="Consulting Doctor"
                                             pluralLabel="Consulting Doctors"
                                             valueList="${event.adtSegmentPV1.consultingDoctors}"/>
                    <cc:label-for-value label="Preadmit Test Indicator"
                                        value="${event.adtSegmentPV1.preadmitTestIndicator}"/>
                    <cc:label-for-value label="Re-admission Indicator"
                                        value="${event.adtSegmentPV1.readmissionIndicator}"/>
                    <cc:label-for-value label="Admit Source" value="${event.adtSegmentPV1.admitSource}"/>
                    <cc:label-for-list-value singularLabel="Ambulatory Status"
                                             pluralLabel="Ambulatory Statuses"
                                             valueList="${event.adtSegmentPV1.ambulatoryStatuses}"/>
                    <cc:label-for-value label="Discharge Disposition"
                                        value="${event.adtSegmentPV1.dischargeDisposition}"/>
                    <c:if test="${not empty event.adtSegmentPV1.dischargedToLocation}">
                        <cc:label-for-value label="Discharged to Location"
                                            value="${event.adtSegmentPV1.dischargedToLocation.dischargeLocation}"/>
                    </c:if>
                    <fmt:formatDate type="BOTH" pattern='MM/dd/yyyy hh:mm a z XXX' value='${event.patientVisit.admitDateTime}' var="admitDateTime"/>
                    <cc:label-for-value label="Admit Date/Time" value="${admitDateTime}"/>
                    <fmt:formatDate type="BOTH" pattern='MM/dd/yyyy hh:mm a zXXX' value='${event.adtSegmentPV1.dischargeDatetime}' var="dischargeDateTime" />
                    <cc:label-for-value label="Discharge Date/Time" value="${dischargeDateTime}"/>
                    <cc:label-for-value label="Servicing Facility" value="${event.adtSegmentPV1.servicingFacility}"/>
                </lt:layout>
            </lt:layout>
        </c:if>
        <c:if test="${not empty event.adtSegmentsPR1}">
            <lt:layout id="procedures_${param.tab}" cssClass="noPadding noTopBorder col-md-12 eventSection">
                <span class="sectionHeadEvent col-md-12">Procedures</span>
                <c:forEach items="${event.adtSegmentsPR1}" var="procedure" varStatus="status">
                    <lt:layout id="#procedure${status.index}_${param.tab}" cssClass="col-md-12 segmentContent">
                        <c:if test="${fn:length(event.adtSegmentsPR1) gt 1}">
                            <span class="sectionSubHeadEvent col-md-12">Procedure #${status.index + 1}</span>
                        </c:if>
                        <cc:label-for-value label="Set Id" value="${procedure.setId}"/>
                        <cc:label-for-value label="Procedure Coding Method" value="${procedure.procedureCodingMethod}"/>
                        <cc:label-for-coded-element-value label="Procedure Code" value="${procedure.procedureCode}"/>
                        <cc:label-for-value label="Procedure Description" value="${procedure.procedureDescription}"/>
                        <fmt:formatDate type="BOTH" pattern='MM/dd/yyyy hh:mm a zXXX' value='${procedure.procedureDatetime}'
                                        var="procedureDatetime"/>
                        <cc:label-for-value label="Procedure Date/Time" value="${procedureDatetime}"/>
                        <cc:label-for-value value="${procedure.procedureFunctionalType}" label="Procedure Functional Type"/>
                        <cc:label-for-coded-element-value label="Associated Diagnosis Code"
                                                          value="${procedure.associatedDiagnosisCode}"/>
                    </lt:layout>
                </c:forEach>
            </lt:layout>
        </c:if>
        <c:if test="${not empty event.adtSegmentsIN1}">
            <lt:layout id="insurance_${param.tab}" cssClass="noPadding noTopBorder col-md-12 eventSection">
                <span class="sectionHeadEvent col-md-12">Insurance</span>
                <c:forEach items="${event.adtSegmentsIN1}" var="insurance" varStatus="status">
                    <lt:layout id="#procedure${status.index}_${param.tab}" cssClass="col-md-12 segmentContent">
                        <c:if test="${fn:length(event.adtSegmentsIN1) gt 1}">
                            <span class="sectionSubHeadEvent col-md-12">Insurance #${status.index + 1}</span>
                        </c:if>
                        <cc:label-for-value label="Set Id" value="${insurance.setId}"/>
                        <cc:label-for-coded-element-value label="Insurance Plan ID" value="${insurance.insurancePlanId}"/>
                        <cc:label-for-value label="Insurance Company ID" value="${insurance.insuranceCompanyId != null ? insurance.insuranceCompanyId.pId : ''}"/>
                        <cc:label-for-value label="Insurance Company Name" value="${insurance.insuranceCompanyName != null ? insurance.insuranceCompanyName.organizationName : ''}"/>
                        <c:forEach items="${insurance.insuranceCoPhoneNumbers}" var="insuranceCoPhoneNumber" varStatus="varStatus">
                            <cc:label-for-xtn-value value="${insuranceCoPhoneNumber}" label="Insurance Co Phone Number #${varStatus.index + 1}"/>
                        </c:forEach>
                        <%--private List<XTNPhoneNumberDto> insuranceCoPhoneNumbers;--%>
                        <%--private String groupNumber;--%>
                        <%--private List<XONExtendedCompositeNameAndIdForOrganizationsDto> groupNames;--%>
                        <fmt:formatDate type="BOTH" pattern='MM/dd/yyyy hh:mm a z XXX'
                                        value='${insurance.planEffectiveDate}'
                                        var="planEffectiveDate"/>
                        <fmt:formatDate type="BOTH" pattern='MM/dd/yyyy hh:mm a z XXX'
                                        value='${insurance.planExpirationDate}'
                                        var="planExpirationDate"/>
                        <cc:label-for-value label="Plan Effective Date" value="${planEffectiveDate}"/>
                        <cc:label-for-value label="Plan Expiration Date" value="${planExpirationDate}"/>
                        <cc:label-for-value label="Plan Type" value="${insurance.planType}"/>
                        <%--private List<String> namesOfInsured;--%>
                        <cc:label-for-coded-element-value label="Insured's Relationship to Patient" value="${insurance.insuredsRelationshipToPatient}"/>
                    </lt:layout>
                </c:forEach>
            </lt:layout>
        </c:if>
        <c:if test="${not empty event.adtSegmentsDG1}">
            <lt:layout id="diagnosis_${param.tab}" cssClass="noPadding noTopBorder col-md-12 eventSection">
                <span class="sectionHeadEvent col-md-12">Diagnosis</span>
                <c:forEach items="${event.adtSegmentsDG1}" var="diagnosis" varStatus="status">
                    <lt:layout id="#diagnosis${status.index}_${param.tab}" cssClass="col-md-12 segmentContent">
                        <c:if test="${fn:length(event.adtSegmentsDG1) gt 1}">
                            <span class="sectionSubHeadEvent col-md-12">Diagnosis #${status.index + 1}</span>
                        </c:if>
                        <cc:label-for-value label="Set ID" value="${diagnosis.setId}"/>
                        <cc:label-for-value label="Diagnosis Coding Method" value="${diagnosis.diagnosisCodingMethod}"/>
                        <cc:label-for-coded-element-value label="Diagnosis Code" value="${diagnosis.diagnosisCode}"/>
                        <cc:label-for-value label="Diagnosis Description" value="${diagnosis.diagnosisDescription}"/>
                        <fmt:formatDate type="BOTH" pattern='MM/dd/yyyy hh:mm a z XXX' value='${diagnosis.diagnosisDateTime}'
                                        var="diagnosisDateTime"/>
                        <cc:label-for-value label="Diagnosis Date/Time" value="${diagnosisDateTime}"/>
                        <cc:label-for-value label="Diagnosis Type" value="${diagnosis.diagnosisType}"/>
                        <cc:label-for-list-value singularLabel="Diagnosing Clinician"
                                                 pluralLabel="Diagnosing Clinicians"
                                                 valueList="${diagnosis.diagnosingClinicianList}"/>
                    </lt:layout>
                </c:forEach>
            </lt:layout>
        </c:if>
        <c:if test="${not empty event.adtSegmentsGT1}">
            <lt:layout id="guarantor_${param.tab}" cssClass="noPadding noTopBorder col-md-12 eventSection">
                <span class="sectionHeadEvent col-md-12">Guarantor</span>
                <c:forEach items="${event.adtSegmentsGT1}" var="guarantor" varStatus="status">
                    <lt:layout id="#guarantor${status.index}_${param.tab}" cssClass="col-md-12 segmentContent">
                        <c:if test="${fn:length(event.adtSegmentsGT1) gt 1}">
                            <span class="sectionSubHeadEvent col-md-12">Guarantor #${status.index + 1}</span>
                        </c:if>
                        <cc:label-for-value label="Set ID" value="${guarantor.setId}"/>
                        <cc:label-for-list-value singularLabel="Name"
                                                 pluralLabel="Names"
                                                 valueList="${guarantor.guarantorNameList}"/>
                        <cc:label-for-coded-element-value label="Primary Language" value="${guarantor.primaryLanguage}"/>
                        <cc:label-for-list-value singularLabel="Address"
                                                 pluralLabel="Addresses"
                                                 valueList="${guarantor.guarantorAddressList}"/>
                        <c:forEach items="${guarantor.guarantorPhNumHomeList}" var="phNum" varStatus="varStatus">
                            <cc:label-for-xtn-value value="${phNum}" label="Guarantor Ph Num-Home #${varStatus.index + 1}"/>
                        </c:forEach>
                    </lt:layout>
                </c:forEach>
            </lt:layout>
        </c:if>
        <c:if test="${not empty event.adtSegmentsAL1}">
            <lt:layout id="allergies_${param.tab}" cssClass="noPadding noTopBorder col-md-12 eventSection">
                <span class="sectionHeadEvent col-md-12">Allergies</span>
                <c:forEach items="${event.adtSegmentsAL1}" var="allergies" varStatus="status">
                    <lt:layout id="#allergies${status.index}_${param.tab}" cssClass="col-md-12 segmentContent">
                        <c:if test="${fn:length(event.adtSegmentsAL1) gt 1}">
                            <span class="sectionSubHeadEvent col-md-12">Allergy #${status.index + 1}</span>
                        </c:if>
                        <cc:label-for-value label="Set ID" value="${allergies.setId}"/>
                        <cc:label-for-value label="Allergy Type" value="${allergies.allergyType}"/>
                        <cc:label-for-value label="Allergy Severity" value="${allergies.allergySeverity}"/>
                        <cc:label-for-list-value singularLabel="Allergy Reaction"
                                                 pluralLabel="Allergy Reactions"
                                                 valueList="${allergies.allergyReactionList}"/>
                    </lt:layout>
                </c:forEach>
            </lt:layout>
        </c:if>
        <%--======================================================--%>
        <%--================= Related notes =====================================--%>
        <c:if test="${event.relatedNotes != null}">
            <lt:layout id="relatedNotes_${param.tab}" cssClass="noPadding noTopBorder col-md-12 eventSection">
                <span class="sectionHeadEvent col-md-12">Related Notes</span>
                <c:forEach items="${event.relatedNotes}" var="relatedNote">
                    <fmt:formatDate type="BOTH" pattern='MM/dd/yyyy hh:mm a (z)' value='${relatedNote.lastModifiedDate}'
                                    var="historyLastModifiedDate"/>
                    <lt:layout cssClass="col-md-12 segmentContent">
                        <p class="col-md-4 eventLabel">Note</p>
                        <p class="col-md-6">
                                ${relatedNote.status} by ${relatedNote.personSubmittingNote}, ${relatedNote.role}
                            on ${historyLastModifiedDate}
                        </p>
                        <p class="col-md-2 view-details-link">
                            <wg:link ajaxUrl="care-coordination/patients"
                                     ajaxUrlParams="note=${relatedNote.id}&patient=${relatedNote.patientId}"
                                     cssClass="relatedNoteLink pull-right" ajaxLoad="true" ajaxAnchor="true">
                                <span class="icon-link-text">View Details</span>
                            </wg:link>
                        </p>
                    </lt:layout>
                </c:forEach>
            </lt:layout>
        </c:if>
        <%--======================================================--%>
    </wgForm:form>
</lt:layout>