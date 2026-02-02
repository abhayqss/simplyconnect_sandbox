<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="headerCssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="name" required="false" rtexprvalue="true" %>
<%@ attribute name="clpHeaderText" required="true" rtexprvalue="true" %>
<%@ attribute name="expHeaderText" required="true" rtexprvalue="true" %>

<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ attribute name="panelHeaderId" required="false" rtexprvalue="true" %>

<%@ attribute name="theme" required="false" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="ajax" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@ attribute name="target" required="false" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="href" required="false" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="disabled" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@ attribute name="expanded" required="false" rtexprvalue="true" type="java.lang.Boolean" %>

<%@ attribute name="badge" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@ attribute name="badgeValue" required="false" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="emptyBadge" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@ attribute name="itemList" required="false" rtexprvalue="true" type="java.util.List"%>


<div class="${cssClass}">
    <div id="${panelHeaderId}" class="clp-pnl-header <c:if test="${not expanded}">collapsed </c:if><c:if test="${disabled}">disabled</c:if>"
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
            <%-- <c:choose>
                <c:when test="${href!=null}">
                    href="${href}"
                </c:when>
                <c:otherwise>
                    href="#${id}"
                </c:otherwise>
            </c:choose> --%>
            <c:if test="${ajax}">
                data-target="${target}"
            </c:if>
         aria-controls="collapseExample">
        <c:choose>
            <c:when test="${theme eq 'gray'}">
                <div class="clp-pnl-header-lt gray" >
                    <c:choose>
                        <c:when test="${not empty headerCssClass}">
                            <label class="${headerCssClass} text-center pnl-clp-head-lbl">${clpHeaderText}</label>
                            <label class="${headerCssClass} text-center pnl-exp-head-lbl">${expHeaderText}</label>
                        </c:when>
                        <c:otherwise>
                            <label class="collapsed-pnl-head text-center pnl-clp-head-lbl">${clpHeaderText}</label>
                            <label class="collapsed-pnl-head text-center pnl-exp-head-lb1">${expHeaderText}</label>
                        </c:otherwise>
                    </c:choose>

                    <c:if test="${badge}">
                            <span class="badge <c:if test="${!emptyBadge && (badgeValue == null || badgeValue == 0)}">hidden</c:if>">
                                <span class="bracket">(</span>
                                <span class="badgeValue">${badgeValue}</span>
                                <span class="bracket">)</span>
                            </span>
                    </c:if>
                    <a class="ldr-ui-toggle-btn"></a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="clp-pnl-header-lt text-center green">
                    <a class="ldr-ui-toggle-btn"></a>
                    <c:choose>
                        <c:when test="${not empty headerCssClass}">
                            <label class="${headerCssClass} text-center pnl-clp-head-lbl">${clpHeaderText}</label>
                            <label class="${headerCssClass} text-center pnl-exp-head-lb1">${expHeaderText}</label>
                        </c:when>
                        <c:otherwise>
                            <label class="collapsed-pnl-head text-center pnl-clp-head-lbl">${clpHeaderText}</label>
                            <label class="collapsed-pnl-head text-center pnl-exp-head-lb1">${expHeaderText}</label>
                        </c:otherwise>
                    </c:choose>
                    <a class="ldr-ui-toggle-btn"></a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="collapse<c:if test="${expanded}"> in</c:if>" id="${id}" name="${name}" href="${href}">
        <div class="clp-pnl-content">
            <%--<c:if test="${itemList!=null}">--%>
                <%--<div id="collapsedItemList">--%>
                    <%--<c:forEach var="item" items="${itemList}">--%>
                        <%--<div>${item}</div>--%>
                    <%--</c:forEach>--%>
                <%--</div>--%>
            <%--</c:if>--%>
            <jsp:doBody/>
        </div>
    </div>
</div>
