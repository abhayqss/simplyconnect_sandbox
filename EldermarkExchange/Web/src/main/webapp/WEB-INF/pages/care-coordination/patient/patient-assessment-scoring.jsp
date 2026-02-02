<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<wgForm:form role="form" id="assessmentScoringForm" commandName="residentAssessmentScoringDto" cssClass="assessments-modal-content-scoring">
    <wgForm:hidden id="assessmentShortName" path="assessmentShortName"/>
    <wgForm:hidden id="assessmentFullName" path="assessmentName"/>
    <wgForm:hidden id="assessmentScore" path="assessmentScore"/>
    <c:choose>
        <c:when test="${residentAssessmentScoringDto.warning eq 'Low'}">
            <c:set value="assessments-alert-success" var="alertClass"></c:set>
            <c:set value="assessments-ok-icon" var="iconClass"></c:set>
        </c:when>
        <c:when test="${residentAssessmentScoringDto.warning eq 'Medium'}">
            <c:set value="assessments-alert-warning" var="alertClass"></c:set>
            <c:set value="assessments-warning-icon" var="iconClass"></c:set>
        </c:when>
        <c:otherwise>
            <c:set value="assessments-alert-danger" var="alertClass"></c:set>
            <c:set value="assessments-attention-icon" var="iconClass"></c:set>
        </c:otherwise>
    </c:choose>

    <lt:layout cssClass="col-md-12 no-horizontal-padding top15 bottom15">
        <c:choose>
            <c:when test="${residentAssessmentScoringDto.assessmentShortName == 'AUDIT'}">
                <span class="sectionHead">${residentAssessmentScoringDto.assessmentShortName} Scoring results</span>
            </c:when>
            <c:otherwise>
                <span class="sectionHead">${residentAssessmentScoringDto.assessmentShortName} Scoring</span>
            </c:otherwise>
        </c:choose>
    </lt:layout>
    <lt:layout cssClass="col-md-12 alert ${alertClass} bottom40">
        <div class="${iconClass}"></div>
        <c:choose>
            <c:when test="${residentAssessmentScoringDto.assessmentShortName == 'GDSS' ||
                            residentAssessmentScoringDto.assessmentShortName == 'GDSL'}">
                <div><b>${residentAssessmentScoringDto.assessmentScore} points</b></div>
                <div>${residentAssessmentScoringDto.comment}</div>
            </c:when>
            <c:when test="${residentAssessmentScoringDto.assessmentShortName == 'AUDIT' ||
                            residentAssessmentScoringDto.assessmentShortName == 'SLUMS'}">
                <div><b>${residentAssessmentScoringDto.assessmentScore} points</b></div>
                <div>${residentAssessmentScoringDto.severityShort}</div>
            </c:when>
            <c:when test="${residentAssessmentScoringDto.assessmentShortName != 'MDQ'}">
                <div><b>${residentAssessmentScoringDto.assessmentScore} points</b></div>
                <div>${residentAssessmentScoringDto.severity}</div>
            </c:when>
            <c:otherwise>
                <div>${residentAssessmentScoringDto.severity}</div>
            </c:otherwise>
        </c:choose>
    </lt:layout>

    <c:if test="${residentAssessmentScoringDto.assessmentShortName != 'MDQ'}">
        <lt:layout cssClass="col-md-12 no-horizontal-padding bottom15">
            <span class="sectionHead">Management</span>
        </lt:layout>
        <lt:layout cssClass="col-md-12 no-horizontal-padding">
            <i>${residentAssessmentScoringDto.managementComment}</i>
        </lt:layout>


    <c:choose>
        <c:when test="${residentAssessmentScoringDto.assessmentShortName == 'CRAFFT'}">
            <lt:layout cssClass="col-md-12 no-horizontal-padding">
                <c:set value="care-coordination/assessment/${residentAssessmentScoringDto.assessmentId}/scoring/list" var="dataUrl"/>
                <c:set value="score,severityShort,comments" var="columnIds"/>
                <c:set value="Score,${residentAssessmentScoringDto.severityColumnName},Suggested Action" var="columnNames"/>
                <c:set value="string,string,string" var="columnFormats"/>
                <wg:grid id="assessmentsScoringGroupList"
                         colIds="${columnIds}"
                         colNames="${columnNames}"
                         colFormats="${columnFormats}"
                         dataUrl="${dataUrl}"/>
            </lt:layout>
        </c:when>
        <c:when test="${residentAssessmentScoringDto.assessmentShortName == 'SLUMS'}">
            <lt:layout cssClass="col-md-12 no-horizontal-padding">
                <c:set value="string,string,string" var="columnFormats"/>
                <c:set value="care-coordination/assessment/slums/${residentAssessmentScoringDto.assessmentId}/scoring/list" var="dataUrl"/>
                <c:set value="severityShort,scoreForPatientsWithHighSchoolEducation,scoreForPatientsWithoutHighSchoolEducation" var="columnIds"/>
                <c:set value="${residentAssessmentScoringDto.severityColumnName}, Total score range for individuals with high school education, Total score range for individuals with less than high school education" var="columnNames"/>
                <wg:grid id="assessmentsScoringGroupList"
                         colIds="${columnIds}"
                         colNames="${columnNames}"
                         colFormats="${columnFormats}"
                         dataUrl="${dataUrl}"/>
            </lt:layout>
        </c:when>
        <c:when test="${residentAssessmentScoringDto.assessmentShortName == 'GDSS'||
                        residentAssessmentScoringDto.assessmentShortName == 'GDSL'}">
            <lt:layout cssClass="col-md-12 no-horizontal-padding">
                <c:set value="care-coordination/assessment/${residentAssessmentScoringDto.assessmentId}/scoring/list" var="dataUrl"/>
                <c:set value="comments, score" var="columnIds"/>
                <c:set value="Result, Total score range" var="columnNames"/>
                <c:set value="string,string" var="columnFormats"/>
                <wg:grid id="assessmentsScoringGroupList"
                         colIds="${columnIds}"
                         colNames="${columnNames}"
                         colFormats="${columnFormats}"
                         dataUrl="${dataUrl}"/>
            </lt:layout>
        </c:when>
        <c:when test="${residentAssessmentScoringDto.assessmentShortName == 'AUDIT'}">
            <lt:layout cssClass="col-md-12 no-horizontal-padding">
                <c:set value="string,string,string" var="columnFormats"/>
                <c:set value="care-coordination/assessment/${residentAssessmentScoringDto.assessmentId}/scoring/list" var="dataUrl"/>
                <c:set value="score,severityShort,comments" var="columnIds"/>
                <c:set value="Score,${residentAssessmentScoringDto.severityColumnName},Recommendation" var="columnNames"/>
                <wg:grid id="assessmentsScoringGroupList"
                         colIds="${columnIds}"
                         colNames="${columnNames}"
                         colFormats="${columnFormats}"
                         dataUrl="${dataUrl}"/>
            </lt:layout>
        </c:when>
        <c:when test="${residentAssessmentScoringDto.assessmentShortName == 'Sad Persons Scale'}">
            <lt:layout cssClass="col-md-12 no-horizontal-padding">
                <c:set value="care-coordination/assessment/sad/${residentAssessmentScoringDto.assessmentId}/scoring/list" var="dataUrl"/>
                <c:set value="scoreRange,description" var="columnIds"/>
                <c:set value="${residentAssessmentScoringDto.severityColumnName}, Interpretation" var="columnNames"/>
                <wg:grid id="assessmentsScoringGroupList"
                         colIds="${columnIds}"
                         colNames="${columnNames}"
                         colFormats="${columnFormats}"
                         dataUrl="${dataUrl}"/>
            </lt:layout>
        </c:when>
        <c:when test="${residentAssessmentScoringDto.assessmentShortName == 'DAST-10'}">
            <lt:layout cssClass="col-md-12 no-horizontal-padding">
                <c:set value="care-coordination/assessment/${residentAssessmentScoringDto.assessmentId}/scoring/list" var="dataUrl"/>
                <c:set value="score,severityShort,comments" var="columnIds"/>
                <c:set value="Score,${residentAssessmentScoringDto.severityColumnName},Suggested Action" var="columnNames"/>
                <c:set value="string,string,string" var="columnFormats"/>
                <wg:grid id="assessmentsScoringGroupList"
                         colIds="${columnIds}"
                         colNames="${columnNames}"
                         colFormats="${columnFormats}"
                         dataUrl="${dataUrl}"/>
            </lt:layout>
        </c:when>
        <c:when test="${residentAssessmentScoringDto.assessmentShortName == 'PC-PTSD'}">
            <lt:layout cssClass="col-md-12 no-horizontal-padding">
                <c:set value="care-coordination/assessment/${residentAssessmentScoringDto.assessmentId}/scoring/list" var="dataUrl"/>
                <c:set value="score,severity" var="columnIds"/>
                <c:set value="Total score range, Interpretation" var="columnNames"/>
                <c:set value="string,string" var="columnFormats"/>
                <wg:grid id="assessmentsScoringGroupList"
                         colIds="${columnIds}"
                         colNames="${columnNames}"
                         colFormats="${columnFormats}"
                         dataUrl="${dataUrl}"/>
            </lt:layout>
        </c:when>
        <c:when test="${residentAssessmentScoringDto.assessmentShortName == 'PTSD'}">
            <lt:layout cssClass="col-md-12 no-horizontal-padding">
                <c:set value="care-coordination/assessment/${residentAssessmentScoringDto.assessmentId}/scoring/list" var="dataUrl"/>
                <c:set value="score,severity" var="columnIds"/>
                <c:set value="Total score range, Interpretation" var="columnNames"/>
                <c:set value="string,string" var="columnFormats"/>
                <wg:grid id="assessmentsScoringGroupList"
                         colIds="${columnIds}"
                         colNames="${columnNames}"
                         colFormats="${columnFormats}"
                         dataUrl="${dataUrl}"/>
            </lt:layout>
        </c:when>
        <c:otherwise>
            <lt:layout cssClass="col-md-12 no-horizontal-padding">
                <c:set value="care-coordination/assessment/${residentAssessmentScoringDto.assessmentId}/scoring/list" var="dataUrl"/>
                <c:set value="score,severityShort,comments" var="columnIds"/>
                <c:set value="Score,${residentAssessmentScoringDto.severityColumnName},Comments" var="columnNames"/>
                <c:set value="string,string,string" var="columnFormats"/>
                <wg:grid id="assessmentsScoringGroupList"
                         colIds="${columnIds}"
                         colNames="${columnNames}"
                         colFormats="${columnFormats}"
                         dataUrl="${dataUrl}"/>
            </lt:layout>
        </c:otherwise>
    </c:choose>
    </c:if>

    <c:if test="${residentAssessmentScoringDto.assessmentScore >=5 && true == residentAssessmentScoringDto.getShouldSendEvents()}">
        <lt:layout cssClass="col-md-12 no-horizontal-padding bottom15">
            <i>By clicking "Submit" button, the system will generate "Assessment risk identified" event. The alerts will be sent to care team members.</i>
        </lt:layout>
    </c:if>


</wgForm:form>