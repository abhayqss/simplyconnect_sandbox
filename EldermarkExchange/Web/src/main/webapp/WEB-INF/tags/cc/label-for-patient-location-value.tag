<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="value" required="true" rtexprvalue="true"
              type="com.scnsoft.eldermark.shared.carecoordination.adt.datatype.PLPatientLocationDto" %>
<%--<%@ attribute name="id" required="false" rtexprvalue="true" %>--%>
<%@ attribute name="label" required="true" rtexprvalue="true" type="java.lang.String" %>
<%--<%@ attribute name="boldValue" required="false" rtexprvalue="true" type="java.lang.Boolean" %>--%>

<c:if test="${not empty value and not (empty value.pointOfCare and empty value.room and empty value.bed and empty value.locationStatus
and empty value.personLocationType and empty value.building and empty value.floor and empty value.locationDescription)}">
    <lt:layout cssClass="col-md-12">
        <p class="noPadding col-md-4 eventLabel">${label}</p>
        <%--<c:if test="${not empty id}">id="${id}"</c:if> <c:if test="${boldValue}">style="font-weight: bold"</c:if>>--%>
        <ul class="col-md-8"
            <c:if test="${not empty id}">id="${id}"</c:if>
            <c:if test="${boldValue}">style="font-weight: bold"</c:if>
        >
            <c:if test="${not empty value.pointOfCare}">
                <p class="codedValueLabel">Point of care</p>
                <p class="codedValue">${value.pointOfCare}</p>
            </c:if>
            <c:if test="${not empty value.room}">
                <p class="codedValueLabel">Room</p>
                <p class="codedValue">${value.room}</p>
            </c:if>
            <c:if test="${not empty value.bed}">
                <p class="codedValueLabel">Bed</p>
                <p class="codedValue">${value.bed}</p>
            </c:if>
            <c:if test="${not empty value.locationStatus}">
                <p class="codedValueLabel">Location Status</p>
                <p class="codedValue">${value.locationStatus}</p>
            </c:if>
            <c:if test="${not empty value.personLocationType}">
                <p class="codedValueLabel">Person Location Type</p>
                <p class="codedValue">${value.personLocationType}</p>
            </c:if>
            <c:if test="${not empty value.building}">
                <p class="codedValueLabel">Building</p>
                <p class="codedValue">${value.building}</p>
            </c:if>
            <c:if test="${not empty value.floor}">
                <p class="codedValueLabel">Floor</p>
                <p class="codedValue">${value.floor}</p>
            </c:if>
            <c:if test="${not empty value.locationDescription}">
                <p class="codedValueLabel">Location Description</p>
                <p class="codedValue">${value.locationDescription}</p>
            </c:if>
        </ul>
    </lt:layout>
</c:if>