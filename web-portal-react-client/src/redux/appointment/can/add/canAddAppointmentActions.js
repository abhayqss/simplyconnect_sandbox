import { Actions } from 'redux/utils/Value'

import actionTypes from './canAddAppointmentActionTypes'

import service from 'services/AppointmentService'

export default Actions({
    actionTypes,
    doLoad: () => service.canAdd()
})