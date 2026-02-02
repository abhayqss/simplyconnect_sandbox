import { Reducer } from 'redux/utils/Value'

import actionTypes from './canViewAssessmentsActionTypes'
import InitialState from './CanViewAssessmentsInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })