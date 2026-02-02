import { useQuery } from '@tanstack/react-query'

import service from 'services/OrganizationService'

const fetch = params => service.canView(params)

export default function useCanViewOrganizationQuery(params, options) {
	return useQuery(['Organization.CanView', params], () => fetch(params), options)
}