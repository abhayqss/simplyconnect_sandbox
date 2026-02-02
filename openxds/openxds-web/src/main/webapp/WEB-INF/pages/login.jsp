<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <link rel="stylesheet" href="<c:url value='/css/login.css'/>" type="text/css" />
</head>
<body>

<h1>OpenXDS Web</h1>

<div id="login">
    <c:url var='j_spring_security_check' value='/j_spring_security_check'/>
    <div>
        Use your Exchange credentials to log in to the page.
    </div>

    <form name='loginForm' action="${j_spring_security_check}" method='POST'>
        <table>
            <tr>
                <td>Company:</td>
                <td><input type='text' name='company' value=''></td>
            </tr>
            <tr>
                <td>Username:</td>
                <td><input type='text' name='username' value=''></td>
            </tr>
            <tr>
                <td>Password:</td>
                <td><input type='password' name='password'/></td>
            </tr>
            <tr>
                <td><input name="submit" type="submit" value="Log In"/></td>
            </tr>
        </table>

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
    </form>
</div>
</body>
</html>