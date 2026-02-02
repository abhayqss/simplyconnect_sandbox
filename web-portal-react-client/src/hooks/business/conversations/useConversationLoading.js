import { useEffect, useCallback } from 'react'

import useConversations from './useConversations'

function useConversationLoading(callback, dependencies) {
    const { on, off } = useConversations()

    const onLoading = useCallback(callback, dependencies)

    useEffect(() => {
        on('conversationLoading', onLoading)

        return () => {
            off('conversationLoading', onLoading)
        }
    }, [off, on, onLoading])
}

export default useConversationLoading
