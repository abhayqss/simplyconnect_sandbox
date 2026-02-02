import { useQuery } from 'hooks/common/redux'

import actions from 'redux/directory/incident/weather/condition/type/list/incidentWeatherConditionTypeListActions'

export default function useIncidentWeatherConditionTypesQuery() {
    useQuery(actions, null)
}