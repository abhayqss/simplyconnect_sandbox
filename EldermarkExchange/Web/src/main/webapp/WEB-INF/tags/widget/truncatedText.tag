<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@ taglib uri="simply-connect-taglib" prefix="sc" %>
<%@ tag pageEncoding="UTF-8" %>

<%@ attribute name="maxSymbolNumber" type="java.lang.Integer" required="true" rtexprvalue="true" %>

<jsp:doBody var="wholeBody"  />
<c:set var="wholeBody" value="${sc:replaceAllSpaces(wholeBody)}"/>

<c:choose>
    <c:when test="${fn:length(wholeBody)< maxSymbolNumber}">
        ${wholeBody}
    </c:when>
    <c:otherwise>
        ${sc:truncateWords(wholeBody, maxSymbolNumber)} ...
    </c:otherwise>
</c:choose>
