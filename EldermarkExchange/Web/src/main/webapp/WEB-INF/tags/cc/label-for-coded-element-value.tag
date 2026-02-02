<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="value" required="true" rtexprvalue="true"
              type="com.scnsoft.eldermark.shared.carecoordination.adt.datatype.CECodedElementDto" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>
<%@ attribute name="label" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="boldValue" required="false" rtexprvalue="true" type="java.lang.Boolean" %>

<c:if test="${not empty value and not (empty value.identifier and empty value.text and empty value.nameOfCodingSystem and empty value.alternateIdentifier
and empty value.alternateText and empty value.nameOfAlternateCodingSystem)}">
    <lt:layout cssClass="col-md-12">
        <p class="noPadding col-md-4 eventLabel" style="font-weight: bold">${label}</p>
        <%--<c:if test="${not empty id}">id="${id}"</c:if>>--%>
        <ul class="col-md-8"
            <c:if test="${not empty id}">id="${id}"</c:if>
            <c:if test="${boldValue}">style="font-weight: bold"</c:if>
        >
            <c:if test="${not empty value.identifier}">
                <p class="codedValueLabel">Identifier</p>
                <p class="codedValue">${value.identifier}</p>
            </c:if>
            <c:if test="${not empty value.text}">
                <p class="codedValueLabel">Text</p>
                <p class="codedValue">${value.text}</p>
            </c:if>
            <c:if test="${not empty value.nameOfCodingSystem}">
                <p class="codedValueLabel">Name Of Coding System</p>
                <p class="codedValue">${value.nameOfCodingSystem}</p>
            </c:if>
            <c:if test="${not empty value.alternateIdentifier}">
                <p class="codedValueLabel">Alternate Identifier</p>
                <p class="codedValue">${value.alternateIdentifier}</p>
            </c:if>
            <c:if test="${not empty value.alternateText}">
                <p class="codedValueLabel">Alternate Text</p>
                <p class="codedValue">${value.alternateText}</p>
            </c:if>
            <c:if test="${not empty value.nameOfAlternateCodingSystem}">
                <p class="codedValueLabel">Name Of Alternate Coding System</p>
                <p class="codedValue">${value.nameOfAlternateCodingSystem}</p>
            </c:if>
        </ul>
    </lt:layout>
</c:if>