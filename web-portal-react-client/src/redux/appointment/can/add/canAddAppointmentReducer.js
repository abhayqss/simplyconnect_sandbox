import { Reducer } from 'redux/utils/Value'

import actionTypes from './canAddAppointmentActionTypes'
import InitialState from './CanAddAppointmentInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })