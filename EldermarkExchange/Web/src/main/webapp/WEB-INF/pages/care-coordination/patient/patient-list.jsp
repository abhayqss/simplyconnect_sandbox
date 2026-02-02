<%@ page import="com.scnsoft.eldermark.entity.CareTeamRoleCode" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<lt:layout cssClass="patientListBox panel panel-primary">
    <lt:layout cssClass="boxHeader panel panel-heading">
       Search Results
    </lt:layout>

    <lt:layout cssClass="boxBody">
        <c:if test="${not affiliatedView}">
            <sec:authorize access="<%=CareTeamRoleCode.IS_CAN_ACTIVATE_PATIENTS%>">
                <lt:layout style="padding:15px 0; " cssClass="col-md-6">
                    <wg:label><input type="checkbox" id="showDeactivatedRecords" class="showDeactivatedRecords">
                        Show Deactivated Records</wg:label>
                </lt:layout>
            </sec:authorize>
            <sec:authorize access="<%=CareTeamRoleCode.IS_CAN_ADD_NEW_PATIENTS%>">
                <lt:layout style="padding:15px 0 40px;">
                    <wg:button name="addPatient"
                               id="addPatient"
                               domType="button"
                               dataToggle="modal"
                               cssClass="btn-primary pull-right">
                        ADD NEW PATIENT
                    </wg:button>
                </lt:layout>
            </sec:authorize>
        </c:if>
        <wg:grid id="patientsList" cssClass="patientsList"
                 colIds="firstName,lastName,gender,birthDate,ssn,eventCount, community, dateCreated, actions"
                 colNames="First Name,Last Name,Gender, Birth Date,Social Security Number,Events, Community, Date Created, "
                 colFormats="string,string,string,string,string,string,string,localDate,fake"
                 dataUrl="care-coordination/patients"
                 deferLoading="true"
                />
    </lt:layout>
</lt:layout>
<%--<a class="showMergedLink hidden">Show Merged Patients</a>--%>