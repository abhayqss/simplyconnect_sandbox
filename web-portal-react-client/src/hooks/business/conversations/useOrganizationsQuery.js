import { useQuery } from 'hooks/common'

import { ConversationService } from 'factories'

const service = ConversationService()

const fetch = params => service.findOrganizations(params)

function useCommunitiesQuery(params, options) {
    return useQuery('Conversations.AccessibleOrganization', params, {
        fetch,
        ...options,
    })
}

export default useCommunitiesQuery
