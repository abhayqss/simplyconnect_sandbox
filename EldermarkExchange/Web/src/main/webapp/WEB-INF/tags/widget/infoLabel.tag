<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>

<%@ attribute name="_for" required="false" rtexprvalue="true" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>
<%@ attribute name="text" required="false" rtexprvalue="true" %>
<%@ attribute name="cssClass" required="false" rtexprvalue="true"%>
<%@ attribute name="data_content" required="false" rtexprvalue="true" %>



<wg:label _for="${_for}" cssClass="documentLbl ${cssClass}">
    ${text}
</wg:label>
<a href="#" class="help-icon" id="${id}" data-toggle="popover"
   data-trigger="hover" data-html="true"
   data-content='${data_content}'>
</a>
