import { useQuery } from '@tanstack/react-query'

import service from 'services/AssessmentService'

const fetch = params => service.canGenerateInTuneReport(params)

export default function useCanGenerateInTuneReportQuery(params, options) {
    return useQuery(['Client.Assessments.CanGenerateInTuneReport', params], () => fetch(params), options)
}