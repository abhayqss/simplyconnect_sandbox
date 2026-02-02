import { useQuery } from '@tanstack/react-query'

import service from 'services/PrivateMarketplaceCommunityService'

const fetch = ({ communityId, ...params }) => service.findLocationById(communityId, params)

function useCommunityLocation(params, options) {
    return useQuery(['CommunityLocation', params], () => fetch(params), options)
}

export default useCommunityLocation
