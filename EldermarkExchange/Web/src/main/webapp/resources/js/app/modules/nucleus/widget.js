var callEstablished, polling = [];

window.addEventListener('message', function (event) {
    //Nucleus events
    console.log('nucleus message:  ', event.data);
    switch (event.data.msg) {
        case "nucleusLoaded":
            console.log("nucleusLoaded");
            break;
        case "handleCallStarted":
            console.log("handleCallStarted");
            break;
        case "handleCallEstablished":
            console.log("handleCallEstablished");
            callEstablished = true;
            break;
        case "handleCallRejected":
            console.log("handleCallRejected");
            setTimeout(endCall, 3000);
            break;
        case "handleCallDisconnected":
            console.log("handleCallDisconnected");
            setTimeout(endCall, 3000);
            break;
        case "handleCallBusy":
            console.log("handleCallBusy");
            setTimeout(endCall, 3000);
            break;
        case "handleCallTimeout":
            console.log("handleCallTimeout");
            setTimeout(endCall, 3000);
            break;
        default:
            break;
    }
}, false);

function isEmpty(str) {
    return (!str || 0 === str.length);
}

function nucleusPost(msg, param) {
    var nucleus = document.getElementById("nucleus").contentWindow;
    nucleus.postMessage({msg: msg, param: param}, "*");
}

function setFrameSrc(nucleusHost, token, fromId, toId, requestId) {
    if (isEmpty(nucleusHost) || isEmpty(token) || isEmpty(fromId) || isEmpty(toId)) {
        return;
    }

    var nucleus = document.getElementById("nucleus");
    var url = nucleusHost + "/client/client.aspx?Token=" + token + "&FromID=" + fromId + "&ToID=" + toId + (requestId ? "&RequestID=" + requestId : "");
    console.log('startCall() frame src = ' + url);
    nucleus.src = url;
}

function endCall() {
    if (callEstablished) {
        nucleusPost('endCall');
    } else {
        nucleusPost('cancelCall');
    }
    $('#nucleusCallWidget').hide();
}

function initCall() {
    if ($('#nucleusCallWidget').is(":visible")) {
        var wantsToMakeNewCall = confirm("There's a call in progress. Are you sure you want to cancel it and make a new call?");
        if (wantsToMakeNewCall) {
            endCall();
        } else {
            return false;
        }
    }

    var muted = false;
    var volumeBeforeMuted = 100;
    callEstablished = false;

    $('#nucleusCallWidget').css({top: 50, left: 50});

    $('#endCall').unbind("click").click(endCall);

    $('#iconUnmute').hide();
    $('#iconMute').show();
    $('#mute').unbind("click").click(function () {
        if (muted) {
            nucleusPost('unmute');

            $('#iconMute').show();
            $('#iconUnmute').hide();
            slider.slider('value', volumeBeforeMuted);
        } else {
            nucleusPost('mute');

            $('#iconMute').hide();
            $('#iconUnmute').show();
            volumeBeforeMuted = slider.slider('value');
            slider.slider('value', 0);
        }
        muted = !muted;
    });

    var slider = $("#slider"),
        tooltip = $('.tooltip');
    tooltip.hide();

    slider.slider({
        range: "min",
        min: 1,
        value: 35,

        start: function (event, ui) {
            tooltip.fadeIn('fast');
        },

        slide: function (event, ui) {
            nucleusPost('setSpeakerVolume', ui.value);
            tooltip.css('left', ui.value).text(ui.value);
            if (muted) {
                $('#iconMute').show();
                $('#iconUnmute').hide();
                muted = false;
            }
        },

        stop: function (event, ui) {
            tooltip.fadeOut('fast');
        }
    });

    $('#decSpeakerVolume').unbind("click").click(function () {
        var value = slider.slider('value');
        if (value > 1) {
            slider.slider('value', value - 2);
            nucleusPost('setSpeakerVolume', value - 2);
        }
    });

    $('#incSpeakerVolume').unbind("click").click(function () {
        var value = slider.slider('value');
        if (value < 99) {
            slider.slider('value', value + 2);
            nucleusPost('setSpeakerVolume', value + 2);
            if (muted) {
                $('#iconMute').show();
                $('#iconUnmute').hide();
                muted = false;
            }
        }
    });

    setTimeout(function () {
        $('#nucleusCallWidget').show();
    }, 500);
    return true;
}

function showNotification(myUserId, request, callbacks) {
    var requestId = request.RequestID;
    //var callerUserId = request.PatientID;
    var callerName = request.Name;
    var isEmergency = request.IsEmergency;

    var growl = $.notify({
        // options
        icon: 'resources/images/' + (isEmergency ? 'incoming-call-emergency.png' :  'incoming-call.png'),
        title: callerName,
        message: 'Incoming call'
    }, {
        // settings
        element: 'body',
        type: "info",
        allow_dismiss: false,
        newest_on_top: false,
        showProgressbar: false,
        placement: {
            from: "bottom",
            align: "right"
        },
        offset: 20,
        spacing: 10,
        z_index: 1061,
        delay: 0,
        animate: {
            enter: 'animated fadeInUp',
            exit: 'animated fadeOutDown'
        },
        icon_type: 'image',
        template: '<div id="growl-' + requestId + '" data-notify="container" class="col-xs-11 col-sm-6 col-md-3 alert alert-{0} nucleus-incoming-call" role="alert">' +
        '<button type="button" aria-hidden="true" class="close" data-notify="dismiss">×</button>' +
        '<div class="ldr-ui-layout row">' +
        '   <div class="ldr-ui-layout col-sm-6 col-xs-6" style="/*background-color: dimgrey*/">' +
        '       <div class="ldr-ui-layout title-container"><span data-notify="title">{1}</span></div>' +
        '       <div class="ldr-ui-layout message-container">' +
        '           <span data-notify="icon"></span> <span data-notify="message">{2}</span>' +
        '       </div>' +
        '   </div>' +
        '   <div class="ldr-ui-layout col-md-offset-2 col-sm-offset-1 col-xs-offset-1 col-sm-4 col-xs-4" style="/*background-color: darkgreen;*/ padding: 0">' +
        '       <div class="ldr-ui-layout row" style="/*background-color: green;*/">' +
        '       <div class="ldr-ui-layout col-sm-6 col-xs-6" style="/*background-color: darkblue;*/ padding: 0">' +
        '           <a role="button" class="ldr-ui-btn btn answer-call-with-video"><img src="resources/images/answer-call-with-video.png"/></a>' +
        '       </div>' +
        '       <div class="ldr-ui-layout col-sm-6 col-xs-6" style="/*background-color: darkred;*/ padding: 0">' +
        '           <a role="button" class="ldr-ui-btn btn decline-call"><img src="resources/images/end-cancel-call.png"/></a>' +
        '       </div></div>' +
        '   </div>' +
        '   <div class="progress" data-notify="progressbar">' +
        '       <div class="progress-bar progress-bar-{0}" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0;"></div>' +
        '   </div>' +
        '</div>' +
        '<a href="{3}" target="{4}" data-notify="url"></a>' +
        '</div>'
    });

    var $growl = $('#growl-' + requestId);
    $growl.find('a.answer-call-with-video').click(function () {
        if (callbacks.onAccepted) {
            if (callbacks.onAccepted(request)) {
                growl.close();
            }
        } else {
            growl.close();
        }
    });
    $growl.find('a.decline-call').click(function () {
        if (callbacks.onDeclined) {
            callbacks.onDeclined(request);
        }
        growl.close();
    });
}

function hideNotification(request, callbacks) {
    var requestId = request.RequestID;
    var $growl = $('#growl-' + requestId);
    $growl.find('[data-notify="dismiss"]').trigger('click');
    if ($growl.length > 0 && callbacks.onIgnored) {
        callbacks.onIgnored(request);
    }
}

function dontListenForIncomingCalls(userId) {
    if (isEmpty(userId)) {
        return;
    }
    polling[userId] = false;
}

function listenForIncomingCalls(url, token, userId, callbacks) {
    if (isEmpty(url) || isEmpty(token) || isEmpty(userId)) {
        return;
    }
    if (polling[userId] === true) {
        return;
    }
    callbacks = callbacks || {};

    console.log("start polling for user \"" + userId + "\"");
    var incomingCalls = [];
    polling[userId] = true;
    (function poll() {
        $.ajax({
            url: url + "?Token=" + token + "&UserID=" + userId,
            type: "GET",
            success: function (data) {
                console.log("polling response: " + JSON.stringify(data));
                if (!data.ok) {
                    // ok: "false" may indicate that Nucleus user does not exist
                    polling[userId] = false;
                    console.warn("stop polling for user \"" + userId + "\"");
                } else {
                    var newIncomingCalls = data.requests.map(function (request) {
                        return request.RequestID;
                    });
                    // show notifications for new incoming calls
                    $.each(data.requests, function (i, request) {
                        if ($.inArray(request.RequestID, incomingCalls) === -1) {
                            showNotification(userId, request, callbacks);
                        }
                    });
                    // hide notifications for missed incoming calls
                    $.each(incomingCalls, function (i, requestID) {
                        if ($.inArray(requestID, newIncomingCalls) === -1) {
                            hideNotification(requestID, callbacks);
                        }
                    });
                    incomingCalls = newIncomingCalls;
                }
            },
            dataType: "json",
            complete: setTimeout(function () {
                if (polling[userId]) {
                    poll();
                }
            }, 6000),
            timeout: 5000
        })
    })();
}
