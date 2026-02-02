import { useQuery } from 'hooks/common'

import { ConversationService } from 'factories'

const service = ConversationService()

const fetch = params => service.findClients(params)

function useClientsQuery(params, options) {
    return useQuery('Conversations.Clients', params, {
        fetch,
        ...options,
    })
}

export default useClientsQuery
