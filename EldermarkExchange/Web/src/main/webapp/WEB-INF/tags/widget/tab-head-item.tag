<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
 <%-- html attributes --%>
<%@ attribute name="name" required="false" rtexprvalue="true" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>
<%@ attribute name="disabled" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@ attribute name="href" required="true" rtexprvalue="true" type="java.lang.String" %>
<%-- if true, then content will be loaded by Ajax --%>
<%@ attribute name="ajax" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@ attribute name="ajaxUrlLoad" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%-- Id of HTML element for loaded content --%>
<%@ attribute name="target" required="false" rtexprvalue="true" %>
<%-- if true then current active tab --%>
<%@ attribute name="active" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%-- If true show additional information in right corner --%>
<%@ attribute name="badge" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@ attribute name="badgeValue" required="false" rtexprvalue="true" type="java.lang.String" %>

<%@ attribute name="ajaxUrlTemplate" required="false" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="ajaxUrlVars" required="false" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="ajaxUrlParams" required="false" rtexprvalue="true" type="java.lang.String" %>


<li id="${id}" role="presentation" class="<c:if test='${active}'>active</c:if> ${cssClass} <c:if test='${disabled}'>disabled</c:if>" >
    <a href="${href}"
            <c:choose>
                <c:when test="${ajax}">
                    data-toggle="ajaxtab"
                    data-target="${target}"
                    data-ajax-load="${ajaxUrlLoad}"
                    data-ajax-url-tmpl="${ajaxUrlTemplate}"
                    data-ajax-url-vars="${ajaxUrlVars}"
                    data-ajax-url-params="${ajaxUrlParams}"
                </c:when>
                <c:otherwise>data-toggle="tab"</c:otherwise>
            </c:choose>
       <c:if test='${active}'>aria-expanded="true"</c:if>
       aria-controls="${fn:replace(href, "#", "")}"
       role="tab">
        <jsp:doBody/>
        <c:if test="${badge}">
            <span class="badge <c:if test="${!emptyBadge && (badgeValue == null || badgeValue == 0)}">hidden</c:if>">
                  <span class="bracket">(</span>
                  <span class="badgeValue">${badgeValue}</span>
                  <span class="bracket">)</span>
             </span>
        </c:if>
    </a>
</li>
