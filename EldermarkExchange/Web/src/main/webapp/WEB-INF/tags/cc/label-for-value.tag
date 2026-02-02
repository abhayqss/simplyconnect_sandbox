<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%--<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>--%>
<%--<%@taglib prefix="format" tagdir="/WEB-INF/tags/format" %>--%>

<%@ attribute name="value" required="true" rtexprvalue="true" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>
<%@ attribute name="label" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="boldValue" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@ attribute name="cssStyle" required="false" rtexprvalue="true" %>
<%--<%@ attribute name="ssn" required="false" %>--%>

<c:if test="${not empty value}">
    <lt:layout cssClass="col-md-12">
        <p class="col-md-4 eventLabel no-padding">${label}</p>
        <p class="col-md-8" <c:if test="${not empty id}">id="${id}"</c:if> style="<c:if test="${boldValue}">font-weight: bold; </c:if>${cssStyle}" >
            <c:choose>
                <c:when test="${value == 'true'}">Yes</c:when>
                <c:when test="${value == 'false'}">No</c:when>
                <%--<c:when test="${ssn}">--%>
                    <%--<format:ssn value="${value}"/>--%>
                <%--</c:when>--%>
                <c:otherwise>
                    ${value}
                </c:otherwise>
            </c:choose>
        </p>
    </lt:layout>
</c:if>