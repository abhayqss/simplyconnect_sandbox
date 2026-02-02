import { useManualInfiniteQuery } from 'hooks/common'

import service from 'services/PublicMarketplaceCommunityService'

const fetch = params => service.find(params)

function useCommunitiesQuery(params, options) {
	return useManualInfiniteQuery({ ...params, size: 4 }, fetch, options)
}

export default useCommunitiesQuery
