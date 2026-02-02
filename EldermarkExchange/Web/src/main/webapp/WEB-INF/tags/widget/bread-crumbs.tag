<%@ tag pageEncoding="UTF-8" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="name" required="false" rtexprvalue="true" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>

<ol id="${id}" name="${name}" class="breadcrumb ${cssClass}">
    <jsp:doBody/>
</ol>
