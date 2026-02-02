import {
    useGendersQuery,
    useClientStatusesQuery,
    useInsuranceNetworkAggregatedNamesQuery
} from 'hooks/directory/query'

const CACHE_CONFIG = {
    cacheTime: 0,
    staleTime: 3 * 60 * 1000
}

export default function useClientFilterDirectory(
    {
        organizationId,
        networkSearchText
    } = {}
) {
    const {
        data: genders
    } = useGendersQuery({}, {
        ...CACHE_CONFIG
    })

    const {
        data: statuses
    } = useClientStatusesQuery({}, {
        ...CACHE_CONFIG
    })

    const {
        data: insuranceNetworkNames
    } = useInsuranceNetworkAggregatedNamesQuery({
        organizationId, text: networkSearchText
    })

    return {
        genders,
        statuses,
        insuranceNetworkNames
    }
}