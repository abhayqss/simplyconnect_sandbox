<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<%@ page import=" com.scnsoft.eldermark.entity.password.PasswordSettingsType" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:url var='j_spring_security_check' value='/j_spring_security_check'/>
<spring:message var="successLogout" code="login.logout.success.message"/>
<spring:message var="loginLabel" code="login.label"/>
<spring:message var="loginHeaderLabel" code="login.header.label"/>
<spring:message var="userName" code="login.label.userName"/>
<spring:message var="password" code="login.label.password"/>
<spring:message var="companyLabel" code="login.label.company"/>
<spring:message var="loginButton" code="login.button"/>

<script language="JavaScript" type="text/javascript"
        src="<c:url value="/resources/js/app/modules/care-coordination/module.change.password.js"/>"></script>

<lt:layout cssClass="loginBox center-block">

    <lt:layout cssClass="boxBody">

        <lt:layout cssClass="loginAlert">
            <div>Your password for</div>
            <div><c:out value="${sessionScope.last_company}"/>, <c:out value="${sessionScope.last_username}"/></div>
            <div>has expired and must be changed.</div>
        </lt:layout>

        <lt:layout id="loginError" cssClass="loginError">
            <c:out value="${errorMsg}"/>
        </lt:layout>

        <wgForm:form commandName="changePasswordDto" id='changePasswordForm' cssClass="form-horizontal loginForm" method="post" action="service/change">

            <wgForm:hidden id="company" path="company" value="${sessionScope.last_company}"/>
            <wgForm:hidden id="username" path="username" value="${sessionScope.last_username}"/>

            <wgForm:input id="password" type='password' name='password' path="password" placeholder="Old Password"
                          align="center" cssClass="login-input" />

            <wgForm:input id="newPassword" type='password' name='newPassword' path="newPassword" placeholder="New Password"
                          align="center" cssClass="login-input"/>

            <lt:layout cssClass="">
                <wgForm:input id="confirmNewPassword" type='password' name='confirmNewPassword' path="confirmNewPassword" placeholder="Confirm Password"
                              align="center" cssClass="login-input"/>
            </lt:layout>

            <jsp:include page="passwordRequirements.jsp">
                <jsp:param name="cssClass" value="passwordHelpChange"/>
            </jsp:include>

            <wg:button type="submit" name="submitChangePasswordForm" id="changePasswordBtn" domType="input" value="CHANGE PASSWORD"
                       cssClass="loginButton btn-lg btn-primary"/>
        </wgForm:form>
        <div><a href="<c:url value='/service/resetRequest'/>">Forgot Password?</a> </div>
    </lt:layout>
</lt:layout>