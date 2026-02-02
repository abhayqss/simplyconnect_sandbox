<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<div class="modal fade" role="dialog" id="noteModal" data-backdrop="static">
    <div class="modal-dialog" role="document" style="width:1000px;">
        <div class="modal-content">
            <wgForm:form cssClass="col-md-12 newNoteForm no-horizontal-padding" method="post" commandName="noteDto"
                         id="noteForm"
                         cssStyle="background-color: white; ">
                <wg:modal-header closeBtn="true">
                    <span id="careTeamMemberHeader">${modalTitle}</span>
                </wg:modal-header>
                <wg:modal-body cssClass="col-md-12 no-border whiteBackground">
                <%--<div class="">--%>
                    <lt:layout cssClass="form-group col-md-6">
                        <wg:label _for="personSubmittingNote">Person Submitting Note*</wg:label>
                        <wgForm:input path="personSubmittingNote" name="personSubmittingNote" id="personSubmittingNote"
                                      cssClass="form-control" disabled="true"/>
                    </lt:layout>

                    <lt:layout cssClass="form-group col-md-6">
                        <wg:label _for="lastModifiedDate">Date and Time*</wg:label>
                        <wgForm:input path="lastModifiedDate" name="lastModifiedDate" type="datetime" autocomplete="off"
                                      id="lastModifiedDate" cssClass="form-control" placeholder="" disabled="${not dateModificationAllowed}"
                        />
                    </lt:layout>

                    <lt:layout cssClass="form-group col-md-6">
                        <wg:label _for="subType.id">Note Type*</wg:label>
                        <wgForm:select path="subType.id" name="subType.id"
                                       id="subTypeId"
                                       cssClass="form-control"
                                       disabled="${disableSubType or readOnly}">
                            <c:forEach var="item" items="${noteSubTypes}">
                                <wgForm:option value="${item.id}" label="${item.label}" data-encounter-code="${item.encounterCode eq null ? '' : item.encounterCode}"  data-follow-up-code="${item.followUpCode eq null ? '' : item.followUpCode}"/>
                            </c:forEach>
                        </wgForm:select>
                    </lt:layout>
                    <c:if test="${not (readOnly or disableSubType) or noteDto.subType.followUpCode ne null}">
                        <lt:layout cssClass="form-group col-md-6">
                            <wg:label _for="noteResidentAdmittanceHistoryDto.id">Admit Date</wg:label>
                            <wgForm:select path="noteResidentAdmittanceHistoryDto.id" name="noteResidentAdmittanceHistoryDto.id"
                                           id="noteResidentAdmittanceHistoryDtoId"
                                           cssClass="form-control" disabled="${disableAdmit or readOnly}">
                                <wgForm:option value="${null}" label="-- Select --"/>
                                <c:forEach var="item" items="${admitDates}">
                                    <fmt:formatDate type="BOTH" pattern='MM/dd/yyyy hh:mm a (z)' value='${item.admitDate}' var="admitDate"/>
                                    <wgForm:option value="${item.id}" label="${admitDate}"/>
                                </c:forEach>
                            </wgForm:select>
                        </lt:layout>
                    </c:if>
                    
    <wgForm:hidden path="timeZoneOffset" id="timeZoneOffset"/>

    <div id="encounter-note-type-content" style=${not empty noteDto.encouterNoteTypeId ? 'display:block' : 'display:none'}>
                     <lt:layout cssClass="form-group col-md-6">
                        <wg:label _for="subType.id">Encounter Type*</wg:label>
                        <wgForm:select path="encouterNoteTypeId" name="encouterNoteTypeId"
                                       id="encouterNoteTypeId"
                                       cssClass="form-control">
                            <wgForm:option value="${null}" label="-- Select --"/>
                            <c:forEach var="item" items="${encounterNoteTypes}">
                                <wgForm:option value="${item.id}" label="${item.label}" />
                            </c:forEach>
                        </wgForm:select>
                    </lt:layout>
                    
                    <lt:layout cssClass="form-group col-md-6">
                        <wg:label _for="clinicianCompletingEncounter">Person Completing the Encounter</wg:label>
                        <wgForm:input path="clinicianCompletingEncounter" name="clinicianCompletingEncounter" autocomplete="off"
                                      id="clinicianCompletingEncounter" cssClass="form-control" placeholder="" 
                        />
                    </lt:layout>

                    <lt:layout cssClass="form-group col-md-3">
                        <wg:label _for="encounterDate">Encounter Date*</wg:label>
                        <wgForm:input path="encounterDate" name="encounterDate" type="datetime" autocomplete="off"
                                      id="encounterDate" cssClass="form-control" placeholder="" 
                        />
                    </lt:layout>

                    <lt:layout cssClass="form-group col-md-3">
                        <wg:label _for="from">From*</wg:label>
                        <wgForm:input path="from" name="from" autocomplete="off"
                                      id="from" cssClass="form-control" placeholder="" 
                        />
                    </lt:layout>

                    <lt:layout cssClass="form-group col-md-3">
                        <wg:label _for="to">To*</wg:label>
                        <wgForm:input path="to" name="to"  autocomplete="off"
                                      id="to" cssClass="form-control" placeholder="" 
                        />
                    </lt:layout>

                    <lt:layout cssClass="form-group col-md-3">
                        <wg:label _for="totalTimeSpent">Total time spent</wg:label>
                        <wgForm:input path="" name="totalTimeSpent" id="totalTimeSpent"
                                      cssClass="form-control" disabled="true" placeholder=""/>
                    </lt:layout>
                    <lt:layout cssClass="form-group col-md-3">
                        <wg:label _for="">Range</wg:label>
                        <wgForm:input path="" name="range" id="range"
                                      cssClass="form-control" disabled="true" placeholder=""/>
                    </lt:layout>
                    <lt:layout cssClass="form-group col-md-3">
                        <wg:label _for="">Unit</wg:label>
                        <wgForm:input path="" name="unit" id="unit"
                                      cssClass="form-control" disabled="true" placeholder=""/>
                    </lt:layout>
    </div>

                    <lt:layout cssClass="form-group col-md-12">
                        <wg:label _for="subjective">Subjective*</wg:label>
                        <wgForm:textarea path="subjective" name="subjective"
                                         id="subjective"
                                         cssClass="form-control"
                                         disabled="${readOnly}"/>
                    </lt:layout>

                    <lt:layout cssClass="form-group col-md-12">
                        <wg:label _for="objective">Objective</wg:label>
                        <wgForm:textarea path="objective" name="objective"
                                         id="objective"
                                         cssClass="form-control"
                                         disabled="${readOnly}"/>
                    </lt:layout>

                    <lt:layout cssClass="form-group col-md-12">
                        <wg:label _for="assessment">Assessment</wg:label>
                        <wgForm:textarea path="assessment" name="assessment"
                                         id="assessment"
                                         disabled="${readOnly}"
                                         cssClass="form-control"/>
                    </lt:layout>

                    <lt:layout cssClass="form-group col-md-12">
                        <wg:label _for="plan">Plan</wg:label>
                        <wgForm:textarea path="plan" name="plan"
                                         id="plan"
                                         disabled="${readOnly}"
                                         cssClass="form-control"/>
                    </lt:layout>

                <%--</div>--%>
                </wg:modal-body>

                <wg:modal-footer-btn-group>
                    <c:choose>
                        <c:when test="${readOnly}">
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
