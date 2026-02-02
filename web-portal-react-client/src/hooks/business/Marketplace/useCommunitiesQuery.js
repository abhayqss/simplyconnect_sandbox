import { useManualInfiniteQuery } from 'hooks/common'

import service from 'services/PrivateMarketplaceCommunityService'

const fetch = params => service.find(params)

function useCommunitiesQuery(params, options) {
    return useManualInfiniteQuery({ ...params, size: 30 }, fetch, options)
}

export default useCommunitiesQuery
