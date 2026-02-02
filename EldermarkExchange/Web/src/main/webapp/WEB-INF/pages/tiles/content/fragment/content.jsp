<%@ page import="com.scnsoft.eldermark.authentication.SecurityExpressions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="markup-fragment-container">
	<tiles:insertAttribute name="appLayout"/>
	<tiles:insertAttribute name="resources"/>
</div>

