<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="patient.info.documents.searchBtn" var="searchBtn"/>


<c:url value="/patient-info/" var="patientInfoUrl"/>
<c:set value="patient-info/{residentId}/compose" var="composeMsgUrl"/>
<input type="hidden" value="${aggregated}" id="aggregatedRecord"/>

<c:set value="${patientInfoUrl}${residentId}/documents/${aggregated}/results?hashKey=${hashKey}&databaseId=${databaseId}" var="documentsUrl"/>

<lt:layout cssClass="documents">
    <lt:layout id="documentSearchError" cssClass="alert alert-warning hidden"/>
    <c:if test="${searchScope == 'ELDERMARK'}">

        <lt:layout cssClass="documentFilter">
            <wgForm:form commandName="documentFilter" cssClass="documentFilterForm" id="documentFilterForm">
                <wgForm:input path="documentTitle" cssClass="documentTitle form-control" cssStyle="display: inline" id="documentTitle"/>
                <wg:button domType="button" cssClass="btn-primary docSearchBtn" id="docSearchBtn">
                    ${searchBtn}
                </wg:button>
            </wgForm:form>
        </lt:layout>
    </c:if>
    <c:choose>
        <c:when test="${aggregated}">
            <c:set value="select,documentTitle,authorPerson,documentType,size,creationTime,dataSource" var="columnIds"/>
            <c:set value="$,Title,Author,Type,Size,Created On, Data Source" var="columnNames"/>
            <c:set value="checkbox,select,string,string,filesize,string,custom" var="columnFormats"/>
        </c:when>
        <c:otherwise>
            <c:set value="select,documentTitle,authorPerson,documentType,size,creationTime" var="columnIds"/>
            <c:set value="$,Title,Author,Type,Size,Created On" var="columnNames"/>
            <c:set value="checkbox,select,string,string,filesize,string" var="columnFormats"/>
        </c:otherwise>
    </c:choose>
    <wg:grid id="documentList"
             colIds="${columnIds}"
             colNames="${columnNames}"
             colFormats="${columnFormats}"
             dataUrl="${documentsUrl}"/>

    <spring:message code="button.delete" var="deleteBtn"/>
    <spring:message code="button.attach.file" var="attachFileBtn"/>
    <spring:message code="button.composeMessage" var="composeMsgBtn"/>
    <spring:message code="button.download" var="downloadBtn"/>

    <lt:layout cssClass="documentListBtnsBox">
        <c:if test="${searchScope == 'ELDERMARK'}">
            <lt:layout cssClass="leftBtnBox">
                <lt:layout cssClass="table-box">
                    <%--<wg:button domType="link" cssClass="btn btn-lg btn-default deleteBtn table-cell-box" badge="true" badgeValue="0"--%>
                               <%--id="deleteDocsBtn">--%>
                        <%--<lt:layout cssClass="glyphicon glyphicon-remove"/>--%>
                        <%--${deleteBtn}--%>
                    <%--</wg:button>--%>
                    <%--<lt:layout cssClass="btn-strut-lg  table-cell-box"></lt:layout>--%>
                    <wg:button domType="link"
                               cssClass="btn btn-lg btn-default attachFileBtn table-cell-box"
                               id="attachFileBtn"
                               dataToggle="modal"
                               dataTarget="#uploadDocumentModal">
                        <lt:layout cssClass="glyphicon glyphicon-paperclip"/>
                        ${attachFileBtn}
                    </wg:button>
                </lt:layout>
            </lt:layout>
        </c:if>
        <lt:layout cssClass="rightBtnBox text-right">
            <lt:layout cssClass="inline-table-box">
                <c:if test="${showMessageCompose}">
                    <wg:button domType="link"
                               linkHref="#${composeMsgUrl}"
                               ajaxUrl="${composeMsgUrl}"
                               ajaxLoad="false"
                               ajaxUrlVars="residentId=${residentId}"
                               ajaxUrlParams="hashKey=${hashKey}&databaseId=${databaseId}"
                               cssClass="btn btn-lg btn-primary composeMsgBtn table-cell-box" badge="true" badgeValue="0"
                               id="${not empty param.composeBtnName ? param.composeBtnName : 'composeMsgBtn'}">
                        <lt:layout cssClass="glyphicon glyphicon-edit"/>
                        ${composeMsgBtn}
                    </wg:button>
                </c:if>
                <lt:layout cssClass="btn-strut-lg table-cell-box"></lt:layout>
                <wg:button domType="link" cssClass="btn btn-lg btn-primary downloadBtn table-cell-box" id="downloadDocsBtn"  badge="true" badgeValue="0">
                    <lt:layout cssClass="glyphicon glyphicon-download-alt"/>
                    ${downloadBtn}
                </wg:button>
            </lt:layout>
        </lt:layout>
    </lt:layout>

    <lt:layout id="documentDataSourceDetailsTemplate" cssClass="datasourcePreview hidden">
        <lt:layout id="dataSourceNameLayout">
            <wg:label cssClass="text">Organization Name</wg:label>
            <wg:label id="dataSourceName" cssClass="value"></wg:label>
        </lt:layout>
        <lt:layout id="dataSourceOIDLayout">
            <wg:label cssClass="text">Organization OID</wg:label>
            <wg:label id="dataSourceOID" cssClass="value"></wg:label>
        </lt:layout>
        <lt:layout id="communityNameLayout">
            <wg:label cssClass="text">Community Name</wg:label>
            <wg:label id="communityName" cssClass="value"></wg:label>
        </lt:layout>
        <lt:layout id="communityOIDLayout">
            <wg:label cssClass="text">Community OID</wg:label>
            <wg:label id="communityOID" cssClass="value"></wg:label>
        </lt:layout>

    </lt:layout>
</lt:layout>
