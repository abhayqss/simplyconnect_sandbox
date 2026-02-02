import { useQuery } from '@tanstack/react-query'

import service from 'services/PublicMarketplaceCommunityService'

const fetch = params => service.find(params, {
	response: { extractDataOnly: true }
})

function useProviderQuery(params, options) {
	return useQuery(['Marketplace.Public.Providers', params], () => fetch(params), options)
}

export default useProviderQuery
