import { Reducer } from 'redux/utils/Value'

import actionTypes from './canAddIncidentReportActionTypes'
import InitialState from './CanAddIncidentReportInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })