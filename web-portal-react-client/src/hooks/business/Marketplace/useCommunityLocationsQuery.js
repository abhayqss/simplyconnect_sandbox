import { useMutation } from 'hooks/common'

import service from 'services/PrivateMarketplaceCommunityService'

const fetch = params => service.findLocations(params)

function useCommunityLocationsQuery(params, options) {
	return useMutation(params, fetch, options)
}

export default useCommunityLocationsQuery
