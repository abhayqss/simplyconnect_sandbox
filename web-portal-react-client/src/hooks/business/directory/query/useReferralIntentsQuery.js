import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findReferralIntents(params)

function useReferralIntentsQuery(params, options) {
    return useQuery(['Directory.ReferralIntents', params], () => fetch(params), options)
}

export default useReferralIntentsQuery
