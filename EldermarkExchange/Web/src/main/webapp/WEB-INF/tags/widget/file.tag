<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="name" required="false" rtexprvalue="true" %>
<%@ attribute name="path" required="false" rtexprvalue="true" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>
<%@ attribute name="buttonText" required="false" rtexprvalue="true"%>
<%@ attribute name="icon" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@ attribute name="accept" required="false" rtexprvalue="true" %>
<%@ attribute name="dataPlaceholder" required="false" rtexprvalue="true" %>
<%@ attribute name="value" required="false" rtexprvalue="true" %>


<input
        type="file"
        class="${cssClass}"
        id="${id}"
        name="${name}"
        data-buttonText="${buttonText}"
        accept="${accept}"
        data-placeholder="${dataPlaceholder}"
        <c:choose>
            <c:when test="${icon}">
                data-icon="true"
            </c:when>
            <c:otherwise>
                data-icon="false"
            </c:otherwise>
        </c:choose>
        value="${value}"
        />
