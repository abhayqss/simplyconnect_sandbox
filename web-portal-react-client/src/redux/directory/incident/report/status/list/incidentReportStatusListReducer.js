import { Reducer } from 'redux/utils/List'

import actionTypes from './incidentReportStatusListActionTypes'
import InitialState from './IncidentReportStatusListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})