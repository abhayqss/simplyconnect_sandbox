import { useInfiniteQuery } from 'hooks/common'

import service from 'services/ClientDashboardService'

const fetch = params => (
    service.findAssessments(params)
)

function useAssessmentsQuery(params, options) {
    return useInfiniteQuery('Assessment', params, { ...options, fetch })
}

export default useAssessmentsQuery
