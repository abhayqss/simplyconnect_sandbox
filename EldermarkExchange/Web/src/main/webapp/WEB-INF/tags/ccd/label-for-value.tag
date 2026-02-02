<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%--<%@taglib prefix="format" tagdir="/WEB-INF/tags/format" %>--%>
<%@ attribute name="value" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="label" required="true" rtexprvalue="true" type="java.lang.String" %>
<%--<%@ attribute name="ssn" required="false" %>--%>
<c:if test="${not empty value}">
    <lt:layout cssClass="row">
        <wg:label cssClass="text">${label}</wg:label>
        <lt:layout cssClass="table-cell-box">
            <wg:label cssClass="value">${value}</wg:label>
            <%--<wg:label cssClass="value">--%>
                <%--<c:choose>--%>
                    <%--<c:when test="${ssn}">--%>
                        <%--<format:ssn value="${value}"/>--%>
                    <%--</c:when>--%>
                    <%--<c:otherwise>--%>
                        <%--${value}--%>
                    <%--</c:otherwise>--%>
                <%--</c:choose>--%>
            <%--</wg:label>--%>
        </lt:layout>
    </lt:layout>
</c:if>