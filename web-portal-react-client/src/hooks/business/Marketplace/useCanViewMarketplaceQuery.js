import { useQuery } from '@tanstack/react-query'

import service from 'services/PrivateMarketplaceCommunityService'

const fetch = () => service.canView()

function useCanViewMarketplaceQuery(params, options) {
    return useQuery(['Marketplace.CanView', params], () => fetch(params), options)
}

export default useCanViewMarketplaceQuery
