import { useMemo } from 'react'

import { useSelector } from 'react-redux'

import { useConversations } from './index'

function useNewMessageCheck() {
    const lastMessages = useSelector(
        state => state.conversations.lastMessages
    )

    const { getByMessage } = useConversations()

    const hasNewMessages = useMemo(() => {
        return lastMessages.some(message => {
            const cv = getByMessage(message)

            return (
                !message.isSystemMessage
                && message.index > cv.lastReadMessageIndex
            )
        })
    }, [lastMessages, getByMessage])

    return { hasNewMessages }
}

export default useNewMessageCheck