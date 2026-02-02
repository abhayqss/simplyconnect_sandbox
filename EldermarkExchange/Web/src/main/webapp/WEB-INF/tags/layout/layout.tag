<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="role" required="false" rtexprvalue="true" %>
<%@ attribute name="name" required="false" rtexprvalue="true" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>
<%@ attribute name="style" required="false" rtexprvalue="true" type="java.lang.String" %>

<%@ attribute name="ajaxLoad" required="false" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ attribute name="ajaxAnchor" required="false" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ attribute name="ajaxUrl" required="false" rtexprvalue="true"%>
<%@ attribute name="ajaxUrlParams" required="false" rtexprvalue="true" type="java.lang.String"%>
<%@ attribute name="ajaxUrlVars" required="false" rtexprvalue="true" type="java.lang.String"%>
<%@ attribute name="data" required="false" rtexprvalue="true" type="java.lang.String"%>

<div id="${id}"
     name="${name}"
     role="${role}"
     style="${style}"
        <c:if test="${ajaxLoad}">
            data-ajax-load="${ajaxLoad}"
            data-ajax-url-tmpl="${ajaxUrl}"
            data-ajax-url-params="${composeMsgUrl}"
            data-ajax-url-vars="${ajaxUrlVars}"
            data-ajax-anchor="${ajaxAnchor}"
        </c:if>
     class="ldr-ui-layout ${cssClass}">
    <jsp:doBody/>
</div>
