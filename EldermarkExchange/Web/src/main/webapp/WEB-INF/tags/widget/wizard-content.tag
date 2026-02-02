<%@ tag pageEncoding="UTF-8" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>

<div class="tab-content ${cssClass}">
    <jsp:doBody/>
</div>
