<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
    <head>
        <link href="<c:url value="/resources/css/standard.css"/>" rel="stylesheet"  type="text/css" />
        <script src="//code.jquery.com/jquery-1.10.2.js"></script>
        <script src="https://ajax.aspnetcdn.com/ajax/jquery.validate/1.13.1/jquery.validate.min.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/login.js"></script>
    </head>

    <h1>ADT Messages Sender</h1>

    <div id="login">
        <c:url var='j_spring_security_check' value='/j_spring_security_check'/>

        <form:form commandName="loginForm" id ='loginForm' action="${j_spring_security_check}" method='POST'>

            <c:if test="${param.error}">
                <div class="msg">
                    <span class="highlighted"><c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/></span>
                </div>
            </c:if>
            <c:if test="${param.logout}">
                <div class="msg">
                    <span class="highlighted">You have been logged out.</span>
                </div>
            </c:if>

            <div class="centered-content">
                <form:input type='text' name='company' path='company' placeholder="Company"/>
                <form:input type='text' name='username' path='username' placeholder="Username"/>
                <form:input type='password' name='password' path='password' placeholder="Password"/>

                <input name="submit" type="submit" class="button" id ="loginButton" value="Log In" />

                <div>
                    Use your Exchange credentials to log in to the page.
                </div>
            </div>
        </form:form>
        </div>
    </body>
</html>