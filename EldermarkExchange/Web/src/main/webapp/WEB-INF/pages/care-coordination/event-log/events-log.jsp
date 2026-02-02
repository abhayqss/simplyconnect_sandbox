<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<lt:layout cssClass="event-log">
    <lt:layout cssClass="col-md-12 filterPanel">
        <div class="col-md-12 sectionHead">Filtering</div>
        <wgForm:form role="form" id="eventsFilterFrom" commandName="eventFilter" cssClass="event-filter-from">
            <fmt:formatDate pattern='MM/dd/yyyy' value='${eventFilter.dateFrom}' var="defaultDateFrom"/>
            <fmt:formatDate pattern='MM/dd/yyyy' value='${eventFilter.dateTo}' var="defaultDateTo"/>


            <input type="hidden" id="defaultDateFrom" value="${defaultDateFrom}">
            <input type="hidden" id="defaultDateTo" value="${defaultDateTo}">

            <lt:layout cssClass="d-flex">
                <lt:layout cssClass="event-filter-from__field">
                    <wg:label _for="eventGroupId">Event type</wg:label>
                    <wgForm:select path="eventGroupId" name="eventGroupId" id="eventGroupId"
                                   cssClass="form-control">
                        <wgForm:option value="" label="All"/>
                        <c:forEach var="item" items="${eventGroups}">
                            <wgForm:option value="${item.id}" label="${item.label}"/>
                        </c:forEach>
                    </wgForm:select>
                </lt:layout>
                <lt:layout cssClass="event-filter-from__field">
                    <wg:label _for="eventTypeId">Patient</wg:label>
                    <wgForm:select path="patientId" name="patientId" id="patientId"
                                   cssClass="form-control">
                        <wgForm:option value="" label="All"/>
                        <c:forEach var="item" items="${patients}">
                            <wgForm:option value="${item.id}" label="${item.label}"/>
                        </c:forEach>
                    </wgForm:select>
                </lt:layout>
                <lt:layout cssClass="event-filter-from__field" id="datepickerFrom">
                    <wg:label _for="dateFrom">Date From</wg:label>
                    <lt:layout cssClass="date">
                        <wgForm:input type="text" class="form-control" name="dateFrom" path="dateFrom" id="dateFrom"/>
                        <wg:icon cssClass="glyphicon-calendar"/>
                    </lt:layout>
                </lt:layout>
                <lt:layout cssClass="event-filter-from__field" id="datepickerTo">
                    <wg:label _for="dateTo" cssClass="">Date To</wg:label>
                    <lt:layout cssClass="date">
                        <wgForm:input type="text" cssClass="form-control" name="dateTo" path="dateTo" id="dateTo"/>
                        <wg:icon cssClass="glyphicon-calendar"/>
                    </lt:layout>
                </lt:layout>
                <lt:layout cssClass="event-filter-from__field event-filter-from__event-with-ir-checkbox-field">
                    <lt:layout cssClass="event-filter-from__event-with-ir-checkbox-wrapper">
                        <wgForm:checkbox path="irRelatedEvent" label="Only show events with incident report"/>
                    </lt:layout>
                </lt:layout>
                <lt:layout cssClass="event-filter-from__btns">
                    <wg:button
                            type="button"
                            domType="button"
                            id="eventSearchClear"
                            name="eventSearchClear"
                            cssClass="event-filter-from__btn btn-default">
                        CLEAR
                    </wg:button>
                    <wg:button
                            type="submit"
                            domType="button"
                            id="eventSearch"
                            name="eventSearch"
                            cssClass="event-filter-from__btn btn-primary">
                        APPLY
                    </wg:button>
                </lt:layout>
            </lt:layout>
        </wgForm:form>
    </lt:layout>
    <div class="col-md-12 eventBottomPanel">
        <%--<div style="display:flex; flex-direction: row; align-content:flex-start;">--%>
        <lt:layout cssClass="col-md-4 eventList">
            <div class="col-md-12 sectionHead">Events List</div>
            <wg:grid id="eventList" cssClass="eventList"
                     colIds="residentName"
                     colNames="residentName"
                     colFormats="string"
                     dataUrl="care-coordination/events-log/events"
                     deferLoading="true"
                    />
        </lt:layout>

        <lt:layout cssClass="col-md-8 eventDetails" id="eventDetails" style="float:right;"/>
        <%--</div>--%>
    </div>
</lt:layout>