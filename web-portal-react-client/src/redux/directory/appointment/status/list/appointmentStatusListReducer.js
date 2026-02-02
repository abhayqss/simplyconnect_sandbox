import { Reducer } from 'redux/utils/List'

import actionTypes from './appointmentStatusListActionTypes'
import InitialState from './AppointmentStatusListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})