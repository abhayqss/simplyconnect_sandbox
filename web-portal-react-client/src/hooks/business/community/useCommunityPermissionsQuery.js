import { useQuery } from '@tanstack/react-query'

import service from 'services/CommunityService'

const fetch = params => (
	service.findPermissions(params)
)

function useCommunityPermissionsQuery(params, options) {
	return useQuery(['Community.Permissions', params], () => fetch(params), options)
}

export default useCommunityPermissionsQuery
