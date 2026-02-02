<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="viewMode" value="${mode eq 'view'}"/>
<c:set var="editMode" value="${mode eq 'edit'}"/>

<div class="modal fade" role="dialog" id="problemsCcdModal" data-backdrop="static">
    <div class="modal-dialog" role="document" style="width:1000px;">
        <div class="modal-content">
            <wgForm:form cssClass="col-md-12 problemsCcdForm no-horizontal-padding" method="post" commandName="problem"
                         id="problemsCcdForm"
                         cssStyle="background-color: white; ">
                <wg:modal-header closeBtn="true">
                    <span id="careTeamMemberHeader">${modalTitle}</span>
                </wg:modal-header>
                <wg:modal-body cssClass="col-md-12 ccdModalBody whiteBackground">
                    <wgForm:hidden path="id"/>

                    <%--Problem details--%>
                    <div class="modal-section col-md-12">
                        <span class="sectionHead col-sm-12">Problem Details</span>

                        <lt:layout cssClass="form-group col-md-12">
                            <wg:label _for="problem.value.id">Problem*</wg:label>
                            <lt:layout cssClass="life-search-dropdown col-md-12 no-horizontal-padding">
                                <wg:dropdown cssClass="no-horizontal-padding life-search-dropdown">
                                    <wg:life-search-dropdown-head id="problem.value.id"
                                                                  path="value.id"
                                                                  name="value.id"
                                                                  cssClass="form-control"
                                                                  placeholder="Search by problem name"
                                                                  options="${problemValue}"
                                                                  autocomplete="off"
                                                                  disabled="${viewMode}"
                                    />
                                    <wg:dropdown-body forHead="problem.value.id"
                                                      cssClass="problemValueDropdownBody">
                                        <lt:layout cssClass="col-sm-12">
                                            <wg:grid id="problemValueDropdown"
                                                     cssClass=""
                                                     colIds="code,codeSystemName,displayName"
                                                     colNames="Code,Code Set,Description"
                                                     colFormats="string,string,string"
                                                     dataUrl="patient-info/${residentId}/ccd/problems/problemValue?hashKey=${hashKey}"
                                                     dataRequestMethod="GET"
                                                     deferLoading="true"
                                            />
                                        </lt:layout>
                                    </wg:dropdown-body>
                                </wg:dropdown>
                            </lt:layout>
                        </lt:layout>


                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="">Primary*</wg:label>
                            <lt:layout cssClass="col-md-12 no-horizontal-padding radio-box">
                                <wg:label cssClass="col-md-2 no-horizontal-padding">
                                    <wgForm:radiobutton path="primary" value="true" name="primary"
                                                        disabled="${viewMode}"/> Yes
                                </wg:label>
                                <wg:label cssClass="col-md-2 no-horizontal-padding">
                                    <wgForm:radiobutton path="primary" value="false" name="primary"
                                                        disabled="${viewMode}"/> No
                                </wg:label>
                                <wg:label cssClass="col-md-8 no-horizontal-padding"/>
                            </lt:layout>
                        </lt:layout>

                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="problem.type.id">Problem Type*</wg:label>
                            <wgForm:select path="type.id"
                                           id="problem.type.id" cssClass="form-control" name="type.id"
                                           disabled="${viewMode}">
                                <wgForm:option value="" label="-- Select --"/>
                                <c:forEach var="item" items="${problemTypes}">
                                    <wgForm:option value="${item.id}" label="${item.displayName}"/>
                                </c:forEach>
                            </wgForm:select>
                        </lt:layout>

                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="problem.status.id">Status*</wg:label>
                            <wgForm:select path="status.id" name="status.id"
                                           id="problem.status.id" cssClass="form-control"
                                           disabled="${viewMode}">
                                <wgForm:option value="" label="-- Select --"/>
                                <c:forEach var="item" items="${problemStatuses}">
                                    <wgForm:option value="${item.id}" label="${item.displayName}"
                                                   data-code="${item.code}"/>
                                </c:forEach>
                            </wgForm:select>
                        </lt:layout>

                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="problem.endDate">Date Resolved*</wg:label>
                            <lt:layout cssClass="no-horizontal-padding date col-md-12">
                                <wgForm:input path="endDate" name="endDate" id="problem.endDate"
                                              type="datetime"
                                              cssClass="form-control"
                                              placeholder=""
                                              autocomplete="off"
                                              disabled="true"/>
                                <wg:icon cssClass="glyphicon-calendar"/>
                            </lt:layout>
                            <div style="clear:both;"/>
                        </lt:layout>

                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="problem.startDate">Date Identified</wg:label>
                            <lt:layout cssClass="no-horizontal-padding date col-md-12">
                                <wgForm:input path="startDate" name="startDate" id="problem.startDate"
                                              type="datetime"
                                              cssClass="form-control"
                                              placeholder=""
                                              autocomplete="off"
                                              disabled="${viewMode}"/>
                                <wg:icon cssClass="glyphicon-calendar"/>
                            </lt:layout>
                            <div style="clear:both;"/>
                        </lt:layout>

                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="problem.onSetDate">Onset Date</wg:label>
                            <lt:layout cssClass="no-horizontal-padding date col-md-12">
                                <wgForm:input path="onSetDate" name="onSetDate" id="problem.onSetDate"
                                              type="datetime"
                                              cssClass="form-control"
                                              placeholder=""
                                              autocomplete="off"
                                              disabled="${viewMode}"/>
                                <wg:icon cssClass="glyphicon-calendar"/>
                            </lt:layout>
                            <div style="clear:both;"/>
                        </lt:layout>

                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="problem.recordedDate">Date Recorded*</wg:label>
                            <lt:layout cssClass="no-horizontal-padding date col-md-12">
                                <wgForm:input path="recordedDate" name="recordedDate" id="problem.recordedDate"
                                              type="datetime"
                                              cssClass="form-control"
                                              placeholder=""
                                              autocomplete="off"
                                              disabled="${viewMode or editMode}"/>
                                <wg:icon cssClass="glyphicon-calendar"/>
                            </lt:layout>
                            <div style="clear:both;"/>
                        </lt:layout>

                        <lt:layout cssClass="col-md-6 form-group">
                            <wg:label _for="problem.recordedBy">Recorded by</wg:label>
                            <wgForm:input path="recordedBy"
                                          id="problem.recordedBy"
                                          cssClass="form-control"
                                          disabled="true"
                            />
                        </lt:layout>

                        <c:if test="${mode eq 'view'}">
                            <lt:layout cssClass="col-md-6 form-group">
                                <wg:label _for="problem.healthStatusObservation">Health Status Observation</wg:label>
                                <wgForm:input path="healthStatusObservation"
                                              id="problem.healthStatusObservation"
                                              cssClass="form-control"
                                              disabled="true"/>
                            </lt:layout>

                            <lt:layout cssClass="col-md-6 form-group">
                                <wg:label _for="problem.dataSource">Data Source</wg:label>
                                <wgForm:input path="dataSource"
                                              id="problem.dataSource"
                                              cssClass="form-control"
                                              disabled="true"/>
                            </lt:layout>
                        </c:if>

                        <lt:layout cssClass="form-group col-md-12">
                            <wg:label _for="problem.comments">Comments</wg:label>
                            <wgForm:textarea path="comments" name="comments"
                                             id="problem.comments"
                                             cssClass="form-control"
                                             disabled="${viewMode}"/>
                        </lt:layout>
                    </div>

                    <div id="diagnosisInformationWrapper"
                        <%--style="display: none"--%>
                    >
                        <div class="col-md-12">
                            <div class="sectionLine"></div>
                        </div>

                            <%--Diagnosis Information--%>
                        <div class="modal-section ccdModalSection col-md-12">
                            <span class="sectionHead col-sm-12">Diagnosis Information</span>

                            <wg:grid id="problemDiagnosisInformation" cssClass="problemDiagnosisInformation col-sm-12"
                                     colIds="code,codeSystemName,displayName"
                                     colNames="Code,Code Set,Description"
                                     colFormats="string,string,string"
                                     dataUrl="patient-info/${residentId}/ccd/problems/diagnosisInfo?hashKey=${hashKey}"
                                     dataRequestMethod="GET"
                                     deferLoading="true"
                            />
                        </div>
                    </div>
                </wg:modal-body>

                <wg:modal-footer-btn-group cssClass="col-md-12">
                    <c:choose>
                        <c:when test="${viewMode}">
                            <wg:button name="cancelBtn"
                                       domType="link"
                                       dataToggle="modal"
                                       dataTarget="#noteModal"
                                       cssClass="btn-primary cancelBtn">
                                CLOSE
                            </wg:button>
                        </c:when>
                        <c:otherwise>
                            <wg:button name="cancelBtn"
                                       domType="link"
                                       dataToggle="modal"
                                       dataTarget="#noteModal"
                                       cssClass="btn-default cancelBtn">
                                CANCEL
                            </wg:button>
                            <wg:button domType="link" cssClass="btn-primary submitBtn" name="submit" id="submitNoteBtn">
                                ${buttonTitle}
                            </wg:button>
                        </c:otherwise>
                    </c:choose>
                </wg:modal-footer-btn-group>
            </wgForm:form>

        </div>
    </div>
</div>
