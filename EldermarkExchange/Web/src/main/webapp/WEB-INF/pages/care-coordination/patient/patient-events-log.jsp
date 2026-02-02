<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<lt:layout cssClass="col-md-12 patient-event-log">
   <lt:layout cssClass="col-md-12 filterPanel">
       <span class="col-md-12 sectionHead">Events List</span>
        <%-- <c:if test="${not affiliatedView}">
            <lt:layout style="font-size: 30px; font-weight: bold;">
                ${patient.displayName} Events
                <wg:button domType="button" type="submit" cssClass="btn-primary pull-right" name="createNewEvent"
                           id="createNewEvent">
                    ADD NEW EVENT
                </wg:button>
            </lt:layout>
        </c:if> --%>

    </lt:layout>
    <div class="col-md-12 eventBottomPanel">
        <lt:layout cssClass="col-md-4 eventList">
            <%-- <span class="col-md-12 sectionHead">Events List</span> --%>
            <wg:grid id="patientEventList" cssClass="eventList"
                     colIds="residentName"
                     colNames="residentName"
                     colFormats="string"
                     dataUrl="care-coordination/patients/patient/${patient.id}/events"
                     deferLoading="true"
                    />
        </lt:layout>

        <lt:layout cssClass="col-md-8 eventDetails" id="eventDetails"/>
    </div>
</lt:layout>

<%-- =================== Create New Event Modal ========================== --%>
<%-- <div id="createNewEventContainer"></div> --%>
