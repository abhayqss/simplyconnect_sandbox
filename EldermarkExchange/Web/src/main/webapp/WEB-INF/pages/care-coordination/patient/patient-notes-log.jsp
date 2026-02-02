<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<lt:layout cssClass="col-md-12 patient-event-log">
    <lt:layout cssClass="col-md-12 filterPanel">
        <span class="col-md-12 sectionHead">Notes List</span>
    </lt:layout>
    <div class="col-md-12 eventBottomPanel">
        <lt:layout cssClass="col-md-4 eventList">
            <wg:grid id="patientNoteList" cssClass="eventList"
                     colIds="type"
                     colNames="type"
                     colFormats="string"
                     dataUrl="care-coordination/notes/patient/${patient.id}"
            />
        </lt:layout>

        <lt:layout cssClass="col-md-8 eventDetails" id="noteDetails"/>
    </div>
</lt:layout>
