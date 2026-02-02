import { useQuery } from '@tanstack/react-query'

import service from 'services/OrganizationService'

const fetch = (params) => (
	service.findPermissions(params)
)

function useOrganizationPermissionsQuery(params, options) {
	return useQuery(['Organization.Permissions', params], () => fetch(params), options)
}

export default useOrganizationPermissionsQuery
