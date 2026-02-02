import { useMemo } from 'react'

import useDetails from 'hooks/common/useDetails'

import service from 'services/IncidentReportService'

const options = {
    doLoad: ({ eventId }) => service.findDefault({ eventId }),
}

function useDefaultIncidentReport({ eventId }) {
    const params = useMemo(() => ({ eventId }), [eventId])
    return useDetails('INCIDENT_REPORT_DEFAULT', params, options)
}

export default useDefaultIncidentReport
