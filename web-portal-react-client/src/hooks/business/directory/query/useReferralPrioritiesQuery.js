import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findReferralPriorities(params, {
    response: { extractDataOnly: true }
})

function useReferralPrioritiesQuery(params, options) {
    return useQuery(['Directory.ReferralPriorities', params], () => fetch(params), options)
}

export default useReferralPrioritiesQuery
