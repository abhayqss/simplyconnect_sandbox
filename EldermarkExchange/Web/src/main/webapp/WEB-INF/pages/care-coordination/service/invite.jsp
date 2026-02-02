<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="wgForm" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script language="JavaScript" type="text/javascript"
        src="<c:url value="/resources/js/plugins/jquery.validate.min.js"/>"></script>

<script language="JavaScript" type="text/javascript"
        src="<c:url value="/resources/js/app/app.js"/>"></script>

<script language="JavaScript" type="text/javascript"
        src="<c:url value="/resources/js/app/app.utils.js"/>"></script>
<script language="JavaScript" type="text/javascript"
        src="<c:url value="/resources/js/app/modules/care-coordination/module.invite.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/care-coordination.css"/>"  media="screen"/>

<lt:layout cssClass="center-block inviteBox" role="document">
    <input type="hidden" id="organizationCode" value="${resetPasswordDto.organizationCode}">
    <wgForm:form role="form" id="resetPasswordForm" commandName="resetPasswordDto">
        <wgForm:hidden path="token"/>
    <c:choose>
        <c:when test="${expired}">
            <lt:layout cssClass="panel-primary">
                <lt:layout cssClass="panel-heading" style="text-align:center;">
                    Link has been expired
                </lt:layout>
            </lt:layout>
            <lt:layout cssClass="boxBody"  style="margin:auto; width:50%; text-align:center;">
                <%--<input type="hidden" id="organizationCode" value="${resetPasswordDto.organizationCode}">--%>

                    <%--<lt:layout cssClass="alert alert-warning serverError hidden" style="margin:20px 30px;"/>--%>
                    <lt:layout id="message" style="padding:30px 0 10px 0;text-align:center;">

                        <%--<lt:layout>--%>
                            The invitation link has been expired
                        <%--</lt:layout>--%>

                        <%--<lt:layout id="loginError" cssClass="passwordErrorReset">--%>
                        <%--<c:out value="${errorMsg}"/>--%>
                        <%--</lt:layout>--%>
                        <%--<wgForm:form id="sendNewInvitationForm">--%>
                            <%--<wgForm:hidden path="token"/>--%>
                        <%--</wgForm:form>--%>
                    </lt:layout>
                <c:if test="${validContact}">
                    <div style="padding:10px 0 30px 0" data-token="${token}">

                        <%--<div style="">--%>
                            <wg:button
                                    name="sendNewInvitationBtn"
                                    id="sendNewInvitationBtn"
                                    domType="button"
                                    type="button"

                                    cssClass="btn btn-primary">
                                SEND NEW INVITATION
                            </wg:button>
                        <%--</div>--%>

                    </div>
                </c:if>
                <%--</wgForm:form>--%>
            </lt:layout>
        </c:when>
        <c:otherwise>
            <lt:layout cssClass="boxHeader panel panel-primary">
                <lt:layout cssClass="panel-heading">
                    You have been invited to join Simply Connect system
                </lt:layout>
            </lt:layout>
            <lt:layout>

                    <lt:layout cssClass="alert alert-warning serverError hidden" style="margin:20px 30px;"/>
                    <lt:layout cssClass="col-md-12 boxBody">

                        <lt:layout cssClass="note">
                            ${resetPasswordDto.creatorName} has invited you to join Simply Connect system.</br>
                            Please set password for future access to <a id="careCoordinationUrl" href='${loginUrl}'>Care
                            Coordination Portal</a>
                        </lt:layout>

                        <lt:layout id="loginError" cssClass="passwordErrorReset">
                            <c:out value="${errorMsg}"/>
                        </lt:layout>

                        <%--<wgForm:hidden path="token"/>--%>


                        <lt:layout cssClass="col-md-12 form-group">
                            <label for="password">Password*</label>
                            <wgForm:password path="password"
                                             id="password"
                                             cssClass="form-control"
                            />
                        </lt:layout>
                        <lt:layout cssClass="col-md-12 form-group">
                            <label for="confirmPassword">Confirm Password*</label>
                            <wgForm:password path="confirmPassword"
                                             id="confirmPassword"
                                             cssClass="form-control"
                            />
                        </lt:layout>

                        <lt:layout  style="clear:both" >
                            <jsp:include page="../../login/passwordRequirements.jsp">
                                <jsp:param name="cssClass" value="passwordHelpReset"/>
                            </jsp:include>
                        </lt:layout>
                    </lt:layout>

                    <lt:layout cssClass="well inviteBtnBox">

                        <div style="width:50%; display:flex; justify-content:flex-start">
                            <wg:button
                                    name="declineInvitationBtn"
                                    id="declineInvitationBtn"
                                    domType="button"
                                    type="button"
                                    cssClass="btn btn-default declineBtn">
                                Decline
                            </wg:button>
                        </div>

                        <div style="width:50%; display:flex; justify-content:flex-end">
                            <wg:button
                                    name="acceptInvitationBtn"
                                    id="acceptInvitationBtn"
                                    domType="button"
                                    type="button"
                                    cssClass="btn btn-primary acceptBtn">
                                Accept
                            </wg:button>
                        </div>

                    </lt:layout>

            </lt:layout>
        </c:otherwise>
    </c:choose>
    </wgForm:form>
</lt:layout>



