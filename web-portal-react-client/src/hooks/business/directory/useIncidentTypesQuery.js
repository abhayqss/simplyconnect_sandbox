import { useQuery } from 'hooks/common/redux'

import actions from 'redux/directory/incident/type/list/incidentTypeListActions'

export default function useIncidentTypesQuery() {
    useQuery(actions, null)
}
