<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<lt:layout cssClass="msgConfigWarningBox ldr-center-block">

    <lt:layout cssClass="boxHeader panel panel-primary">
        <lt:layout cssClass="panel-heading">
            Secure Messaging
        </lt:layout>
    </lt:layout>

    <lt:layout cssClass="boxBody">
        <lt:layout cssClass="ldr-details-pnl">
            <wg:label>
                Your Secure Messaging account has not been set up yet.
                <c:choose>
                    <c:when test="${isReadyForActivation}">
                        Please activate it.
                    </c:when>
                    <c:otherwise>
                        Please contact your manager.
                    </c:otherwise>
                </c:choose>
            </wg:label>
            <c:if test="${isReadyForActivation}">
                <wg:button domType="button"
                           cssClass="btn-primary display-block" id="activateSesBtn">
                    <lt:layout cssClass="glyphicon glyphicon-off"/>
                    Activate your Secure Messaging
                </wg:button>
            </c:if>
        </lt:layout>
    </lt:layout>
</lt:layout>
