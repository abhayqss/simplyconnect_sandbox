<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="secure.msg.inbox.head.label" var="headLabel"/>
<spring:message code="button.delete" var="deleteBtn"/>
<spring:message code="button.composeMessage" var="composeMsgBtn"/>
<c:url value="/resources/images/sheet.png" var="sheetImgUrl"/>

<c:set value="secure-messaging/compose" var="composeUrl"/>

<spring:message code="secure.msg.details.from" var="from"/>
<spring:message code="secure.msg.details.date" var="date"/>


<lt:layout cssClass="msgListBox">
    <lt:layout cssClass="boxHeader panel panel-primary">
        <lt:layout cssClass="panel-heading">
            ${headLabel}
        </lt:layout>
    </lt:layout>
    <lt:layout cssClass="boxBody">
        <lt:layout cssClass="msgListBtnBox">
            <lt:layout cssClass="text-left display-inline">
                <wg:button domType="button"
                           cssClass="btn btn-lg btn-default deleteMsgsBtn"
                           badge="true" badgeValue="0" id="deleteMsgsBtn">
                    <lt:layout cssClass="glyphicon glyphicon-remove"/>
                    ${deleteBtn}
                </wg:button>
            </lt:layout>
            <lt:layout cssClass="pull-right text-right display-inline">
                <wg:link href="#${composeUrl}" cssClass="btn btn-primary composeMsgBtn" id="composeMsgBtn"
                         ajaxLoad="true"
                         ajaxUrl="${composeUrl}" >
                    <lt:layout cssClass="glyphicon glyphicon-edit"/>
                    ${composeMsgBtn}
                </wg:link>
            </lt:layout>
        </lt:layout>
        <wg:grid
                id="msgList" cssClass="msgList"
                colIds="select,subject,from,date"
                colNames="Select,Subject,From,Date"
                dataUrl="secure-messaging/results"
                colFormats="checkbox,string,string,string"/>
    </lt:layout>
</lt:layout>

<!-- Message Details Modal -->
<wg:modal id="msgDetailModal" cssClass="msgDetail" modalCssClass="modal-lg"/>