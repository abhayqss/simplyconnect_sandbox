import { useQuery } from '@tanstack/react-query'

import service from 'services/PrivateMarketplaceCommunityService'

function fetch({ communityId, ...params } = {}) {
	return service.findById(communityId, params)
}

function useCommunityQuery(params, options) {
	return useQuery(['Marketplace.Community', params], () => fetch(params), options)
}

export default useCommunityQuery
