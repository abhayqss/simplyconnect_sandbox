import { useQuery } from '@tanstack/react-query'

import service from 'services/PublicMarketplaceCommunityService'

const fetch = ({ providerId, ...params }) => service.findById(providerId, params)

function useProviderQuery(params, options) {
	return useQuery(['Marketplace.Public.Provider', params], () => fetch(params), options)
}

export default useProviderQuery
