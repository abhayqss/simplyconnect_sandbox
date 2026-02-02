<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>


<spring:message text="At least 2 characters" var="namePlaceholder"/>
<spring:message text="USA Zip Code (5 digits)" var="zipPlaceholder"/>
<spring:message text="email" var="emailPlaceholder"/>
<spring:message text="yyyy-MM-dd hh:mm a Z" var="dateTimePlaceholder"/>


<div class="modal fade" role="dialog" id="createNewEventModal" data-backdrop="static">
    <div class="modal-dialog" role="document" style="width:1000px;">
        <div class="modal-content">

            <wgForm:form cssClass="col-md-12 newEventForm no-horizontal-padding" method="post" commandName="eventDto"
                         id="newEventForm"
                         cssStyle="background-color: white; ">
                <wg:modal-header closeBtn="true">
                    <span id="careTeamMemberHeader">Create New Event</span>
                </wg:modal-header>
                <%--<wg:modal-body>--%>
                <div class="col-md-12 whiteBackground">
                    <div class="eventSection no-border col-md-12">
                        <span class="sectionHead col-md-12">Patient info</span>
                        <wgForm:hidden path="patient.id" name="patient.id"/>
                        <lt:layout cssClass="form-group col-md-4">
                            <wg:label _for="patientFirstName">First Name</wg:label>
                            <wgForm:input path="patient.firstName" name="patient.firstName" id="patient.firstName"
                                          cssClass="form-control" disabled="true"/>
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-4">
                            <wg:label _for="patient.lastName">Last Name</wg:label>
                            <wgForm:input path="patient.lastName" name="patient.lastName" id="patient.lastName"
                                          cssClass="form-control"
                                          disabled="true"/>
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-4">
                            <wg:label _for="patient.birthDate">Date of Birth</wg:label>
                            <wgForm:input path="patient.birthDate" name="patient.birthDate" id="patient.birthDate"
                                          type="text"
                                          cssClass="form-control datepicker" data-provide="datepicker" disabled="true"/>
                        </lt:layout>

                        <lt:layout cssClass="form-group col-md-4">
                            <wg:label _for="patient.ssn">SSN</wg:label>
                            <wgForm:input path="patient.ssn" name="patient.ssn" id="patient.ssn" cssClass="form-control"
                                          disabled="true"/>
                        </lt:layout>

                        <lt:layout cssClass="form-group col-md-4">
                            <wg:label _for="patient.gender">Gender</wg:label>
                            <wgForm:input path="patient.gender" name="patient.gender" id="patient.gender"
                                          cssClass="form-control"
                                          disabled="true"/>
                        </lt:layout>

                        <lt:layout cssClass="form-group col-md-4">
                            <wg:label _for="patient.maritalStatus">Marital Status</wg:label>
                            <wgForm:input path="patient.maritalStatus" name="patient.maritalStatus"
                                          id="patient.maritalStatus"
                                          cssClass="form-control" disabled="true"/>
                        </lt:layout>

                        <lt:layout cssClass="form-group col-md-12">
                            <wg:label _for="patient.address">Address</wg:label>
                            <wgForm:input path="patient.address.displayAddress" name="patient.address"
                                          id="patient.address"
                                          cssClass="form-control"
                                          disabled="true"/>
                        </lt:layout>

                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="patient.organization">Organization</wg:label>
                            <wgForm:input path="patient.organization" name="patient.organization"
                                          id="patient.organization"
                                          cssClass="form-control"
                                          disabled="true"/>
                        </lt:layout>


                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="patient.community">Community</wg:label>
                            <wgForm:input path="patient.community" name="patient.community"
                                          id="patient.community"
                                          cssClass="form-control"
                                          disabled="true"/>
                        </lt:layout>

                    </div>
                    <div class="eventSection col-md-12">
                        <span class="sectionHead col-md-12">Event Essentials</span>

                        <lt:layout cssClass="form-group col-md-8">
                            <wg:label _for="employee.name">Person Submitting Event</wg:label>
                            <wgForm:input path="employee.displayName" name="employee.name" id="employee.name"
                                          cssClass="form-control"
                                          disabled="true"/>
                        </lt:layout>

                        <lt:layout cssClass="form-group col-md-4">
                            <wg:label _for="employee.roleId">Care Team Role*</wg:label>
                            <wgForm:select path="employee.roleId" name="employee.roleId" id="employee.roleId"
                                           cssClass="form-control">
                                <c:forEach var="item" items="${careTeamRoles}">
                                    <wgForm:option value="${item.id}" label="${item.label}"/>
                                </c:forEach>
                            </wgForm:select>
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-4">
                            <wg:label _for="eventDetails.eventDatetime">Event Date and Time*</wg:label>
                            <wgForm:input path="eventDetails.eventDatetime" name="eventDetails.eventDatetime" type="datetime"
                                          id="eventDetails.eventDatetime" cssClass="form-control" placeholder="" autocomplete="off"
                                    />
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-8">
                            <wg:label _for="eventDetails.eventTypeId">Event Type*</wg:label>
                            <wgForm:select path="eventDetails.eventTypeId" name="eventDetails.eventTypeId"
                                           id="eventDetails.eventTypeId"
                                           cssClass="form-control">
                                <c:forEach var="item" items="${eventTypes}">
                                    <wgForm:option value="${item.id}" label="${item.label}"/>
                                </c:forEach>
                            </wgForm:select>
                        </lt:layout>
                        <lt:layout cssClass="col-md-6">
                            <label>
                                <wgForm:checkbox path="eventDetails.emergencyVisit" name="eventDetails.emergencyVisit"
                                                 id="eventDetails.emergencyVisit"
                                        />
                                Emergency Department Visit
                            </label>
                        </lt:layout>
                        <lt:layout cssClass="col-md-6">
                            <label>
                                <wgForm:checkbox path="eventDetails.overnightPatient"
                                                 name="eventDetails.overnightPatient"
                                                 id="eventDetails.overnightPatient"/>
                                Overnight In-patient</label>
                        </lt:layout>

                    </div>

                    <div class="eventSection col-md-12">

                        <span class="sectionHead col-md-12">Event Description</span>

                        <lt:layout cssClass="form-group col-md-12">
                            <wg:label _for="eventDetails.location">Location</wg:label>
                            <wgForm:input path="eventDetails.location" name="eventDetails.location"
                                          id="eventDetails.location"
                                          cssClass="form-control" maxlength="500"
                                    />
                        </lt:layout>

                        <lt:layout cssClass="form-group col-md-12">
                            <wg:label _for="eventDetails.situation">Situation</wg:label>
                            <wgForm:textarea path="eventDetails.situation" name="eventDetails.situation"
                                             id="eventDetails.situation"
                                             cssClass="form-control" />
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-12">
                            <wg:label _for="eventDetails.background">Background</wg:label>
                            <wgForm:textarea path="eventDetails.background" name="eventDetails.background"
                                             id="eventDetails.background"
                                             cssClass="form-control"/>
                        </lt:layout>
                        <lt:layout cssClass="form-group col-md-12">
                            <wg:label _for="eventDetails.assessment">Assessment</wg:label>
                            <wgForm:textarea path="eventDetails.assessment" name="eventDetails.assessment"
                                             id="eventDetails.assessment"
                                             cssClass="form-control"/>
                        </lt:layout>

                        <lt:layout cssClass="col-md-12">
                            <label>
                                <wgForm:checkbox path="eventDetails.injury" name="eventDetails.injury"
                                                 id="eventDetails.injury"/>
                                Injury
                            </label>
                        </lt:layout>

                        <lt:layout cssClass="col-md-12">
                            <label>
                                <wgForm:checkbox path="eventDetails.followUpExpected"
                                                 name="eventDetails.followUpExpected"
                                                 id="eventDetails.followUpExpected"
                                        />
                                Follow Up Expected
                            </label>
                        </lt:layout>

                        <lt:layout cssClass="form-group col-md-12" id="followUpDetailsContent" style="display:none">
                            <wg:label _for="eventDetails.followUpDetails">Follow Up Details*</wg:label>
                            <wgForm:textarea path="eventDetails.followUpDetails" name="eventDetails.followUpDetails"
                                             id="eventDetails.followUpDetails"
                                             cssClass="form-control"/>
                        </lt:layout>


                    </div>


                    <div class="eventSection col-md-12">
                        <span class="sectionHead col-md-12">Treatment Details</span>
                            <%-- =====================================================================================--%>
                            <%-- Treating Physician --%>
                            <%-- =====================================================================================--%>
                        <lt:layout cssClass="col-md-12">
                            <label>
                                <wgForm:checkbox path="includeTreatingPhysician" name="includeTreatingPhysician"
                                                 id="includeTreatingPhysician"/>
                                Include Details of Treating Physician
                            </label>
                        </lt:layout>

                        <lt:layout id="includeTreatingPhysicianContent" style="display:none">
                            <lt:layout cssClass="form-group col-md-6">
                                <wg:label _for="treatingPhysician.firstName">First Name*</wg:label>
                                <wgForm:input path="treatingPhysician.firstName" name="treatingPhysician.firstName"
                                              id="treatingPhysician.firstName" placeholder="${namePlaceholder}"
                                              cssClass="form-control"/>
                            </lt:layout>
                            <lt:layout cssClass="form-group col-md-6">
                                <wg:label _for="treatingPhysician.lastName">Last Name*</wg:label>
                                <wgForm:input path="treatingPhysician.lastName" name="treatingPhysician.lastName"
                                              id="treatingPhysician.lastName" placeholder="${namePlaceholder}"
                                              cssClass="form-control"
                                        />
                            </lt:layout>
                            <lt:layout cssClass="col-md-12">
                                <label>
                                    <wgForm:checkbox path="treatingPhysician.includeAddress"
                                                     name="treatingPhysician.includeAddress"
                                                     id="treatingPhysician.includeAddress"/>
                                    Physician Address
                                </label>

                            </lt:layout>
                            <lt:layout id="includeTreatingPhysicianAddressContent" style="display:none">
                                <lt:layout cssClass="form-group col-md-6">
                                    <wg:label _for="treatingPhysician.address.street">Street*</wg:label>
                                    <wgForm:input path="treatingPhysician.address.street"
                                                  name="treatingPhysician.address.street"
                                                  id="treatingPhysician.address.street"
                                                  cssClass="form-control"
                                            />
                                </lt:layout>
                                <lt:layout cssClass="form-group col-md-6">
                                    <wg:label _for="treatingPhysician.address.city">City*</wg:label>
                                    <wgForm:input path="treatingPhysician.address.city"
                                                  name="treatingPhysician.address.city"
                                                  id="treatingPhysician.address.city"
                                                  cssClass="form-control"
                                            />
                                </lt:layout>
                                <lt:layout cssClass="form-group col-md-6">
                                    <wg:label _for="treatingPhysician.address.state">State*</wg:label>
                                    <wgForm:select path="treatingPhysician.address.state.id"
                                                   name="treatingPhysician.address.state"
                                                   id="treatingPhysician.address.state" cssClass="form-control">
                                        <wgForm:option value="" label="-- Select State --"/>
                                        <c:forEach var="item" items="${states}">
                                            <wgForm:option value="${item.id}" label="${item.label}"/>
                                        </c:forEach>
                                    </wgForm:select>
                                </lt:layout>
                                <lt:layout cssClass="form-group col-md-6">
                                    <wg:label _for="treatingPhysician.address.zip">Zip Code*</wg:label>
                                    <wgForm:input path="treatingPhysician.address.zip"
                                                  name="treatingPhysician.address.zip"
                                                  id="treatingPhysician.address.zip"
                                                  cssClass="form-control"
                                            />
                                </lt:layout>
                            </lt:layout>
                            <lt:layout cssClass="form-group col-md-12">
                                <wg:label _for="treatingPhysician.phone">Physician Phone</wg:label>
                                <wgForm:input path="treatingPhysician.phone" name="treatingPhysician.phone"
                                              id="treatingPhysician.phone"
                                              cssClass="form-control"
                                              maxlength="16"
                                        />
                            </lt:layout>

                        </lt:layout>
                            <%--=====================================================================================--%>
                            <%-- Hospital --%>
                            <%--=====================================================================================--%>
                        <lt:layout cssClass="col-md-12">
                            <label>
                                <wgForm:checkbox path="includeHospital" name="includeHospital" id="includeHospital"/>
                                Include Details of Treating Hospital
                            </label>
                        </lt:layout>

                        <lt:layout id="includeHospitalContent" style="display:none">
                            <lt:layout cssClass="form-group col-md-12">
                                <wg:label _for="treatingHospital.name">Hospital / Clinic*</wg:label>
                                <wgForm:input path="treatingHospital.name" name="treatingHospital.name"
                                              id="treatingHospital.name" placeholder="${namePlaceholder}"
                                              cssClass="form-control"/>
                            </lt:layout>

                            <lt:layout cssClass="col-md-12">
                                <label>
                                    <wgForm:checkbox path="treatingHospital.includeAddress"
                                                     name="treatingHospital.includeAddress"
                                                     id="treatingHospital.includeAddress"/>
                                    Hospital Clinic Address
                                </label>

                            </lt:layout>
                            <lt:layout id="includeHospitalAddressContent" style="display:none">
                                <lt:layout cssClass="form-group col-md-6">
                                    <wg:label _for="treatingHospital.address.street">Street*</wg:label>
                                    <wgForm:input path="treatingHospital.address.street"
                                                  name="treatingHospital.address.street"
                                                  id="treatingHospital.address.street"
                                                  cssClass="form-control"
                                            />
                                </lt:layout>
                                <lt:layout cssClass="form-group col-md-6">
                                    <wg:label _for="treatingHospital.address.city">City*</wg:label>
                                    <wgForm:input path="treatingHospital.address.city"
                                                  name="treatingHospital.address.city"
                                                  id="treatingHospital.address.city"
                                                  cssClass="form-control"
                                            />
                                </lt:layout>
                                <lt:layout cssClass="form-group col-md-6">
                                    <wg:label _for="treatingHospital.address.state">State*</wg:label>
                                    <wgForm:select path="treatingHospital.address.state.id"
                                                   name="treatingHospital.address.state"
                                                   id="treatingHospital.address.state" cssClass="form-control"
                                            >
                                        <wgForm:option value="" label="-- Select State --"/>
                                        <c:forEach var="item" items="${states}">
                                            <wgForm:option value="${item.id}" label="${item.label}"/>
                                        </c:forEach>

                                    </wgForm:select>
                                </lt:layout>
                                <lt:layout cssClass="form-group col-md-6">
                                    <wg:label _for="treatingHospital.address.zip">Zip Code*</wg:label>
                                    <wgForm:input path="treatingHospital.address.zip"
                                                  name="treatingHospital.address.zip"
                                                  id="treatingHospital.address.zip"
                                                  cssClass="form-control"
                                            />
                                </lt:layout>
                            </lt:layout>
                            <lt:layout cssClass="form-group col-md-12">
                                <wg:label _for="treatingHospital.phone">Hospital Phone</wg:label>
                                <wgForm:input path="treatingHospital.phone" name="treatingHospital.phone"
                                              id="treatingHospital.phone"
                                              cssClass="form-control"
                                              maxlength="16"
                                        />
                            </lt:layout>

                        </lt:layout>


                    </div>


                    <div class="eventSection col-md-12">

                        <span class="sectionHead col-md-12">Details of Responsible Manager</span>

                        <div class="col-md-12">
                            <label>
                                <wgForm:checkbox path="includeManager" id="includeManager" value="false"/>
                                Include Details of Responsible Manager
                            </label>
                        </div>
                        <lt:layout id="includeManagerContent" style="display:none">
                            <lt:layout cssClass="form-group col-md-6">
                                <wg:label _for="manager.firstName">First Name*</wg:label>
                                <wgForm:input path="manager.firstName" name="manager.firstName" id="manager.firstName"
                                              cssClass="form-control" placeholder="${namePlaceholder}"/>
                            </lt:layout>
                            <lt:layout cssClass="form-group col-md-6">
                                <wg:label _for="manager.lastName">Last Name*</wg:label>
                                <wgForm:input path="manager.lastName" name="manager.lastName" id="manager.lastName"
                                              cssClass="form-control"
                                              placeholder="${namePlaceholder}"
                                        />
                            </lt:layout>

                            <lt:layout cssClass="form-group col-md-6">
                                <wg:label _for="manager.email">Email</wg:label>
                                <wgForm:input path="manager.email" name="manager.email" id="manager.email"
                                              cssClass="form-control"
                                              placeholder="${emailPlaceholder}"
                                        />
                            </lt:layout>
                            <lt:layout cssClass="form-group col-md-6">
                                <wg:label _for="manager.phone">Phone</wg:label>
                                <wgForm:input path="manager.phone" name="manager.phone" id="manager.phone"
                                              cssClass="form-control"
                                              maxlength="16"
                                        />
                            </lt:layout>
                        </lt:layout>
                    </div>

                    <div class="eventSection col-md-12">
                        <span class="sectionHead col-md-12">Details of Registered Nurse (RN)</span>

                        <div class="col-md-12">
                            <label>
                                <wgForm:checkbox path="includeResponsible" id="includeResponsible" value="false"/>
                                Include Details of Registered Nurse (RN)
                            </label>

                        </div>
                            <%-- Details of Responsible Person --%>
                            <%--=====================================================================================--%>

                        <lt:layout id="includeResponsibleContent" style="display:none">
                            <lt:layout cssClass="form-group col-md-6">
                                <wg:label _for="responsible.firstName">First Name*</wg:label>
                                <wgForm:input path="responsible.firstName" name="responsible.firstName"
                                              id="responsible.firstName"
                                              cssClass="form-control" placeholder="${namePlaceholder}"/>
                            </lt:layout>
                            <lt:layout cssClass="form-group col-md-6">
                                <wg:label _for="responsible.lastName">Last Name*</wg:label>
                                <wgForm:input path="responsible.lastName" name="responsible.lastName"
                                              id="responsible.lastName"
                                              cssClass="form-control" placeholder="${namePlaceholder}"
                                        />
                            </lt:layout>

                            <lt:layout cssClass="col-md-12">
                                <label>
                                    <wgForm:checkbox path="responsible.includeAddress" name="responsible.includeAddress"
                                                     id="responsible.includeAddress"/>
                                    Address
                                </label>
                            </lt:layout>

                            <lt:layout id="includeResponsibleAddressContent" style="display:none">
                                <lt:layout cssClass="form-group col-md-6">
                                    <wg:label _for="responsible.address.street">Street*</wg:label>
                                    <wgForm:input path="responsible.address.street" name="responsible.address.street"
                                                  id="responsible.address.street"
                                                  cssClass="form-control" placeholder="${namePlaceholder}"
                                            />
                                </lt:layout>
                                <lt:layout cssClass="form-group col-md-6">
                                    <wg:label _for="responsible.address.city">City*</wg:label>
                                    <wgForm:input path="responsible.address.city" name="responsible.address.city"
                                                  id="responsible.address.city"
                                                  cssClass="form-control" placeholder="${namePlaceholder}"
                                            />
                                </lt:layout>
                                <lt:layout cssClass="form-group col-md-6">
                                    <wg:label _for="responsible.address.state">State*</wg:label>
                                    <wgForm:select path="responsible.address.state.id" name="responsible.address.state"
                                                   id="responsible.address.state" cssClass="form-control"
                                            >
                                        <wgForm:option value="" label="-- Select State --"/>
                                        <c:forEach var="item" items="${states}">
                                            <wgForm:option value="${item.id}" label="${item.label}"/>
                                        </c:forEach>

                                    </wgForm:select>
                                </lt:layout>
                                <lt:layout cssClass="form-group col-md-6">
                                    <wg:label _for="responsible.address.zip">Zip Code*</wg:label>
                                    <wgForm:input path="responsible.address.zip" name="responsible.address.zip"
                                                  id="responsible.address.zip"
                                                  cssClass="form-control" placeholder="${zipPlaceholder}"
                                            />
                                </lt:layout>
                            </lt:layout>
                        </lt:layout>
                    </div>
                </div>
                <%--</wg:modal-body>--%>

                <wg:modal-footer-btn-group>

                    <wg:button name="cancelBtn"
                               domType="link"
                               dataToggle="modal"
                               dataTarget="#createNewEventModal"
                               cssClass="btn-default cancelBtn">
                        CANCEL
                    </wg:button>
                    <wg:button domType="link" cssClass="btn-primary submitBtn" name="submit" id="createNewEventBtn">
                        SUBMIT
                    </wg:button>

                </wg:modal-footer-btn-group>
            </wgForm:form>

        </div>
    </div>
</div>
