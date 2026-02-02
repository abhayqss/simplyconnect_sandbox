<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wgForm" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@ attribute name="cssClass" required="false" rtexprvalue="true" %>
<%@ attribute name="name" required="false" rtexprvalue="true" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>
<%@ attribute name="path" required="false" rtexprvalue="true" %>
<%@ attribute name="planPath" required="false" rtexprvalue="true" %>
<jsp:useBean id="insuranceCarriers" scope="request" type="java.util.List"/>

<input type="text" placeholder="Search" id="searchInput" class="search-input" autocomplete="user-input"/>
<a href="#" class="search-icon" id="searchIcon"></a>
<wgForm:select path="${path}" id="selectSearch" cssClass="form-control spicker search-select"
               title="Select" multiple="true">
    <optgroup id="groupAll">
        <wgForm:option value="0" label="All"/>
        <c:forEach var="item" items="${insuranceCarriers}">
            <wgForm:option data-hidden="true" value="${item.id}" label="${item.label}"/>
        </c:forEach>
        <wgForm:option value="-1" label="None"/>
    </optgroup>
</wgForm:select>
<div id="tableElement">
</div>