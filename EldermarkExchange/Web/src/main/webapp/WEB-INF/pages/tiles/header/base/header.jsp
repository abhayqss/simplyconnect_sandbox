<%@ page import="com.scnsoft.eldermark.authentication.SecurityExpressions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:url value="/resources/images/logo.svg" var="logoImgUrl"/>
<c:url value="/resources/images/sheet.gif" var="sheetImgUrl"/>
<c:url value="/resources/images/sheet.gif" var="envelope"/>

<spring:message var="personalHealthRecord" code="header.link.text.personalHealthRecord"/>
<spring:message var="secureMessaging" code="header.link.text.secureMessaging"/>
<spring:message var="careCoordination" code="header.link.text.careCoordination"/>
<spring:message var="administration" code="header.link.text.administration"/>

<lt:layout cssClass="headerBorderLine"/>

<lt:layout cssClass="ldr-center-block baseHeader">
    <lt:layout cssClass="table-row-box">
        <lt:layout id="defaultLogoHeader" style="display:none" cssClass="logo table-cell-box">
            <wg:img src="resources/images/logo.svg"/>
        </lt:layout>

        <lt:layout  id="sponsoredLogoHeader" style="display:none" cssClass="logo-sponsored table-cell-box">
            <wg:img id="mainLogoImage"  src=""/>
        </lt:layout>

        <tiles:insertAttribute name="headerLinks"/>
        <div class="ldr-ui-layout alt-logo table-cell-box" id="altLogoBlock" style="display: none;">
            <img src="" id="altLogoImage" width="200">
        </div>
    </lt:layout>

</lt:layout>
<sec:authorize access="<%=SecurityExpressions.IS_CC_USER%>">
    <jsp:include page="subMenu.jsp"/>
</sec:authorize>
