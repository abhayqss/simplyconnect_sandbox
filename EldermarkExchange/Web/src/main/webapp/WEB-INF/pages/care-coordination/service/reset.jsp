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
        src="<c:url value="/resources/js/app/modules/care-coordination/module.reset.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/care-coordination.css"/>"  media="screen"/>

<lt:layout cssClass="center-block resetBox" role="document">

    <lt:layout cssClass="boxHeader panel panel-primary">
        <lt:layout cssClass="panel-heading">
            Reset Your Password
        </lt:layout>
    </lt:layout>
    <lt:layout cssClass="boxBody">
        <input type="hidden" id="organizationCode" value="${resetPasswordDto.organizationCode}">
        <wgForm:form role="form" id="resetPasswordForm" commandName="resetPasswordDto">
            <lt:layout cssClass="alert alert-warning serverError hidden" style="margin:20px 30px;"/>
            <lt:layout cssClass="col-md-12 boxBody">

                <lt:layout cssClass="note">
                    Please set password for access to <a id="careCoordinationUrl" href='${loginUrl}'>Care Coordination
                    Portal</a>.
                </lt:layout>

                <lt:layout id="loginError" cssClass="passwordErrorReset">
                    <c:out value="${errorMsg}"/>
                </lt:layout>

                <wgForm:hidden path="token"/>

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


                <lt:layout style="clear:both">
                    <jsp:include page="../../login/passwordRequirements.jsp">
                        <jsp:param name="cssClass" value="passwordHelpReset"/>
                    </jsp:include>
                </lt:layout>
            </lt:layout>



            <lt:layout cssClass="well resetBtnBox">
                <div style="width:50%; display:flex; justify-content:flex-start">
                    <wg:button
                            name="declineResetBtn"
                            id="declineResetBtn"
                            domType="button"
                            type="button"
                            cssClass="btn btn-default declineBtn">
                        Cancel
                    </wg:button>
                </div>


                <div style="width:50%; display:flex; justify-content:flex-end">
                    <wg:button
                            name="acceptResetBtn"
                            id="acceptResetBtn"
                            domType="button"
                            type="button"
                            cssClass="btn btn-primary acceptBtn">
                        Save
                    </wg:button>
                </div>

            </lt:layout>
        </wgForm:form>
    </lt:layout>
</lt:layout>



