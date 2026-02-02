<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
<spring:message var="marketPlaceLink" code="login.link.marketplace"/>

<c:url var="arrowRightIcon" value="/resources/images/arrow_right.png"/>


<lt:layout cssClass="loginBox center-block">

    <lt:layout cssClass="boxBody">
        <c:if test="${param.error}">
            <lt:layout cssClass="loginError">
                <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>
            </lt:layout>
            <c:set var="loginParts" value="${fn:split(SPRING_SECURITY_LAST_EXCEPTION.authentication.principal, '/')}" />
            <c:set var="company" value="${loginParts[0]}" />
            <c:set var="username" value="${loginParts[1]}" />
        </c:if>

        <c:if test="${param.logout}">
            <lt:layout cssClass="loginAlert">
                <c:out value="${successLogout}"/>
            </lt:layout>
        </c:if>

        <c:if test="${pwdChanged}">
            <lt:layout cssClass="loginAlert">
                Password has been changed
            </lt:layout>
        </c:if>


        <c:if test="${linkExisting}">
            <div>Please enter the credentials</div>
            <div>associated with your existing account</div>
        </c:if>


        <wgForm:form commandName="loginForm" id='loginForm' action="${j_spring_security_check}" method='POST'
                     cssClass="form-horizontal loginForm">

            <wgForm:hidden path="linkExisting" value="${linkExisting}"/>
            <wgForm:hidden path="token" value="${token}"/>

            <%--<lt:layout cssClass="form-group">--%>
                <%--<wg:label _for="company" cssClass="col-md-4 control-label text-left label-min-width">--%>
                    <%--${company}--%>
                <%--</wg:label>--%>
                <%--<lt:layout cssClass="login-input">--%>
                    <%--<lt:layout cssClass="">--%>
                        <wgForm:input type='text' name='company' path="company" placeholder="${companyLabel}"
                                      align="center" cssClass="login-input" value="${company}"/>
                    <%--</lt:layout>--%>
                <%--</lt:layout>--%>
            <%--</lt:layout>--%>

            <%--<lt:layout cssClass="form-group">--%>
                <%--<wg:label _for="username" cssClass="col-md-4 control-label text-left label-min-width">--%>
                    <%--${loginLabel}--%>
                <%--</wg:label>--%>
                <%--<lt:layout cssClass="login-input">--%>
                    <%--<lt:layout cssClass="">--%>
                        <wgForm:input type='text' name='username' path="username" placeholder="${loginLabel}"
                                      align="center" cssClass="login-input" value="${username}"/>
                    <%--</lt:layout>--%>
                <%--</lt:layout>--%>
            <%--</lt:layout>--%>

            <%--<lt:layout cssClass="form-group">--%>
                <%--<wg:label _for="password" cssClass="col-md-4 control-label text-left label-min-width">--%>
                    <%--${password}--%>
                <%--</wg:label>--%>
                <%--<lt:layout cssClass="login-input">--%>
                    <lt:layout cssClass="">
                        <wgForm:input type='password' name='password' path="password" placeholder="${password}"
                                      align="center" cssClass="login-input"/>
                    </lt:layout>
                <%--</lt:layout>--%>
            <%--</lt:layout>--%>

            <%--<lt:layout cssClass="well loginBtnBox text-center">--%>
                <wg:button type="submit" name="submit" id="loginBtn" domType="input" value="${loginButton}"
                           cssClass="loginButton btn-lg btn-primary"/>
            <%--</lt:layout>--%>
        </wgForm:form>
        <%--<div><a href='<spring:eval expression="@propertyConfigurer.getProperty('reset.password.url')" />'>Forgot Password</a> </div>--%>
        <div>
            <a href="<c:url value='/service/resetRequest'/>">Forgot Password?</a>
        </div>

        <div style="margin-top: 10%">
            <a style="font-weight: normal; text-decoration: none"
               href="<c:url value='https://dev.simplyhie.com/web-portal/marketplace'/>">
                Find Help & Treatment&nbsp;&nbsp;
            </a>
            <img style="vertical-align: baseline" src="<c:url value='/resources/images/arrow_right.png'/>">
        </div>
    </lt:layout>
</lt:layout>