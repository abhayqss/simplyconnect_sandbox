import { Reducer } from 'redux/utils/Value'

import actionTypes from './canDeleteUserManualActionTypes'
import InitialState from './CanDeleteUserManualInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })