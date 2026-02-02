import { useQuery } from 'hooks/common'

import { ConversationService } from 'factories'

const service = ConversationService()

const fetch = params => service.findCommunities(params)

function useCommunitiesQuery(params, options) {
    return useQuery('Conversations.AccessibleCommunity', params, {
        fetch,
        ...options,
    })
}

export default useCommunitiesQuery
