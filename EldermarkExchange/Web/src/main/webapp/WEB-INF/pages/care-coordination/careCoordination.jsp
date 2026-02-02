<%@ page import="com.scnsoft.eldermark.authentication.SecurityExpressions" %>
<%@ page import="com.scnsoft.eldermark.entity.CareTeamRoleCode" %>
<%@page contentType="text/html;charset=UTF-8" language="java" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:url value="/care-coordination/events-log" var="eventsLogUrl"/>
<c:url value="care-coordination/events-log" var="eventsLogModuleUrl"/>

<c:url value="care-coordination/templates/communities" var="communityModuleUrl"/>
<c:url value="/care-coordination/templates/communities" var="communityUrl"/>

<c:url value="care-coordination/patients" var="patientsModuleUrl"/>
<c:url value="/care-coordination/patients" var="patientsUrl"/>

<c:url value="care-coordination/contacts" var="contactsModuleUrl"/>
<c:url value="/care-coordination/contacts" var="contactsUrl"/>


<c:url value="care-coordination/templates/organizations" var="organizationModuleUrl"/>
<c:url value="/care-coordination/templates/organizations" var="organizationUrl"/>

<%--<sec:authorize access="<%=SecurityExpressions.IS_ROLES_CAN_VIEW_CONTACTS%>">--%>
    <%--<c:set var="isRolesCanViewCOntacts" value="true"/>--%>
<%--</sec:authorize>--%>

<wg:tabs>
    <wg:tab-header>
        <wg:tab-head-item
                          href="${patientsUrl}"
                          ajaxUrlLoad="true"
                          ajaxUrlTemplate="${patientsModuleUrl}"
                          ajax="true"
                          target="#patientsTabContent"
                          id="patientsTab"
                >Patients (<span id="patientsCountTabPanel">${currentOrgPatientsCount}</span>)</wg:tab-head-item>
        <wg:tab-head-item
                href="${eventsLogUrl}"
                ajaxUrlLoad="true"
                ajaxUrlTemplate="${eventsLogModuleUrl}"
                ajax="true"
                target="#eventsLogTabContent"
                id="eventsLogTab"
                active="true"
                >Events</wg:tab-head-item>
        <wg:tab-head-item
                href="${contactsUrl}"
                ajaxUrlLoad="true"
                ajaxUrlTemplate="${contactsModuleUrl}"
                ajax="true"
                target="#contactsTabContent"
                id="contactsTab"
                >Contacts</wg:tab-head-item>
        <%-- <sec:authorize access="<%=CareTeamRoleCode.IS_CAN_SEE_COMMUNITIES%>"> --%>
        <wg:tab-head-item
                          href="${communityUrl}"
                          ajaxUrlLoad="true"
                          ajaxUrlTemplate="${communityModuleUrl}"
                          ajax="true"
                          target="#communitiesTabContent"
                          id="communitiesTab"
                          cssClass="${showCommunitiesTab ? '' : 'hiddenElement'}"
                >
            Communities List (<span id="communityCountTabPanel">${currentOrgCommunityCount}</span>) </wg:tab-head-item>

        <%-- </sec:authorize> --%>

        <sec:authorize access="<%=CareTeamRoleCode.IS_SUPER_ADMINISTRATOR %>" >
            <wg:tab-head-item
                        href="${organizationUrl}"
                        ajaxUrlLoad="true"
                        ajaxUrlTemplate="${organizationModuleUrl}"
                        ajax="true"
                        target="#organizationsTabContent"
                        id="organizationsTab"
                        >
                    Organizations</wg:tab-head-item>
        </sec:authorize>
    </wg:tab-header>

    <wg:tab-content>
        <wg:tab-content-item id="patientsTabContent" cssClass="patientsTabContent"/>
        <wg:tab-content-item id="eventsLogTabContent"/>
        <wg:tab-content-item id="communitiesTabContent"/>
        <wg:tab-content-item id="contactsTabContent"/>
        <wg:tab-content-item id="organizationsTabContent"/>
    </wg:tab-content>
</wg:tabs>

<script language="JavaScript" type="text/javascript">
    <%-- startListening() is used in module.care-coordination.js --%>
    function startListening() {
        var FromID = "${loggedInEmployeeNucleusUserId}";
        var Token = "${nucleusPollingAuthToken}";
        var Url = "${nucleusHost}/ws/CareApi.asmx/GetRequest";

        listenForIncomingCalls(Url, Token, FromID, {
            onAccepted: function (request) {
                console.log('call accepted callback');
                var ok = initCall();
                if (ok) {
                    setFrameSrc("${nucleusHost}", "${nucleusAuthToken}", FromID, request.PatientID, request.RequestID);
                }
                return ok;
            },
            onDeclined: function (request) {
                console.log('call declined callback');
                var CancelUrl = "${nucleusHost}/ws/CareApi.asmx/CancelRequest?Token=${nucleusPollingAuthToken}&RequestID=" + request.RequestID;
                $.get(CancelUrl, function(data) {
                    console.log("call canceling response: " + JSON.stringify(data));
                });
            },
            onIgnored: function (request) {
                console.log('call ignored callback');
            }
        });
    }
</script>
