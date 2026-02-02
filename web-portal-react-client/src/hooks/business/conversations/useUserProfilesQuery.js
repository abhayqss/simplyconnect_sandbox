import { useQuery } from 'hooks/common'

import { ConversationService } from 'factories'

const service = ConversationService()

const fetch = params => service.getUserProfiles(params)

function useUserProfilesQuery(params, options) {
    return useQuery('Conversations.UserProfiles', params, {
        fetch,
        ...options,
    })
}

export default useUserProfilesQuery
