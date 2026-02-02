import { useMemo } from 'react'

import { useDownload } from 'hooks/common'

import service from 'services/IncidentReportService'

const options = {
    doDownload: ({ reportId, ...params }) => service.downloadById(reportId, params),
}

function useIncidentReportDownload(reportId, { format }) {
    const params = useMemo(() => ({
        reportId, format
    }), [reportId, format])

    return useDownload('INCIDENT_REPORT', params, options)
}

export default useIncidentReportDownload
