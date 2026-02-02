<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<lt class="modal fade" role="dialog" id="passwordSettingsModal" data-backdrop="static">
    <lt:layout cssClass="modal-dialog" role="document" style="width:710px;">
        <lt:layout cssClass="modal-content">
            <wgForm:form role="form" id="passwordSettingsForm" commandName="passwordSettingsDto" >
                <wg:modal-header closeBtn="true">
                    <span>Password Settings</span>
                </wg:modal-header>
                <wg:modal-body cssClass="col-md-12 createOrgBody">
                    <wgForm:hidden id="orgId" path="organizationId"/>
                    <lt:layout cssClass="italic" style="padding-left: 15px">Please configure password policies depending on the needs of your organization.</lt:layout>
                    <hr class="linkNewAccountLine"/>

                    <lt:layout cssClass="col-md-12 no-horizontal-padding">
                        <span class="sectionHead col-md-12" style="margin-bottom: 15px; margin-top: 2px">Password Age Options</span>
                        <jsp:include page="organization-password-property-group.jsp">
                            <jsp:param name="groupName" value="Password"></jsp:param>
                        </jsp:include>
                    </lt:layout>

                    <lt:layout cssClass="col-md-12 no-horizontal-padding">
                        <span class="sectionHead col-md-12" style="margin-bottom: 15px; margin-top: 2px">Account Lockout Policy</span>
                        <jsp:include page="organization-password-property-group.jsp">
                            <jsp:param name="groupName" value="Account"></jsp:param>
                        </jsp:include>
                    </lt:layout>

                    <lt:layout cssClass="col-md-12 no-horizontal-padding">
                        <span class="sectionHead col-md-12" style="margin-bottom: 15px; margin-top: 2px">Password Complexity Requirements</span>
                        <jsp:include page="organization-password-property-group.jsp">
                            <jsp:param name="groupName" value="Complexity"></jsp:param>
                        </jsp:include>
                    </lt:layout>

                </wg:modal-body>
                <wg:modal-footer-btn-group>
                    <wg:button name="cancelBtn"
                               domType="link"
                               dataToggle="modal"
                               dataTarget="#passwordSettingsModal"
                               cssClass="btn-default cancelBtn">
                        CANCEL
                    </wg:button>

                    <wg:button name="savePasswordSettings"
                               id="savePasswordSettings"
                               domType="link"
                               dataToggle="modal"
                               cssClass="btn-primary submitBtn">
                        <span id="contactHeader">SAVE</span>
                    </wg:button>
                </wg:modal-footer-btn-group>
            </wgForm:form>
        </lt:layout>
    </lt:layout>
</lt>