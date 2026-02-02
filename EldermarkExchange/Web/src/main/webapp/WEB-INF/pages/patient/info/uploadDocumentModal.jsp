<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="ccd" tagdir="/WEB-INF/tags/ccd" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!-- Attach File Modal -->

<wg:modal id="uploadDocumentModal">
    <wgForm:form commandName="uploadDocumentForm" id="uploadDocumentForm" enctype="multipart/form-data"
                 cssClass="uploadDocForm">
        <wg:modal-header>
            Attach File
        </wg:modal-header>
        <wg:modal-body>
            <lt:layout cssClass="ldr-details-pnl">
                <c:url value="/employee/company" var="companyNameUrl"/>
                <lt:layout cssClass="form-group">
                    <wg:label _for="keyStore" cssClass="documentLbl">
                        Choose the document
                    </wg:label>
                </lt:layout>

                <lt:layout cssClass="form-group poz-relative">
                    <wg:file name="document" cssClass="filestyle form-control" buttonText="BROWSE"
                             id="document"/>
                </lt:layout>

                <wg:link id="companyNameUrl" href="${companyNameUrl}" cssClass="hidden"/>
                <lt:layout cssClass="form-group">
                    <lt:layout cssClass="radio">
                        <wg:label>
                            <wgForm:radiobutton name="sharingOption" path="sharingOption"
                                                value="MY_COMPANY"/> Share with <wg:label id="companyName"/>
                        </wg:label>
                    </lt:layout>
                    <lt:layout cssClass="radio">
                        <wg:label>
                            <wgForm:radiobutton name="sharingOption" path="sharingOption" value="ALL"/> Share with All
                        </wg:label>
                    </lt:layout>
                </lt:layout>

            </lt:layout>
        </wg:modal-body>
        <wg:modal-footer-btn-group>
            <wg:button name="cancelBtn"
                       domType="link"
                       dataToggle="modal"
                       dataTarget="#uploadDocumentModal"
                       cssClass="btn-default">
                CANCEL
            </wg:button>
            <lt:layout cssClass="btn-group" role="group">
                <wg:button name="uploadDocumentBtn" id="uploadDocumentBtn" domType="button" type="submit"
                           cssClass="btn-primary">
                    UPLOAD
                </wg:button>
            </lt:layout>
        </wg:modal-footer-btn-group>
    </wgForm:form>
</wg:modal>
