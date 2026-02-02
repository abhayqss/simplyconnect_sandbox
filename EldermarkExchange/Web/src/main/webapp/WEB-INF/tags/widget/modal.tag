<%@ tag pageEncoding="UTF-8" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="modalCssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="name" required="false" rtexprvalue="true" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>
<%@ attribute name="headerText" required="false" rtexprvalue="true" %>

<div class="modal fade" id="${id}" name="${name}">
    <div class="modal-dialog ${modalCssClass}">
        <div class="modal-content ${cssClass}">
            <jsp:doBody/>
        </div>
    </div>
</div>
