import { useQuery } from '@tanstack/react-query'

import service from 'services/PrivateMarketplaceCommunityService'

const fetch = params => service.canEditPartnerProviders(params)

function useCanEditPartnerProviders(params, options) {
    return useQuery(['Marketplace.CanEditPartnerProviders', params], () => fetch(params), options)
}

export default useCanEditPartnerProviders
