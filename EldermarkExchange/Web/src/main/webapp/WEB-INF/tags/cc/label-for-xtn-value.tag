<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="value" required="true" rtexprvalue="true"
              type="com.scnsoft.eldermark.shared.carecoordination.adt.datatype.XTNPhoneNumberDto" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>
<%@ attribute name="label" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="boldValue" required="false" rtexprvalue="true" type="java.lang.Boolean" %>

<c:if test="${not empty value and not (empty value.telephoneNumber and empty value.phoneNumber and empty value.countryCode and empty value.areaCode
and empty value.extension and empty value.email and empty value.anyText)}">
    <lt:layout cssClass="col-md-12">
        <p class="noPadding col-md-4 eventLabel" style="font-weight: bold">${label}</p>
        <%--<c:if test="${not empty id}">id="${id}"</c:if>>--%>
        <ul class="col-md-8"
            <c:if test="${not empty id}">id="${id}"</c:if>
            <c:if test="${boldValue}">style="font-weight: bold"</c:if>
        >
            <c:if test="${not empty value.telephoneNumber}">
                <p class="codedValueLabel">Telephone Number</p>
                <p class="codedValue">${value.telephoneNumber}</p>
            </c:if>
            <c:if test="${not empty value.email}">
                <p class="codedValueLabel">Email Address</p>
                <p class="codedValue">${value.email}</p>
            </c:if>
            <c:if test="${not empty value.countryCode}">
                <p class="codedValueLabel">Country Code</p>
                <p class="codedValue">${value.countryCode}</p>
            </c:if>
            <c:if test="${not empty value.areaCode}">
                <p class="codedValueLabel">Area/City Code</p>
                <p class="codedValue">${value.areaCode}</p>
            </c:if>
            <c:if test="${not empty value.phoneNumber}">
                <p class="codedValueLabel">Local Number</p>
                <p class="codedValue">${value.phoneNumber}</p>
            </c:if>
            <c:if test="${not empty value.extension}">
                <p class="codedValueLabel">Extension</p>
                <p class="codedValue">${value.extension}</p>
            </c:if>
            <c:if test="${not empty value.anyText}">
                <p class="codedValueLabel">Any Text</p>
                <p class="codedValue">${value.anyText}</p>
            </c:if>
        </ul>
    </lt:layout>
</c:if>