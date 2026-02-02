import React from 'react'

import cn from 'classnames'

import { ReactComponent as MicrophoneMuted } from 'images/microphone-muted.svg'

import './ParticipantInfo.scss'

function ParticipantInfo({ participant, isMuted, className }) {
    return (
        <div className={cn('ParticipantInfo', className)}>
            <div className="ParticipantInfo-FullName">
                {participant.firstName} {participant.lastName}
            </div>

            {isMuted && (
                <MicrophoneMuted className="ParticipantInfo-Status" />
            )}
        </div>
    )
}

export default ParticipantInfo
