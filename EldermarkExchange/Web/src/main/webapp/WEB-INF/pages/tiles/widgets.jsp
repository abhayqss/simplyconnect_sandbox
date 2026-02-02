<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="lt" tagdir="/WEB-INF/tags/layout" %>
<%@taglib prefix="wg" tagdir="/WEB-INF/tags/widget" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url value="/resources/images/end-cancel-call.png" var="endCall"/>
<c:url value="/resources/images/mute.png" var="mute"/>
<c:url value="/resources/images/unmute.png" var="unmute"/>
<c:url value="/resources/images/sound-on.png" var="soundOn"/>
<c:url value="/resources/images/sound-off.png" var="soundOff"/>
<spring:message code="nucleus.videoCall.header.label" var="nucleusVideoCallHeaderLabel"/>

<lt:layout id="nucleusCallWidget" cssClass="draggableBox">
    <lt:layout cssClass="boxHeader panel panel-primary">
        <lt:layout cssClass="panel-heading">
            ${nucleusVideoCallHeaderLabel}
        </lt:layout>
    </lt:layout>
    <lt:layout cssClass="boxBody">
        <lt:layout cssClass="video-call-frame-container">
            <iframe id="nucleus" src="about:blank" allow="microphone; camera"></iframe>
        </lt:layout>

        <lt:layout cssClass="video-call-controls-container">

            <lt:layout cssClass="volume-container pull-right">
                <wg:button id="decSpeakerVolume" domType="link">
                    <wg:img cssClass="icon-link-img" src="${soundOff}"/>
                </wg:button>
                <section class="volume-control">
                    <span class="tooltip"></span>
                    <div id="slider"></div>
                </section>
                <wg:button id="incSpeakerVolume" domType="link">
                    <wg:img cssClass="icon-link-img" src="${soundOn}"/>
                </wg:button>
            </lt:layout>

            <lt:layout cssClass="action-buttons-container">
                <wg:button id="mute" domType="link">
                    <wg:img id="iconMute" cssClass="icon-link-img" src="${mute}"/>
                    <wg:img id="iconUnmute" cssClass="icon-link-img" src="${unmute}"/>
                </wg:button>
                <wg:button id="endCall" domType="link">
                    <wg:img cssClass="icon-link-img" src="${endCall}"/>
                </wg:button>
            </lt:layout>
        </lt:layout>
    </lt:layout>
</lt:layout>
