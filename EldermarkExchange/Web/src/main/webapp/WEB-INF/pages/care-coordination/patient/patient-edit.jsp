<%@ page import="com.scnsoft.eldermark.authentication.SecurityExpressions" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:message code="patient.search.field.dateOfBirth.format" var="dateFormat"/>

<lt:layout cssClass="modal fade" role="dialog" id="createPatientModal">
    <lt:layout cssClass="modal-dialog" role="document" style="width:1000px;">
        <lt:layout cssClass="modal-content">
            <wgForm:form role="form" id="patientForm" commandName="patientDto">
                <c:set var="isNew" value="${patientDto.id == null}"/>
                <sec:authorize access="<%=SecurityExpressions.IS_CC_SUPERADMIN%>">
                    <c:set var="isCCSuperAdmin" value="true"/>
                </sec:authorize>
                <wg:modal-header closeBtn="true">
                    <%--<span id="patientHeader"> Add New Patient</span>--%>
                    <span id="patientHeader">
                    <c:choose>
                        <c:when test="${isNew}">
                            Add New Patient
                        </c:when>
                        <c:otherwise>
                            Edit Patient
                        </c:otherwise>
                    </c:choose>
                    </span>
                </wg:modal-header>
                <wg:modal-body cssClass="col-md-12 createPatientBody whiteBackground">
                    <wgForm:hidden path="id"/>

                    <%--Demographics section--%>
                    <div class="patientSection col-md-12">
                        <span class="sectionHead col-sm-12">Demographics</span>
                        <input type="hidden" name="patientId" id="patientId"/>
                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="patient.firstName">First Name*</wg:label>
                            <wgForm:input path="firstName"
                                          id="patient.firstName"
                                          cssClass="form-control"
                                          maxlength="128"
                                    />
                        </lt:layout>
                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="patient.lastName">Last Name*</wg:label>
                            <wgForm:input path="lastName"
                                          id="patient.lastName"
                                          cssClass="form-control"
                                          maxlength="128"
                                    />
                        </lt:layout>
                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="patient.ssn">Social Security Number*</wg:label>
                            <wgForm:input path="ssn"
                                          id="patient.ssn"
                                          cssClass="form-control"
                                          maxlength="9"
                                          placeholder="XXX XX XXXX"
                                    />
                        </lt:layout>
                        <%--<lt:layout cssClass="col-md-6 form-group">--%>
                            <%--<wg:label _for="patient.birthDate">Date of Birth*</wg:label>--%>
                            <%--<wgForm:input path="birthDate" name="birthDate" id="patient.birthDate"--%>
                                          <%--type="text"--%>
                                          <%--cssClass="form-control datepicker" data-provide="datepicker"/>--%>
                            <%--<wg:icon cssClass="glyphicon-calendar"/>--%>
                        <%--</lt:layout>--%>

                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="patient.birthDate">Date of Birth*</wg:label>
                            <lt:layout cssClass="no-horizontal-padding date col-md-12">
                                <wgForm:input path="birthDate" name="birthDate" id="patient.birthDate" type="text"
                                              placeholder="${dateFormat}" cssClass="form-control datepicker" data-provide="datepicker"/>
                                <wg:icon cssClass="glyphicon-calendar"/>
                            </lt:layout>
                        </lt:layout>
                        <div style="clear:both;"/>

                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="patient.gender">Gender*</wg:label>
                            <wgForm:select path="gender"
                                           id="patient.gender" cssClass="form-control">
                                <wgForm:option value="" label="-- Select --"/>
                                <wgForm:option value="M" label="Male"/>
                                <wgForm:option value="F" label="Female"/>
                                <wgForm:option value="UN" label="Undefined"/>
                            </wgForm:select>
                        </lt:layout>
                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="patient.maritalStatus">Marital Status</wg:label>
                            <wgForm:select path="maritalStatus"
                                           id="patient.maritalStatus" cssClass="form-control">
                                <wgForm:option value="" label="-- Select --"/>
                                <wgForm:option value="Annulled" label="Annulled"/>
                                <wgForm:option value="Divorced" label="Divorced"/>
                                <wgForm:option value="Domestic Partner" label="Domestic Partner"/>
                                <wgForm:option value="Interlocutory" label="Interlocutory"/>
                                <wgForm:option value="Legally Separated" label="Legally Separated"/>
                                <wgForm:option value="Married" label="Married"/>
                                <wgForm:option value="Never Married" label="Never Married"/>
                                <wgForm:option value="Polygamous" label="Polygamous"/>
                                <wgForm:option value="Widowed" label="Widowed"/>
                            </wgForm:select>
                        </lt:layout>
                    </div>
                    <div class="col-md-12">
                        <div class="sectionLine"></div>
                    </div>

                    <%--Community section--%>
                    <div class="patientSection col-md-12">
                        <span class="sectionHead col-sm-12">Community</span>
                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="patient.organizationId">Organization*</wg:label>
                            <wgForm:select path="organizationId"
                                          id="patient.organizationId"
                                          cssClass="form-control"
                                          disabled="${!isCCSuperAdmin}">
                                <c:forEach var="item" items="${organizations}">
                                    <wgForm:option value="${item.id}" label="${item.label}"/>
                                </c:forEach>
                            </wgForm:select>
                        </lt:layout>
                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="patient.communityId">Community*</wg:label>
                            <wgForm:select path="communityId"
                                           id="patient.communityId" cssClass="form-control"
                                           disabled="${not isNew}">
                                <c:forEach var="item" items="${communities}">
                                    <wgForm:option value="${item.id}" label="${item.label}"/>
                                </c:forEach>
                            </wgForm:select>
                            <div id="communityWarning" class="alert alert-warning" style="display: none; margin-bottom:0; margin-top: 15px">
                                There is no community created for current organization. You can not create a patient record until the community is created.
                            </div>
                        </lt:layout>
                    </div>
                    <div class="col-md-12">
                        <div class="sectionLine"></div>
                    </div>

                    <%--Address section--%>
                    <div class="patientSection col-md-12">
                        <span class="sectionHead col-sm-12">Address</span>
                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="patient.address.street">Street*</wg:label>
                            <wgForm:input path="address.street"
                                          id="patient.address.street"
                                          maxlength="255"
                                          cssClass="form-control"
                            />
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="patient.address.city">City*</wg:label>
                            <wgForm:input path="address.city"
                                          id="patient.address.city"
                                          maxlength="128"
                                          cssClass="form-control"
                            />
                        </lt:layout>

                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="patient.address.state">State*</wg:label>
                            <wgForm:select path="address.state.id"
                                           id="patient.address.state" cssClass="form-control">
                                <wgForm:option value="" disabled="true" label="Select"/>
                                <c:forEach var="item" items="${states}">
                                    <wgForm:option value="${item.id}" label="${item.label}"/>
                                </c:forEach>
                            </wgForm:select>
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="patient.address.zip">Zip Code*</wg:label>
                            <wgForm:input path="address.zip"
                                          id="patient.address.zip"
                                          cssClass="form-control"
                                          maxlength="5"
                            />

                        </lt:layout>
                    </div>
                    <div class="col-md-12">
                        <div class="sectionLine"></div>
                    </div>

                    <%--Telecom section--%>
                    <div class="patientSection col-md-12">
                        <span class="sectionHead col-sm-12">Telecom</span>
                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="patient.cellPhone">Cell Phone Number*</wg:label>
                            <wgForm:input path="cellPhone"
                                          name="cellPhone"
                                          id="patient.cellPhone"
                                          cssClass="form-control"
                                          maxlength="16"
                            />
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-6 form-group">
                            <wg:label _for="patient.phone">Home Phone Number</wg:label>
                            <wgForm:input path="phone"
                                          name="phone"
                                          id="patient.phone"
                                          cssClass="form-control"
                                          maxlength="16"
                            />
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-6 ">
                            <wg:label _for="contact.email" id="email.label">Email*</wg:label>
                            <wgForm:input path="email"
                                          id="contact.email"
                                          cssClass="form-control"
                                          maxlength="255"
                            />
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-6 " style="margin-top: 30px;">
                            <wg:label><input type="checkbox" id="noemail"> Client doesn't have email</wg:label>
                        </lt:layout>

                    </div>
                    <div class="col-md-12">
                        <div class="sectionLine"></div>
                    </div>

                    <%--Insurance Section--%>
                    <div class="patientSection col-md-12">
                        <span class="sectionHead col-sm-12">Insurance</span>
                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="patient.insuranceId">Network</wg:label>
                            <lt:layout cssClass="life-search-dropdown col-md-12" style="padding-left: 0; padding-right:0;">
                                <wgForm:select path="insuranceId"
                                               id="patient.insuranceId" name="insuranceId" cssClass="form-control">
                                    <c:forEach var="item" items="${insurances}">
                                        <wgForm:option value="${item.id}" label="${item.label}"/>
                                    </c:forEach>

                                </wgForm:select>
                                <wg:icon cssClass="glyphicon-search"/>
                            </lt:layout>


                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="patient.insurancePlan">Plan</wg:label>
                            <wgForm:input path="insurancePlan"
                                          id="patient.insurancePlan"
                                          cssClass="form-control"
                                          maxlength="128"
                            />
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="patient.groupNumber">Group Number</wg:label>
                            <wgForm:input path="groupNumber"
                                          id="patient.groupNumber" name="groupNumber"
                                          cssClass="form-control"
                                          maxlength="250"
                            />
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="patient.memberNumber">Member Number</wg:label>
                            <wgForm:input path="memberNumber"
                                          id="patient.memberNumber" name="memberNumber"
                                          cssClass="form-control"
                                          maxlength="250"
                            />
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="patient.medicareNumber">Medicare Number</wg:label>
                            <wgForm:input path="medicareNumber"
                                          id="patient.medicareNumber" name="medicareNumber"
                                          cssClass="form-control"
                                          maxlength="250"
                            />
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="patient.medicaidNumber">Medicaid Number</wg:label>
                            <wgForm:input path="medicaidNumber"
                                          id="patient.medicaidNumber" name="medicaidNumber"
                                          cssClass="form-control"
                                          maxlength="250"
                            />
                        </lt:layout>
                    </div>
                    <div class="col-md-12">
                        <div class="sectionLine"></div>
                    </div>

                    <%--Ancillary Information Section--%>
                    <div class="patientSection col-md-12">
                        <span class="sectionHead col-sm-12">Ancillary Information</span>
                        <div class="col-md-12"  style="padding: 0">
                            <lt:layout cssClass="form-group col-md-6">
                                <wg:label _for="patient.retained">Retained</wg:label>
                                <wgForm:select path="retained"
                                               id="patient.retained"
                                               name="retained"
                                               cssClass="form-control">
                                    <wgForm:option value="" label="-- Select --"/>
                                    <wgForm:option value="false" label="No"/>
                                    <wgForm:option value="true" label="Yes"/>
                                </wgForm:select>
                            </lt:layout>
                            <lt:layout cssClass="form-group col-md-6">
                                <wg:label _for="patient.primaryCarePhysician">Primary Care Physician</wg:label>
                                <wgForm:input path="primaryCarePhysician"
                                              id="patient.primaryCarePhysician"
                                              name="primaryCarePhysician"
                                              cssClass="form-control"
                                              maxlength="250"
                                />
                            </lt:layout>
                        </div>
                        <%--<div class="col-md-12"  style="padding: 0">--%>
                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="patient.intakeDate">Intake Date</wg:label>
                            <lt:layout cssClass="no-horizontal-padding date col-md-12">
                                <wgForm:input path="intakeDate" name="intakeDate" type="datetime"
                                              id="patient.intakeDate" cssClass="form-control" placeholder="" autocomplete="off"
                                />
                                <wg:icon cssClass="glyphicon-calendar"/>
                            </lt:layout>
                            <div style="clear:both;"/>
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="patient.referralSource">Referral Source</wg:label>
                            <wgForm:input path="referralSource"
                                          id="patient.referralSource"
                                          name="referralSource"
                                          cssClass="form-control"
                                          maxlength="250"
                            />
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-12">
                            <wg:label _for="patient.curentPharmacyName">Current Pharmacy Name</wg:label>
                            <wgForm:input path="currentPharmacyName"
                                          id="patient.curentPharmacyName"
                                          name="curentPharmacyName"
                                          cssClass="form-control"
                                          maxlength="250"
                            />
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="patient.curentPharmacyName">Device ID</wg:label>
                            <wgForm:input path="deviceID"
                                          id="patient.deviceID" name="deviceID"
                                          cssClass="form-control"
                                          maxlength="250"
                            />
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label >Device ID (Secondary)</wg:label>
                            <wgForm:input path="deviceIDSecondary"
                                          id="patient.deviceIDSecondary" name="deviceIDSecondary"
                                          cssClass="form-control"
                                          maxlength="250"
                            />
                        </lt:layout>
                    </div>

                <div id="formError" class="form-error"></div>
                </wg:modal-body>
                <wg:modal-footer-btn-group>
                    <%--<lt:layout cssClass="btn-group" role="group">--%>
                        <wg:button name="cancelBtn"
                                   domType="link"
                                   dataToggle="modal"
                                   dataTarget="#createPatientModal"
                                   id="patientCreateCancelBtn"
                                   cssClass="btn-default btn cancelBtn">
                            CANCEL
                        </wg:button>

                        <wg:button name="savePatient"
                                   id="savePatient"
                                   domType="link"
                                   dataToggle="modal"
                                   cssClass="btn-primary btn submitBtn">
                            SAVE
                        </wg:button>
                </wg:modal-footer-btn-group>
            </wgForm:form>
        </lt:layout>
    </lt:layout>
</lt:layout>

<div id="matchedPatientListContainer"></div>