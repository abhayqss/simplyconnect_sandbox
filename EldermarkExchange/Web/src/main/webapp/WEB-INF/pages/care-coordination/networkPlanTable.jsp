<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wgForm" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="selectedNetworks" scope="request" type="java.util.List"/>

<lt:layout cssClass="tableElement">
    <lt:layout cssClass="boxBody">
        <table id="in-network-insurance-table" class="table-div in-network-insurance-table">
            <tr>
                <th class="table-title">Network / Payment method</th>
                <th class="table-title">Plan</th>
                <th></th>
            </tr>
            <c:forEach var="network" items="${selectedNetworks}" varStatus="itemLoop">
                <tr data-network-id="${network.id}"
                    data-network-name="${network.name}"
                    role="row"
                    class="modal-table-row">
                    <td class="network-name">
                        <div class="network-text">${network.name}</div>
                    </td>
                    <td class="plans">
                        <c:if test="${not empty network.insurancePlans}">
                            <div class="btn-group bootstrap-select show-tick form-control">
                                <wgForm:select path="marketplace.selectedInNetworkInsurancePlanIds['${network.id}']" id="selectedPlan${network.id}"
                                               cssClass="form-control spicker dropdown-8 selected-plan"
                                               title="Select Value" multiple="true">
                                    <option data-hidden="true"></option>
                                    <wgForm:option value="0" label="All"/>
                                    <c:forEach var="plan" items="${network.insurancePlans}">
                                        <wgForm:option value="${plan.id}" label="${plan.displayName}"/>
                                    </c:forEach>
                                </wgForm:select>
                            </div>
                        </c:if>
                    </td>
                    <td class="remove-button-td"><a class="remove-button"></a></td>
                </tr>
            </c:forEach>
        </table>
    </lt:layout>
</lt:layout>