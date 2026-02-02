import { useQuery } from 'hooks/common'

import { ConversationService } from 'factories'

const service = ConversationService()

const fetch = params => service.findClients(params)

function useChatClientsQuery(params, options) {
    return useQuery('ChatClient', params, {
        fetch,
        ...options,
    })
}

export default useChatClientsQuery
