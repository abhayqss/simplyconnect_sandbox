import { useQuery } from '@tanstack/react-query'

import service from 'services/CommunityService'

const fetch = params => service.canConfigure(
    params, { response: { extractDataOnly: true } }
)

function useCanConfigureQuery(params, options) {
    return useQuery(['CanConfigureCommunity', params], () => fetch(params), options)
}

export default useCanConfigureQuery
