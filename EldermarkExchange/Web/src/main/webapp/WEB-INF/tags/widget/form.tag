<%@ tag pageEncoding="UTF-8" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="name" required="false" rtexprvalue="true" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>
<%@ attribute name="action" required="false" rtexprvalue="true" %>
<%@ attribute name="method" required="false" rtexprvalue="true" %>
<%@ attribute name="autocomplete" required="false" rtexprvalue="true" %>

<form action="${action}" method="${method}" id="${id}" class="${cssClass}" name="${name}" autocomplete="${autocomplete}">
    <jsp:doBody/>
</form>