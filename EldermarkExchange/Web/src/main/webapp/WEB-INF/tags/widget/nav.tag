<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="name" required="false" rtexprvalue="true" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>
<%@ attribute name="type" required="false" rtexprvalue="true" type="java.lang.String" %>

<ul id="${id}"
    name="${name}"
    class="nav nav-pills
    <c:if test="${type eq 'vertical'}">nav-stacked</c:if> ${cssClass}">
    <jsp:doBody/>
</ul>
