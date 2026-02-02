<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <link href="<c:url value="/resources/css/standard.css"/>" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/send-tcp-packet.js"></script>
</head>

<body class="panel-body">

<c:set var="rand"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %></c:set>

<div class="panel panel-primary">
    <div class="panel-heading">Request</div>

    <div class="panel-body">
        <form:form method="POST" commandName="requestDto" class="container">
            <div class="form-group row">
                <label for="host-input" class="col-xs-2 col-form-label">Host</label>
                <div class="col-xs-10">
                    <form:input path="host" type="text" class="form-control" id="host-input"/>
                </div>
            </div>
            <div class="form-group row">
                <label for="port-input" class="col-xs-2 col-form-label">Port</label>
                <div class="col-xs-10">
                    <form:input path="port" type="text" class="form-control" id="port-input"/>
                </div>
            </div>
            <div class="form-group row">
                <label for="message-input" class="col-xs-2 col-form-label">Message</label>
                <div class="col-xs-10">
                    <form:textarea path="message" class="form-control monospace" rows="15" id="message-input"/>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-xs-2 col-xs-offset-2">
                    <button id="send-adt" type="submit" class="btn btn-primary">Send</button>
                </div>
            </div>
        </form:form>
    </div>
</div>

<c:if test="${not empty responseDto}">
    <div id="adt-server-response" class="panel ${responseDto.hasErrors ? 'panel-primary' : 'panel-success'}">
        <div class="panel-heading">Response</div>
        <div class="panel-body">
            <label>Status</label> : <span class="${responseDto.status ? 'ok' : 'nok'}">${responseDto.status ? 'OK' : 'NOK'}</span>
            <p/>
            <label>Text</label> : ${responseDto.text}
        </div>
    </div>
</c:if>

<div class="panel-group monospace" id="accordion">
    <div class="panel panel-default">
        <div class="panel-heading">
            <h4 class="panel-title">
                <a data-toggle="collapse" href="#collapse1">
                    ADT-A01 sample (Admission of patient)
                </a>
            </h4>
        </div>
        <div id="collapse1" class="panel-collapse collapse in">
            <div class="panel-body">
                MSH|^~\&|OTHER_KIOSK|HIMSSSANDIEGO|XDSb_REG_MISYS|MISYS|20090512132906-0300||ADT^A04^ADT_A01|7723510070655179915|P|2.3.1<br/>
                EVN||20090512132906-0300<br/>
                PID|||<mark>S${rand}^^^IHENA&1.3.6.1.4.1.21367.2010.1.2.300&ISO</mark>||DUCK^DONALD||19781208|M|||820 JORIE
                BLVD^^CHICAGO^IL^60523|GL|(414)379-1212|(414)271-3434||S||MRN12345001^2^M10|123456789|987654^NC<br/>
                PV1||O|
            </div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h4 class="panel-title">
                <a data-toggle="collapse" href="#collapse2">
                    ADT-A39 sample (Merge patients)
                </a>
            </h4>
        </div>
        <div id="collapse2" class="panel-collapse collapse">
            <div class="panel-body">MSH|^~\&|OTHER_KIOSK|HIMSSSANDIEGO|XDSb_REG_MISYS|MISYS|20090512132906-0300||ADT^A40^ADT_A39|4143361005927619863|P|2.3.1<br/>
                EVN||20090512132906-0300<br/>
                PID|||<mark>S${rand}^^^IHENA&1.3.6.1.4.1.21367.2010.1.2.300&ISO</mark>||DUCK^DONALD||19781208|M|<br/>
                MRG|<mark>N${rand}^^^IHENA&1.3.6.1.4.1.21367.2010.1.2.300&ISO</mark><br/>
                PV1||O|
            </div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h4 class="panel-title">
                <a data-toggle="collapse" href="#collapse3">
                    ADT-A08 sample (Patient information update)
                </a>
            </h4>
        </div>
        <div id="collapse3" class="panel-collapse collapse">
            <div class="panel-body">
                MSH|^~\&|OTHER_KIOSK|HIMSSSANDIEGO|XDSb_REG_MISYS|MISYS|20090512132906-0300||ADT^A04^ADT_A08|7723510070655179915|P|2.3.1<br/>
                EVN||20090512132906-0300<br/>
                PID|||<mark>S${rand}^^^IHENA&1.3.6.1.4.1.21367.2010.1.2.300&ISO</mark>||DUCK^DONALD111||19781208|M|||820 JORIE
                BLVD^^CHICAGO^IL^60523|GL|(414)379-1212|(414)271-3434||S||MRN12345001^2^M10|123456789|987654^NC<br/>
                PV1||O|
            </div>
        </div>
    </div>
</div>

</body>
</html>