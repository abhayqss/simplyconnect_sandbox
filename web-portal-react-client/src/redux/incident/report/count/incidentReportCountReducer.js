import { Reducer } from 'redux/utils/Value'

import actionTypes from './incidentReportCountActionTypes'
import InitialState from './IncidentReportCountInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })