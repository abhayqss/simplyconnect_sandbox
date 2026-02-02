import { useMemo } from 'react'

import useList from 'hooks/common/useList'

import service from 'services/IncidentReportService'

const options = {
    doLoad: (params) => service.findHistory(params),
}

function useIncidentReportChangeHistory(reportId) {
    return useList('INCIDENT_REPORT_HISTORY', { reportId }, options)
}

export default useIncidentReportChangeHistory
