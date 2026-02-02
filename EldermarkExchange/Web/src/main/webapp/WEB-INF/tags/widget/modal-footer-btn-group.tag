<%@ tag pageEncoding="UTF-8" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="btnGroupCssClass" required="false" rtexprvalue="true" %>

<div class="modal-footer with-border-spacing ${cssClass}">
    <div class="btn-group btn-group-justified footer ${(empty btnGroupCssClass) ? 'ldr-2-btn-group' : btnGroupCssClass}" role="group">
        <jsp:doBody/>
    </div>
</div>
