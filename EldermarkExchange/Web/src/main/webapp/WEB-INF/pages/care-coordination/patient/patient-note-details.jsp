<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="cc" tagdir="/WEB-INF/tags/cc" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<lt:layout cssClass="col-md-12" style="padding-right:0px;">
<c:url value="/resources/images/edit.png" var="editImg"/>


    <wgForm:form cssClass="form-inline">
        <input type="hidden" value="${note.id}" id="currentNoteId"/>

        <lt:layout id="note_summary" cssClass="col-md-12 noteSection no-border">
            <span class="sectionHead col-md-12">Summary
                <c:if test="${addedBySelf}">
                    <wg:link id="editNote" cssClass="pull-right no-text-decoration">
                        <wg:img cssClass="icon-link-img" src="${editImg}"/><span class="icon-link-text">Edit</span>
                    </wg:link>
                </c:if>
            </span>
            <cc:label-for-value label="Type" value="${note.type}"/>
            <c:if test="${note.type eq 'Event Note'}">
                <cc:label-for-value label="Subtype" value="${note.subType.label}"/>
                <lt:layout cssClass="col-md-12">
                    <p class="col-md-4 eventLabel no-padding">Event</p>
                    <p class="col-md-6" style="font-weight: bold">
                        ${note.event.description}, <cc:local-date-format date="${note.event.date}" pattern="MM/DD/YYYY hh:mm A Z" />
                    </p>
                    <p class="col-md-2 view-details-link">
                        <wg:link id="relatedEventId" ajaxUrl="care-coordination/patients" ajaxUrlParams="event=${note.event.id}&patient=${note.patientId}" ajaxLoad="true" ajaxAnchor="true" cssClass="pull-right">
                            <span class="icon-link-text">View Details</span>
                        </wg:link>
                    </p>
                </lt:layout>

            </c:if>
            <cc:label-for-value label="Status" value="${note.status}" boldValue="true"/>
            <c:choose>
                <c:when test="${note.status eq 'Created'}">
                    <cc:label-for-value label="Date Created">
                    <jsp:attribute name="value">
                        <cc:local-date-format date="${note.lastModifiedDate}" pattern="MM/DD/YYYY hh:mm A Z" />
                    </jsp:attribute>
                    </cc:label-for-value>
                </c:when>
                <c:otherwise>
                    <cc:label-for-value label="Last Modified Date">
                    <jsp:attribute name="value">
                        <cc:local-date-format date="${note.lastModifiedDate}" pattern="MM/DD/YYYY hh:mm A Z" />
                    </jsp:attribute>
                    </cc:label-for-value>
                </c:otherwise>
            </c:choose>
            <cc:label-for-value label="Person Submitting Note" value="${note.personSubmittingNote}"/>
            <cc:label-for-value label="Role" value="${note.role}"/>

        </lt:layout>

        <lt:layout id="note_description" cssClass="col-md-12  noteSection">
            <span class="sectionHead col-md-12">Description</span>
            <c:if test="${not empty note.encoutnerNoteType}">
                <cc:label-for-value label="Encoutner Type" value="${note.encoutnerNoteType}"/>
                <cc:label-for-value label="Person Completing the Encounter" value="${note.clinicianCompletingEncounter}"/>
                <cc:label-for-value label="Encoutner Date">
                    <jsp:attribute name="value">
                            <cc:local-date-format date="${note.encounterDate}" pattern="MM/DD/YYYY" />
                    </jsp:attribute>
                </cc:label-for-value>
                
                <fmt:formatDate value="${note.from}" var="fromTime" pattern="hh:mm a" />
                <fmt:formatDate value="${note.to}" var="toTime" pattern="hh:mm a" />
                <cc:label-for-value label="From" value="${fromTime}" />
	            <cc:label-for-value label="To" value="${toTime}" />
                <cc:label-for-value label="Total Time Spent" value="${note.totalTimeSpent}"/>
	            <cc:label-for-value label="Range" value="${note.range}"/>
	            <cc:label-for-value label="Unit" value="${note.units}"/>
            </c:if>
            <cc:label-for-value label="Subjective" value="${note.subjective}"/>
            <cc:label-for-value label="Objective" value="${note.objective}"/>
            <%-- <cc:label-for-value label="Assessment" value="${note.assessment}"/> --%>
            <c:forEach items="${note.assessment.split(';')}" var="assessment" varStatus="loop"> 
                <c:if test="${loop.index == 0}">  
                	<cc:label-for-value label="Assessment" value="${assessment}"/>  
                </c:if>  
                <c:if test="${loop.index > 0}">          
                 <cc:label-for-value label=""  value="${assessment}"/>
                 </c:if>
                </c:forEach>
            <cc:label-for-value label="Plan" value="${note.plan}"/>
        </lt:layout>
        
        <c:if test="${note.status eq 'Updated'}">
            <lt:layout id="note_history" cssClass="col-md-12  noteSection">
                <span class="sectionHead col-md-12">History</span>
                <c:forEach items="${note.historyNotes}" var="historyNote" varStatus = "historyNotesIterationStatus">

                    <lt:layout cssClass="col-md-12">
                        <p class="col-md-4 eventLabel no-padding">${historyNote.status}:</p>
                        <p class="col-md-6">
                            By ${historyNote.personSubmittingNote}, ${historyNote.role} on <cc:local-date-format date="${historyNote.lastModifiedDate}" pattern="MM/DD/YYYY hh:mm A Z" />

                        </p>
                        <p class="col-md-2 view-details-link">
                            <c:if test="${not historyNotesIterationStatus.first}">
                                <wg:link href="/c" id="historyNoteId-${historyNote.id}" cssClass="viewNoteLink pull-right">
                                    <span class="icon-link-text">View Details</span>
                                </wg:link>
                            </c:if>
                        </p>
                    </lt:layout>
                </c:forEach>
            </lt:layout>
        </c:if>

    </wgForm:form>
</lt:layout>