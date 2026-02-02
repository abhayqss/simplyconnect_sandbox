<%@ page import="com.scnsoft.eldermark.authentication.SecurityExpressions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="auditReportContextPath" value="${auditReportContext}"/>

<spring:message code="auditReportLoginUrl" var="loginUrl"/>
<spring:message code="auditReportLogoutUrl" var="logoutUrl"/>

<!DOCTYPE html>
<html class="html">
<head xmlns:tiles="http://tiles.apache.org/tags-tiles">
    <%-- IE should use the latest (edge) version of its rendering engine + IE should use the Chrome rendering engine if installed --%>
    <meta http-equiv="X-UA-Compatible" content="IE=Edge,chrome=1">
    <sec:csrfMetaTags/>
    <title></title>

    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/bootstrap.min.css"/>" media="screen"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/bootstrap-theme.min.css"/>"
          media="screen"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/eldermark-wgt.min.css"/>" media="screen"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/eldermark-theme.min.css"/>" media="screen"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/care-coordination.min.css"/>" media="screen"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/jquery-ui.min.css"/>" media="screen"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/jquery-ui.structure.min.css"/>"
          media="screen"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/jquery-ui.theme.min.css"/>"
          media="screen"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/bootstrap-select.min.css"/>"
          media="screen"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/bootstrap-slider.min.css"/>"
          media="screen"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/animate.min.css"/>" media="screen"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/nucleus-call.min.css"/>" media="screen"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/surveyeditor.min.css"/>" media="screen"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/survey.pretty-checkbox.min.css"/>" media="screen"/>
    <link rel="shortcut icon" type="image/x-icon" href="<c:url value="/resources/images/favicon.ico"/>"/>

    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/es5-shim.min.js"/>"></script>

    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/es6-shim.min.js"/>"></script>

    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/promise-jquery-fix.min.js"/>"></script>

    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/date-format.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/jquery-1.12.4.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/jquery-ui.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/bootstrap-select.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/jquery.serializejson.min.js"/>"></script>

    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/jquery.scrollTo.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/bootstrap.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/bootstrap-multimodal.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/bootstrap-slider.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/jquery.ba-bbq.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/bootbox.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/jquery.validate.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/bootstrap-notify.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/modules/nucleus/widget.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/survey.jquery.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/select2.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/jquery.dynamic-textarea.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/json2html.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/jquery.json2html.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/inputmask/dist/jquery.inputmask.bundle.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/plugins/surveyjs-widgets.js"/>"></script>

    <script language="JavaScript" type="text/javascript"
                src="<c:url value="/resources/js/plugins/moment.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
                src="<c:url value="/resources/js/plugins/underscore.min.js"/>"></script>

        <%--todo uncommit for pretty checkBox/radio buttons--%>
    <%--<script language="JavaScript" type="text/javascript" async--%>
                <%--src="<c:url value="/resources/js/plugins/icheck.min.js"/>"></script>--%>
    <script language="JavaScript" type="text/javascript" async
                src="<c:url value="/resources/js/plugins/underscore-min.js"/>"></script>

    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/app.js"/>"></script>
    <script language="JavaScript" type="text/javascript">
        ExchangeApp.info.context = '${pageContext.request.contextPath}';
    </script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/app.utils.js"/>"></script>

    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/error-messages.js"/>"></script>

    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/modules/Module.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/modules/module-manager.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/modules/event-manager.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/loaders/loader.module-loader.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/loaders/loader.fragment-loader.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/loaders/loader.component-loader.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/loaders/loader.service-loader.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/routers/router.module-router.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/services/CURDService.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/services/ServiceProvider.min.js"/>"></script>

    <script language="JavaScript" type="text/javascript"
                src="<c:url value="/resources/js/app/components/care-coordination/assessments/AssessmentFooter.min.js"/>"></script>

    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/components/Component.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/components/Widget.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/components/Modal.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/components/ConfirmModal.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/components/SuccessConfirmModal.min.js"/>"></script>
    <script language="JavaScript" type="text/javascript"
            src="<c:url value="/resources/js/app/components/care-coordination/assessments/AssessmentWizard.min.js"/>"></script>

    <script language="JavaScript" type="text/javascript">
        window.auditReportLoginUrl = '/${auditReportContextPath}/${loginUrl}';
        window.auditReportLogoutUrl = '/${auditReportContextPath}/${logoutUrl}';
    </script>

    <c:choose>
        <c:when test="${startPage==null}">
            <sec:authorize access="<%=SecurityExpressions.IS_CC_USER%>">
                <script language="JavaScript" type="text/javascript">
                    ExchangeApp.info.startPage = 'care-coordination/events-log';
                </script>
            </sec:authorize>
            <sec:authorize access="<%=SecurityExpressions.IS_ELDERMARK_USER%>">
                <c:if test="${not unaffiliatedUser}">
                    <script language="JavaScript" type="text/javascript">
                        ExchangeApp.info.startPage = 'patient-search';
                    </script>
                </c:if>
            </sec:authorize>
            <sec:authorize access="<%=SecurityExpressions.IS_LINKING_NEW_ACCOUNT%>">
                <script language="JavaScript" type="text/javascript">
                    ExchangeApp.info.startPage = 'profile/show';
                </script>
            </sec:authorize>
        </c:when>
        <c:otherwise>
            <sec:authorize access="<%=SecurityExpressions.IS_CC_USER%>">
                <script language="JavaScript" type="text/javascript">
                    ExchangeApp.info.startPage = '${startPage}';
                    <c:if test="${not empty id}">ExchangeApp.info.params = 'id=${id}';
                    </c:if>
                    <c:if test="${not empty note and not empty patient}">ExchangeApp.info.params = 'note=${note}&patient=${patient}';
                    </c:if>
                </script>
            </sec:authorize>
        </c:otherwise>
    </c:choose>

    <tiles:insertAttribute name="resources"/>
    <tiles:insertAttribute name="errors"/>

</head>
<body class="ldr-ui">
<tiles:insertAttribute name="widgets"/>
<tiles:insertAttribute name="appLayout"/>
</body>
</html>
