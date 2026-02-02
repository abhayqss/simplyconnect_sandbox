import { Reducer } from 'redux/utils/List'

import actionTypes from './incidentPlaceListActionTypes'
import InitialState from './IncidentPlaceListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})