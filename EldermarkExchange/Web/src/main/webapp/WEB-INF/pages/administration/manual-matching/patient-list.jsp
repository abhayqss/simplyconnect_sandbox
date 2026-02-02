<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<lt:layout cssClass="patientListBox panel panel-primary">
    <lt:layout cssClass="boxHeader panel panel-heading">
       Search Results
    </lt:layout>

    <lt:layout cssClass="boxBody">
        <lt:layout id="manualMatchingShowSuggestedRecordsLayout" style="padding:5px 0;" cssClass="col-md-6 hidden">
            <wg:label><input type="checkbox" id="manualMatchingShowSuggestedRecords" class="showDeactivatedRecords"
                >Show Suggested Records</wg:label>
        </lt:layout>
        <wg:grid id="patientsListManual" cssClass="patientsList"
                 colIds="select,firstName,lastName,genderDisplayName,dateOfBirth,ssn,organizationName,databaseName"
                 colNames="Select,First Name,Last Name,Gender,Date of Birth,Social Security #,Community,Source Company"
                 colFormats="checkbox,string,string,string,string,ssn,string,string"
                 dataUrl="administration/manual-matching/patients"
                 deferLoading="true"
                />

        <lt:layout cssClass="form-inline buttons">
            <wg:button domType="button" type="submit" cssClass="btn-primary nextBtn"
                       name="manualMatchingNext" id="manualMatchingNext"
            >NEXT</wg:button>
        </lt:layout>
    </lt:layout>
</lt:layout>