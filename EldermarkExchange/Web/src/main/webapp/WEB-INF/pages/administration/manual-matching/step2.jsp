<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="cc" tagdir="/WEB-INF/tags/cc" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="ldr-ui-layout patientsComparedNavigator ldr-center-block" style="padding:10px">
    <ol class="breadcrumb">
        <li class="backToManualMatching">
             <span class="ldr-ui-label">
                <c:url value="/resources/images/nav-arrow-left.png" var="backImg"/>
                <img src="${backImg}" class="back"/>
             </span>
            <span class="crumb">
                Search Results
            </span>
        </li>
        <li class="active">
            <span class="crumb">
                Matching Records
            </span>
        </li>
    </ol>
</div>

<lt:layout cssClass="patientsComparedTableContent">

    <lt:layout style="margin: 0px" cssClass="patientListBox panel panel-primary clearfix">
        <lt:layout cssClass="boxHeader panel panel-heading patientListBoxHeader">
            Information
        </lt:layout>
        <lt:layout cssClass="boxBody">
            <table class="display transposed dataTable" cellspacing="0" role="grid"
                   style="vertical-align: top; display: inline-block; width: 226px;">
                <thead>
                <tr role="row">
                    <th class="sorting_disabled" rowspan="1" colspan="1" ></th>
                    <th class="sorting_disabled" rowspan="1" colspan="1" >First Name</th>
                    <th class="sorting_disabled" rowspan="1" colspan="1" >Last Name</th>
                    <th class="sorting_disabled" rowspan="1" colspan="1" >Gender</th>
                    <th class="sorting_disabled" rowspan="1" colspan="1" >Date of Birth</th>
                    <th class="sorting_disabled" rowspan="1" colspan="1" >Social Security #</th>
                    <th class="sorting_disabled" rowspan="1" colspan="1" >Street Address</th>
                    <th class="sorting_disabled" rowspan="1" colspan="1" >City</th>
                    <th class="sorting_disabled" rowspan="1" colspan="1" >State</th>
                    <th class="sorting_disabled" rowspan="1" colspan="1" >Zip Code</th>
                    <th class="sorting_disabled" rowspan="1" colspan="1" >Organization</th>
                    <th class="sorting_disabled" rowspan="1" colspan="1" >Community</th>
                    <th class="sorting_disabled" rowspan="1" colspan="1" >Records Matched Automatically</th>
                    <th class="sorting_disabled" rowspan="1" colspan="1" >Choose Matching Records</th>
                </tr>
                </thead>
            </table>
            <wg:grid id="manualMatchingPatientsCompared"
                     cssClass="transposed"
                     colIds="selfUrl,firstName,lastName,genderDisplayName,dateOfBirth,ssn,streetAddress,city,state,postalCode,databaseName,organizationName,matchedAutomatically,select"
                     colNames=" , First Name, Last Name, Gender, Date of Birth, Social Security #, Street Address, City, State, Zip Code, Organization, Community, Records Matched Automatically, Choose Matching Records"
                     dataUrl="administration/manual-matching/patients"
                     dataRequestMethod="GET"
                     colFormats="custom,string,string,string,string,ssn,string,string,string,string,string,string,custom,checkbox"/>

            <lt:layout cssClass="col-md-offset-8 form-inline buttons" style="padding-top: 20px">
                <wg:button domType="button" type="button" cssClass="btn-default cancelBtn"
                           id="manualMatchingCancel" name="manualMatchingCancel"
                >CANCEL</wg:button>

                <wg:button domType="button" type="submit" cssClass="btn-primary submitBtn"
                           name="manualMatchingSave" id="manualMatchingSave"
                >SAVE</wg:button>
            </lt:layout>
        </lt:layout>
    </lt:layout>

</lt:layout>

<%-- =================== Confirmation Modal ========================== --%>
<div id="manualMatchingConfirmationContainer"></div>
