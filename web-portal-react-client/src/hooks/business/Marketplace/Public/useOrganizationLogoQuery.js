import { useQuery } from '@tanstack/react-query'

import service from 'services/PublicMarketplaceCommunityService'

const fetch = params => service.downloadOrganizationLogo(params)

function useOrganizationLogoQuery(params, options) {
	return useQuery(['Marketplace.Public.Organization.Logo', params], () => fetch(params), options)
}

export default useOrganizationLogoQuery
