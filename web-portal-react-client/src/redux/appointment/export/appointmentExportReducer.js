import { Reducer } from 'redux/utils/Details'

import actionTypes from './appointmentExportActionTypes'
import InitialState from './AppointmentExportInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})