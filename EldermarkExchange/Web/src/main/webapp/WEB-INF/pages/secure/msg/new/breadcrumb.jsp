<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<c:url value="/resources/images/sheet.png" var="sheetImgUrl"/>
<c:url value="/resources/images/nav-arrow-left.png" var="navArrowLeftImgUrl"/>

<c:set value="secure-messaging" var="secureMessagingUrl"/>


<lt:layout cssClass="patientInfoNavigator ldr-center-block">
    <wg:link id="oneStepBackword"
            cssClass="backLnk"
            href="#${secureMessagingUrl}"
            ajaxLoad="true"
            ajaxUrl="${secureMessagingUrl}">
        <wg:img src="${navArrowLeftImgUrl}" cssClass="back"/>
    </wg:link>
    <wg:bread-crumbs>
        <wg:crumb href="#${secureMessagingUrl}" ajaxLoad="true" ajaxUrl="${secureMessagingUrl}">Message Inbox</wg:crumb>
        <wg:crumb href="#" cssClass="active">Compose Message</wg:crumb>
    </wg:bread-crumbs>
</lt:layout>