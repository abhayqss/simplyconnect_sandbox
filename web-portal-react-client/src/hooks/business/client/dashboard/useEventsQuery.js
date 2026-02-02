import { useQuery } from 'hooks/common'

import service from 'services/ClientDashboardService'

const fetch = params => (
    service.findEvents(params)
)

function useEventsQuery(params, options) {
    return useQuery('Events', params, {
        fetch,
        ...options,
        staleTime: 0
    })
}

export default useEventsQuery
