import { useQuery } from 'hooks/common'

import service from 'services/DirectoryService'

const fetch = params => service.findInsurancePaymentPlans(params)

function useInsuranceNetworksQuery(params, options) {
    return useQuery('InsurancePaymentPlans', params, {
        fetch,
        staleTime: 0,
        ...options
    })
}

export default useInsuranceNetworksQuery
