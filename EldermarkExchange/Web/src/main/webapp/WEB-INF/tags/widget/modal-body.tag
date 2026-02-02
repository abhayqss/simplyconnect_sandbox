<%@ tag pageEncoding="UTF-8" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>

<div class="modal-body ${cssClass}">
    <jsp:doBody/>
</div>
