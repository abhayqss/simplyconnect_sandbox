<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="name" required="false" rtexprvalue="true" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>
<%@ attribute name="href" required="false" rtexprvalue="true" %>
<%@ attribute name="openInPopup" required="false" rtexprvalue="true" type="java.lang.Boolean" %>

<%@ attribute name="ajaxLoad" required="false" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ attribute name="ajaxUrl" required="false" rtexprvalue="true"%>
<%@ attribute name="ajaxUrlParams" required="false" rtexprvalue="true" type="java.lang.String"%>
<%@ attribute name="ajaxUrlVars" required="false" rtexprvalue="true" type="java.lang.String"%>
<%@ attribute name="ajaxAnchor" required="false" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ attribute name="toggle" required="false" rtexprvalue="true" type="java.lang.String"%>

<a
        id="${id}"
        class="ldr-ui-label ${cssClass}"
        data-ajax-load="${ajaxLoad}"
        data-ajax-url-tmpl="${ajaxUrl}"
        data-ajax-url-params="${ajaxUrlParams}"
        data-ajax-url-vars="${ajaxUrlVars}"
        data-ajax-anchor="${ajaxAnchor}"
        <c:if test="${not empty ajaxUrl}">onclick="return false;"</c:if>
        <c:if test="${not empty toggle}">data-toggle="${toggle}"</c:if>
        <c:if test="${openInPopup and not empty href}">
            onclick="window.open('${href}', '_blank', 'resizable=no,width=500,height=600'); return false;"
        </c:if>
        <c:if test="${not openInPopup and not empty href}">href="${href}"</c:if>
        >
    <jsp:doBody/>
</a>