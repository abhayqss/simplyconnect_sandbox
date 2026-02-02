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
        src="<c:url value="/resources/js/app/modules/care-coordination/module.reset.request.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/care-coordination.css"/>"  media="screen"/>

<lt:layout cssClass="center-block resetRequestBox" role="document">

    <lt:layout cssClass="boxHeader panel panel-primary">
        <lt:layout cssClass="panel-heading">
            Error!
        </lt:layout>
    </lt:layout>
    <lt:layout cssClass="boxBody">
        <wgForm:form role="form" id="resetPasswordRequestForm" commandName="dto">
        <lt:layout cssClass="error-page-msg">
            ${errMsg}
        </lt:layout>
        </wgForm:form>
    </lt:layout>
</lt:layout>



