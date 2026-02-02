import { useQuery } from 'hooks/common/redux'

import actions from 'redux/incident/report/oldest/date/oldestIncidentReportDateActions'

import { isInteger } from 'lib/utils/Utils'

export default function useOldestIncidentReportDateQuery(params, options) {
    useQuery(actions, params, {
        condition: prevParams => (
            isInteger(params.organizationId)
            && params.organizationId !== prevParams.organizationId
        ),
        ...options
    })
}