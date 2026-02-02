<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<c:url value="/resources/images/arrow2-top.svg" var="toTop"/>

<div class="modal fade view-assessment-modal" role="dialog" id="viewAssessmentResultModal" data-backdrop="static">
    <div class="modal-dialog" role="document" style="width:1000px;">
        <div class="modal-content">
            <wg:modal-header closeBtn="true">
            <c:choose>
                <c:when test="${residentAssessmentScoringDto.assessmentShortName == 'CRAFFT'}">
                    <span id="assessmentResultHeader">${residentAssessmentScoringDto.assessmentName}</span>
                </c:when>
                <c:otherwise>
                    <span id="assessmentResultHeader">View ${residentAssessmentScoringDto.assessmentName}</span>
                </c:otherwise>
            </c:choose>
            </wg:modal-header>
            <wg:modal-body cssClass="no-border modal-background new-modal-body view-assessment-body">
                <wg:wizard id="assessmentView" cssClass="assessmentWzrd default-assessment-wizard">
                    <wg:wizard-header id="assessmentTabHeader"
                                      cssClass="nav nav-tabs no-bottom-margin wizard-nav-border">
                        <wg:wizard-head-item href="#detailsAssessmentTab" cssClass="assessmentWizardLnk"
                                             linkId="assessmentDetailsLnk"
                                             linkCssClass="ldr-ui-label ldr-head-lnk table-cell-box modal-tab modal-tab-text">
                            <wg:label cssClass="lnk-text">Assessment Details</wg:label>
                            <lt:layout/>
                        </wg:wizard-head-item>
                        <wg:wizard-head-item href="#historyAssessmentTab" cssClass="assessmentWizardLnk"
                                             linkId="assessmentHistoryLnk"
                                             linkCssClass="ldr-ui-label ldr-head-lnk table-cell-box modal-tab modal-tab-text">
                            <wg:label cssClass="lnk-text">Assessment History</wg:label>
                            <lt:layout/>
                        </wg:wizard-head-item>
                    </wg:wizard-header>

                    <wg:wizard-content cssClass="col-md-12 nav-tab-box-shadow wizard-content">
                        <%--details--%>
                        <wg:wizard-content-item id="detailsAssessmentTab" cssClass="detailsAssessmentTab row fade">
                            <lt:layout cssClass="col-md-12 no-horizontal-padding top15 bottom15" id="assessmentViewSectionName">
                                <span class="sectionHead">General</span>
                            </lt:layout>
                            <div>
                                <wgForm:form role="form" id="assessmentResultForm"
                                             commandName="residentAssessmentScoringDto">
                                    <wgForm:hidden id="residentAssessmentResult" path="assessmentResult"/>
                                    <wgForm:hidden id="residentAssessmentContent" path="assessmentContent"/>
                                    <wgForm:hidden id="scoringEnabled" path="scoringEnabled"/>
                                    <wgForm:hidden id="hasNumeration" path="hasNumeration"/>
                                    <wgForm:hidden id="assessmentType" path="type"/>
                                    <lt:layout cssClass="form-group col-md-6">
                                        <wg:label _for="dateCompleted">Date Completed</wg:label>
                                        <fmt:formatDate type="BOTH" pattern='MM/dd/yyyy hh:mm a (z)'
                                                        value='${residentAssessmentScoringDto.dateCompleted}'
                                                        var="assessmentDateCompleted"/>
                                        <wgForm:input path="dateCompleted" value="${assessmentDateCompleted}"
                                                      name="dateCompleted"
                                                      type="datetime" autocomplete="off"
                                                      id="dateCompleted" cssClass="form-control" placeholder=""/>
                                    </lt:layout>

                                    <lt:layout cssClass="form-group col-md-6">
                                        <wg:label _for="employeeName">Completed By</wg:label>
                                        <wgForm:input path="completedBy" name="completedBy" id="completedBy"
                                                      cssClass="form-control"/>
                                    </lt:layout>

                                    <c:if test="${not empty residentAssessmentScoringDto.comment}">
                                        <lt:layout cssClass="form-group col-md-12 assessmentComment">
                                            <wg:label _for="subjective">Comment</wg:label>
                                            <wgForm:textarea path="comment" name="comment"
                                                             id="subjective"
                                                             cssClass="form-control"/>
                                        </lt:layout>
                                    </c:if>
                                </wgForm:form>
                                <div class="clearfix"></div>
                            </div>

                            <c:if test="${residentAssessmentScoringDto.scoringEnabled}">

                                <div class="assessments-modal-content-scoring">
                                    <div id="assessmentViewModalContent">
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

                                        <lt:layout cssClass="col-md-12 no-horizontal-padding assessments-section bottom15">
                                            <c:choose>
                                                <c:when test="${residentAssessmentScoringDto.assessmentShortName == 'CRAFFT'}">
                                                    <span class="sectionHead">${residentAssessmentScoringDto.assessmentShortName} Scoring results</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="sectionHead">${residentAssessmentScoringDto.assessmentShortName} Scoring</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </lt:layout>
                                        <lt:layout cssClass="col-md-12 alert ${alertClass}">
                                            <div class="${iconClass}"></div>
                                            <div>
                                                <c:choose>
                                                    <c:when test="${residentAssessmentScoringDto.assessmentShortName != 'MDQ'}">
                                                        <div><b>${residentAssessmentScoringDto.assessmentScore} points</b></div>
                                                        <div>${residentAssessmentScoringDto.severity}</div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div>${residentAssessmentScoringDto.severity}</div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </lt:layout>

                                    </div>

                                    <%--<lt:layout cssClass="col-md-12 no-horizontal-padding assessments-section bottom15">--%>
                                        <%--<span class="sectionHead">Management</span>--%>
                                    <%--</lt:layout>--%>
                                    <%--<lt:layout cssClass="col-md-12 no-horizontal-padding">--%>
                                        <%--<i>${residentAssessmentScoringDto.managementComment}</i>--%>
                                    <%--</lt:layout>--%>
                                    <c:choose>
                                        <c:when test="${residentAssessmentScoringDto.assessmentShortName == 'DAST-10'}">
                                            <lt:layout cssClass="col-md-12 no-horizontal-padding assessments-section bottom15">
                                                <span class="sectionHead">Management</span>
                                            </lt:layout>
                                            <lt:layout cssClass="col-md-12 no-horizontal-padding">
                                                <i>${residentAssessmentScoringDto.managementComment}</i>
                                            </lt:layout>

                                            <lt:layout cssClass="col-md-12 no-horizontal-padding">
                                                <c:set value="care-coordination/assessment/${residentAssessmentScoringDto.assessmentId}/scoring/list"
                                                       var="dataUrl"/>
                                                <c:set value="score,severityShort,comments" var="columnIds"/>
                                                <c:set value="Score,${residentAssessmentScoringDto.severityColumnName},Comments"
                                                       var="columnNames"/>
                                                <c:set value="string,string,string" var="columnFormats"/>
                                                <wg:grid id="assessmentsScoringGroupViewList"
                                                         colIds="${columnIds}"
                                                         colNames="${columnNames}"
                                                         colFormats="${columnFormats}"
                                                         dataUrl="${dataUrl}"/>
                                            </lt:layout>
                                        </c:when>
                                        <c:when test="${residentAssessmentScoringDto.assessmentShortName == 'AUDIT' ||
                                                        residentAssessmentScoringDto.assessmentShortName == 'CRAFFT'}">
                                            <lt:layout cssClass="col-md-12 no-horizontal-padding assessments-section bottom15">
                                                <span class="sectionHead">Management</span>
                                            </lt:layout>
                                            <lt:layout cssClass="col-md-12 no-horizontal-padding">
                                                <i>${residentAssessmentScoringDto.managementComment}</i>
                                            </lt:layout>

                                            <lt:layout cssClass="col-md-12 no-horizontal-padding">
                                                <c:set value="care-coordination/assessment/${residentAssessmentScoringDto.assessmentId}/scoring/list"
                                                       var="dataUrl"/>
                                                <c:set value="score,severityShort,comments" var="columnIds"/>
                                                <c:set value="Score,${residentAssessmentScoringDto.severityColumnName},Comments"
                                                       var="columnNames"/>
                                                <c:set value="string,string,string" var="columnFormats"/>
                                                <wg:grid id="assessmentsScoringGroupViewList"
                                                         colIds="${columnIds}"
                                                         colNames="${columnNames}"
                                                         colFormats="${columnFormats}"
                                                         dataUrl="${dataUrl}"/>
                                            </lt:layout>
                                        </c:when>
                                        <c:otherwise>
                                            <lt:layout cssClass="col-md-12 no-horizontal-padding no-display">
                                                <c:set value="care-coordination/assessment/${residentAssessmentScoringDto.assessmentId}/scoring/list"
                                                       var="dataUrl"/>
                                                <c:set value="score,severityShort,comments" var="columnIds"/>
                                                <c:set value="Score,${residentAssessmentScoringDto.severityColumnName},Comments"
                                                       var="columnNames"/>
                                                <c:set value="string,string,string" var="columnFormats"/>
                                                <wg:grid id="assessmentsScoringGroupViewList"
                                                         colIds="${columnIds}"
                                                         colNames="${columnNames}"
                                                         colFormats="${columnFormats}"
                                                         dataUrl="${dataUrl}"/>
                                            </lt:layout>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:if>

                            <div id="assessmentContentViewContainer"></div>
                        </wg:wizard-content-item>

                        <%--history--%>
                        <wg:wizard-content-item id="historyAssessmentTab" cssClass="historyAssessmentTab row fade">
                            <div class="assessments-modal-content-scoring">
                                <lt:layout
                                        cssClass="col-md-12 no-horizontal-padding assessments-history-section bottom17">
                                    <span class="sectionHead">Change History</span>
                                </lt:layout>

                                <lt:layout cssClass="col-md-12 no-horizontal-padding">
                                    <c:set value="care-coordination/assessment/${residentAssessmentScoringDto.id}/history"
                                           var="dataUrl"/>
                                    <c:set value="date,status,author,updates" var="columnIds"/>
                                    <c:set value="Date,Status,Author,Updates" var="columnNames"/>
                                    <c:set value="localDateTime,string,string,fake" var="columnFormats"/>
                                    <wg:grid id="assessmentsHistoryViewList"
                                             colIds="${columnIds}"
                                             colNames="${columnNames}"
                                             colFormats="${columnFormats}"
                                             dataUrl="${dataUrl}"/>
                                </lt:layout>
                            </div>

                        </wg:wizard-content-item>
                    </wg:wizard-content>
                    <div class="up-scroller">
                        <img src="${toTop}"/>
                    </div>
                </wg:wizard>
            </wg:modal-body>

            <wg:modal-footer cssClass="default-modal-footer wzBtns default-assessment-footer with-border-spacing">
                <div class="btn-groups justify-content_space-between">
                    <div class="btn-group btn-group-flex align-items_center footer" role="group">
                        <a class="assessment-footer__left-arrow"></a>
                        <a class="assessment-footer__back-btn">Back</a>
                        <a class="assessment-footer__next-btn">Next</a>
                        <a class="assessment-footer__right-arrow"></a>
                    </div>
                    <div class="btn-group btn-group-flex align-items_center justify-content_flex-end footer" role="group">
                        <wg:button id="cancelBtn"
                                   name="cancelBtn"
                                   domType="link"
                                   dataToggle="modal"
                                   cssClass="btn-primary normalFont">
                            CLOSE
                        </wg:button>
                    </div>
                </div>
            </wg:modal-footer>

            <lt:layout id="assessmentHistoryRowActions" cssClass="hidden rowActions">
                <a class="seeHistoryLink"/>
            </lt:layout>
        </div>
    </div>
</div>
