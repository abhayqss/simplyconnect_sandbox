<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:url value="/administration/manualMatches" var="manualMatchingUrl"/>
<c:url value="administration/manual-matching" var="manualMatchingModuleUrl"/>

<c:url value="administration/suggested-matches" var="suggestedMatchesModuleUrl"/>
<c:url value="/administration/suggestedMatches" var="suggestedMatchesUrl"/>

<wg:tabs>
    <wg:tab-header>
        <wg:tab-head-item
                href="${suggestedMatchesUrl}"
                ajaxUrlLoad="true"
                ajaxUrlTemplate="${suggestedMatchesModuleUrl}"
                ajax="true"
                target="#suggestedMatchesTabContent"
                id="suggestedMatchesTab"
                active="true"
        >Suggested Matches</wg:tab-head-item>
        <wg:tab-head-item
                href="${manualMatchingUrl}"
                ajaxUrlLoad="true"
                ajaxUrlTemplate="${manualMatchingModuleUrl}"
                ajax="true"
                target="#manualMatchingTabContent"
                id="manualMatchingTab"
        >Manual Matching</wg:tab-head-item>
    </wg:tab-header>

    <wg:tab-content>
        <wg:tab-content-item id="suggestedMatchesTabContent" cssClass="suggestedMatchesTabContent" active="true"/>
        <wg:tab-content-item id="manualMatchingTabContent" cssClass="manualMatchingTabContent"/>
    </wg:tab-content>
</wg:tabs>
