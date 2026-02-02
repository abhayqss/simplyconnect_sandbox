import { useQuery } from 'hooks/common/redux'

import actions from 'redux/directory/incident/place/list/incidentPlaceListActions'

export default function useIncidentPlacesQuery() {
    useQuery(actions, null)
}
