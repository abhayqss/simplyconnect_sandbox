import { useQuery } from 'hooks/common'

import service from 'services/ClientProblemService'

const fetch = ({ clientId }) => (
    service.findStatisticsById(clientId)
)

function useProblemStatisticsQuery(params, options) {
    return useQuery('ProblemStatistics', params, {
        fetch,
        ...options,
        staleTime: 0
    })
}

export default useProblemStatisticsQuery
