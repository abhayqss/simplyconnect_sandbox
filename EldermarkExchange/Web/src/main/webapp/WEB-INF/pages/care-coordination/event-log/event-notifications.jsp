<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="ccd" tagdir="/WEB-INF/tags/ccd" %>
<%@taglib prefix="cc" tagdir="/WEB-INF/tags/cc" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>


<lt:layout cssClass="" style="">
  <wg:grid id="eventNotificationList"
           colNames="Date,Notification,Contact Name,Role,Description,Organization,Responsibility"
           colIds="dateTime,notificationText,contactName,careTeamRole,description,organization,responsibility"
           colFormats="string"
           dataUrl="care-coordination/events-log/event/${eventId}/sent-notification"
           deferLoading="true"/>

</lt:layout>