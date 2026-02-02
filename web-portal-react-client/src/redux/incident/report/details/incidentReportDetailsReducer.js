import { Reducer } from 'redux/utils/Details'

import actionTypes from './incidentReportDetailsActionTypes'
import InitialState from './IncidentReportDetailsInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})