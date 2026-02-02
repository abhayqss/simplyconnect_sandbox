import { useQuery } from '@tanstack/react-query'

import service from 'services/AssessmentService'

const fetch = params => service.count(
    params, { response: { extractDataOnly: true } }
)

export default function useClientAssessmentCountQuery(params, options) {
    return useQuery(['Client.Assessments.Count', params], () => fetch(params), options)
}