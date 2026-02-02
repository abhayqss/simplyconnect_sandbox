import { Reducer } from 'redux/utils/List'

import actionTypes from './incidentTypeListActionTypes'
import InitialState from './IncidentTypeListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})