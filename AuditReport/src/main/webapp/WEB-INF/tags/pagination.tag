<%@ tag pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%@ attribute name="baseUrl" required="true" rtexprvalue="true" %>
<%@ attribute name="totalPages" required="true" rtexprvalue="true" %>
<%@ attribute name="currentIndex" required="true" rtexprvalue="true" %>

<c:url var="firstUrl" value="/${baseUrl}/1"/>
<c:url var="lastUrl" value="/${baseUrl}/${totalPages}"/>
<c:url var="prevUrl" value="/${baseUrl}/${currentIndex - 1}"/>
<c:url var="nextUrl" value="/${baseUrl}/${currentIndex + 1}"/>

<c:choose>
    <c:when test="${currentIndex == 1}">
        <form:button class="page disabled" disabled="true">First</form:button>
        <form:button class="page disabled" disabled="true">Prev</form:button>
    </c:when>
    <c:otherwise>
        <form:button class="page" onclick="form.action='${firstUrl}';">First</form:button>
        <form:button class="page" onclick="form.action='${prevUrl}';">Prev</form:button>
    </c:otherwise>
</c:choose>
<c:forEach var="i" begin="${beginIndex}" end="${endIndex}">
    <c:url var="pageUrl" value="/${baseUrl}/${i}"/>
    <c:choose>
        <c:when test="${i == currentIndex}">
            <form:button class="page active" onclick="form.action='${pageUrl}';">
                <c:out value='${i}'/>
            </form:button>
        </c:when>
        <c:otherwise>
            <form:button class="page" onclick="form.action='${pageUrl}';">
                <c:out value='${i}'/>
            </form:button>
        </c:otherwise>
    </c:choose>
</c:forEach>
<c:choose>
    <c:when test="${currentIndex == totalPages}">
        <form:button class="page disabled" disabled="true">Next</form:button>
        <form:button class="page disabled" disabled="true">Last (<c:out value='${totalPages}'/>)</form:button>
    </c:when>
    <c:otherwise>
        <form:button class="page" onclick="form.action='${nextUrl}';">Next</form:button>
        <form:button class="page" onclick="form.action='${lastUrl}';">Last (<c:out value='${totalPages}'/>)</form:button>
    </c:otherwise>
</c:choose>