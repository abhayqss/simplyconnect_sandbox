import { useQuery } from 'hooks/common'

import { ConversationService } from 'factories'

const service = ConversationService()

const fetch = params => service.findContacts(params)

function useContactsQuery(params, options) {
    return useQuery('Conversations.AccessibleContact', params, {
        fetch,
        ...options,
    })
}

export default useContactsQuery
