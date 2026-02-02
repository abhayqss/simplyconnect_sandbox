import { useQuery } from '@tanstack/react-query'

import service from 'services/PrivateMarketplaceCommunityService'

const fetch = params => service.findProviders(params)

function useFeaturedCommunitiesQuery(params, options) {
	return useQuery(['Marketplace.FeaturedCommunities', params], () => fetch(params), options)
}

export default useFeaturedCommunitiesQuery
