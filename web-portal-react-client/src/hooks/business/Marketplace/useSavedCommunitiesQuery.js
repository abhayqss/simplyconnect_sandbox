import { useQuery } from '@tanstack/react-query'

import service from 'services/PrivateMarketplaceCommunityService'

const fetch = params => service.findSaved(params)

function useSavedCommunitiesQuery(params, options) {
	return useQuery(['Marketplace.SavedCommunities', params], () => fetch(params), options)
}

export default useSavedCommunitiesQuery
