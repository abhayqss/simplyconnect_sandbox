import { useQuery } from '@tanstack/react-query'

import service from 'services/AssessmentService'

const fetch = params => service.canDownloadInTuneReport(params)

export default function useCanDownloadInTuneReportQuery(params, options) {
    return useQuery(['Client.Assessments.CanDownloadInTuneReport', params], () => fetch(params), options)
}