import { Actions } from 'redux/utils/List'

import actionTypes from './appointmentListActionTypes'

import service from 'services/AppointmentService'

export default Actions({
    actionTypes,
    doLoad: params => service.find(params)
})