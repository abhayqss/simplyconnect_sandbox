<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<lt:layout cssClass="panel panel-primary" style="padding: 0px 20px 20px 20px">
    <lt:layout cssClass="boxBody">
        <wg:grid id="patientsListSuggested" cssClass="patientsList"
                 colIds="firstName,lastName,genderDisplayName,dateOfBirth,ssn,organizationName,databaseName"
                 colNames="First Name,Last Name,Gender,Date of Birth,Social Security #,Community,Source Company"
                 colFormats="string,string,string,string,ssn,string,string"
                 dataUrl="administration/suggested-matches/patients"
                 deferLoading="true"
                />
    </lt:layout>
</lt:layout>