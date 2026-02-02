<%@ page import="com.scnsoft.eldermark.authentication.SecurityExpressions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="wgForm" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<c:url value="/resources/images/logo.svg" var="logoImgUrl"/>
<c:url value="/resources/images/resident.png" var="residentUrl"/>
<c:url value="/resources/images/envelope.png" var="envelope"/>
<c:url value="/resources/images/teamwork.png" var="teamwork"/>
<c:url value="/resources/images/cogwheel.png" var="cogwheel"/>
<c:url value="/resources/images/analytics.png" var="analytics"/>

<c:url value='/j_spring_security_logout' var='j_spring_security_logout'/>
<c:url value="/employee" var="employeInfoUrl"/>
<c:url value="/secure-messaging/unread-inbox-count" var="unreadInboxCountUrl"/>
<c:url value="/secure-messaging/employee-secure-email" var="employeeSecureEmailUrl"/>
<c:url value="/j_spring_security_logout" var="logoutUrl" />

<c:set value="patient-search" var="patientSearchUrl"/>
<c:set value="secure-messaging" var="secureMessagingUrl"/>
<c:set value="secure-messaging/setup" var="messagingSetupUrl"/>
<c:set value="care-coordination" var="careCoordinationUrl"/>
<c:set value="administration" var="administrationUrl"/>
<c:url value="profile/show" var="profileUrl"/>
<c:set value="reports" var="reportsUrl"/>
<c:set value="marketplace" var="marketplaceUrl"/>

<c:set var="request" value="${pageContext.request}"/>
<c:set var="auditReportContextPath" value="${auditReportContext}"/>
<c:set var="auditReportURL" value="${request.scheme}://${request.serverName}:${request.serverPort}/${auditReportContextPath}"/>

<spring:message var="personalHealthRecord" code="header.link.text.personalHealthRecord"/>
<spring:message var="secureMessaging" code="header.link.text.secureMessaging"/>
<spring:message var="careCoordination" code="header.link.text.careCoordination"/>
<spring:message var="administration" code="header.link.text.administration"/>
<spring:message var="reports" code="header.link.text.reports"/>
<%--<spring:message var="marketplace" code="header.link.text.marketplace"/>--%>

<wg:link id="unreadInboxCountUrl" href="${unreadInboxCountUrl}" cssClass="hidden"/>
<wg:link id="employeInfoUrl" href="${employeInfoUrl}" cssClass="hidden"/>
<wg:link id="employeeSecureEmailUrl" href="${employeeSecureEmailUrl}" cssClass="hidden"/>

<sec:authorize access="<%=SecurityExpressions.IS_ELDERMARK_USER%>">
    <c:if test="${not unaffiliatedUser}">
    <wg:link id="patientSearch"
             cssClass="ldr-head-lnk personalHealthRecordLnk table-cell-box"
             href="#${patientSearchUrl}"
             ajaxUrlParams="navigate=link"
             ajaxUrl="${patientSearchUrl}"
             ajaxLoad="true">
        <wg:img  cssClass="lnk-img" src="${residentUrl}"/>
        <wg:label cssClass="lnk-text">${personalHealthRecord}</wg:label>
        <lt:layout cssClass="ldr-arrow"/>
    </wg:link>
    </c:if>
    <wg:link id="secureMessaging"
             cssClass="ldr-head-lnk secureMsgLnk table-cell-box"
             href="#${secureMessagingUrl}"
             ajaxUrl="${secureMessagingUrl}"
             ajaxLoad="true">
        <wg:img cssClass="lnk-img" src="${envelope}"/>
        <lt:layout id="secureMessageTotal" cssClass="badge hidden"/>
        <wg:label cssClass="lnk-text">${secureMessaging}</wg:label>
        <lt:layout cssClass="ldr-arrow"/>
    </wg:link>
</sec:authorize>
<sec:authorize access="<%=SecurityExpressions.IS_CC_USER%>">
    <wg:link id="careCoordination"
             cssClass="ldr-head-lnk careCoordinationLnk table-cell-box"
             href="#${careCoordinationUrl}"
             ajaxUrl="${careCoordinationUrl}"
             ajaxLoad="true">
        <wg:img cssClass="lnk-img" src="${teamwork}"/>
        <%--<lt:layout id="secureMessageTotal" cssClass="badge hidden"/>--%>
        <wg:label cssClass="lnk-text">${careCoordination}</wg:label>
        <lt:layout cssClass="ldr-arrow"/>
    </wg:link>
</sec:authorize>
<%-- <c:if test="${showReports}">
    <wg:link id="reports"
             cssClass="ldr-head-lnk reportsLnk table-cell-box"
             href="#${reportsUrl}"
             ajaxUrl="${reportsUrl}"
             ajaxLoad="true">
        <wg:img cssClass="lnk-img" src="${analytics}"/>
        <wg:label cssClass="lnk-text">${reports}</wg:label>
        <lt:layout cssClass="ldr-arrow"/>
    </wg:link>
</c:if> --%>
<sec:authorize access="<%=SecurityExpressions.IS_CC_SUPERADMIN%>">
    <wg:link id="administration"
             cssClass="ldr-head-lnk administrationLnk table-cell-box"
             href="#${administrationUrl}"
             ajaxUrl="${administrationUrl}"
             ajaxLoad="true">
        <wg:img cssClass="lnk-img" src="${cogwheel}"/>
        <wg:label cssClass="lnk-text">${administration}</wg:label>
        <lt:layout cssClass="ldr-arrow"/>
    </wg:link>
</sec:authorize>

<lt:layout cssClass="userTab table-cell-box text-right">
<wg:dropdown cssClass="userOptions ">
    <wg:dropdown-head href="#" id="userOptionsHead">
        <lt:layout cssClass="display-inline-block text-right valign-middle head-wrp">
            <wg:label cssClass="user"/>
            <%--
            <sec:authorize access="<%=SecurityExpressions.IS_EXCHANGE_USER%>">
                <wg:label cssClass="email"/>
            </sec:authorize>
            --%>
        </lt:layout>
    </wg:dropdown-head>
    <wg:dropdown-body forHead="userOptionsHead" cssClass="dropdown-menu-right">
        <sec:authorize access="hasAnyRole('ROLE_MANAGER', 'ROLE_SUPER_MANAGER')">
            <wg:dropdown-item cssClass="option" href="${auditReportURL}">
                View Audit Logs
            </wg:dropdown-item>
        </sec:authorize>
        <wg:dropdown-item
                cssClass="option "
                ajaxLoad="true"
                ajaxUrl="${profileUrl}"
                href="#${profileUrl}">
            View Profile
        </wg:dropdown-item>
        <sec:authorize access="hasRole('ROLE_DIRECT_MANAGER')">
            <wg:dropdown-item
                    cssClass="option manageSES"
                    ajaxLoad="true"
                    ajaxUrl="${messagingSetupUrl}"
                    href="#${messagingSetupUrl}">
                Manage Secure Messaging
            </wg:dropdown-item>
        </sec:authorize>
        <wg:dropdown-item cssClass="option logOut">
            Log out
        </wg:dropdown-item>
        <wgForm:form id="logoutForm" action="${logoutUrl}"/>
    </wg:dropdown-body>
</wg:dropdown>
    <lt:layout cssClass="roleLabel"/>
</lt:layout>