<%@ page contentType="text/html;charset=UTF-8" language="java" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="patient.search.info" var="patientSearchInfo"/>
<spring:message code="patient.search.field.searchScope" var="searchScope"/>
<spring:message code="patient.search.field.firstName" var="firstName"/>
<spring:message code="patient.search.field.lastName" var="lastName"/>
<spring:message code="patient.search.field.gender" var="gender"/>
<spring:message code="patient.search.field.dateOfBirth" var="dateOfBirth"/>
<spring:message code="patient.search.field.dateOfBirth.format" var="dateFormat"/>
<spring:message code="patient.search.field.middleName" var="middleName"/>
<spring:message code="patient.search.field.city" var="city"/>
<spring:message code="patient.search.field.socialSecurityNumber" var="socialSecurityNumber"/>
<spring:message code="patient.search.field.middleName" var="middleName"/>
<spring:message code="patient.search.field.street" var="street"/>
<spring:message code="patient.search.field.state" var="state"/>
<spring:message code="patient.search.field.postalCode" var="postalCode"/>
<spring:message code="patient.search.field.phone" var="phone"/>
<spring:message code="button.clear" var="clear"/>
<spring:message code="button.search" var="search"/>
<spring:message code="patient.search.panel.header.label" var="patientSearchPanelHeaderLabel"/>
<spring:message code="patient.search.placeholder.socialSecurityNumber" var="ssnPlaceholder"/>
<spring:message code="invalid.hash.key.err.message.1" var="hashKeyErrorLine1"/>
<spring:message code="invalid.hash.key.err.message.2" var="hashKeyErrorLine2"/>

<c:url value="/employee" var="loggedInEmployeeUrl"/>
<wg:link id="loggedInEmployeeUrl" href="${loggedInEmployeeUrl}" cssClass="hidden"/>

<lt:layout cssClass="patientFilterBox">

    <lt:layout cssClass="boxHeader panel panel-primary">
        <lt:layout cssClass="panel-heading">
            ${patientSearchPanelHeaderLabel}
        </lt:layout>
    </lt:layout>

    <lt:layout cssClass="boxBody">

        <c:if test="${param.hashKey eq 'error'}">
            <lt:layout id="hashKeyError" cssClass="alert alert-warning">
                <wg:b>${hashKeyErrorLine1}</wg:b>
                <br>
                ${hashKeyErrorLine2}
            </lt:layout>
        </c:if>

        <wgForm:form commandName="patientFilter" method="post" action="patients"
                     cssClass="patientFilterForm form-horizontal" id="patientFilterForm">

            <wgForm:input path="ssnRequired" name="ssnRequired" id="ssnRequired" cssClass="hidden"/>
            <wgForm:input path="dateOfBirthRequired" name="dateOfBirthRequired" id="dateOfBirthRequired" cssClass="hidden"/>

            <lt:layout cssClass="form-group">
                <wg:label _for="searchScope" cssClass="col-md-12 control-label label-min-width block-header-label">
                    ${patientSearchInfo}
                </wg:label>
            </lt:layout>

            <lt:layout cssClass="form-group">
                <wg:label _for="searchScope" cssClass="col-md-5 control-label label-min-width">
                    ${searchScope}<span class="mandatory-asterisk">*</span>:
                </wg:label>
                <lt:layout cssClass="col-md-7">
                    <c:forEach var="item" items="${searchScopeValues}">
                        <lt:layout cssClass="checkbox">
                                <spring:message code="patient.search.radio.scope.${fn:toLowerCase(item)}" var="scopeLabel"/>
                                <wgForm:checkbox path="searchScopes" value="${item}" label="${scopeLabel}"/>
                        </lt:layout>
                    </c:forEach>
                </lt:layout>
            </lt:layout>

            <%--<wgForm:input path="showMergedPatients" name="showMergedPatients" id="showMergedPatients" cssClass="hidden"/>--%>

            <lt:layout cssClass="form-group">
                <wg:label _for="firstName" cssClass="col-md-5 control-label label-min-width">
                    ${firstName}<span class="mandatory-asterisk">*</span>:
                </wg:label>
                <lt:layout cssClass="col-md-7">
                    <wgForm:input path="firstName" name="firstName" id="firstName" type="text" cssClass="form-control"/>
                </lt:layout>
            </lt:layout>

            <lt:layout cssClass="form-group">
                <wg:label _for="lastName" cssClass="col-md-5 control-label label-min-width">
                    ${lastName}<span class="mandatory-asterisk">*</span>
                </wg:label>
                <lt:layout cssClass="col-md-7">
                    <wgForm:input path="lastName" name="lastName" id="lastName" type="text" cssClass="form-control"/>
                </lt:layout>
            </lt:layout>

            <lt:layout cssClass="form-group">
                <wg:label _for="gender" cssClass="col-md-5 control-label">
                    ${gender}<span class="mandatory-asterisk">*</span>:
                </wg:label>
                <lt:layout cssClass="col-md-7">
                    <c:forEach var="item" items="${genderValues}">
                        <lt:layout cssClass="radio">
                                <spring:message code="patient.search.radio.gender.${fn:toLowerCase(item)}" var="genderLabel"/>
                                <wgForm:radiobutton path="gender" name="gender" value="${item}" label="${genderLabel}"/>
                        </lt:layout>
                    </c:forEach>
                </lt:layout>
            </lt:layout>

            <lt:layout cssClass="form-group">
                <wg:label _for="dateOfBirth" cssClass="col-md-5 control-label label-min-width">
                    ${dateOfBirth}<span class="mandatory-asterisk">*</span>:
                </wg:label>
                <lt:layout cssClass="col-md-7 date">
                    <wgForm:input path="dateOfBirth" name="dateOfBirth" id="dateOfBirth" type="text" placeholder="${dateFormat}"
                                  cssClass="form-control datepicker" data-provide="datepicker"/>
                    <wg:icon cssClass="glyphicon-calendar"/>
                </lt:layout>
            </lt:layout>

            <lt:layout cssClass="form-group">
                <wg:label _for="socialSecurityNumber" cssClass="col-md-5 control-label label-min-width">
                    ${socialSecurityNumber}:
                </wg:label>
                <lt:layout cssClass="col-md-7">
                    <lt:layout cssClass="ssn-container">
                        <lt:layout cssClass="ssn-xxx">
                            <input id="ssn1" name="ssn1" class="form-control" maxlength="3" placeholder="XXX"/>
                        </lt:layout>
                        <lt:layout cssClass="ssn-xx">
                            <input id="ssn2" name="ssn2" class="form-control" maxlength="2" placeholder="XX"/>
                        </lt:layout>
                        <lt:layout cssClass="ssn-xxxx">
                            <span class="mandatory-asterisk">*</span>
                            <wgForm:input path="lastFourDigitsOfSsn" id="lastFourDigitsOfSsn" cssClass="form-control" maxlength="4" placeholder="XXXX"/>
                        </lt:layout>
                    </lt:layout>
                </lt:layout>
                <lt:layout cssClass="ssn-help-msg col-md-12">
                    <h6><em>Simply Connect system search requires last 4 digits SSN<br>
                        NwHIN search requires 9-digits SSN (optional)</em></h6>
                </lt:layout>
            </lt:layout>

            <wg:collapsed-panel id="moreDetails" expanded = "true" clpHeaderText="More Details" expHeaderText="Less Details">
                <lt:layout cssClass="form-group">
                    <wg:label _for="middleName" cssClass="col-md-5 control-label label-min-width">
                        ${middleName}:
                    </wg:label>
                    <lt:layout cssClass="col-md-7">
                        <wgForm:input path="middleName" name="middleName" id="middleName" type="text"
                                      cssClass="form-control"/>
                    </lt:layout>
                </lt:layout>

                <lt:layout cssClass="form-group">
                    <wg:label _for="street" cssClass="col-md-5 control-label label-min-width">
                        ${street}<span class="mandatory-asterisk">*</span>:
                    </wg:label>
                    <lt:layout cssClass="col-md-7">
                        <wgForm:input path="street" name="street" id="street" type="text"
                                      cssClass="form-control"/>
                    </lt:layout>
                </lt:layout>

                <lt:layout cssClass="form-group">
                    <wg:label _for="city" cssClass="col-md-5 control-label label-min-width">
                        ${city}<span class="mandatory-asterisk">*</span>:
                    </wg:label>
                    <lt:layout cssClass="col-md-7">
                        <wgForm:input path="city" name="city" id="city" type="text"
                                      cssClass="form-control"/>
                    </lt:layout>
                </lt:layout>

                <lt:layout cssClass="form-group">
                    <wg:label _for="state" cssClass="col-md-5 control-label label-min-width">
                        ${state}<span class="mandatory-asterisk">*</span>:
                    </wg:label>
                    <lt:layout cssClass="col-md-4">
                        <wgForm:input path="state" name="state" id="state" type="text"
                                      cssClass="form-control"/>
                    </lt:layout>
                </lt:layout>

                <lt:layout cssClass="form-group">
                    <wg:label _for="postalCode" cssClass="col-md-5 control-label label-min-width">
                        ${postalCode}<span class="mandatory-asterisk">*</span>:
                    </wg:label>
                    <lt:layout cssClass="col-md-7">
                        <wgForm:input path="postalCode" name="postalCode" id="postalCode" type="text"
                                      cssClass="form-control"/>
                    </lt:layout>
                </lt:layout>

                <lt:layout cssClass="form-group">
                    <wg:label _for="phone" cssClass="col-md-5 control-label label-min-width">
                        ${phone}:
                    </wg:label>
                    <lt:layout cssClass="col-md-7">
                        <wgForm:input path="phone" name="phone" id="phone" type="text"
                                      cssClass="form-control"/>
                    </lt:layout>
                </lt:layout>

            </wg:collapsed-panel>

            <lt:layout cssClass="well searchPatientPanelBtnBox">
                <wg:button domType="link" cssClass="btn-lg btn-default clearBtn" name="clear" id="clear">
                    ${clear}
                </wg:button>

                <wg:button domType="button" type="submit" cssClass="pull-right btn-lg btn-primary searchBtn" name="search"
                           id="search">
                    ${search}
                </wg:button>
            </lt:layout>
        </wgForm:form>
    </lt:layout>
</lt:layout>
