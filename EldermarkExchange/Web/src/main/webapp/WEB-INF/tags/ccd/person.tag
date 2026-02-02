<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="ccd" tagdir="/WEB-INF/tags/ccd" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="person" required="true" rtexprvalue="true" type="com.scnsoft.eldermark.shared.ccd.PersonDto" %>

<spring:message code="patient.detail.field.name.person" var="nameLabel"/>
<spring:message code="patient.detail.field.code" var="codeLabel"/>
<spring:message code="patient.detail.field.contactInfo" var="contactInfoLabel"/>
<spring:message code="patient.detail.field.telecom" var="telecomLabel"/>

<c:if test="${not empty person}">
    <lt:layout>
        <lt:layout cssClass="row">
            <wg:label cssClass="text">${nameLabel}</wg:label>
            <lt:layout cssClass="table-cell-box">
                <c:forEach var="name" items="${person.names}">
                    <wg:label cssClass="value green">
                        ${name.fullName}
                        <c:if test="${not empty name.useCode}"> (${name.useCode})</c:if>
                    </wg:label>
                </c:forEach>
            </lt:layout>
        </lt:layout>
        <c:if test="${not empty person.code}">
            <lt:layout cssClass="row">
                <wg:label cssClass="text">${codeLabel}</wg:label>
                <lt:layout cssClass="table-cell-box">
                    <wg:label cssClass="value">${person.code}</wg:label>
                </lt:layout>
            </lt:layout>
        </c:if>

        <c:if test="${not empty person.addresses}">
            <ccd:label-for-value value=" " label="${contactInfoLabel}"/>
        </c:if>

        <c:forEach var="address" items="${person.addresses}">
            <lt:layout cssClass="row">
                <wg:label cssClass="subtext">Address:
                    <%--<c:choose>--%>
                        <%--<c:when test="${(not empty address.postalAddressUse) and (not(address.postalAddressUse=='UNKNOWN'))}">--%>
                            <%--${address.postalAddressUse}:--%>
                        <%--</c:when>--%>
                        <%--<c:otherwise>--%>
                            <%--Address--%>
                        <%--</c:otherwise>--%>
                    <%--</c:choose>--%>
                </wg:label>
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

        <c:if test="${not empty person.telecoms}">
            <lt:layout cssClass="row">
                <wg:label cssClass="text">${telecomLabel}</wg:label>
                <lt:layout cssClass="table-cell-box">
                    <c:forEach var="telecom" items="${person.telecoms}">
                        <c:if test="${not empty telecom.value}">
                            <wg:label cssClass="value display-block">
                                <c:out value="${telecom.value}"/>
                            </wg:label>
                        </c:if>
                    </c:forEach>
                </lt:layout>
            </lt:layout>
        </c:if>
    </lt:layout>
</c:if>