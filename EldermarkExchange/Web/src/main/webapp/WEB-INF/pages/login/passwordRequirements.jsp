<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import=" com.scnsoft.eldermark.entity.password.PasswordSettingsType" %>
<jsp:useBean id="organizationPasswordSettings" scope="request" type="java.util.List"/>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="COMPLEXITY_LOWERCASE_COUNT" value="<%=PasswordSettingsType.COMPLEXITY_LOWERCASE_COUNT%>"/>
<c:set var="COMPLEXITY_ARABIC_NUMERALS_COUNT" value="<%=PasswordSettingsType.COMPLEXITY_ARABIC_NUMERALS_COUNT%>"/>
<c:set var="COMPLEXITY_UPPERCASE_COUNT" value="<%=PasswordSettingsType.COMPLEXITY_UPPERCASE_COUNT%>"/>
<c:set var="COMPLEXITY_NON_ALPHANUMERIC_COUNT" value="<%=PasswordSettingsType.COMPLEXITY_NON_ALPHANUMERIC_COUNT%>"/>
<c:set var="COMPLEXITY_ALPHABETIC_COUNT" value="<%=PasswordSettingsType.COMPLEXITY_ALPHABETIC_COUNT%>"/>
<c:set var="COMPLEXITY_PASSWORD_LENGTH" value="<%=PasswordSettingsType.COMPLEXITY_PASSWORD_LENGTH%>"/>

<div class="passwordHelpGeneral ${param.cssClass}">
    <ul>
        <c:forEach var="item" items="${organizationPasswordSettings}" varStatus="i">
            <c:choose>
                <c:when test="${item.passwordSettingsType eq COMPLEXITY_LOWERCASE_COUNT && item.enabled}">
                    <li>&#8226 ${item.value} lowercase character${item.value > 1 ? "s" : ""}</li>
                    <li>&#8226 No spaces allowed</li>
                </c:when>
                <c:when test="${item.passwordSettingsType eq COMPLEXITY_ARABIC_NUMERALS_COUNT && item.enabled}">
                    <li>&#8226 ${item.value} number${item.value > 1 ? "s" : ""}</li>
                </c:when>
                <c:when test="${item.passwordSettingsType eq COMPLEXITY_UPPERCASE_COUNT && item.enabled}">
                    <li>&#8226 ${item.value} uppercase character${item.value > 1 ? "s" : ""}</li>
                </c:when>
                <c:when test="${item.passwordSettingsType eq COMPLEXITY_NON_ALPHANUMERIC_COUNT && item.enabled && item.value > 0}">
                    <li>&#8226 ${item.value} special symbol${item.value > 1 ? "s" : ""} (e.g. @#$%!)</li>
                </c:when>
                <c:when test="${item.passwordSettingsType eq COMPLEXITY_ALPHABETIC_COUNT && item.enabled}">
                    <li>&#8226 ${item.value} alphabetic character${item.value > 1 ? "s" : ""}</li>
                </c:when>
                <c:when test="${item.passwordSettingsType eq COMPLEXITY_PASSWORD_LENGTH && item.enabled}">
                    <li>&#8226 ${item.value} character${item.value > 1 ? "s" : ""} minimum</li>
                </c:when>
            </c:choose>
        </c:forEach>
    </ul>
</div>