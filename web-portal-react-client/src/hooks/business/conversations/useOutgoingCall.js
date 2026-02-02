import { useEffect, useCallback } from 'react'

import { useSelector } from 'react-redux'

import { useConversations } from 'hooks/business/conversations/index'

const selectIsReady = state => state.conversations.isReady

function useOutgoingCall({ onOutgoingCall }) {
    const isReady = useSelector(selectIsReady)

    const { on, off } = useConversations()

    useEffect(() => {
        if (isReady) {
            on('attemptCall', onOutgoingCall)

            return () => {
                off('attemptCall', onOutgoingCall)
            }
        }
    }, [on, off, isReady, onOutgoingCall])
}

export default useOutgoingCall