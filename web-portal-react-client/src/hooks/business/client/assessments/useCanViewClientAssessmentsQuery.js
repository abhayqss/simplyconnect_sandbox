import { useQuery } from '@tanstack/react-query'

import service from 'services/AssessmentService'

const fetch = params => service.canView(
    params, { response: { extractDataOnly: true } }
)

export default function useCanViewClientAssessmentsQuery(params, options) {
    return useQuery(['Client.Assessments.CanView', params], () => fetch(params), options)
}