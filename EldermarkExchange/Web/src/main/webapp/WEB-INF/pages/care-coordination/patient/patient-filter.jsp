<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<lt:layout cssClass="patientFilterBox">

    <lt:layout cssClass="boxHeader panel panel-primary">
        <lt:layout cssClass="panel-heading">
            Search
        </lt:layout>
    </lt:layout>

    <wgForm:form role="form" id="patientsFilter" commandName="filter" style="padding: 0 20px 20px 20px">
        <lt:layout cssClass="form-group">
            <wg:label _for="firstName">First Name</wg:label>
            <wgForm:input path="firstName" type="text" name="firstName" cssClass="form-control"/>
        </lt:layout>
        <lt:layout cssClass="form-group">
            <wg:label _for="lastName">Last Name</wg:label>
            <wgForm:input path="lastName" type="text" name="lastName" cssClass="form-control"/>
        </lt:layout>
        <lt:layout cssClass="form-group">
            <wg:label _for="gender">Gender</wg:label>
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
            <wg:label _for="birthDate">Date of Birth</wg:label>
            <lt:layout cssClass="no-horizontal-padding date col-md-12">
                <wgForm:input path="birthDate" name="birthDate" id="birthDate" type="text"
                              cssClass="form-control datepicker" data-provide="datepicker"/>
                <wg:icon cssClass="glyphicon-calendar"/>
            </lt:layout>
            <div style="clear:both;"/>
        </lt:layout>

        <lt:layout cssClass="form-group">
            <wg:label _for="lastName">Social Security Number</wg:label>
            <wgForm:input path="lastFourSsn" type="text" name="lastFourSsn" cssClass="form-control" placeholder="Last 4 digits"/>
        </lt:layout>
        <input type="hidden" name="showDeactivated" value="false" id="showDeactivated"/>

        <lt:layout cssClass="form-group">
            <wg:label _for="primaryCarePhysician">Primary Care Physician</wg:label>
            <wgForm:input path="primaryCarePhysician" type="text" name="primaryCarePhysician" cssClass="form-control"/>
        </lt:layout>

        <lt:layout cssClass="form-group">
            <wg:label _for="insuranceNetwork">Insurance Network</wg:label>
            <wgForm:input path="insuranceNetwork" type="text" name="Insurance Network" cssClass="form-control"/>
        </lt:layout>

        <lt:layout cssClass="form-inline buttons">
            <wg:button domType="button" id="patientSearchClear" name="patientSearchClear" type="button"
                       cssClass="btn-default pull-rigth">
                CLEAR
            </wg:button>

            <wg:button domType="button" type="submit" cssClass="btn-primary" name="patientSearch"
                       id="patientSearch">
                SEARCH
            </wg:button>

        </lt:layout>

    </wgForm:form>
</lt:layout>