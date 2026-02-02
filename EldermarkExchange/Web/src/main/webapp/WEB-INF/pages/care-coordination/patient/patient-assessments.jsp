<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<c:set value="care-coordination/assessment/patient/${patient.id}/list" var="dataUrl"/>

<lt:layout cssClass="patientAssessments">

    <wgForm:form role="form" id="assessmentsFilterForm" commandName="assessmentsFilter">
        <lt:layout cssClass="col-md-2" style="width:400px;padding-left:0px;margin-bottom:20px;">
            <wgForm:input path="name"
                          id="filter.name"
                          cssClass="form-control" cssStyle="height: 34px"
            />
        </lt:layout>
        <lt:layout cssClass="col-md-2" style="width:100px">
            <wg:button name="searchAssessments"
                       id="searchAssessments"
                       domType="button"
                       dataToggle="modal"
                       cssClass="btn-primary pull-right">
                SEARCH
            </wg:button>
        </lt:layout>
    </wgForm:form>

    <c:set value="assessmentName,status,dateAssigned,dateCompleted,author,actions" var="columnIds"/>
    <c:set value="Assessment,Status,Date Started,Date Completed,Author,Actions" var="columnNames"/>
    <c:set value="string,string,localDate,localDate,string,fake" var="columnFormats"/>
    <wg:grid id="assessmentsList"
             colIds="${columnIds}"
             colNames="${columnNames}"
             colFormats="${columnFormats}"
             dataUrl="${dataUrl}"/>
    <div id="loader-div" class="hidden ajaxLoader"/>
</lt:layout>
