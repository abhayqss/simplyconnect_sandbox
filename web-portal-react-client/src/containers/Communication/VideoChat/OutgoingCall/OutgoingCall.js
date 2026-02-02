import React, { useEffect } from 'react'

import { Avatar } from 'components/communication'
import { ControlButton, Popup } from 'components/communication/videochat'

import { useVideoPopupDOMProperties } from 'hooks/business/conversations'

import { getInitials } from 'lib/utils/Utils'
import { AudioPlayer } from 'lib/utils/DomUtils'

import Ring from 'sounds/outgoing-call.mp3'

import { ReactComponent as Close } from 'images/close-call.svg'
import { ReactComponent as Loader } from 'images/ellipsis-loader.svg'
import { ReactComponent as DeclineCall } from 'images/decline-call.svg'

import './OutgoingCall.scss'

const getFullName = o => `${o.firstName} ${o.lastName}`

const player = AudioPlayer(Ring, { isInfinite: true })

function OutgoingCall(
    {
        room,
        call,
        onConnected,
        onCancel,
        onClose
    }
) {
    const {
        size,
        isMobileView,
        isDesktopView
    } = useVideoPopupDOMProperties()
    const callerAvatarId = call.callees.size > 1
        ? null
        : call.callees.first().avatarId

    const callerFullName = call.callees.size > 1
        ? call.friendlyName || call.callees.map(getFullName).join(', ')
        : getFullName(call.callees.first())

    useEffect(() => {
        if (room) {
            room.on('participantConnected', onConnected)

            return () => {
                room.off('participantConnected', onConnected)
            }
        }
    }, [room, onConnected])

    useEffect(() => {
        player.play()

        return player.stop
    }, [])

    useEffect(() => {
        if (call.isDeclined) {
            player.stop()
        }
    }, [call.isDeclined])

    return (
        <Popup
            size={size}
            dragBounds=".App"
            offsetParent=".App"
            hasMinimizeButton={false}
            defaultPosition="center"
            isResizable={isDesktopView}
            hasMaximizeButton={!isMobileView}
            isMaximizedByDefault={isMobileView}
        >
            <div className="OutgoingCall">
                <div className="OutgoingCall-Body">
                    <div className="OutgoingCall-Info">
                        <div className="OutgoingCall-Participant">
                            <Avatar
                                id={callerAvatarId}
                                className="OutgoingCall-ParticipantAvatar"
                            >
                                {getInitials({ fullName: callerFullName })}
                            </Avatar>
                            <div className="OutgoingCall-ParticipantFullName">
                                {callerFullName}
                            </div>
                        </div>
                        <div className="OutgoingCall-CallType">
                            {call.isDeclined ? 'Call declined' : (
                                <div className="h-flexbox justify-content-center">
                                    <div className="OutgoingCall-StatusText margin-left-12">Ringing</div>
                                    <Loader height="15px" className="OutgoingCall-Loader"/>
                                </div>
                            )}
                        </div>
                    </div>

                    <div className="OutgoingCall-Actions">
                        {call.isDeclined ? (
                            <div>
                                <ControlButton
                                    size={30}
                                    color="red"
                                    text="Close"
                                    Icon={Close}
                                    onClick={onClose}
                                    className="OutgoingCall-Action"
                                />
                            </div>
                        ) : (
                            <div>
                                <ControlButton
                                    size={30}
                                    color="red"
                                    text="Decline"
                                    Icon={DeclineCall}
                                    onClick={onCancel}
                                    className="OutgoingCall-Action"
                                />
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </Popup>
    )
}

export default OutgoingCall
