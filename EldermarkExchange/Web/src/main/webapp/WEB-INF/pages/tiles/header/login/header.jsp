<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<c:url value="/resources/images/logo.svg" var="logoImgUrl"/>
<lt:layout cssClass="headerBorderLine"/>
<lt:layout cssClass="ldr-center-block loginHeader">
    <lt:layout cssClass="table-row-box">
        <lt:layout cssClass="logo table-cell-box">
            <wg:img src="${logoImgUrl}"/>
        </lt:layout>
    </lt:layout>
</lt:layout>