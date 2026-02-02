<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<div id="organizationInfoPopup" class="marketplace-popup">
<c:forEach var="community" items="${marketplaces}" varStatus="varStatus">
    <lt:layout cssClass="communityItem ">
        <div class="communityName truncated-text"style="padding-bottom: 2px;">
                ${community.organizationName}, ${community.communityName}
        </div>
        <div class="marketplace-community-type" style="padding-bottom: 15px;">
            <wg:truncatedText maxSymbolNumber="45">
            <c:forEach var="type"  items="${community.communityTypes}" varStatus="typeLoop">
                ${fn:trim(type)}<c:if test="${!typeLoop.last}">,</c:if>
            </c:forEach>
            </wg:truncatedText>
        </div>
        <div class="marketplace-details-address" data-latitude="${community.location.latitude}"
             data-longitude="${community.location.longitude}">
            <span>${community.address}</span>
        </div>
        <div class="marketplace-details-phone" style="margin-top: 5px">
            <a href="tel:${community.phoneNumber}">
                ${community.phoneNumber}
            </a>
        </div>
        <div style="margin-top: 20px">
            <a class="btn btn-primary marketplace__schedule-an-appointment-btn">
                SCHEDULE AN APPOINTMENT
            </a>
        </div>
        <div class="text-right" style="margin-top: 5px">
            <a href="#" class="viewDetails marketplaceDetailsLink marketplace__view-details-btn" data-id="${community.id}">
                View details
            </a>
        </div>
        <c:if test="${not varStatus.last}">
            <hr />
        </c:if>
    </lt:layout>
</c:forEach>
</div>
