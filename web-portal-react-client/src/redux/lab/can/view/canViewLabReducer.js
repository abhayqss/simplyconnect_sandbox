import { Reducer } from 'redux/utils/Value'

import actionTypes from './canViewLabActionTypes'
import InitialState from './CanViewLabInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })