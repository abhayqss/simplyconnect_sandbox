<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="panelHeaderId" required="false" rtexprvalue="true" %>
<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="panelCssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="name" required="false" rtexprvalue="true" %>
<%@ attribute name="clpHeaderText" required="true" rtexprvalue="true" %>
<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ attribute name="disabled" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@ attribute name="expanded" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@ attribute name="itemList" required="false" rtexprvalue="true" type="java.util.List"%>


<div class="${cssClass}">
    <div id="${panelHeaderId}" class="${panelCssClass} clp-pnl-header <c:if test="${not expanded}">collapsed </c:if><c:if test="${disabled}">disabled</c:if>"
         data-toggle="collapse"
            <c:choose>
                <c:when test="${expanded}">
                    aria-expanded="true"
                </c:when>
                <c:otherwise>
                    aria-expanded="false"
                </c:otherwise>
            </c:choose>
         href="#${id}"
         aria-controls="collapseExample">
                <div class="clp-pnl-header-lt gray simple-clp">
                    <label class="collapsed-pnl-head text-center pnl-clp-head-lbl simple-clp-header">${clpHeaderText}</label>
                    <label class="collapsed-pnl-head text-center pnl-exp-head-lbl simple-clp-header">${clpHeaderText}</label>
                    <a class="ldr-ui-toggle-btn"></a>
                </div>
    </div>
    <div class="collapse<c:if test="${expanded}"> in</c:if>" id="${id}" name="${name}" href="${href}">
        <div>
            <c:if test="${itemList!=null}">
                <div id="collapsedItemList" >
                    <c:forEach var="item" items="${itemList}">
                        <div class="simple-clp-item">${item}</div>
                    </c:forEach>
                </div>
            </c:if>
            <jsp:doBody/>
        </div>
    </div>
</div>
