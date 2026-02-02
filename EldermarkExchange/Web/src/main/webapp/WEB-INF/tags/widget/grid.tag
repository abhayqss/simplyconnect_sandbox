<%@ tag pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="name" required="false" rtexprvalue="true" %>
<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ attribute name="colIds" required="true" rtexprvalue="true" %>
<%@ attribute name="colNames" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="dataUrl" required="true" rtexprvalue="true" %>
<%@ attribute name="dataRequestMethod" required="false" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="colFormats" required="true" rtexprvalue="true" %>
<%@ attribute name="deferLoading" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@ attribute name="showHeader" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@ attribute name="showFooter" required="false" rtexprvalue="true" type="java.lang.Boolean" %>

<table id="${id}"
       name="${name}"
       class="display ${cssClass}"
       cellspacing="0"
       width="100%"
       data-col-ids="${colIds}"
       data-col-names="${colNames}"
       data-col-formats="${colFormats}"
       data-url="${dataUrl}"
       data-request-method="${dataRequestMethod}"
        <c:if test="${not empty deferLoading}">
            data-defer-loading="${deferLoading}"
        </c:if>
>
    <c:if test="${showHeader != 'false'}">
        <thead>
        <tr>
            <c:forEach var="colName" items="${fn:split(colNames, ',')}">
                <th>${colName}</th>
            </c:forEach>
        </tr>
        </thead>
    </c:if>
    <tfoot>
    <tr>
        <c:forEach var="colName" items="${fn:split(colNames, ',')}">
            <th>${colName}</th>
        </c:forEach>
    </tr>
    </tfoot>
</table>
