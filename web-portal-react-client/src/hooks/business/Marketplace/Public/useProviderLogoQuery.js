import { useQuery } from '@tanstack/react-query'

import service from 'services/PublicMarketplaceCommunityService'

const fetch = params => service.downloadLogo(params)

function useProviderLogoQuery(params, options) {
	return useQuery(['Marketplace.Public.Provider.Logo', params], () => fetch(params), options)
}

export default useProviderLogoQuery
