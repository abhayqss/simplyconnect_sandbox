<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="patient.info.ccd.allergies" var="allergiesLabel"/>
<spring:message code="patient.info.ccd.medications" var="medicationsLabel"/>
<spring:message code="patient.info.ccd.problems" var="problemsLabel"/>
<spring:message code="patient.info.ccd.procedures" var="proceduresLabel"/>
<spring:message code="patient.info.ccd.results" var="resultsLabel"/>
<spring:message code="patient.info.ccd.encounters" var="encountersLabel"/>
<spring:message code="patient.info.ccd.advanceDirectives" var="advanceDirectivesLabel"/>
<spring:message code="patient.info.ccd.familyHistory" var="familyHistoryLabel"/>
<spring:message code="patient.info.ccd.vitalSigns" var="vitalSignsLabel"/>
<spring:message code="patient.info.ccd.immunizations" var="immunizationsLabel"/>
<spring:message code="patient.info.ccd.payerProviders" var="payerProvidersLabel"/>
<spring:message code="patient.info.ccd.medicalEquipment" var="medicalEquipmentLabel"/>
<spring:message code="patient.info.ccd.socialHistory" var="socialHistoryLabel"/>
<spring:message code="patient.info.ccd.planOfCare" var="planOfCareLabel"/>
<spring:message code="patient.info.ccd.openAllBtn" var="openAllBtn"/>
<spring:message code="patient.info.ccd.closeAllBtn" var="closeAllBtn"/>
<spring:message code="patient.info.ccd.addToCcdBtn" var="addToCcdBtn"/>

<c:set value="/patient-info" var="patientInfoUrl"/>

<c:url value="${patientInfoUrl}/${residentId}/ccd/allergies/${aggregated}?hashKey=${hashKey}" var="allergiesViewUrl"/>
<c:url value="${patientInfoUrl}/${residentId}/ccd/problems/${aggregated}?hashKey=${hashKey}" var="problemsViewUrl"/>
<c:url value="${patientInfoUrl}/${residentId}/ccd/medications/${aggregated}?hashKey=${hashKey}" var="medicationsViewUrl"/>
<c:url value="${patientInfoUrl}/${residentId}/ccd/procedures/${aggregated}?hashKey=${hashKey}" var="proceduresViewUrl"/>
<c:url value="${patientInfoUrl}/${residentId}/ccd/results/${aggregated}?hashKey=${hashKey}" var="resultsViewUrl"/>
<c:url value="${patientInfoUrl}/${residentId}/ccd/encounters/${aggregated}?hashKey=${hashKey}" var="encountersViewUrl"/>
<c:url value="${patientInfoUrl}/${residentId}/ccd/advanceDirectives/${aggregated}?hashKey=${hashKey}" var="advanceDirectivesViewUrl"/>
<c:url value="${patientInfoUrl}/${residentId}/ccd/familyHistory/${aggregated}?hashKey=${hashKey}" var="familyHistoryViewUrl"/>
<c:url value="${patientInfoUrl}/${residentId}/ccd/vitalSigns/${aggregated}?hashKey=${hashKey}" var="vitalSignsViewUrl"/>
<c:url value="${patientInfoUrl}/${residentId}/ccd/immunizations/${aggregated}?hashKey=${hashKey}" var="immunizationsViewUrl"/>
<c:url value="${patientInfoUrl}/${residentId}/ccd/payerProviders/${aggregated}?hashKey=${hashKey}" var="payerProvidersViewUrl"/>
<c:url value="${patientInfoUrl}/${residentId}/ccd/medicalEquipment/${aggregated}?hashKey=${hashKey}" var="medicalEquipmentViewUrl"/>
<c:url value="${patientInfoUrl}/${residentId}/ccd/socialHistory/${aggregated}?hashKey=${hashKey}" var="socialHistoryViewUrl"/>
<c:url value="${patientInfoUrl}/${residentId}/ccd/planOfCare/${aggregated}?hashKey=${hashKey}" var="planOfCareViewUrl"/>

<lt:layout cssClass="ccdDetails">
    <lt:layout>
        <lt:layout cssClass="jumpPanel display-inline-block">
            <lt:layout cssClass="text-right openCloseAllBtnBox">
                <wg:button domType="button" cssClass="btn btn-md btn-default closeAllBtn hidden" name="closeAllBtn" id="closeAllBtn">
                    ${closeAllBtn}
                </wg:button>
                <wg:button domType="button" cssClass="btn btn-md btn-default openAllBtn" name="openAllBtn" id="openAllBtn">
                    ${openAllBtn}
                </wg:button>
                <wg:dropdown cssClass="pull-right">
                    <wg:dropdown-head href="#" id="addToCcdHead" cssClass="btn btn-md btn-primary addToCcdDropdownHead">
                        ${addToCcdBtn}&nbsp;
                    </wg:dropdown-head>
                    <wg:dropdown-body forHead="addToCcdHead" cssClass="dropdown-menu-right addToCcdDropdownBody">
                        <%--Uncomment item when section is implemented--%>
                        <%--<wg:dropdown-item--%>
                                <%--id="allergiesSectionOption"--%>
                                <%--cssClass="option "--%>
                                <%--href="#"--%>
                                <%--data-section-name="allergies">--%>
                            <%--${allergiesLabel}--%>
                        <%--</wg:dropdown-item>--%>
                        <%--<wg:dropdown-item--%>
                                <%--id="medicationsSectionOption"--%>
                                <%--cssClass="option "--%>
                                <%--href="#"--%>
                                <%--data-section-name="medications">--%>
                            <%--${medicationsLabel}--%>
                        <%--</wg:dropdown-item>--%>
                        <wg:dropdown-item
                                id="problemsSectionOption"
                                cssClass="option "
                                href="#"
                                data-section-name="problems">
                            ${problemsLabel}
                        </wg:dropdown-item>
                        <%--<wg:dropdown-item--%>
                                <%--id="proceduresSectionOption"--%>
                                <%--cssClass="option "--%>
                                <%--href="#">--%>
                            <%--${proceduresLabel}--%>
                        <%--</wg:dropdown-item>--%>
                        <%--<wg:dropdown-item--%>
                                <%--id="results"--%>
                                <%--cssClass="option "--%>
                                <%--href="#">--%>
                            <%--${resultsLabel}--%>
                        <%--</wg:dropdown-item>--%>
                        <%--<wg:dropdown-item--%>
                                <%--id="encountersSectionOption"--%>
                                <%--cssClass="option "--%>
                                <%--href="#">--%>
                            <%--${encountersLabel}--%>
                        <%--</wg:dropdown-item>--%>
                        <%--<wg:dropdown-item--%>
                                <%--id="advanceDirectivesSectionOption"--%>
                                <%--cssClass="option "--%>
                                <%--href="#">--%>
                            <%--${advanceDirectivesLabel}--%>
                        <%--</wg:dropdown-item>--%>
                        <%--<wg:dropdown-item--%>
                                <%--id="familyHistorySectionOption"--%>
                                <%--cssClass="option "--%>
                                <%--href="#">--%>
                            <%--${familyHistoryLabel}--%>
                        <%--</wg:dropdown-item>--%>
                        <%--<wg:dropdown-item--%>
                                <%--id="vitalSignsSectionOption"--%>
                                <%--cssClass="option "--%>
                                <%--href="#">--%>
                            <%--${vitalSignsLabel}--%>
                        <%--</wg:dropdown-item>--%>
                        <%--<wg:dropdown-item--%>
                                <%--id="immunizationsSectionOption"--%>
                                <%--cssClass="option "--%>
                                <%--href="#">--%>
                            <%--${immunizationsLabel}--%>
                        <%--</wg:dropdown-item>--%>
                        <%--<wg:dropdown-item--%>
                                <%--id="payerProvidersSectionOption"--%>
                                <%--cssClass="option "--%>
                                <%--href="#">--%>
                            <%--${payerProvidersLabel}--%>
                        <%--</wg:dropdown-item>--%>
                        <%--<wg:dropdown-item--%>
                                <%--id="medicalEquipmentSectionOption"--%>
                                <%--cssClass="option "--%>
                                <%--href="#">--%>
                            <%--${medicalEquipmentLabel}--%>
                        <%--</wg:dropdown-item>--%>
                        <%--<wg:dropdown-item--%>
                                <%--id="socialHistorySectionOption"--%>
                                <%--cssClass="option "--%>
                                <%--href="#">--%>
                            <%--${socialHistoryLabel}--%>
                        <%--</wg:dropdown-item>--%>
                        <%--<wg:dropdown-item--%>
                                <%--id="planOfCareSectionOption"--%>
                                <%--cssClass="option "--%>
                                <%--href="#">--%>
                            <%--${planOfCareLabel}--%>
                        <%--</wg:dropdown-item>--%>
                    </wg:dropdown-body>

                </wg:dropdown>

            </lt:layout>
            <lt:layout cssClass="jumpToLinks">
                <wg:link href="#allergies" ajaxLoad="true" ajaxAnchor="true" cssClass="jumpLnk">${allergiesLabel}</wg:link>
                <wg:link href="#medications" ajaxLoad="true" ajaxAnchor="true" cssClass="jumpLnk">${medicationsLabel}</wg:link>
                <wg:link href="#problems" ajaxLoad="true" ajaxAnchor="true" cssClass="jumpLnk">${problemsLabel}</wg:link>
                <wg:link href="#procedures" ajaxLoad="true" ajaxAnchor="true" cssClass="jumpLnk">${proceduresLabel}</wg:link>
                <wg:link href="#results" ajaxLoad="true" ajaxAnchor="true" cssClass="jumpLnk">${resultsLabel}</wg:link>
                <wg:link href="#encounters" ajaxLoad="true" ajaxAnchor="true" cssClass="jumpLnk">${encountersLabel}</wg:link>
                <wg:link href="#advanceDirectives" ajaxLoad="true" ajaxAnchor="true" cssClass="jumpLnk">${advanceDirectivesLabel}</wg:link>
                <wg:link href="#familyHistory" ajaxLoad="true" ajaxAnchor="true" cssClass="jumpLnk">${familyHistoryLabel}</wg:link>
                <wg:link href="#vitalSigns" ajaxLoad="true" ajaxAnchor="true" cssClass="jumpLnk">${vitalSignsLabel}</wg:link>
                <wg:link href="#immunizations" ajaxLoad="true" ajaxAnchor="true" cssClass="jumpLnk">${immunizationsLabel}</wg:link>
                <wg:link href="#payerProviders" ajaxLoad="true" ajaxAnchor="true" cssClass="jumpLnk">${payerProvidersLabel}</wg:link>
                <wg:link href="#medicalEquipment" ajaxLoad="true" ajaxAnchor="true" cssClass="jumpLnk">${medicalEquipmentLabel}</wg:link>
                <wg:link href="#socialHistory" ajaxLoad="true" ajaxAnchor="true" cssClass="jumpLnk">${socialHistoryLabel}</wg:link>
                <wg:link href="#planOfCare" ajaxLoad="true" ajaxAnchor="true" cssClass="jumpLnk">${planOfCareLabel}</wg:link>
            </lt:layout>
        </lt:layout>
    </lt:layout>

    <lt:layout cssClass="ccdDetailItems">
        <wg:collapsed-panel id="allergies" panelHeaderId = "allergiesCollapsedPanel"
                            theme="gray" cssClass="ccdDetailItem"
                            clpHeaderText="${allergiesLabel}"
                            expHeaderText="${allergiesLabel}"
                            ajax="true"
                            href="${allergiesViewUrl}"
                            badge="true">
        </wg:collapsed-panel>
        <wg:collapsed-panel id="medications" panelHeaderId = "medicationsCollapsedPanel"
                            theme="gray"
                            cssClass="ccdDetailItem"
                            clpHeaderText="${medicationsLabel}"
                            expHeaderText="${medicationsLabel}"
                            ajax="true"
                            href="${medicationsViewUrl}"
                            badge="true">
        </wg:collapsed-panel>
        <wg:collapsed-panel id="problems" panelHeaderId = "problemsCollapsedPanel"
                            theme="gray" cssClass="ccdDetailItem"
                            clpHeaderText="${problemsLabel}"
                            expHeaderText="${problemsLabel}"
                            ajax="true"
                            href="${problemsViewUrl}"
                            badge="true">
        </wg:collapsed-panel>
        <wg:collapsed-panel id="procedures" panelHeaderId = "proceduresCollapsedPanel"
                            theme="gray" cssClass="ccdDetailItem"
                            clpHeaderText="${proceduresLabel}"
                            expHeaderText="${proceduresLabel}"
                            ajax="true"
                            href="${proceduresViewUrl}"
                            badge="true">
        </wg:collapsed-panel>
        <wg:collapsed-panel id="results" panelHeaderId = "resultsCollapsedPanel"
                            theme="gray" cssClass="ccdDetailItem"
                            clpHeaderText="${resultsLabel}"
                            expHeaderText="${resultsLabel}"
                            ajax="true"
                            href="${resultsViewUrl}"
                            badge="true">
        </wg:collapsed-panel>
        <wg:collapsed-panel id="encounters" panelHeaderId = "encountersCollapsedPanel"
                            theme="gray" cssClass="ccdDetailItem"
                            clpHeaderText="${encountersLabel}"
                            expHeaderText="${encountersLabel}"
                            ajax="true"
                            href="${encountersViewUrl}"
                            badge="true">
        </wg:collapsed-panel>
        <wg:collapsed-panel id="advanceDirectives" panelHeaderId = "advanceDirectivesCollapsedPanel"
                            theme="gray" cssClass="ccdDetailItem"
                            clpHeaderText="${advanceDirectivesLabel}"
                            expHeaderText="${advanceDirectivesLabel}"
                            ajax="true"
                            href="${advanceDirectivesViewUrl}"
                            badge="true">
        </wg:collapsed-panel>
        <wg:collapsed-panel id="familyHistory" panelHeaderId = "familyHistoryCollapsedPanel"
                            theme="gray" cssClass="ccdDetailItem"
                            clpHeaderText="${familyHistoryLabel}"
                            expHeaderText="${familyHistoryLabel}"
                            ajax="true"
                            href="${familyHistoryViewUrl}"
                            badge="true">
        </wg:collapsed-panel>
        <wg:collapsed-panel id="vitalSigns" panelHeaderId = "vitalSignsCollapsedPanel"
                            theme="gray" cssClass="ccdDetailItem"
                            clpHeaderText="${vitalSignsLabel}"
                            expHeaderText="${vitalSignsLabel}"
                            ajax="true"
                            href="${vitalSignsViewUrl}"
                            badge="true">
        </wg:collapsed-panel>
        <wg:collapsed-panel id="immunizations" panelHeaderId = "immunizationsCollapsedPanel"
                            theme="gray" cssClass="ccdDetailItem"
                            clpHeaderText="${immunizationsLabel}"
                            expHeaderText="${immunizationsLabel}"
                            ajax="true"
                            href="${immunizationsViewUrl}"
                            badge="true">
        </wg:collapsed-panel>
        <wg:collapsed-panel id="payerProviders" panelHeaderId = "payerProvidersCollapsedPanel"
                            theme="gray" cssClass="ccdDetailItem"
                            clpHeaderText="${payerProvidersLabel}"
                            expHeaderText="${payerProvidersLabel}"
                            ajax="true"
                            href="${payerProvidersViewUrl}"
                            badge="true">
        </wg:collapsed-panel>
        <wg:collapsed-panel id="medicalEquipment" panelHeaderId = "medicalEquipmentCollapsedPanel"
                            theme="gray" cssClass="ccdDetailItem"
                            clpHeaderText="${medicalEquipmentLabel}"
                            expHeaderText="${medicalEquipmentLabel}"
                            ajax="true"
                            href="${medicalEquipmentViewUrl}"
                            badge="true">
        </wg:collapsed-panel>
        <wg:collapsed-panel id="socialHistory" panelHeaderId = "socialHistoryCollapsedPanel"
                            theme="gray" cssClass="ccdDetailItem"
                            clpHeaderText="${socialHistoryLabel}"
                            expHeaderText="${socialHistoryLabel}"
                            ajax="true"
                            href="${socialHistoryViewUrl}"
                            badge="true">
        </wg:collapsed-panel>
        <wg:collapsed-panel id="planOfCare" panelHeaderId = "planOfCareCollapsedPanel"
                            theme="gray" cssClass="ccdDetailItem"
                            clpHeaderText="${planOfCareLabel}"
                            expHeaderText="${planOfCareLabel}"
                            ajax="true"
                            href="${planOfCareViewUrl}"
                            badge="true">
        </wg:collapsed-panel>
    </lt:layout>
</lt:layout>

<lt:layout id="dataSourceDetailsTemplate" cssClass="datasourcePreview hidden">
    <lt:layout id="dataSourceNameLayout">
        <wg:label cssClass="text">Organization Name</wg:label>
        <wg:label id="dataSourceName" cssClass="value"></wg:label>
    </lt:layout>
    <lt:layout id="dataSourceOIDLayout">
        <wg:label cssClass="text">Organization OID</wg:label>
        <wg:label id="dataSourceOID" cssClass="value"></wg:label>
    </lt:layout>
    <lt:layout id="communityNameLayout">
        <wg:label cssClass="text">Community Name</wg:label>
        <wg:label id="communityName" cssClass="value"></wg:label>
    </lt:layout>
    <lt:layout id="communityOIDLayout">
        <wg:label cssClass="text">Community OID</wg:label>
        <wg:label id="communityOID" cssClass="value"></wg:label>
    </lt:layout>

</lt:layout>

<lt:layout id="ccdRowActions" cssClass="hidden rowActions">
    <a type="button" class="btn btn-default editCcd hidden">
        <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
    </a>
    <a type="button"
       class="btn btn-default deleteCcd hidden"
       data-toggle="modal"
       data-target="#deleteCareTeamMemberModal">
        <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
    </a>
</lt:layout>

<lt:layout id="ccdRowActions" cssClass="hidden rowActions">
    <a type="button" class="btn btn-default editCcd hidden">
        <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
    </a>
    <a type="button"
       class="btn btn-default deleteCcd hidden"
       data-toggle="modal"
       data-target="#deleteCareTeamMemberModal">
        <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
    </a>
</lt:layout>

<%-- =================== Ccd Modal ========================== --%>
<div id="ccdContainer"></div>

