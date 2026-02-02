<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="_for" required="false" rtexprvalue="true" %>
<%@ attribute name="path" required="false" rtexprvalue="true" %>
<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="name" required="false" rtexprvalue="true" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>

<label
        <c:if test="${not empty id}">id="${id}"</c:if>
        <c:if test="${not empty _for}">for="${_for}"</c:if>
        class="ldr-ui-label ${cssClass}">
    <jsp:doBody/>
</label>
