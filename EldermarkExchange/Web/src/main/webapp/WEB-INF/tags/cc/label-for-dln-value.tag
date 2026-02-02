<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="value" required="true" rtexprvalue="true"
              type="com.scnsoft.eldermark.shared.carecoordination.adt.datatype.DLNDriverSLicenseNumberDto" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>
<%@ attribute name="label" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="boldValue" required="false" rtexprvalue="true" type="java.lang.Boolean" %>

<c:if test="${not empty value and not (empty value.licenseNumber and empty value.issuingStateProvinceCountry and empty value.expirationDate)}">
    <lt:layout cssClass="col-md-12">
        <p class="noPadding col-md-4 eventLabel" style="font-weight: bold">${label}</p>
        <%--<c:if test="${not empty id}">id="${id}"</c:if>>--%>
        <ul class="col-md-8"
            <c:if test="${not empty id}">id="${id}"</c:if>
            <c:if test="${boldValue}">style="font-weight: bold"</c:if>
        >
            <c:if test="${not empty value.licenseNumber}">
                <p class="codedValueLabel">License Number</p>
                <p class="codedValue">${value.licenseNumber}</p>
            </c:if>
            <c:if test="${not empty value.issuingStateProvinceCountry}">
                <p class="codedValueLabel">Issuing State, Province, Country</p>
                <p class="codedValue">${value.issuingStateProvinceCountry}</p>
            </c:if>
            <c:if test="${not empty value.expirationDate}">
                <p class="codedValueLabel">Expiration Date</p>
                <p class="codedValue">${value.expirationDate}</p>
            </c:if>
        </ul>
    </lt:layout>
</c:if>