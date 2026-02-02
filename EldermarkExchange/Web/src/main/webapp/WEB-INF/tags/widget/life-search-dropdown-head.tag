<%@ taglib prefix="wgForm" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>

<%@ tag pageEncoding="UTF-8" %>

<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="name" required="false" rtexprvalue="true" %>
<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ attribute name="path" required="true" rtexprvalue="true" %>
<%@ attribute name="placeholder" required="false" rtexprvalue="true" %>
<%@ attribute name="disabled" required="false" rtexprvalue="true" %>
<%@ attribute name="options" required="false" rtexprvalue="true" type="java.util.Collection" %>
<%@ attribute name="autocomplete" required="false" rtexprvalue="true" %>
<c:url value="/resources/images/close.svg" var="deleteText"/>


<%-- ======= showing input instead of select ======== --%>
<wgForm:input path=""
              id="${id}_searchInput"
              cssClass="${cssClass}"
              name="${name}_searchInput"
              data-target="#"
              data-toggle="dropdown"
              aria-haspopup="true"
              aria-expanded="false"
              placeholder="${placeholder}"
              autocomplete="${autocomplete}"
              disabled="${disabled}"/>
<wg:icon cssClass="glyphicon-search"/>
<wg:img src="${deleteText}" cssClass="delete-text"/>

<%-- ======= hiding select =======--%>
<div class="bootstrap-select">
    <wgForm:select path="${path}"
                   id="${id}"
                   cssClass=""
                   name="${name}"
                   disabled="${disabled}">
        <c:forEach var="item" items="${options}">
            <wgForm:option value="${item.id}" label="${item.displayName}"/>
        </c:forEach>
    </wgForm:select>
</div>

