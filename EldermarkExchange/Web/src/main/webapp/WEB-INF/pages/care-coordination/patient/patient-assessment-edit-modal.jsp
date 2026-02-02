<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<c:url value="/resources/images/arrow2-top.svg" var="toTop"/>


<div class="modal fade edit-assessment-modal" role="dialog" id="addAssessmentResultModal" data-backdrop="static">
    <div class="modal-dialog" role="document" style="width:1036px;">
        <div class="modal-content assessment-modal-content">
            <%--  <wgForm:form cssClass="col-md-12 newNoteForm no-horizontal-padding" method="post" commandName="noteDto"
                           id="noteForm"
                           cssStyle="background-color: white; "> --%>
            <wg:modal-header closeBtn="true">
                <span id="assessmentResultHeader">${modalTitle}</span>
            </wg:modal-header>

            <%--<lt:layout cssClass="modal-layout">--%>
            <wg:modal-body cssClass="no-border modal-background new-modal-body">
                <wg:wizard id="assessmentView" cssClass="assessmentWzrd default-assessment-wizard">
                    <wg:wizard-content cssClass="nav-tab-box-shadow wizard-content">
                        <div id="assessmentModalContentSurvey"
                             class="assessment-content assessment-survey-content">
                            <jsp:include page="patient-assessment-result.jsp"/>
                        </div>
                        <div id="assessmentModalContentResults"
                             class="assessment-content assessment-results-content">
                        </div>
                        <div id="assessmentReviewContentContainer"
                             class="assessment-content assessment-results-content">
                        </div>
                    </wg:wizard-content>
                    <div class="up-scroller">
                        <img src="${toTop}"/>
                    </div>
                </wg:wizard>
            </wg:modal-body>
            <%--</lt:layout>--%>

            <wg:modal-footer-btn-group cssClass="default-modal-footer wzBtns default-assessment-footer">
                <wg:button id="cancelBtn"
                           name="cancelBtn"
                           domType="link"
                           dataToggle="modal"
                           dataTarget="#addAssessmentResultModal"
                           cssClass="btn-default selectAssessmentTypeStep assessmentResultStep assessmentResultStepScoring normalFont">
                    CANCEL
                </wg:button>

                <wg:button id="nextBtn" name="nextBtn" domType="link"
                           cssClass="btn-primary next selectAssessmentTypeStep normalFont">
                    NEXT
                </wg:button>
                <wg:button id="nextBtnScoring" name="nextBtnScoring" domType="link"
                           cssClass="btn-primary next assessmentResultStepScoring normalFont">
                    NEXT
                </wg:button>

                <wg:button name="saveAssessmentResult"
                           id="saveAssessmentResult"
                           domType="link"
                           dataToggle="modal"
                           cssClass="btn-primary finish assessmentResultStep normalFont">
                    SAVE
                </wg:button>
                <wg:button id="backBtn" name="backBtn" domType="link"
                           cssClass="btn-default previous assessmentScoringStep normalFont">
                    BACK
                </wg:button>
                <wg:button name="saveAssessmentResultScoring"
                           id="saveAssessmentResultScoring"
                           domType="link"
                           dataToggle="modal"
                           cssClass="btn-primary finish assessmentScoringStep normalFont">
                    SAVE
                </wg:button>
            </wg:modal-footer-btn-group>
        </div>
    </div>
</div>
