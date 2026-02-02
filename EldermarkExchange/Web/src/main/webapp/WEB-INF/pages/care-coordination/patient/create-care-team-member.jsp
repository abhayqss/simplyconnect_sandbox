<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="employees" scope="request" type="java.util.List"/>
<jsp:useBean id="roles" scope="request" type="java.util.List"/>
<jsp:useBean id="careTeamMemberDto" scope="request" type="com.scnsoft.eldermark.shared.carecoordination.careteam.CareTeamMemberDto"/>

<div class="modal fade" role="dialog" id="createCareTeamMemberModal" data-backdrop="static">
    <div class="modal-dialog" role="document" style="width:1000px;">
        <div class="modal-content">
            <wgForm:form role="form" id="careTeamMemberForm" commandName="careTeamMemberDto">
                <wg:modal-header closeBtn="true">
                    <span id="careTeamMemberHeader"> Care Team Member</span>
                </wg:modal-header>
                <wg:modal-body cssClass="col-md-12 createCareTeamMemberBody">
                    <span class="sectionHead">General</span>
                    <lt:layout style="padding-top: 10px;">
                        <input type="hidden" name="careTeamMemberId" id="careTeamMemberId"/>


                        <lt:layout id="careTeamEmpSelectBox" cssClass="col-md-4 form-group">
                            <wg:label _for="careTeamEmployeeSelect">Contact Name*</wg:label>

                            <wgForm:select path="careTeamEmployeeSelect"
                                           id="careTeamEmployeeSelect"
                                           items="${employees}"
                                           itemValue="id"
                                           itemLabel="label"
                                           cssClass="form-control">
                                <%--<wgForm:option value="" label="-- Select Contact-- "/>--%>
                            </wgForm:select>

                        </lt:layout>



                        <lt:layout id="careTeamRoleSelectBox" cssClass="col-md-4 form-group">
                            <wg:label _for="careTeamRoleSelect">Role*</wg:label>
                            <wgForm:select path="careTeamRoleSelect" id="careTeamRoleSelect" name="careTeamRoleSelect" cssClass="form-control"
                            >
                                <c:forEach var="item" items="${roles}">
                                    <wgForm:option value="${item.id}" label="${item.label}"/>
                                </c:forEach>
                            </wgForm:select>

                        </lt:layout>
                        <lt:layout cssClass="col-md-4 form-group">
                            <wg:label _for="careTeamDescription">Description</wg:label>
                            <input id="careTeamDescription" name="careTeamDescription" class="form-control"
                                   placeholder="-- Enter Description --" maxlength="128"/>
                        </lt:layout>
                        <lt:layout cssClass="col-md-12 form-group">
                        	<wg:label>
                                    <wgForm:checkbox  path="includeInFaceSheet" id="includeContactInFacesheetId" />
                                    Include contact in the face sheet document
                            </wg:label>
                        </lt:layout>
                    </lt:layout>

                    <span class="sectionHead">Notification Preferences</span>
                    <lt:layout cssClass="col-md-12 notificationPreferencesHeader no-horizontal-padding">
                        <lt:layout cssClass="col-md-6 notificationPreferencesColHeader "/>
                        <lt:layout cssClass="col-md-3 notificationPreferencesColHeader " style="padding-left:25px">Responsibility</lt:layout>
                        <lt:layout cssClass="col-md-3 notificationPreferencesColHeader " style="padding-left:20px">Channel</lt:layout>

                        <lt:layout id="careTeamMemberNotificationPreferences"
                                   cssClass="col-md-12 no-horizontal-padding"/>
                    </lt:layout>

                <div id="formError" class="form-error"></div>
                </wg:modal-body>
                <wg:modal-footer-btn-group>
                    <wg:button name="cancelBtn"
                               domType="link"
                               dataToggle="modal"

                               cssClass="btn-default cancelBtn">
                        CANCEL
                    </wg:button>

                    <wg:button name="saveCareTeamMember"
                               id="saveCareTeamMember"
                               domType="link"
                               dataToggle="modal"
                               cssClass="btn-primary submitBtn">
                        SAVE
                    </wg:button>

                </wg:modal-footer-btn-group>
            </wgForm:form>
        </div>
    </div>
</div>
<!-- Col template -->

<lt:layout cssClass="col-md-12 hidden no-horizontal-padding notificationPreferencesTemplate"
           id="notificationPreferencesTemplate">
    <input type="hidden" class="notificationPreferencesId">
    <input type="hidden" class="eventTypeId"/>

    <span class="col-md-6 eventType "></span>

    <div class="col-md-3 ">
        <wgForm:select class="responsibility selectpicker" data-style="notificationsSelect" path="responsibility">
            <c:forEach var="item" items="${responsibilities}">
                <c:choose>
                    <c:when test="${item == 'R'}">
                        <wgForm:option value="${item}" label="${item.description}" disabled="true"/>
                    </c:when>
                    <c:otherwise>
                        <wgForm:option value="${item}" label="${item.description}"/>
                    </c:otherwise>

                </c:choose>

            </c:forEach>
        </wgForm:select>
    </div>
    <div class="col-md-3 ">
        <wgForm:select class="notificationType selectpicker" data-style="notificationsSelect" path="notificationTypeList" multiple="true">
            <c:forEach var="item" items="${notificationTypes}">
                <wgForm:option value="${item}" label="${item.description}"/>
            </c:forEach>
        </wgForm:select>
    </div>
</lt:layout>