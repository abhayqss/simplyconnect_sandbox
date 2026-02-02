import { useQuery } from 'hooks/common/redux'

import actions from 'redux/directory/incident/report/classMember/type/list/incidentReportClassMemberTypeListActions'

export default function useClassMemberTypesQuery() {
    useQuery(actions, null)
}