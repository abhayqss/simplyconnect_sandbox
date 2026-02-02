<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>

<spring:message code="compose.msg.headLabel" var="composingMsgHeaderLabel"/>

<c:url value="/resources/images/wz-step-done.png" var="wzStepDoneImgUrl"/>

<c:url value="patient-search" var="patientSearchUrl"/>
<c:url value="secure-messaging" var="secureMessagingUrl"/>

<c:url value="/secure-messaging/setup/set-keystore" var="setupKeyStoreUrl"/>
<c:url value="/secure-messaging/setup/set-pin" var="setupPinUrl"/>
<c:url value="/secure-messaging/setup/verify" var="verifyUrl"/>

<lt:layout cssClass="msgSetup ldr-center-block">

    <lt:layout cssClass="msgSetupHeader panel panel-primary">
        <lt:layout cssClass="panel-heading">
            Secure Messaging
        </lt:layout>
    </lt:layout>

    <lt:layout cssClass="msgSetupBody ldr-pnl-body">
        <c:set var="activeStep" value="${msgSetup.configured ? 3 : 0}"/>

        <wg:wizard id="msgSetupWzd" activeStep="${activeStep}">

            <wg:wizard-header>
                <wg:wizard-head-item href="#keyStoreTab">
                    <wg:img cssClass="wz-step-done" src="${wzStepDoneImgUrl}"/>
                    <wg:b>STEP 1:</wg:b> UPLOAD KEYSTORE
                </wg:wizard-head-item>
                <lt:layout cssClass="wzSplitter">/</lt:layout>
                <wg:wizard-head-item href="#pinTab">
                    <wg:img cssClass="wz-step-done" src="${wzStepDoneImgUrl}"/>
                    <wg:b>STEP 2:</wg:b> CHANGE PIN
                </wg:wizard-head-item>
                <lt:layout cssClass="wzSplitter">/</lt:layout>
                <wg:wizard-head-item href="#testConfigTab">
                    <wg:img cssClass="wz-step-done" src="${wzStepDoneImgUrl}"/>
                    <wg:b>STEP 3:</wg:b> TEST CONFIGURATION
                </wg:wizard-head-item>
            </wg:wizard-header>

            <wg:wizard-content>
                <wg:wizard-content-item id="keyStoreTab" cssClass="keyStoreTab">
                    <wgForm:form commandName="msgSetup" enctype="multipart/form-data" action="${setupKeyStoreUrl}"
                                 id="keyStoreForm" cssClass="keyStoreForm ldr-details-pnl">


                        <lt:layout cssClass="form-group">
                            <wg:label _for="keyStore" cssClass="keyStoreLbl">
                                Download a copy of your company's encryption certificate from SES and upload it below.
                            </wg:label>
                            <wg:file name="keystore" cssClass="filestyle col-md-11 form-control" buttonText="BROWSE"
                                     id="keyStore" accept=".pfx"/>
                        </lt:layout>
                    </wgForm:form>
                </wg:wizard-content-item>

                <wg:wizard-content-item id="pinTab" cssClass="pinTab">
                    <wgForm:form commandName="msgSetup" action="${setupPinUrl}" id="pinForm" cssClass="pinForm ldr-details-pnl">

                        <lt:layout cssClass="form-group">
                            <wg:label _for="pin" cssClass="pinLbl">
                                View your private key pin number and enter it below.
                            </wg:label>
                            <wgForm:input path="pin" id="pin" name="pin" cssClass="form-control"/>
                        </lt:layout>
                    </wgForm:form>
                </wg:wizard-content-item>

                <wg:wizard-content-item id="testConfigTab" cssClass="testConfigTab">
                    <lt:layout cssClass="testConfigDetails ldr-details-pnl">
                        <lt:layout cssClass="detail-item">
                            <wg:label cssClass="detail-name">Certificate:</wg:label>
                            <wg:label id="certName" cssClass="detail-value"></wg:label>
                        </lt:layout>
                        <lt:layout cssClass="detail-item">
                            <wg:label cssClass="detail-name">Pin:</wg:label>
                            <wg:label id="pinValue" cssClass="detail-value"></wg:label>
                        </lt:layout>
                    </lt:layout>

                    <lt:layout cssClass="testConfigResult ldr-details-pnl hidden">
                        <lt:layout cssClass="detail-item">
                            <wg:label cssClass="detail-value">Secure Messaging Functionality was configured successfully.</wg:label>
                        </lt:layout>
                    </lt:layout>
                </wg:wizard-content-item>
            </wg:wizard-content>

        </wg:wizard>
    </lt:layout>
    <lt:layout cssClass="wzBtns">
        <lt:layout cssClass="keyStoreStep btn-lt hidden">
            <wg:button id="cancelBtn" name="cancelBtn" domType="link" linkHref="${patientSearchUrl}" ajaxUrl="${patientSearchUrl}" ajaxLoad="true" cssClass="btn-default">
                CANCEL
            </wg:button>
            <wg:button id="uploadBtn" name="uploadBtn" domType="button" cssClass="btn-primary next">
                UPLOAD
            </wg:button>
        </lt:layout>
        <lt:layout cssClass="pinStep btn-lt hidden">
            <wg:button name="backBtn1" domType="button" cssClass="btn-default first">
                BACK TO STEP 1
            </wg:button>
            <wg:button id="submitBtn" name="submitBtn" domType="button" cssClass="btn-primary next">
                SUBMIT
            </wg:button>
        </lt:layout>
        <lt:layout cssClass="testStep btn-lt hidden">
            <wg:button name="backBtn1" domType="button" cssClass="btn-default first">
                BACK TO STEP 1
            </wg:button>
            <wg:button id="testBtn" name="testBtn" domType="button" cssClass="btn-primary">
                TEST
            </wg:button>
        </lt:layout>
        <lt:layout cssClass="resultStep btn-lt hidden">
            <wg:button name="backBtn1" domType="button" cssClass="btn-default first">
                UPDATE
            </wg:button>
            <wg:button id="continueBtn" name="continueBtn" domType="link" linkHref="#${secureMessagingUrl}" ajaxUrl="${secureMessagingUrl}" ajaxLoad="true" cssClass="btn-primary">
                CONTINUE TO INBOX
            </wg:button>
        </lt:layout>
    </lt:layout>
</lt:layout>
