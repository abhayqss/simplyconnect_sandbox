<%@ tag pageEncoding="UTF-8" dynamic-attributes="dynamicattrs"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="name" required="false" rtexprvalue="true" %>
<%@ attribute name="href" required="false" rtexprvalue="true" %>
<%@ attribute name="newWindow" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>

<%@ attribute name="ajaxLoad" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@ attribute name="ajaxUrl" required="false" rtexprvalue="true" %>
<%@ attribute name="ajaxUrlParams" required="false" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="ajaxUrlVars" required="false" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="ajaxAnchor" required="false" rtexprvalue="true" type="java.lang.Boolean" %>

<li role="presentation">
    <a <c:if test="${not empty id}">id="${id}"</c:if>
            role="menuitem"
            tabindex="-1"
            href="${href}"
            <c:if test="${newWindow}">
                target="_blank"
            </c:if>
            <c:if test="${ajaxLoad}">
                data-ajax-load="${ajaxLoad}"
                data-ajax-url-tmpl="${ajaxUrl}"
                data-ajax-url-params="${ajaxUrlParams}"
                data-ajax-url-vars="${ajaxUrlVars}"
                data-ajax-anchor="${ajaxAnchor}"
            </c:if>
            class="${cssClass}"
            <c:forEach var="item" items="${dynamicattrs}">
                ${item.key}="${item.value}"
            </c:forEach>
    >
        <jsp:doBody/>
    </a>
</li>
