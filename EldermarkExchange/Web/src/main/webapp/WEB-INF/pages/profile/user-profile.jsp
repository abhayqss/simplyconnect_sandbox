<%@page import="com.scnsoft.eldermark.entity.CareTeamRoleCode" %>
<%@ page import="com.scnsoft.eldermark.authentication.SecurityExpressions" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="cc" tagdir="/WEB-INF/tags/cc" %>
<%@taglib prefix="ccd" tagdir="/WEB-INF/tags/ccd" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="ldr-ui-layout communityInfoNavigator ldr-center-block">
    <ol class="breadcrumb">
        <li class="backToPrevious">
             <span class="ldr-ui-label">
                <c:url value="/resources/images/nav-arrow-left.png" var="backImg"/>
                <img src="${backImg}" class="back"/>
             </span>
            <span class="crumb">
                Back
            </span>
        </li>
        <li class="active">
            <span class="crumb">
                Profile Settings
            </span>
        </li>
    </ol>
</div>

<lt:layout cssClass="profileDetails col-md-12">
    <lt:layout cssClass="col-md-12 communityDetailsInfoContent">


        <lt:layout style="padding:20px 0;">
            <span class="sectionHead">${contactDto.firstName} ${contactDto.lastName}</span>
            <span class="pull-right">
                <c:if test="${showLinkButton}">
                    <wg:button name="linkAccounts"
                               id="linkAccounts"
                               domType="button"
                               dataToggle="modal"
                               cssClass="btn-default linkAccountsButton">
                        LINK ACCOUNTS
                    </wg:button>
                </c:if>
                <wg:button name="editProfile"
                           id="editProfile"
                           domType="button"
                           dataToggle="modal"
                           cssClass="btn-primary">
                    EDIT RECORD
                </wg:button>
            </span>
        </lt:layout>

        <lt:layout id="createdLinkedAccount" cssClass="alert alert-info" style="display:none">
            <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
            <div>Your accounts have been linked.</div>
            <div>You can use the credentials from any linked account in order to access Simply Connect data.</div>
        </lt:layout>

        <lt:layout id="unLinkedAccount" cssClass="alert alert-info" style="display:none">
            <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
            <div>Account has been unlinked.</div>
        </lt:layout>

        <lt:layout cssClass="ccdHeader" style="margin-top: 10px;">

            <lt:layout cssClass="ccdHeaderItem">
                <input type="hidden" name="newUserToLink" value="${newUserToLink}" id="newUserToLink"/>
                <lt:layout cssClass="commonInfo" id="profileCommonInfo">
                    <jsp:include page="common-profile-info.jsp"/>
                </lt:layout>

                <lt:layout id="linkedEmployeesDetails"/>

            </lt:layout>

        </lt:layout>
    </lt:layout>
</lt:layout>

<lt:layout id="linkedEmployeeListMain" cssClass="hidden">
<lt:layout cssClass="boxBody">
    <wg:grid id="linkedEmployeeList"
             colIds="companyId, login, displayName, role, organization, community"
             colNames="Company ID, Login, Name, Role, Organization, Community"
             colFormats="string, string, string, string, string, string"
             dataUrl="profile/linkedlist"
             dataRequestMethod="GET"
             deferLoading="true"
            />
</lt:layout>
</lt:layout>
<lt:layout id="linkAccountsContainer"/>
<lt:layout id="editContactContainer"/>
