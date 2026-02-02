import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findReferralReasons(params)

function useReferralReasonsQuery(params, options) {
    return useQuery(['Directory.ReferralReasons', params], () => fetch(params), options)
}

export default useReferralReasonsQuery
