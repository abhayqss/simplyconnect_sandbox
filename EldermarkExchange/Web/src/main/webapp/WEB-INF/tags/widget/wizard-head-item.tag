<%@ tag pageEncoding="UTF-8" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="linkCssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="href" required="true" rtexprvalue="true" %>
<%@ attribute name="linkId" required="false" rtexprvalue="true" %>

<li class="${cssClass}">
    <a id="${linkId}" href="${href}" class="${linkCssClass}" data-toggle="tab">
        <jsp:doBody/>
    </a>
</li>
