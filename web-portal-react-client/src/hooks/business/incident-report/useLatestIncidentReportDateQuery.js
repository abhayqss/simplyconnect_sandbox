import { useQuery } from 'hooks/common/redux'

import actions from 'redux/incident/report/latest/date/latestIncidentReportDateActions'

import { isInteger } from 'lib/utils/Utils'

export default function useLatestIncidentReportDateQuery(params, options) {
    useQuery(actions, params, {
        condition: prevParams => (
            isInteger(params.organizationId)
            && params.organizationId !== prevParams.organizationId
        ),
        ...options
    })
}