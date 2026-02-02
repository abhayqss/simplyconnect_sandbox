<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="compose.msg.headLabel" var="composingMsgHeaderLabel"/>
<spring:message code="compose.msg.receiver" var="receiver"/>
<spring:message code="compose.msg.subject" var="subject"/>
<spring:message code="compose.msg.message" var="message"/>
<spring:message code="button.attach.file" var="attachFileBtn"/>
<spring:message code="button.attach" var="attachBtn"/>
<spring:message code="button.sendMessage" var="sendMsgBtn"/>
<spring:message code="button.search" var="searchBtn"/>
<spring:message code="button.cancel" var="cancelBtn"/>
<spring:message code="button.ok" var="okBtn"/>
<spring:message code="compose.msg.file.choose.text" var="fileChooseText"/>
<spring:message code="addressbook.headLabel" var="addressBookLabel"/>

<c:url value="${sendMessageUrlTemplate}" var="sendMessageUrl"/>
<c:url value="/secure-messaging/directory" var="accountsDirectoryUrl"/>

<!-- Breadcrumb -->
<tiles:insertAttribute name="breadcrumb"/>

<lt:layout cssClass="composingMsg ldr-center-block">
    <lt:layout cssClass="panel panel-primary">
        <lt:layout cssClass="panel-heading">
            ${composingMsgHeaderLabel}
        </lt:layout>
    </lt:layout>

    <wgForm:form cssClass="msgDetailsForm form-horizontal" enctype="multipart/form-data" commandName="msgDetails" id="msgDetailsForm" method="post" action="${sendMessageUrl}">
        <lt:layout cssClass="msgDetails">

            <c:forEach var="error" items="${errorList}">
                <lt:layout id="msgComposeErrorsBox" cssClass="alert alert-warning">
                    ${error}
                    <lt:layout cssClass="close">
                        <lt:layout>×</lt:layout>
                    </lt:layout>
                </lt:layout>
            </c:forEach>

            <lt:layout cssClass="form-group">
                <wg:label _for="to" cssClass="name control-label col-xs-12 col-sm-2">${receiver}</wg:label>
                <lt:layout cssClass="col-xs-12 col-sm-10 col-lg-7">
                    <wgForm:textarea id="to" rows="3" path="to" name="to" cssClass="form-control"/>
                </lt:layout>

                <lt:layout cssClass="col-xs-12 col-sm-10 col-sm-offset-2 col-lg-offset-0 col-lg-3">
                    <wg:button id="addressBookBtn"
                               domType="link"
                               dataToggle="modal"
                               dataTarget="#addressBookModal"
                               cssClass="directory btn-link">
                        <wg:icon cssClass="glyphicon-book"/>
                        ${addressBookLabel}
                    </wg:button>
                </lt:layout>
            </lt:layout>

            <lt:layout cssClass="form-group">
                <wg:label _for="subject" cssClass="name control-label col-xs-12 col-sm-2">${subject}</wg:label>
                <lt:layout cssClass="col-xs-12 col-sm-10 col-lg-7">
                    <wgForm:input path="subject" name="subject" cssClass="form-control"/>
                </lt:layout>
            </lt:layout>

            <lt:layout cssClass="form-group">
                <wg:label _for="body" cssClass="name control-label col-xs-12 col-sm-2 col-md-2">${message}</wg:label>
                <lt:layout cssClass="col-xs-12 col-sm-10">
                    <wgForm:textarea id="body" rows="8" path="body" name="body" cssClass="form-control"/>
                </lt:layout>
            </lt:layout>

        </lt:layout>

        <lt:layout cssClass="msgDetailsBtnBox">
            <lt:layout cssClass="text-left display-inline">
                <wg:button domType="link"
                           cssClass="btn btn-default deleteBtn"
                           id="cancelBtn">
                    ${cancelBtn}
                </wg:button>
            </lt:layout>

            <lt:layout cssClass="pull-right text-right display-inline">
                <wg:button domType="link"
                           cssClass="btn btn-default attachFileBtn"
                           dataToggle="modal"
                           dataTarget="#uploadFileModal"
                           id="attachFileBtn">
                    <lt:layout cssClass="glyphicon glyphicon-paperclip"/>
                    ${attachFileBtn}
                </wg:button>
                <wg:button domType="submit"
                           cssClass="btn btn-primary sendMsgBtn"
                           id="sendMsgBtn">
                    <lt:layout cssClass="glyphicon glyphicon-send"/>
                    ${sendMsgBtn}
                </wg:button>
            </lt:layout>
        </lt:layout>
    </wgForm:form>

    <!-- Attach File Modal -->
    <wg:modal id="uploadFileModal">
        <wgForm:form id="chooseFileForm" cssClass="chooseFileForm">
            <wg:modal-header>
                Attach File
            </wg:modal-header>
            <wg:modal-body>
                <lt:layout cssClass="ldr-details-pnl">
                    <lt:layout cssClass="form-group">
                        <wg:label _for="keyStore" cssClass="fileLbl">
                            ${fileChooseText}
                        </wg:label>
                    </lt:layout>

                    <lt:layout cssClass="form-group poz-relative chosenFileBox">
                        <wg:file name="chosenFile" cssClass="filestyle form-control" buttonText="BROWSE"
                                 id="chosenFile"/>
                    </lt:layout>

                </lt:layout>
            </wg:modal-body>
            <wg:modal-footer-btn-group>
                <wg:button name="cancelBtn"
                           domType="link"
                           dataToggle="modal"
                           dataTarget="#uploadFileModal"
                           cssClass="btn-default">
                    ${cancelBtn}
                </wg:button>
                <wg:button id="chooseFileBtn" domType="link" cssClass="btn-primary">
                    ${attachBtn}
                </wg:button>
            </wg:modal-footer-btn-group>
        </wgForm:form>
    </wg:modal>

    <!-- Address Book modal-->
    <wg:modal id="addressBookModal" cssClass="addressBookModal" modalCssClass="modal-lg">
        <wg:modal-header>
            ${addressBookLabel}
        </wg:modal-header>
        <wg:modal-body>
            <lt:layout cssClass="addressBookFilter">
                <wgForm:form commandName="addressBookFilter" id="addressBookFilter">
                    <lt:layout cssClass="form-horizontal form-group">
                        <wgForm:input path="secureEmail" cssClass="secureEmail" id="secureEmail"/>
                        <wg:button domType="button" cssClass="btn-primary addressBookSearchBtn" id="addressBookSearchBtn">
                            ${searchBtn}
                        </wg:button>
                    </lt:layout>
                    <c:forEach var="item" items="${addressBookSourceValues}">
                        <lt:layout cssClass="radio form-group">
                            <wg:label>
                                <spring:message code="addressbook.radio.${fn:toLowerCase(item)}" var="sourceLabel"/>
                                <wgForm:radiobutton path="addressBookSource" value="${item}"/> ${sourceLabel}
                            </wg:label>
                        </lt:layout>
                    </c:forEach>
                </wgForm:form>
            </lt:layout>
            <wg:grid id="accountsDirectory"
                     colIds="name,email,speciality,stateLicences,registrationType,npiNumbers"
                     colNames="Name,Secure Email,Speciality,State Licenses,Registration Type,NPI Number"
                     dataUrl="${accountsDirectoryUrl}"
                     cssClass="accountsDirectory"
                     colFormats="string,string,string,custom,string,custom"
                     deferLoading="true"/>
            <wg:grid-chose-rows-viewer id="recieversViewer" chooseTargetName="To:"/>
        </wg:modal-body>
        <wg:modal-footer-btn-group>
            <wg:button id="addressBookCancel"
                       domType="link"
                       cssClass="btn-default">
                ${cancelBtn}
            </wg:button>
            <wg:button id="addressBookSubmit"
                       domType="link"
                       cssClass="btn-primary">
                ${okBtn}
            </wg:button>
        </wg:modal-footer-btn-group>
    </wg:modal>

</lt:layout>
