import { useListQuery } from 'hooks/common'

import service from 'services/ClientService'

const fetch = params => (
    service.findRecords(params)
)

function useClientRecordsQuery(params, options) {
    return useListQuery(
        'ClientRecords',
        { size: 15, ...params },
        {
            fetch,
            ...options,
            cacheTime: 0,
            staleTime: 0
        }
    )
}

export default useClientRecordsQuery
