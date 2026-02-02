<%@ page import="com.scnsoft.eldermark.entity.CareTeamRoleCode" %>
<%@ page import="com.scnsoft.eldermark.authentication.SecurityExpressions" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="cc" tagdir="/WEB-INF/tags/cc" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<sec:authorize access="<%=SecurityExpressions.IS_CC_SUPERADMIN%>">
    <c:set var="isCCSuperAdmin" value="true"/>
</sec:authorize>
<sec:authorize access="<%=SecurityExpressions.IS_CC_ADMIN%>">
    <c:set var="isCCAdmin" value="true"/>
</sec:authorize>
<div class="ldr-ui-layout communityInfoNavigator ldr-center-block">
    <ol class="breadcrumb">
        <li class="backToCommunityList">
             <span class="ldr-ui-label">
                <c:url value="/resources/images/nav-arrow-left.png" var="backImg"/>
                <img src="${backImg}" class="back"/>
             </span>
            <span class="crumb">
                Communities List
            </span>
        </li>
        <li class="active">
            <span class="crumb">
                Details of ${community.name}
            </span>
        </li>
    </ol>
</div>

<div class="communityDetails col-md-12">
    <lt:layout cssClass="col-md-12 communityDetailsInfoContent">


        <lt:layout style="padding:20px 0;">
            <span class="sectionHead">Community Details </span>
            <c:if test="${editable}">
                <wg:button name="editCommunity"
                           id="editCommunity"
                           domType="button"
                           dataToggle="modal"
                           cssClass="btn-primary pull-right">
                    EDIT COMMUNITY
                </wg:button>
            </c:if>
        </lt:layout>

        <lt:layout id="newlyCreatedAlert" cssClass="alert alert-info alert-dismissable" style="display:none">
            <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
            Community has been created. Please add members to Community Care Team.
        </lt:layout>

        <lt:layout cssClass="col-md-12 communityDetailsDetailsContent">

            <input type="hidden" value="${community.id}" id="currentCommunityId"/>
            <input type="hidden" value="${affiliatedView}" id="affiliatedView" >
            <input type="hidden" value="${isCCAdmin}" id="isCCAdmin" >
            <input type="hidden" value="${isCCSuperAdmin}" id="isCCSuperAdmin" >
            <cc:label-for-value label="Name" value="${community.name}"/>
            <cc:label-for-value label="Community OID" value="${community.oid}"/>
            <cc:label-for-value label="Email" value="${community.email}"/>
            <cc:label-for-value label="Phone" value="${community.phone}"/>
            <cc:label-for-value label="Address" value="${community.address.displayAddress}"/>
            <cc:label-for-value label="Telecom" value="${community.telecom}"/>
            <cc:label-for-value label="Organization" value="${community.datasourceName}"/>

            <c:if test="${not empty community.mainLogoPath}">
                <lt:layout cssClass="col-md-12">
                    <p class="col-md-4 no-padding eventLabel">Community Logo</p>
                    <p class="col-md-8">
                        <wg:img src="resources/images/internal/${community.mainLogoPath}" height="42"/>
                    </p>
                </lt:layout>
            </c:if>
            <c:forEach var="item" items="${community.affiliatedCommunities}" varStatus="loop">
                <div id="4" style="" class="ldr-ui-layout col-md-12">
                    <p class="col-md-4 no-padding eventLabel">${loop.index == 0 ? 'Affiliated communities' : ''}</p>
                    <p class="col-md-8" >
                    <c:choose>
                        <c:when test="${isCCSuperAdmin}">
                            <wg:link id="affiliatedCommunityDetailsLink-${loop.index}" href="#">
                                <input type="hidden" value="${item.id}" id="affiliatedCommunityId-${loop.index}"/>
                                <input type="hidden" value="${item.databaseId}" id="affiliatedDatabaseId-${loop.index}"/>
                                ${item.name}
                            </wg:link>
                        </c:when>
                        <c:otherwise>${item.name}</c:otherwise>
                    </c:choose>
                    </p>
                </div>
            </c:forEach>
            <c:forEach var="item" items="${community.affiliatedDatabases}" varStatus="loop">
                <div id="4" name="" role="" style="" class="ldr-ui-layout col-md-12">
                    <p class="col-md-4 no-padding eventLabel">${loop.index == 0 && empty community.affiliatedCommunities? 'Affiliated communities' : ''}</p>
                    <p class="col-md-8" >All communities from
                        <c:choose>
                            <c:when test="${isCCSuperAdmin}">
                                <wg:link id="affiliatedDatabaseDetailsLink-${loop.index}" href="#">
                                    <input type="hidden" value="${item.id}"/>
                                    ${item.name}
                                </wg:link>
                            </c:when>
                            <c:otherwise>${item.name}</c:otherwise>
                        </c:choose>organization
                    </p>
                </div>
            </c:forEach>
            <c:forEach var="item" items="${community.initialCommunities}" varStatus="loop">
                <div id="4" style="" class="ldr-ui-layout col-md-12">
                    <p class="col-md-4">${loop.index == 0 ? 'Current community is added as affiliated community for' : ''}</p>
                    <p class="col-md-8" >
                        <c:choose>
                            <c:when test="${isCCSuperAdmin}">
                                <wg:link id="initialCommunityDetailsLink-${loop.index}" href="#">
                                    <input type="hidden" value="${item.id}" id="initialCommunityId-${loop.index}"/>
                                    <input type="hidden" value="${item.databaseId}" id="initialDatabaseId-${loop.index}"/>
                                    ${item.name}
                                </wg:link>
                            </c:when>
                            <c:otherwise>${item.name}</c:otherwise>
                        </c:choose>
                    </p>
                </div>
            </c:forEach>
            <c:forEach var="item" items="${community.initialDatabases}" varStatus="loop">
                <div id="4" style="" class="ldr-ui-layout col-md-12">
                    <p class="col-md-4">${loop.index == 0 && empty community.initialCommunities? 'Current community is added as affiliated community for' : ''}</p>
                    <p class="col-md-8" >All communities from
                        <c:choose>
                            <c:when test="${isCCSuperAdmin}">
                                <wg:link id="initialDatabaseDetailsLink-${loop.index}" href="#">
                                    <input type="hidden" value="${item.id}"/>
                                    ${item.name}
                                </wg:link>
                            </c:when>
                            <c:otherwise>${item.name}</c:otherwise>
                        </c:choose>organization
                    </p>
                </div>
            </c:forEach>
            <%--<cc:label-for-value label="Current community is added as affiliated community for :" value="${community.affiliatedForCommunities}"/>--%>
            <%--<cc:label-for-value label="Affiliated communities :" value="${community.affiliatedCommunities}"/>--%>

        </lt:layout>
    </lt:layout>

    <lt:layout cssClass="col-md-12 communityDetailsCareTeamContent">
        <lt:layout style="padding:20px 0;">
            <span class="sectionHead">Community Care Team </span>
            <c:if test="${canAddCtm}">
                    <a type="button" class="btn btn-primary createCareTeamMember pull-right" id="createCareTeamMember">ADD
                        NEW MEMBER</a>
            </c:if>
        </lt:layout>

        <lt:layout cssClass="communityListBox panel panel-primary">
            <lt:layout cssClass="boxHeader panel panel-heading communityListBoxHeader">
                Team Members
            </lt:layout>
            <lt:layout cssClass="boxBody">
                <c:choose>
                    <c:when test="${affiliatedView}">
                        <wg:grid id="communityCareTeam"
                                 colIds="employee.label,role.label,description"
                                 colNames="Name, Role, Description"
                                 dataUrl="care-coordination/communities/community/${communityId}/care-team/false"
                                 colFormats="string,string,string"/>
                    </c:when>
                    <c:otherwise>
                        <wg:grid id="communityCareTeam"
                                 colIds="employee.label,role.label,description, actions"
                                 colNames="Name, Role, Description, Actions"
                                 dataUrl="care-coordination/communities/community/${communityId}/care-team/false"
                                 colFormats="string,string,string,fake"/>
                    </c:otherwise>
                </c:choose>
            </lt:layout>
        </lt:layout>
    </lt:layout>

    <c:if test="${(community.hasAffiliated && isCCSuperAdmin) || affiliatedView}">
    <lt:layout cssClass="col-md-12 communityDetailsCareTeamContent">
        <lt:layout style="padding:20px 0;">
            <span class="sectionHead">
                <c:choose>
                <c:when test="${isCCSuperAdmin}">
                    Affiliated Communities Care Team
                </c:when>
                <c:otherwise>
                    People Responsible for Handling Events Coming to ${community.name}
                </c:otherwise>
            </c:choose></span>
            <c:if test="${hasAddAffiliatedCtm}">
                <a type="button" class="btn btn-primary pull-right" style="margin:0 5px 0 10px;" id="createAffiliatedCareTeamMember">ADD
                    NEW MEMBER</a>
            </c:if>
            <sec:authorize access="<%=CareTeamRoleCode.IS_ADMINISTRATOR %>" >
                    <a type="button" class="btn btn-default pull-right" id="copySettings" <c:if test="${not community.copySettings}">style="display: none;"</c:if>>
                        COPY SETTINGS</a>
            </sec:authorize>
        </lt:layout>
        <lt:layout cssClass="communityListBox panel panel-primary">
            <lt:layout cssClass="boxHeader panel panel-heading communityListBoxHeader">
                Team Members
            </lt:layout>
            <lt:layout cssClass="boxBody">

                <wg:grid id="affiliatedCommunityCareTeam"
                         colIds="employee.label,role.label,description, actions"
                         colNames="Name, Role, Description, Actions"
                         dataUrl="care-coordination/communities/community/${communityId}/care-team/true"
                         colFormats="string,string,string,fake"/>


            </lt:layout>
        </lt:layout>
    </lt:layout>
    </c:if>
    <%--<c:if test="${affiliated}">hidden</c:if>--%>

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

</div>
<%-- =================== Create Care Team Member Modal ========================== --%>
<div id="createCareTeamContainer"></div>
<%-- =================== Delete CTM Confirmation =================== --%>
<div id="deleteCareTeamContainer"></div>
<div id="copySettingsContainer"></div>

<script language="JavaScript" type="text/javascript">
    <%-- startCall() is used in module.communities.js --%>
    function startCall(toId) {
        var FromID = "${loggedInEmployeeNucleusUserId}";
        var ToID = toId || "${patientNucleusUserId}";

        setFrameSrc("${nucleusHost}", "${nucleusAuthToken}", FromID, ToID);
    }
</script>