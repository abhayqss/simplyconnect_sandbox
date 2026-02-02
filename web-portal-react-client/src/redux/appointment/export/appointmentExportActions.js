import { Actions } from 'redux/utils/Details'

import service from 'services/AppointmentService'

import actionTypes from './appointmentExportActionTypes'

export default Actions({
    actionTypes,
    doDownload: (params) => service.export(params)
})