<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:message code="button.deleteMessage" var="deleteMsgBtn"/>
<spring:message code="button.composeMessage" var="composeMsgBtn"/>
<spring:message code="button.replyTo" var="replyMsgBtn"/>
<c:url value="/resources/images/sheet.png" var="sheetImgUrl"/>

<c:set value="secure-messaging/{messageId}/reply-to" var="replyUrl"/>

<spring:message code="secure.msg.details.from" var="from"/>
<spring:message code="secure.msg.details.date" var="date"/>
<spring:message code="secure.msg.details.to" var="to"/>

<wg:modal-header closeBtn="true">
    ${message.subject}
</wg:modal-header>

<wg:modal-body>
    <div id="msgDetails" data-message-id="${message.messageId}" class="ldr-ui-layout msgDetails">
        <lt:layout cssClass="textItem">
            <wg:label id="from" cssClass="text">${from}</wg:label>
            <wg:label cssClass="value">${message.from}</wg:label>
        </lt:layout>
        <lt:layout cssClass="textItem">
            <wg:label id="to" cssClass="text">${to}</wg:label>
            <wg:label cssClass="value">${message.toAsString}</wg:label>
        </lt:layout>
        <lt:layout cssClass="textItem">
            <wg:label id="date" cssClass="text">${date}</wg:label>
            <wg:label cssClass="value">${message.date}</wg:label>
        </lt:layout>
        <lt:layout cssClass="textItem">
            <lt:layout id="description" cssClass="value">
                ${message.body}
            </lt:layout>
        </lt:layout>

        <c:forEach var="attachment" items="${message.attachments}">
            <lt:layout cssClass="lnkItem">
                <wg:link href="secure-messaging/${attachment.messageId}/attachment?partIndex=${attachment.partIndex}"
                         cssClass="downloadDocLnk">
                    <wg:img cssClass="lnk-img" src="${sheetImgUrl}"/>
                    ${attachment.name}
                </wg:link>
            </lt:layout>
        </c:forEach>
        <div style="clear:both"></div>
    </div>

    <lt:layout cssClass="msgDetailsBtns">
        <lt:layout cssClass="text-left display-inline">
            <wg:button domType="button"
                       cssClass="btn btn-lg btn-default deleteBtn" id="deleteMsgBtn">
                <lt:layout cssClass="glyphicon glyphicon-remove"/>
                ${deleteMsgBtn}
            </wg:button>
        </lt:layout>
        <lt:layout cssClass="pull-right text-right display-inline">
            <wg:button domType="link"
                       linkHref="#${replyUrl}"
                       ajaxUrl="${replyUrl}"
                       ajaxLoad="true"
                       ajaxUrlVars="messageId=${message.messageId}"
                       cssClass="btn btn-primary replyMsgBtn"
                       id="replyMsgBtn">
                <lt:layout cssClass="glyphicon glyphicon-edit"/>
                ${replyMsgBtn}
            </wg:button>
        </lt:layout>
    </lt:layout>
</wg:modal-body>


