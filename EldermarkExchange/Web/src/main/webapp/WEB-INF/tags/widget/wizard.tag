<%@ tag pageEncoding="UTF-8" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="name" required="false" rtexprvalue="true" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>
<%@ attribute name="activeStep" required="false" rtexprvalue="true" %>

<div role="wizard" class="wizard ${cssClass}" id="${id}" name="${name}" data-active-step="${activeStep}">
    <jsp:doBody/>
</div>
