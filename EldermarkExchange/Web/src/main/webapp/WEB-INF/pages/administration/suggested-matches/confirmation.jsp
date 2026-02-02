<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="format" tagdir="/WEB-INF/tags/format" %>

<lt:layout cssClass="modal fade" role="dialog" id="suggestedMatchesConfirmationModal">
    <lt:layout cssClass="modal-dialog" role="document" style="width:1000px;">
        <lt:layout cssClass="modal-content">
            <wg:modal-header closeBtn="true">
                <span id="suggestedMatchesConfirmationHeader" style="padding-left: 10px">Confirmation</span>
            </wg:modal-header>
            <wg:modal-body cssClass="col-md-12 matchedPatientsBody">
                <div class="sectionSubHead2">Matching Records</div>
                <table class="display dataTable matchedPatientsTable">
                    <thead>
                        <tr style="background-color: #f9f9f9;">
                            <th class="sorting_disabled">First Name</th>
                            <th class="sorting_disabled">Last Name</th>
                            <th class="sorting_disabled">Gender</th>
                            <th class="sorting_disabled">Date of Birth</th>
                            <th class="sorting_disabled">Social Security #</th>
                            <th class="sorting_disabled" style="width: 130px">Community</th>
                            <th class="sorting_disabled" style="width: 200px">Address</th>
                        </tr>
                    </thead>
                    <tbody>
                    <jsp:useBean id="matchingResidents" scope="request" type="java.util.List<com.scnsoft.eldermark.shared.ResidentDto>"/>
                    <c:forEach var="resident" items="${matchingResidents}" varStatus="loopStatus">
                        <tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
                            <td>${resident.firstName}</td>
                            <td>${resident.lastName}</td>
                            <td>${resident.genderDisplayName}</td>
                            <td><fmt:formatDate value="${resident.dateOfBirth}" pattern="MM/dd/yyyy" /></td>
                            <td style="white-space: nowrap;"><format:ssn value="${resident.ssn}"/></td>
                            <td>${resident.organizationName}</td>
                            <td>${resident.streetAddress} ${resident.cityStateAndPostalCode}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                <br>
                <div class="sectionSubHead2">Not a Match</div>
                <table class="display dataTable matchedPatientsTable">
                    <thead>
                        <tr style="background-color: #f9f9f9;">
                            <th class="sorting_disabled">First Name</th>
                            <th class="sorting_disabled">Last Name</th>
                            <th class="sorting_disabled">Gender</th>
                            <th class="sorting_disabled">Date of Birth</th>
                            <th class="sorting_disabled">Social Security #</th>
                            <th class="sorting_disabled" style="width: 130px">Community</th>
                            <th class="sorting_disabled" style="width: 200px">Address</th>
                        </tr>
                    </thead>
                    <tbody>
                    <jsp:useBean id="mismatchingResidents" scope="request" type="java.util.List<com.scnsoft.eldermark.shared.ResidentDto>"/>
                    <c:forEach var="resident" items="${mismatchingResidents}" varStatus="loopStatus">
                        <tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
                            <td>${resident.firstName}</td>
                            <td>${resident.lastName}</td>
                            <td>${resident.genderDisplayName}</td>
                            <td><fmt:formatDate value="${resident.dateOfBirth}" pattern="MM/dd/yyyy" /></td>
                            <td style="white-space: nowrap;"><format:ssn value="${resident.ssn}"/></td>
                            <td>${resident.organizationName}</td>
                            <td>${resident.streetAddress} ${resident.cityStateAndPostalCode}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </wg:modal-body>
            <wg:modal-footer-btn-group btnGroupCssClass="patientMatchesButtons">
                    <wg:button name="cancelBtn"
                               domType="link"
                               dataToggle="modal"
                               dataTarget="#suggestedMatchesConfirmationModal"
                               id="suggestedMatchesConfirmationCancelBtn"
                               cssClass="btn-default btn cancelBtn">
                        CANCEL
                    </wg:button>

                    <wg:button name="resolveBtn"
                               id="suggestedMatchesConfirmationResolveBtn"
                               domType="link"
                               dataToggle="modal"
                               cssClass="btn-primary btn submitBtn">
                        RESOLVE
                    </wg:button>
            </wg:modal-footer-btn-group>
        </lt:layout>
    </lt:layout>
</lt:layout>