import { Reducer } from 'redux/utils/List'

import actionTypes from './incidentWeatherConditionTypeListActionTypes'
import InitialState from './IncidentWeatherConditionTypeListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})