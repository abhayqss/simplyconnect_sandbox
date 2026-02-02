<%@page import="com.scnsoft.eldermark.entity.CareTeamRoleCode" %>
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

<div class="ldr-ui-layout communityInfoNavigator ldr-center-block">
    <ol class="breadcrumb">
        <li class="backToOrganizationsList">
             <span class="ldr-ui-label">
                <c:url value="/resources/images/nav-arrow-left.png" var="backImg"/>
                <img src="${backImg}" class="back"/>
             </span>
            <span class="crumb">
                Organizations List
            </span>
        </li>
        <li class="active">
            <span class="crumb">
                ${organization.name}
            </span>
        </li>
    </ol>
</div>

<div class="communityDetails col-md-12">
    <lt:layout cssClass="col-md-12 communityDetailsInfoContent">


        <lt:layout style="padding:20px 0;">
            <span class="sectionHead">Organization Details </span>
            <span class="pull-right">
                <sec:authorize access="<%=CareTeamRoleCode.IS_CAN_EDIT_ORGANIZATIONS %>" >
                    <wg:button name="passwordSettings"
                               id="passwordSettings"
                               domType="button"
                               dataToggle="modal"
                               cssClass="btn-default passwordSettingsButton">
                        PASSWORD SETTINGS
                    </wg:button>
                </sec:authorize>
                <sec:authorize access="<%=CareTeamRoleCode.IS_CAN_EDIT_ORGANIZATIONS%>">
                    <wg:button name="editOrganization"
                               id="editOrganization"
                               domType="button"
                               dataToggle="modal"
                               cssClass="btn-primary">
                        EDIT DETAILS
                    </wg:button>
                </sec:authorize>
            </span>
        </lt:layout>

        <lt:layout cssClass="col-md-12 communityDetailsDetailsContent">

            <input type="hidden" value="${organization.id}" id="currentOrganizationId"/>
            <cc:label-for-value label="Organization Name" value="${organization.name}"/>
            <cc:label-for-value label="Organization OID" value="${organization.oid}"/>
            <cc:label-for-value label="Company Code" value="${organization.loginCompanyId}"/>
            <cc:label-for-value label="Email" value="${organization.email}"/>
            <cc:label-for-value label="Phone" value="${organization.phone}"/>
            <cc:label-for-value label="Address" value="${organization.displayAddress}"/>

            <c:if test="${not empty organization.mainLogoPath}">
                <lt:layout cssClass="col-md-12">
                    <p class="col-md-4 eventLabel no-padding">Organization Logo</p>

                    <p class="col-md-8">
                        <wg:img src="resources/images/internal/${organization.mainLogoPath}" height="42"/>
                    </p>
                </lt:layout>
            </c:if>

            <c:forEach var="community" items="${organization.communities}" varStatus="loop">
                <div id="4" name="" role="" style="" class="ldr-ui-layout col-md-12">
                    <p class="col-md-4 no-padding eventLabel">${loop.index == 0 ? 'Communities' : ''}</p>
                    <p class="col-md-8" >
                        <wg:link id="communityDetailsLink-${loop.index}" href="#">
                            <input type="hidden" value="${community.id}" id="communityDetailsId-${loop.index}"/>
                            ${community.name}
                        </wg:link>
                    </p>
                </div>
            </c:forEach>

           <c:forEach var="affiliatedOrg" items="${affiliatedOrganizations}" varStatus="loop">
               <div id="4" name="" role="" style="" class="ldr-ui-layout col-md-12">
                   <p class="col-md-4 no-padding eventLabel">${loop.index == 0 ? 'Affiliate relationship' : ''}</p>
                   <cc:afiiliated-orgs-details affiliatedOrg="${affiliatedOrg}"/>
               </div>
            </c:forEach>

            <c:forEach var="primaryOrg" items="${primaryOrganizations}" varStatus="loop">
                <div id="4" name="" role="" style="" class="ldr-ui-layout col-md-12">
                    <p class="col-md-4 no-padding eventLabel">${loop.index == 0 && (empty affiliatedOrganizations) ? 'Affiliate relationship' : ''}</p>
                    <cc:afiiliated-orgs-details affiliatedOrg="${primaryOrg}"/>
                </div>
            </c:forEach>

        </lt:layout>
    </lt:layout>
</div>
