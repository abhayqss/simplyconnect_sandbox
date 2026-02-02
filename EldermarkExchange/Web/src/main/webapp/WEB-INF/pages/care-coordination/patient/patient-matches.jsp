<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="format" tagdir="/WEB-INF/tags/format" %>
<lt:layout cssClass="modal fade" role="dialog" id="matchedPatientListModal">
    <lt:layout cssClass="modal-dialog" role="document"  style="width:1000px;">
        <lt:layout cssClass="modal-content">
            <%--<wgForm:form role="form" id="patientForm" commandName="patientDto">--%>
                <%--<c:set var="isNew" value="${patientDto.id == null}"/>--%>
                <wg:modal-header closeBtn="true">
                    <%--<span id="patientHeader"> Add New Patient</span>--%>
                    <span id="patientHeader" style="padding-left: 10px">Similar Records Found</span>
                </wg:modal-header>
                <wg:modal-body cssClass="col-md-12 matchedPatientsBody">
                    <%--<span>The similar record(s) already exists in the system:</span>--%>
                    <lt:layout id="newlyCreatedAlert" cssClass="alert alert-info" style="padding-left: 10px">
                        The similar records exist in the system. The details are in the table below.<br>
                        Please confirm that you are not creating the duplicate record by clicking "Create Record" button.
                    </lt:layout>
                    <%--<wgForm:hidden path="id"/>--%>
                    <%--<wg:grid id="patientsList" cssClass="patientsList"--%>
                             <%--colIds="firstName,lastName,gender,birthDate,ssn,eventCount, community"--%>
                             <%--colNames="First Name,Last Name,Gender, Birth Date,Social Security Number,Events, Communitiy"--%>
                             <%--colFormats="string"--%>
                             <%--dataUrl="care-coordination/patients"--%>
                             <%--deferLoading="true"--%>
                            <%--/>--%>
                    <%--<lt:layout cssClass="col-md-12 no-horizontal-padding">--%>
                        <div class="sectionSubHead2">New Record</div>
                    <%--</lt:layout>--%>
                    <table class="display dataTable matchedPatientsTable">
                        <tbody>
                        <tr style="background-color: #f9f9f9;">
                            <%--<th width="80px">First Name</th><th width="80px">Last Name</th><th width="80px">Gender</th><th width="80px">Date of Birth</th>--%>
                            <%--<th width="100px">Social Security #</th><th width="80px">Community</th><th>Address</th>--%>
                            <th>First Name</th><th>Last Name</th><th>Gender</th><th>Date of Birth</th><th>Social Security #</th>
                            <th style="width:130px">Community</th><th style="width:200px">Address</th>
                        </tr>
                            <tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
                                <td>${newPatient.firstName}</td><td>${newPatient.lastName}</td>
                                <td>${newPatient.gender}</td>
                                <td><fmt:formatDate value="${newPatient.birthDate}" pattern="MM/dd/yyyy" /></td>
                                <td style="white-space: nowrap;"><format:ssn value="${newPatient.ssn}"/></td>
                                <td>${newPatient.community}</td>
                                <td>${newPatient.address.displayAddress}</td>
                            </tr>
                        </tbody>
                    </table>
                    <br>
                    <%--<lt:layout cssClass="col-md-12 no-horizontal-padding">--%>
                        <div class="sectionSubHead2">Similar Records</div>
                    <%--</lt:layout>--%>
                    <table class="display dataTable matchedPatientsTable">
                        <tbody>
                        <tr style="background-color: #f9f9f9;">
                            <th>First Name</th><th>Last Name</th><th>Gender</th><th>Date of Birth</th><th>Social Security #</th>
                            <th style="width:130px">Community</th><th style="width:200px">Address</th>
                        </tr>
                        <c:forEach var="patient" items="${patientMatchesList}" varStatus="loopStatus">
                            <tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
                                <td>${patient.firstName}</td><td>${patient.lastName}</td>
                                <td>${patient.gender}</td>
                                <td><fmt:formatDate value="${patient.birthDate}" pattern="MM/dd/yyyy" /></td>
                                <td style="white-space: nowrap;"><format:ssn value="${patient.ssn}"/></td>
                                <td>${patient.community}</td>

                                <td>${patient.address.displayAddress}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </wg:modal-body>
                <wg:modal-footer-btn-group btnGroupCssClass="patientMatchesButtons">
                    <%--<lt:layout cssClass="btn-group" role="group">--%>
                        <wg:button name="cancelBtn"
                                   domType="link"
                                   dataToggle="modal"
                                   dataTarget="#matchedPatientListModal"
                                   id="matchedPatientListCancelBtn"
                                   cssClass="btn-default btn cancelBtn">
                            CANCEL
                        </wg:button>

                        <wg:button name="createRecord"
                                   id="createRecord"
                                   domType="link"
                                   dataToggle="modal"
                                   cssClass="btn-primary btn submitBtn">
                            CREATE RECORD
                        </wg:button>
                </wg:modal-footer-btn-group>
            <%--</wgForm:form>--%>
        </lt:layout>
    </lt:layout>
</lt:layout>