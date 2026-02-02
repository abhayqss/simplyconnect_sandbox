import React, {
    useMemo,
    useCallback
} from 'react'

import cn from 'classnames'

import {
    map,
    noop,
    first,
    reject
} from 'underscore'

import { getInitials } from 'lib/utils/Utils'

import { ReactComponent as Close } from 'images/close.svg'

import { Avatar } from '../../'
import { MessageTypeView } from '../'

import './MessageNotification.scss'

export default function MessageNotification(
    {
        message,
        className,
        conversation,
        onClose = noop,
        onClick: onClickCb
    }
) {
    const otherParticipants = reject(
        conversation.participants, o => o.isCurrentUser
    )

    const friendlyName = useMemo(() => (
        conversation.friendlyName ?? map(
            otherParticipants, o => `${o.firstName} ${o.lastName}`
        ).join(', ')
    ), [conversation, otherParticipants])

    const isGroup = otherParticipants.length > 1

    const onClick = useCallback(() => {
        onClickCb(message)
    }, [message, onClickCb])

    return (
        <div className={cn('MessageNotification', className)}>
            <div className="MessageNotification-Body">
                <div className="h-flexbox flex-1 cursor-pointer" onClick={onClick}>
                    <div className="MessageNotification-AuthorAvatarWrapper">
                        <Avatar
                            className="MessageNotification-AuthorAvatar"
                            id={!isGroup ? first(otherParticipants)?.avatarId : null}
                        >
                            {getInitials({ fullName: friendlyName })}
                        </Avatar>
                    </div>
                    <div className="v-flexbox">
                        <div className="MessageNotification-Author">
                            {friendlyName}
                        </div>
                        <MessageTypeView message={message}/>
                    </div>
                </div>
                <Close
                    onClick={onClose}
                    className="MessageNotification-Close margin-left-24"
                />
            </div>
        </div>
    )
}