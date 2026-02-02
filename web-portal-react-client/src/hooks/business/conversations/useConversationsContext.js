import { useMemo, useCallback } from 'react'

import { useHistory } from 'react-router-dom'

import {
    useStrategy,
    useQueryParams,
    useLocationState,
} from 'hooks/common'

import { ConversationSearchStrategy } from 'strategies/conversations'

function useConversationsContext({
    conversations,
    sidOfLastSelected,
    selectedConversation,
}) {
    const [
        locationState,
    ] = useLocationState({ isCached: false })

    const isMobileView = window.innerWidth <= 667

    const queryParams = useQueryParams(null, ['conversationSid'])

    const context = useMemo(() => ({
        queryParams,
        locationState,
        conversations,
        isMobileView,
        sidOfLastSelected,
        selectedConversation
    }), [
        queryParams,
        locationState,
        conversations,
        isMobileView,
        sidOfLastSelected,
        selectedConversation
    ])

    const { execute } = useStrategy(ConversationSearchStrategy)

    return {
        searchDefaultConversation: useCallback(() => execute(context), [context, execute])
    }
}

export default useConversationsContext