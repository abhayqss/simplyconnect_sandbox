<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ attribute name="content" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="cssClass" required="false" rtexprvalue="true"%>
<%@ attribute name="name" required="true" rtexprvalue="true" %>

<c:if test="${not empty content}">
    <lt:layout cssClass="ccdHeaderItem ${cssClass}">
        <lt:layout cssClass="row">
            <wg:label cssClass="name">${name}</wg:label>
        </lt:layout>

        <jsp:doBody/>

    </lt:layout>
</c:if>