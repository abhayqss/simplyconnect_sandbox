import { Reducer } from 'redux/utils/List'

import actionTypes from './appointmentTypeListActionTypes'
import InitialState from './AppointmentTypeListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})