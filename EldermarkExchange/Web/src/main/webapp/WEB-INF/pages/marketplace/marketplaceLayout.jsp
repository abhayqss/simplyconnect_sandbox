<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>

<spring:message var="enterLocationHeader" code="marketplace.modal.location.header"/>
<spring:message var="inputPlaceholderText" code="marketplace.modal.location.input.placeholder"/>
<spring:message var="backButtonTitle" code="marketplace.modal.location.button.back"/>
<spring:message var="goButtonTitle" code="marketplace.modal.location.button.go"/>

<%--<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyC8yLpobUyUMRNXaiA1b0LbpLMbWrmbQ2I&libraries=places&callback=initObjects"></script>--%>

<style>

</style>
<wg:modal id="startLocationModalId">
    <wg:modal-body cssClass="col-md-12">
        <div class="col-md-12">
            <span class="sectionHead col-sm-12" style="padding-left: 0;">${enterLocationHeader}</span>
        </div>
        <div class="col-md-12">
            <input style="width: 100%" id="startLocationAutocompleteId" class="form-control modal-pac-input clearable" placeholder="${inputPlaceholderText}">
        </div>
    </wg:modal-body>
    <wg:modal-footer-btn-group btnGroupCssClass="ldr-2-btn-group">
        <wg:button
                domType="link"
                dataToggle="modal"
                dataTarget="#createPatientModal"
                cssClass="btn-default btn cancelBtn"
                id="marketLocationButtonIdBack">
            ${backButtonTitle}
        </wg:button>
        <wg:button
                name="savePatient"
                domType="link"
                dataToggle="modal"
                cssClass="btn-primary btn submitBtn"
                id="marketLocationButtonIdGo">
            ${goButtonTitle}
        </wg:button>
    </wg:modal-footer-btn-group>
</wg:modal>

<table class="ldr-ui-app layout-table">
    <tr id="header" class="ldr-ui-header">
        <td class="markup-frg1">
            <tiles:insertAttribute name="header"/>
        </td>
    </tr>
    <tr>
        <td id="content" class="ldr-ui-content">
            <div class="loader"></div>
            <table class="ldr-ui-main markup-frg <tiles:insertAttribute name="mainCssClass"/>">
                <tr id="filter" class="ldr-ui-header">
                        <td colspan="2"  style="padding-bottom: 15px">
                        <tiles:insertAttribute name="filter"/>
                    </td>
                </tr>
                <tr >
                    <td width="300" class="ldr-ui-menu" style="padding-top: 15px">
                        <tiles:insertAttribute name="menu"/>
                    </td>
                    <td class="ldr-ui-body" style="padding-top: 15px">
                        <tiles:insertAttribute name="body"/>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>