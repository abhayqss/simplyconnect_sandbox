import { useQuery } from 'hooks/common/redux'

import actions from 'redux/incident/report/can/view/canViewIncidentReportsActions'

export default function useCanViewIncidentReports(params, options) {
    useQuery(actions, params, options)
}