import { Reducer } from 'redux/utils/Value'

import actionTypes from './canUploadUserManualActionTypes'
import InitialState from './CanUploadUserManualInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })