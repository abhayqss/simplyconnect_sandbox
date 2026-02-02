<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="name" required="false" rtexprvalue="true" %>
<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ attribute name="active" required="false" rtexprvalue="true" type="java.lang.Boolean"%>

<div id="${id}" name="${name}" role="tabpanel" class="tab-pane ${cssClass}<c:if test='${active}'> active</c:if>">
    <jsp:doBody/>
</div>
