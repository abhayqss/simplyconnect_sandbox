import { useQuery } from 'hooks/common'

import service from 'services/ClientDashboardService'

const fetch = params => (
    service.findNotes(params)
)

function useNotesQuery(params, options) {
    return useQuery('Notes', params, {
        fetch,
        ...options,
        staleTime: 0
    })
}

export default useNotesQuery
