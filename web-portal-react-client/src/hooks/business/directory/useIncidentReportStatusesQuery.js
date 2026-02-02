import { useQuery } from 'hooks/common'

import service from 'services/DirectoryService'

const fetch = params => service.findIncidentReportStatuses(params)

export default function useIncidentReportStatusesQuery(params, options) {
    return useQuery('IncidentReportStatuses', params, { ...options, fetch })
}