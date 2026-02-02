import { useEffect, useCallback } from 'react'

import useConversations from './useConversations'

function useConversationUpdates(callback, dependencies) {
    const { on, off } = useConversations()

    const onUpdated = useCallback(callback, dependencies)

    useEffect(() => {
        on('conversationUpdated', onUpdated)

        return () => off('conversationUpdated', onUpdated)
    }, [on, off, onUpdated])
}

export default useConversationUpdates
