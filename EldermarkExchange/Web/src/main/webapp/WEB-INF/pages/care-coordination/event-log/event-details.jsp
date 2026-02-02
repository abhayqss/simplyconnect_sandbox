<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<c:url value="/care-coordination/events-log/event/${eventId}/events-description" var="eventsDescriptionUrl"/>
<c:url value="/care-coordination/events-log/event/${eventId}/sent-notification" var="sentNotificationUrl"/>
<c:url value="care-coordination/events-log/event/${eventId}/events-description" var="eventsDescriptionModuleUrl"/>
<c:url value="care-coordination/events-log/event/${eventId}/sent-notification" var="sentNotificationModuleUrl"/>

<wg:tabs>
    <wg:tab-header>
        <wg:tab-head-item id="eventsDescriptionTab" cssClass="eventTabs"
                href="#eventsDescriptionContent" active="true"
                >Event Description</wg:tab-head-item>
        <wg:tab-head-item id="sentNotificationsTab" cssClass="eventTabs"
                href="#sentNotificationsContent"

                >Sent Notifications</wg:tab-head-item>
    </wg:tab-header>
    <wg:tab-content>
        <wg:tab-content-item id="eventsDescriptionContent" active="true">
            <jsp:include page="event-description.jsp">
                <jsp:param name="tab" value="eventTab" />
            </jsp:include>
        </wg:tab-content-item>
        <wg:tab-content-item id="sentNotificationsContent">
            <jsp:include page="event-notifications.jsp"/>
        </wg:tab-content-item>
    </wg:tab-content>
</wg:tabs>
<div id="noteContainer"></div>


