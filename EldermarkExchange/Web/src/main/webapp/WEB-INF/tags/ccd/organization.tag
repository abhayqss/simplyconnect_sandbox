<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="ccd" tagdir="/WEB-INF/tags/ccd" %>

<%@ attribute name="organization" required="true" rtexprvalue="true"
              type="com.scnsoft.eldermark.shared.ccd.OrganizationDto" %>

<spring:message code="patient.detail.field.name.organization" var="nameLabel"/>
<spring:message code="patient.detail.field.contactInfo" var="contactInfoLabel"/>
<spring:message code="patient.detail.field.telecom" var="telecomLabel"/>

<c:if test="${not empty organization}">
    <lt:layout>
        <lt:layout cssClass="row">
            <wg:label cssClass="text">${nameLabel}</wg:label>
            <lt:layout cssClass="table-cell-box">
                <wg:label cssClass="value green">${organization.name}</wg:label>
            </lt:layout>
        </lt:layout>

        <c:if test="${not empty organization.addresses}">
            <ccd:label-for-value value=" " label="${contactInfoLabel}"/>
        </c:if>

        <c:forEach var="address" items="${organization.addresses}">
            <lt:layout cssClass="row">
                <%--<wg:label cssClass="text">--%>
                    <%--${not empty address.postalAddressUse ? address.postalAddressUse : 'Address'}:--%>
                <%--</wg:label>--%>
                <wg:label cssClass="subtext">Address:</wg:label>
                <lt:layout cssClass="table-cell-box">
                    <c:if test="${not empty address.streetAddress}">
                        <wg:label cssClass="value display-block">
                            <c:out value="${address.streetAddress}"/>
                        </wg:label>
                    </c:if>
                    <c:if test="${not empty address.cityStatePostalCodeAndCountry}">
                        <wg:label cssClass="value display-block">
                            <c:out value="${address.cityStatePostalCodeAndCountry}"/>
                        </wg:label>
                    </c:if>
                </lt:layout>
            </lt:layout>
        </c:forEach>

        <c:if test="${not empty organization.telecom && not empty organization.telecom.value}">
            <lt:layout cssClass="row">
                <wg:label cssClass="text">${telecomLabel}</wg:label>
                <lt:layout cssClass="table-cell-box">
                    <wg:label cssClass="value">
                        <c:out value="${organization.telecom.value}"/>
                    </wg:label>
                </lt:layout>
            </lt:layout>
        </c:if>
    </lt:layout>

</c:if>
