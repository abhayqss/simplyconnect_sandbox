import { Reducer } from 'redux/utils/List'

import actionTypes from './incidentReportListActionTypes'
import InitialState from './IncidentReportListInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })