import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findInsuranceNetworkAggregatedNames(
    params, { response: { extractDataOnly: true } }
)

export default function useInsuranceNetworkAggregatedNamesQuery(params, options) {
    return useQuery(['Directory.InsuranceNetworkAggregatedNames', params], () => fetch(params), options)
}
