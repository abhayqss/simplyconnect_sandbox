import { useQuery } from '@tanstack/react-query'

import service from 'services/PublicMarketplaceCommunityService'

const fetch = ({ organizationCode, ...params }) => service.findOrganizationByCode(organizationCode, params)

function useOrganizationQuery(params, options) {
	return useQuery(['Marketplace.Public.Organization', params], () => fetch(params), options)
}

export default useOrganizationQuery
