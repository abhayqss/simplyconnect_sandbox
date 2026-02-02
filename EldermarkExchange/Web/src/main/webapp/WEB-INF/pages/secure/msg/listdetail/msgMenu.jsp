<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<lt:layout cssClass="secureMsgMenuBox panel panel-primary">
    <wg:nav type="vertical">
        <c:forEach var="item" items="${messageTypeValues}">
            <wg:nav-item href="#${fn:toLowerCase(item)}" cssClass="${msgFilter.messageType == item ? 'active secureMsgMenuBoxItem' : 'secureMsgMenuBoxItem'}">
                <lt:layout cssClass="folderIcon"/>
                <spring:message code="secure.msg.menu.${fn:toLowerCase(item)}" var="navigationLabel"/>
                <c:out value="${navigationLabel}"/>
            </wg:nav-item>
        </c:forEach>
    </wg:nav>

    <wgForm:form commandName="msgFilter" method="post" cssClass="hidden" id="msgFilterForm">
        <wgForm:select id="messageType" name="messageType"  path="messageType">
            <c:forEach var="item" items="${messageTypeValues}">
                <wgForm:option value="${item}" label=""/>
            </c:forEach>
        </wgForm:select>
    </wgForm:form>
</lt:layout>
