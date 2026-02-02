<jsp:useBean id="contactDto" scope="request" type="com.scnsoft.eldermark.shared.carecoordination.contacts.ContactDto"/>
<%@ page import="com.scnsoft.eldermark.authentication.SecurityExpressions" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:url value="/resources/images/wz-step-done.png" var="secureEmailRegistered"/>

<lt:layout cssClass="modal fade" role="dialog" id="createContactModal">
    <lt:layout cssClass="modal-dialog" role="document" style="width:1000px;">
        <lt:layout cssClass="modal-content">
            <wgForm:form role="form" id="contactForm" commandName="contactDto">
                <div id="expired" data-value="${expired}"/>
                <input type="hidden" name="unafilliatedDatabaseId" value="${unafilliatedDatabaseId}" id="unafilliatedDatabaseId"/>
                <c:set var="isNew" value="${contactDto.id == null}"/>
                <c:set var="is4dContact" value="${contactDto.contact4d != null && contactDto.contact4d}"/>
                <sec:authorize access="<%=SecurityExpressions.IS_CC_SUPERADMIN%>">
                    <c:set var="isCCSuperAdmin" value="true"/>
                </sec:authorize>
                <sec:authorize access="<%=SecurityExpressions.IS_CC_ADMIN%>">
                    <c:set var="isCCAdmin" value="true"/>
                </sec:authorize>
                <sec:authorize access="<%=SecurityExpressions.IS_CC_COMMUNITYADMIN%>">
                    <c:set var="isCCCommunityAdmin" value="true"/>
                </sec:authorize>
                <sec:authorize access="<%=SecurityExpressions.IS_DIRECT_MANAGER%>">
                    <c:set var="isDirectManager" value="true"/>
                </sec:authorize>
                <wg:modal-header closeBtn="true">
                    <span id="contactHeader"> Add new Contact</span>
                </wg:modal-header>
                <wg:modal-body cssClass="col-md-12 createContactBody">

                    <wgForm:hidden path="id"/>
                    <lt:layout cssClass="col-md-12 no-horizontal-padding">
                        <span class="sectionHead">General</span>
                    </lt:layout>
                    <lt:layout>
                        <input type="hidden" name="contactId" id="contactId"/>
                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="contact.firstName">First Name*</wg:label>
                            <wgForm:input path="firstName"
                                          id="contact.firstName"
                                          cssClass="form-control"
                                          maxlength="128"
                                          disabled="${not empty contactDto.firstName || expired}"
                                    />
                        </lt:layout>
                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="contact.lastName">Last Name*</wg:label>
                            <wgForm:input path="lastName"
                                          id="contact.lastName"
                                          cssClass="form-control"
                                          maxlength="128"
                                          disabled="${not empty contactDto.lastName || expired}"
                                    />
                        </lt:layout>
                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="contact.role.id">System Role*</wg:label>
                            <wgForm:select path="role.id"
                                           id="contact.role.id" cssClass="form-control" disabled="${expired}">
                                <c:forEach var="item" items="${careTeamRolesToEdit}">
                                    <wgForm:option value="${item.id}" label="${item.label}"/>
                                </c:forEach>
                            </wgForm:select>
                        </lt:layout>
                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="contact.email">Email${is4dContact ? "" : " (Login)"}*</wg:label>
                            <wgForm:input path="email"
                                          id="contact.email"
                                          cssClass="form-control"
                                          maxlength="255"
                                          disabled="${!isNew && !is4dContact || expired}"
                                    />
                        </lt:layout>


                        <lt:layout cssClass="col-md-12 no-horizontal-padding">
                            <lt:layout cssClass="col-md-6 form-group">
                                <wg:label _for="contact.organization">Organization*</wg:label>
                                <wgForm:select path="organization.id"
                                               id="contact.organization"
                                               cssClass="form-control"
                                               maxlength="255"
                                               disabled="${!isCCSuperAdmin || !isNew || expired}">
                                    <c:choose>
                                        <c:when test="${isCCSuperAdmin && isNew}">
                                            <c:forEach var="item" items="${organizations}">
                                                <wgForm:option value="${item.id}" label="${item.label}"/>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <wgForm:option value="${contactDto.organization.id}"
                                                           label="${contactDto.organization.label}"/>
                                        </c:otherwise>
                                    </c:choose>
                                </wgForm:select>
                            </lt:layout>

                            <lt:layout cssClass="col-md-6 form-group">
                                <wg:label _for="contact.company">Company</wg:label>
                                <wgForm:input path="company"
                                              id="contact.company"
                                              cssClass="form-control"
                                              maxlength="255"
                                              disabled="${expired}"
                                        />
                            </lt:layout>
                        </lt:layout>

                        <sec:authorize access="<%=SecurityExpressions.IS_CC_ADMIN%>">
                            <lt:layout id = "communityListLayout" cssClass="col-md-6 form-group">
                                <wg:label _for="contact.community">Community*</wg:label>
                                <wgForm:select path="communityId"
                                               id="contact.community"
                                               cssClass="form-control"
                                               disabled="${unaffiliatedOrg || expired}"
                                               maxlength="255">
                                            <c:forEach var="item" items="${communities}">
                                                <wgForm:option value="${item.id}" label="${item.label}"/>
                                            </c:forEach>
                                </wgForm:select>
                            </lt:layout>
                        </sec:authorize>
                        <sec:authorize access="<%=SecurityExpressions.NOT_CC_ADMIN%>">
                             <wgForm:input path="communityId" type="hidden"/>
                        </sec:authorize>

                        <c:choose>
                            <c:when test="${is4dContact}">
                                <lt:layout cssClass="col-md-12 form-group">
                                    <wg:label _for="contact.login4d">Login*</wg:label>
                                    <wgForm:input path="login4d"
                                                  id="contact.login4d"
                                                  cssClass="form-control"
                                                  maxlength="255"
                                                  disabled="true"
                                            />
                                </lt:layout>
                            </c:when>
                        </c:choose>
                    </lt:layout>

                    <lt:layout cssClass="col-md-12 no-horizontal-padding">
                        <span class="sectionHead">Address</span>
                    </lt:layout>

                    <lt:layout cssClass="col-md-12 no-horizontal-padding">
                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="contact.address.street">Street*</wg:label>
                            <wgForm:input path="address.street"
                                          id="contact.address.street"
                                          maxlength="255"
                                          cssClass="form-control"
                                          disabled="${expired}"
                                    />
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="contact.address.city">City*</wg:label>
                            <wgForm:input path="address.city"
                                          id="contact.address.city"
                                          maxlength="128"
                                          cssClass="form-control"
                                          disabled="${expired}"
                                    />
                        </lt:layout>

                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="contact.address.state">State*</wg:label>
                            <wgForm:select path="address.state.id"
                                           id="contact.address.state" cssClass="form-control" disabled="${expired}">
                                <wgForm:option value="" label="-- Select State --"/>
                                <c:forEach var="item" items="${states}">
                                    <wgForm:option value="${item.id}" label="${item.label}"/>
                                </c:forEach>
                            </wgForm:select>
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="contact.address.zip">Zip Code*</wg:label>
                            <wgForm:input path="address.zip"
                                          id="contact.address.zip"
                                          cssClass="form-control"
                                          maxlength="5"
                                          disabled="${expired}"
                                    />

                        </lt:layout>
                    </lt:layout>
                    <lt:layout cssClass="col-md-12 no-horizontal-padding">
                        <span class="sectionHead">Numbers</span>
                    </lt:layout>
                    <lt:layout cssClass="col-md-12 no-horizontal-padding ">
                        <lt:layout cssClass="form-group col-md-6 ">
                            <wg:label _for="contact.phone">Phone Number*</wg:label>
                            <wgForm:input path="phone"
                                          name="phone"
                                          id="contact.phone"
                                          cssClass="form-control"
                                          maxlength="16"
                                          disabled="${expired}"
                                    />
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-6 ">
                            <wg:label _for="contact.fax">Fax Number</wg:label>
                            <a href="#" class="help-icon" id="faxHelp" data-toggle="popover" data-trigger="hover" data-content="Please provide value for FAX number, in order to receive FAX notifications when new event for patient occurs"></a>
                            <wgForm:input path="fax"
                                          name="fax"
                                          id="contact.fax"
                                          cssClass="form-control"
                                          maxlength="16"
                                          disabled="${expired}"
                                    />
                        </lt:layout>
                    </lt:layout>

                    <div style="${(showPhrSM || isCCSuperAdmin) ? '' : 'display: none'}">
                        <lt:layout cssClass="col-md-12 no-horizontal-padding">
                            <span class="sectionHead">Modules</span>
                        </lt:layout>
                    </div>

                    <lt:layout cssClass="col-md-12 no-horizontal-padding">
                        <div id="phrSmModule" style="${(showPhrSM || isCCSuperAdmin) ? '' : 'display: none'}">
                            <lt:layout cssClass="col-md-6">
                                <wg:label>
                                    <wgForm:checkbox path="enabledExchange" id="enabledExchangeId"
                                                     disabled="${!(isDirectManager || isCCCommunityAdmin || isCCAdmin || isNew) || (isCCSuperAdmin && !showPhrSM) || expired}"/>
                                    Enable Secure Messaging<c:if test="${not unaffiliatedOrg}"> and PHR modules</c:if>
                                </wg:label>
                                <div id="phrWarning" class="alert alert-warning" style="display: none; margin-bottom:0;">
                                    PHR and Secure Messaging are not enabled. Please click ${isNew ? "Send Invite" : "Submit"}  button if you want to continue without changes.
                                </div>
                            </lt:layout>
                        </div>
                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label>Secure Email</wg:label>

                            <!-- active/warning icon -->
                            <c:choose>
                                <c:when test="${contactDto.secureMessagingActive}">
                                    <span class="active-icon"/>
                                </c:when>
                                <c:when test="${isDirectManager || isCCCommunityAdmin || isCCAdmin}">
                                    <lt:layout id="secureMessagingError1-content" cssClass="hidden">
                                        <c:choose>
                                            <c:when test="${empty contactDto.secureMessagingError}">
                                                The secure messaging account was not activated yet.
                                            </c:when>
                                            <c:when test="${contactDto.secureMessagingError eq 'CERTIFICATE_ERROR'}">
                                                Please ensure that Secure Messaging is configured for ${contactDto.organization.label} Organization and uploaded Certificate file and PIN are valid.
                                            </c:when>
                                            <c:when test="${contactDto.secureMessagingError eq 'REQUEST_NOT_AUTHORIZED'}">
                                                Please check <a href='https://service.directaddress.net/portal/'>SES portal</a> to resolve the issue.<br/>
                                                Possible reasons:
                                                <ul>
                                                    <li>Organization certificate error occurred</li>
                                                    <li>Direct email was deleted in the portal</li>
                                                </ul>
                                            </c:when>
                                            <c:when test="${contactDto.secureMessagingError eq 'EMPLOYEE_NOT_VALID'}">
                                                Please make sure that all mandatory fields are filled and ask ${contactDto.firstName} ${contactDto.lastName} to activate the Secure Messaging account.
                                                <ul>
                                                    <li>Secure Email</li>
                                                    <li>Address: Street, City, State, Zip code</li>
                                                    <li>Contact Email (Login)</li>
                                                    <li>Phone Number</li>
                                                </ul>
                                            </c:when>
                                            <c:when test="${contactDto.secureMessagingError eq 'ACCOUNT_NOT_REGISTERED' and not empty contactDto.secureMessaging}">
                                                Please register manually the requested Secure Messaging account for ${contactDto.firstName} ${contactDto.lastName} at <a href='https://service.directaddress.net/portal/'>SES portal</a> to resolve the issue.
                                            </c:when>
                                            <c:when test="${contactDto.secureMessagingError eq 'ACCOUNT_NOT_REGISTERED' and empty contactDto.secureMessaging}">
                                            </c:when>
                                            <c:otherwise>
                                                Please contact an administrator.
                                            </c:otherwise>
                                        </c:choose>
                                    </lt:layout>
                                    <a href="#" class="warning-icon" id="secureMessagingError1" data-toggle="popover" data-trigger="hover"></a>
                                </c:when>
                                <c:otherwise>
                                    <a href="#" class="warning-icon" id="secureMessagingError2" data-toggle="popover" data-trigger="hover" data-content='Please contact your manager to get access to the account.'></a>
                                </c:otherwise>
                            </c:choose>

                                <wgForm:input path="secureMessaging"
                                              id="contact.secureMessaging"
                                              cssClass="form-control"
                                              placeholder="Enter your existing secure email"
                                              maxlength="255"
                                              disabled="${!(isDirectManager || isCCCommunityAdmin || isCCAdmin || isNew) || expired}"
                                              aria-describedby="domain-addon"/>
                        </lt:layout>
                        <div id="formError" class="form-error"/>
                    </lt:layout>
                    <div>
                        <lt:layout cssClass="col-md-12 no-horizontal-padding">
                            <span class="sectionHead">Permissions</span>
                        </lt:layout>
                    </div>
                    
                    <lt:layout cssClass="col-md-12 no-horizontal-padding">
                    	<div id="qaIncidentReportings">
                            <lt:layout cssClass="col-md-12">
                                <wg:label>
                                    <wgForm:checkbox path="qaIncidentReports" id="qaIncidentReportsId"
                                                     disabled="${expired}"/>
                                    QA (Incident reports)
                                </wg:label>
                            </lt:layout>
                        </div>
                    </lt:layout>
                </wg:modal-body>
                <wg:modal-footer-btn-group>
                    <%--<lt:layout cssClass="btn-group" role="group">--%>
                        <wg:button name="cancelBtn"
                                   domType="link"
                                   dataToggle="modal"
                                   dataTarget="#createContactModal"
                                   id="contactCreateCancelBtn"
                                   cssClass="btn-default btn cancelBtn">
                            CANCEL
                        </wg:button>

                        <wg:button name="saveContact"
                                   id="saveContact"
                                   domType="link"
                                   dataToggle="modal"
                                   cssClass="btn-primary btn submitBtn">
                            SAVE
                        </wg:button>
                    <%--</lt:layout>--%>
                </wg:modal-footer-btn-group>
            </wgForm:form>
        </lt:layout>
    </lt:layout>
</lt:layout>