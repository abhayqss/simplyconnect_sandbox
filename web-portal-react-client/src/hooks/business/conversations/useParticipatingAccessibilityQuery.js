import { useQuery } from 'hooks/common'

import { ConversationService } from 'factories'

const service = ConversationService()

const fetch = params => service.getParticipatingAccessibility(params)

function useParticipatingAccessibilityQuery(params, options) {
    return useQuery('Conversations.Accessibility', params, {
        fetch,
        ...options,
    })
}

export default useParticipatingAccessibilityQuery
