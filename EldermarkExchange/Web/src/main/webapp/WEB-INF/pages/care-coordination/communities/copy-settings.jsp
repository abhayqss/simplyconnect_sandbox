<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<lt:layout cssClass="modal fade" role="dialog" id="copySettingsModal">
    <lt:layout cssClass="modal-dialog" role="document" style="width:1000px;">
        <lt:layout cssClass="modal-content">
    <%--<wgForm:form>--%>
        <wg:modal-header closeBtn="true">
            Copy Notifications Settings
        </wg:modal-header>
        <wg:modal-body cssClass="col-md-12 createCareTeamMemberBody">

            <lt:layout cssClass="col-md-12 copySettingsDescription">
                This screen allows you to copy the notifications settings configured for other affiliated communities. Please choose the community which settings will be copied to "${communityName}" community settings.
            </lt:layout>
            <lt:layout id = "communityListLayout" style="padding-top:20px" cssClass="col-md-12 form-group">
                <label for="copySettingsCommunitySelect">Community *</label>
                <%--<wgForm:select path="communityId"--%>
                               <%--id="copySettingsCommunitySelect"--%>
                               <%--cssClass="form-control"--%>
                               <%--maxlength="255">--%>
                    <%--<c:forEach var="item" items="${communities}">--%>
                        <%--<wgForm:option value="${item.id}" label="${item.name}"/>--%>
                    <%--</c:forEach>--%>
                <%--</wgForm:select>--%>

                <wgForm:select class="form-control affOrgSelect" id="copySettingsCommunitySelect" path="communityId" items="${communities}" itemValue="id"  itemLabel="name"/>


            </lt:layout>
        </wg:modal-body>
        <wg:modal-footer-btn-group>
            <wg:button name="cancelBtn"
                       domType="link"
                       dataTarget="#copySettingsModal"
                       cssClass="btn-default cancelBtn"
                       dataToggle="modal">
                CANCEL
            </wg:button>

            <wg:button name="copySettingsBtn"
                       id="copySettingsBtn"
                       domType="link"
                       dataToggle="modal"
                       cssClass="btn-primary submitBtn">
                COPY
            </wg:button>

        </wg:modal-footer-btn-group>
    <%--</wgForm:form>--%>
        </lt:layout>
    </lt:layout>
</lt:layout>
