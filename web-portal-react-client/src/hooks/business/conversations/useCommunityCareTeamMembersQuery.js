import { useQuery } from '@tanstack/react-query'

import {
    ConversationService,
    VideoConversationService
} from 'factories'

const videoService = VideoConversationService()
const conversationService = ConversationService()

function Fetch(communicationType = 'conversation') {
    const service = communicationType === 'video' ? videoService : conversationService
    return params => () => service.findCommunityCareTeamMembers(params)
}

function useCommunityCareTeamMembersQuery({ communicationType, ...params }, options) {
    return useQuery({
        queryKey: [`Conversations.${communicationType}.CommunityCareTeamMembers`, params],
        queryFn: Fetch(communicationType)(params),
        ...options
    })
}

export default useCommunityCareTeamMembersQuery