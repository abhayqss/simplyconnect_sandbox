import { useQuery } from '@tanstack/react-query'

import service from 'services/PrivateMarketplaceCommunityService'

const fetch = params => service.inNetworkCommunityExists(params)

function useInNetworkCommunityExistsQuery(params, options) {
	return useQuery(['Marketplace.InNetworkCommunityExists', params], () => fetch(params), options)
}

export default useInNetworkCommunityExistsQuery
