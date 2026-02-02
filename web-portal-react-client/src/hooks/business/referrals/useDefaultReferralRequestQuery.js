import { useQuery } from '@tanstack/react-query'

import service from 'services/ReferralService'

const fetch = params => service.findDefault(params)

function useDefaultReferralRequestQuery(params, options) {
    return useQuery(['ReferralRequest.Default', params], () => fetch(params), options)
}

export default useDefaultReferralRequestQuery
