<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<c:url value="/resources/images/nav-arrow-left.png" var="navArrowLeftImgUrl"/>

<c:set value="patient-search" var="patientSearchUrl"/>
<c:set value="care-coordination/patients/patient/{patientId}/details" var="patientInfoUrl"/>


<lt:layout cssClass="patientInfoNavigator ldr-center-block">
    <ol class="breadcrumb">
        <li class="backToPrevious" onclick="parent.history.back();">
             <span class="ldr-ui-label">
                <c:url value="/resources/images/nav-arrow-left.png" var="backImg"/>
                <img src="${backImg}" class="back"/>
             </span>
            <span class="crumb">
                Details of ${fullName}
            </span>
        </li>
        <li class="active">
            <span class="crumb">
                Compose Message
            </span>
        </li>
    </ol>
</lt:layout>
