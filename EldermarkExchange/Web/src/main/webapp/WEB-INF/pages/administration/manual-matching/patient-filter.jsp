<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<spring:message code="patient.search.field.firstName" var="firstName"/>
<spring:message code="patient.search.field.lastName" var="lastName"/>
<spring:message code="patient.search.field.gender" var="gender"/>
<spring:message code="patient.search.field.dateOfBirth" var="dateOfBirth"/>
<spring:message code="patient.search.field.dateOfBirth.format" var="dateFormat"/>
<spring:message code="patient.search.field.city" var="city"/>
<spring:message code="patient.search.field.socialSecurityNumber" var="socialSecurityNumber"/>
<spring:message code="patient.search.field.street" var="street"/>
<spring:message code="patient.search.field.state" var="state"/>
<spring:message code="patient.search.field.postalCode" var="postalCode"/>
<spring:message code="button.clear" var="clear"/>
<spring:message code="button.search" var="search"/>
<spring:message code="patient.search.table.header.label" var="patientSearchTableHeaderLabel"/>
<spring:message code="patient.search.panel.header.label" var="patientSearchPanelHeaderLabel"/>
<spring:message code="patient.search.placeholder.socialSecurityNumber" var="ssnPlaceholder"/>
<spring:message code="patient.search.radio.mode.matchAll" var="searchModeMatchAll"/>
<spring:message code="patient.search.radio.mode.matchAny" var="searchModeMatchAny"/>


<lt:layout cssClass="patientFilterBox">

    <lt:layout cssClass="boxHeader panel panel-primary">
        <lt:layout cssClass="panel-heading">
            ${patientSearchPanelHeaderLabel}
        </lt:layout>
    </lt:layout>

    <wgForm:form role="form" id="patientsFilterManual" commandName="patientFilterManual" method="post" style="padding: 0 20px 20px 20px">
        <lt:layout cssClass="form-inline" style="padding-bottom: 20px">
            <lt:layout cssClass="radio" style="padding: 0 5px 5px 0;">
                <wg:label>
                    <wgForm:radiobutton path="mode" value="MATCH_ALL"/> ${searchModeMatchAll}
                </wg:label>
            </lt:layout>
            <lt:layout cssClass="radio" style="padding: 0 0 5px 0;">
                <wg:label>
                    <wgForm:radiobutton path="mode" value="MATCH_ANY"/>  ${searchModeMatchAny}
                </wg:label>
            </lt:layout>
        </lt:layout>

        <lt:layout cssClass="form-group">
            <wg:label _for="firstName">${firstName}</wg:label>
            <wgForm:input path="firstName" type="text" name="firstName" cssClass="form-control"/>
        </lt:layout>
        <lt:layout cssClass="form-group">
            <wg:label _for="lastName">${lastName}</wg:label>
            <wgForm:input path="lastName" type="text" name="lastName" cssClass="form-control"/>
        </lt:layout>
        <lt:layout cssClass="form-group">
            <wg:label _for="gender">${gender}</wg:label>
            <lt:layout cssClass="form-inline">
                <c:forEach var="item" items="${genderValues}">
                    <lt:layout cssClass="radio">
                        <wg:label>
                            <wgForm:radiobutton path="gender" value="${item}"/> <spring:message
                                code="patient.search.radio.gender.${fn:toLowerCase(item)}"/>
                        </wg:label>
                    </lt:layout>
                </c:forEach>
            </lt:layout>
        </lt:layout>

        <lt:layout cssClass="form-group">
            <wg:label _for="dateOfBirth">${dateOfBirth}</wg:label>
            <lt:layout cssClass="no-horizontal-padding date col-md-12">
                <wgForm:input path="dateOfBirth" name="dateOfBirth" id="dateOfBirth" type="text"
                              cssClass="form-control datepicker" data-provide="datepicker"
                              placeholder="${dateFormat}"/>
                <wg:icon cssClass="glyphicon-calendar"/>
            </lt:layout>
            <div style="clear:both;"></div>
        </lt:layout>

        <lt:layout cssClass="form-group">
            <wg:label _for="ssn">${socialSecurityNumber}</wg:label>
            <wgForm:input path="ssn" type="text" name="ssn" cssClass="form-control" maxlength="9" placeholder="XXX XX XXXX"/>
        </lt:layout>

        <lt:layout cssClass="form-group">
            <wg:label _for="street" cssClass="control-label label-min-width">${street}</wg:label>
            <wgForm:input path="street" name="street" id="street" type="text" cssClass="form-control"/>
        </lt:layout>

        <lt:layout cssClass="form-group">
            <wg:label _for="city" cssClass="control-label label-min-width">${city}</wg:label>
            <wgForm:input path="city" name="city" id="city" type="text" cssClass="form-control"/>
        </lt:layout>

        <lt:layout cssClass="form-group">
            <wg:label _for="state" cssClass="control-label label-min-width">${state}</wg:label>
            <wgForm:input path="state" name="state" id="state" type="text" cssClass="form-control"/>
        </lt:layout>

        <lt:layout cssClass="form-group">
            <wg:label _for="postalCode" cssClass="control-label label-min-width">${postalCode}</wg:label>
            <wgForm:input path="postalCode" name="postalCode" id="postalCode" type="text" cssClass="form-control"/>
        </lt:layout>

        <input type="hidden" name="showSuggested" value="false" id="showSuggested"/>

        <lt:layout cssClass="form-inline buttons">
            <wg:button domType="button" type="button" cssClass="btn-default pull-rigth"
                       id="patientSearchManualClear" name="patientSearchManualClear"
            >${clear}</wg:button>

            <wg:button domType="button" type="submit" cssClass="btn-primary"
                       name="patientSearchManual" id="patientSearchManual"
            >${search}</wg:button>
        </lt:layout>

    </wgForm:form>
</lt:layout>