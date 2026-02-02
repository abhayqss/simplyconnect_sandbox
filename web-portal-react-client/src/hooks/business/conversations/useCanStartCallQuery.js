import { useQuery } from '@tanstack/react-query'

import { VideoConversationService } from 'factories'

const service = VideoConversationService()

const fetch = params => service.canStartCall(params)

export default function useCanStartCallQuery(params, options) {
    return useQuery(['Conversations.CanStartCall', params], () => fetch(params), options)
}
