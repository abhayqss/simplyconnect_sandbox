import React, { useEffect } from 'react'

import { Avatar } from 'components/communication'
import { ControlButton, Popup } from 'components/communication/videochat'

import { useVideoPopupDOMProperties } from 'hooks/business/conversations'

import { getInitials } from 'lib/utils/Utils'
import { AudioPlayer } from 'lib/utils/DomUtils'

import Ring from 'sounds/incoming-call.mp3'

import { ReactComponent as AcceptCall } from 'images/call-to.svg'
import { ReactComponent as Camera } from 'images/camera-enabled.svg'
import { ReactComponent as Loader } from 'images/ellipsis-loader.svg'
import { ReactComponent as DeclineCall } from 'images/decline-call.svg'

import './IncomingCall.scss'

const getFullName = o => `${o.firstName} ${o.lastName}`
const player = AudioPlayer(Ring, { isInfinite: true })

function IncomingCall(
    {
        call,
        onAccept,
        onDecline,
        onAcceptWithVideo,
    }
) {
    const {
        size,
        isMobileView,
        isDesktopView
    } = useVideoPopupDOMProperties()

    const participants = call.callees.push(call.caller)
    const callerFullName = participants.size > 2
        ? call.friendlyName || participants.map(getFullName).join(', ')
        : getFullName(call.caller)

    const callerAvatarId = participants.size > 2 ? null : call.caller.avatarId

    useEffect(() => {
        player.play()

        return player.stop
    }, [])

    return (
        <Popup
            size={size}
            dragBounds=".App"
            offsetParent=".App"
            defaultPosition="center"
            hasMinimizeButton={false}
            hasMaximizeButton={!isMobileView}
            isResizable={isDesktopView}
            isMaximizedByDefault={isMobileView}
        >
            <div className="IncomingCall">
                <div className="IncomingCall-Body">
                    <div className="IncomingCall-Info">
                        <div className="IncomingCall-Participant">
                            <Avatar
                                id={callerAvatarId}
                                className="IncomingCall-ParticipantAvatar"
                            >
                                {getInitials({ fullName: callerFullName })}
                            </Avatar>
                            <div className="IncomingCall-ParticipantFullName">
                                {callerFullName}
                            </div>
                        </div>
                        <div className="IncomingCall-CallType">
                            <div className="IncomingCall-StatusText">
                                Incoming call
                            </div>
                            <Loader height="15px" className="IncomingCall-Loader" />
                        </div>
                    </div>

                    <div className="IncomingCall-Actions">
                        <div className="IncomingCall-Action">
                            <ControlButton
                                size={30}
                                text="Decline"
                                Icon={Camera}
                                onClick={onAcceptWithVideo}
                                className="IncomingCall-DeclineCallAction"
                            />
                        </div>

                        <div className="IncomingCall-Action">
                            <ControlButton
                                size={30}
                                color="red"
                                text="Decline"
                                Icon={DeclineCall}
                                onClick={onDecline}
                                className="IncomingCall-DeclineCallAction"
                            />
                        </div>

                        <div className="IncomingCall-Action">
                            <ControlButton
                                size={30}
                                Icon={AcceptCall}
                                onClick={onAccept}
                                className="IncomingCall-AcceptCallAction"
                            />
                        </div>
                    </div>
                </div>
            </div>
        </Popup>
    )
}

export default IncomingCall
