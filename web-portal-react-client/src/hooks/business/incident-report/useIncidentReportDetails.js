import { useMemo } from 'react'

import { useDetails } from 'hooks/common'

import service from 'services/IncidentReportService'

const options = {
    doLoad: ({ reportId }) => service.findById(reportId),
}

function useIncidentReportDetails(reportId) {
    const params = useMemo(() => ({ reportId }), [reportId])
    return useDetails('INCIDENT_REPORT', params, options)
}

export default useIncidentReportDetails
