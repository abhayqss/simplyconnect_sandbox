<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<wgForm:form role="form" id="assessmentResultForm" commandName="residentAssessmentResultDto">
    <wgForm:hidden path="patientId"/>
    <wgForm:hidden path="employeeId"/>
    <wgForm:hidden path="employeeName"/>
    <wgForm:hidden id="assessmentIdFormField" path="assessmentId"/>
    <wgForm:hidden id="resultJson" path="resultJson"/>
    <wgForm:hidden id="assessmentResultId" path="id"/>
    <wgForm:hidden id="chainId" path="chainId"/>
    <wgForm:hidden id="score" path="score"/>
    <wgForm:hidden id="dateAssigned" path="dateAssigned"/>

    <div class="assessmentGeneral">
        <lt:layout cssClass="col-md-12 no-horizontal-padding top15 bottom15">
            <span class="sectionHead">General</span>
        </lt:layout>
        <lt:layout cssClass="form-group col-md-6 bottom15">
            <wg:label _for="dateCompleted">Date Completed*</wg:label>
            <wgForm:input path="dateCompleted" name="dateCompleted" type="datetime"
                          id="dateCompleted" cssClass="form-control" placeholder=""
            />
        </lt:layout>

        <lt:layout cssClass="form-group col-md-6 bottom15">
            <wg:label _for="employeeName">Completed By*</wg:label>
            <wgForm:input path="employeeNameAndRole" name="employeeNameAndRole" id="employeeNameAndRole"
                          cssClass="form-control" disabled="true"/>
        </lt:layout>
    </div>
    <div class="clearfix"></div>
    <div id="assessmentContentContainer"></div>

    <div class="assessmentComment">
    <lt:layout cssClass="assessmentComment form-group col-md-12">
        <wg:label _for="subjective">Comment</wg:label>
        <wgForm:textarea path="comment" name="comment"
                         id="subjective"
                         cssClass="form-control"
                         disabled="${readOnly}"/>
    </lt:layout>
    </div>
</wgForm:form>