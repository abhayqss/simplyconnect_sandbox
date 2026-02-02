import { useManualPaginatedQuery } from 'hooks/common'

import { VideoConversationService } from 'factories'

const service = VideoConversationService()

const fetch = params => service.findHistory(params)

export default function useClientCallHistoryQuery(params, options) {
    return useManualPaginatedQuery({ size: 15, ...params }, fetch, options)
}
