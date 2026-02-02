<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<wgForm:form role="form" id="assessmentReviewForm" commandName="residentAssessmentPriorityCheckDto"
             cssClass="assessments-modal-content-scoring">
    <wgForm:hidden id="assessmentFullName" path="assessmentName"/>

    <div id="reviewContentContainer" class="">
        No questions for review
    </div>
</wgForm:form>
<%--There is no visible page or question in the survey.--%>