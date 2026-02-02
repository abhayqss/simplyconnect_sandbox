<%@ tag pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@ taglib prefix="cc" tagdir="/WEB-INF/tags/cc" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="valueList" required="true" rtexprvalue="true" type="java.util.Collection" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>
<%@ attribute name="singularLabel" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="pluralLabel" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="boldValue" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@ attribute name="separator" required="false" rtexprvalue="true" type="java.lang.String" %>

<c:choose>
    <c:when test="${fn:length(valueList) gt 1}">
        <c:if test="${empty separator}">
            <c:set var="separator" value=", "/>
        </c:if>
        <c:set var="valueStr" value=""/>
        <c:forEach items="${valueList}" var="value" varStatus="loop">
            <c:choose>
                <c:when test="${loop.first}">
                    <c:set var="valueStr" value="${value}"/>
                </c:when>
                <c:otherwise>
                    <c:set var="valueStr" value="${valueStr}${separator}${value}"/>
                </c:otherwise>
            </c:choose>
        </c:forEach>
        <cc:label-for-value id="${id}" boldValue="${boldValue}"
                            label="${pluralLabel}" value="${valueStr}"/>
    </c:when>
    <c:when test="${fn:length(valueList) eq 1}">
        <cc:label-for-value id="${id}" boldValue="${boldValue}"
                            label="${singularLabel}" value="${valueList[0]}"/>
    </c:when>
</c:choose>

