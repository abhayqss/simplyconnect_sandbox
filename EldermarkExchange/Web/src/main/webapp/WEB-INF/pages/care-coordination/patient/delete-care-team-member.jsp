<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<wg:modal id="deleteCareTeamMemberModal">
    <wgForm:form>
        <wg:modal-header>
            Confirmation
        </wg:modal-header>
        <wg:modal-body>
            <lt:layout cssClass="ldr-details-pnl">
                Are you sure you want to delete <span id="careTeamMemberName"></span> Care Team Member?
            </lt:layout>
        </wg:modal-body>
        <wg:modal-footer-btn-group>
            <wg:button name="cancelBtn"
                       domType="link"
                       cssClass="btn-default cancelBtn"
                       dataToggle="modal">
                CANCEL
            </wg:button>

            <wg:button name="deleteCareTeamMember"
                       id="deleteCareTeamMember"
                       domType="link"
                       dataToggle="modal"
                       cssClass="btn-primary submitBtn">
                DELETE
            </wg:button>

        </wg:modal-footer-btn-group>
    </wgForm:form>
</wg:modal>