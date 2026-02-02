<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="name" required="false" rtexprvalue="true" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>
<%@ attribute name="href" required="false" rtexprvalue="true"%>

<%@ attribute name="ajaxLoad" required="false" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ attribute name="ajaxAnchor" required="false" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ attribute name="ajaxUrl" required="false" rtexprvalue="true"%>
<%@ attribute name="ajaxUrlParams" required="false" rtexprvalue="true" type="java.lang.String"%>
<%@ attribute name="ajaxUrlVars" required="false" rtexprvalue="true" type="java.lang.String"%>

<li role="presentation" class="${cssClass}">
    <a
            id="${id}"
            name="${name}"
            <c:if test="${ajaxLoad}">
                data-ajax-load="${ajaxLoad}"
                data-ajax-url-tmpl="${ajaxUrl}"
                data-ajax-url-params="${ajaxUrlParams}"
                data-ajax-url-vars="${ajaxUrlVars}"
                data-ajax-anchor="${ajaxAnchor}"
            </c:if>
            href="${href}">
        <jsp:doBody/>
    </a>
</li>
