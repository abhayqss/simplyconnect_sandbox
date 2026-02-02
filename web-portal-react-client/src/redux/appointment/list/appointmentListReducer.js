import { Reducer } from 'redux/utils/List'

import actionTypes from './appointmentListActionTypes'
import InitialState from './AppointmentListInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})