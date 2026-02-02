import { useQuery } from '@tanstack/react-query'

import service from 'services/CommunityService'

const fetch = ({ communityId, organizationId, isMarketplaceDataIncluded }) => (
    service.findById(
        communityId,
        { organizationId, isMarketplaceDataIncluded },
        { response: { extractDataOnly: true } }
    )
)

function useCommunityQuery(params, options) {
    return useQuery(['Community', params], () => fetch(params), options)
}

export default useCommunityQuery
