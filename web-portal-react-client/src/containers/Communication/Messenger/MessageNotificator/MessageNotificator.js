import React, {
    useState,
    useEffect,
    useCallback
} from 'react'

import {
    useHistory,
    useRouteMatch
} from 'react-router-dom'

import { useAuthUser } from 'hooks/common/redux'
import { useMessageNotification } from 'hooks/business/conversations'

import { MessageNotification } from 'components/communication/messenger'

import { path } from 'lib/utils/ContextUtils'
import { findWhere } from 'underscore'

export default function MessageNotificator() {
    const [isNotificationShown, toggleNotification] = useState(false)

    const user = useAuthUser()
    const history = useHistory()
    const match = useRouteMatch({ path: '*/chats' })

    const notification = useMessageNotification({
        shouldIgnore: useCallback((message) => !!match || message.isSystemMessage, [match])
    })

    const onCloseNotification = useCallback(() => {
        toggleNotification(false)
    }, [])

    const onClickNotification = useCallback(message => {
        toggleNotification(false)

        history.push(path('/chats'), {
            conversationSid: message.conversationSid
        })
    }, [history])

    useEffect(() => {
        if (user && notification) {
            const {
                message,
                conversation: { participants }
            } = notification

            const author = findWhere(
                participants, { identity: message?.author }
            )

            if (notification && author?.employeeId !== user.id && !message?.isSystemMessage) {
                toggleNotification(true)
            }
        }
    }, [user, notification])

    return isNotificationShown && notification && (
        <MessageNotification
            {...notification}
            onClose={onCloseNotification}
            onClick={onClickNotification}
            className="PopupMessageNotification"
        />
    )
}