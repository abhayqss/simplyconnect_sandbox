import { Reducer } from 'redux/utils/Value'

import actionTypes from './canViewIncidentReportsActionTypes'
import InitialState from './CanViewIncidentReportsInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })