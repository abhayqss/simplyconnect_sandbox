<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<c:url var='j_spring_security_check' value='/j_spring_security_check'/>
<spring:message var="successLogout" code="login.logout.success.message"/>
<spring:message var="loginLabel" code="login.label"/>
<spring:message var="loginHeaderLabel" code="login.header.label"/>
<spring:message var="userName" code="login.label.userName"/>
<spring:message var="password" code="login.label.password"/>
<spring:message var="company" code="login.label.company"/>
<spring:message var="loginButton" code="login.button"/>

<h4>Access is denied</h4>
<h5>You do not have enough permission to access the selected resource. Please contact your administrator.</h5>