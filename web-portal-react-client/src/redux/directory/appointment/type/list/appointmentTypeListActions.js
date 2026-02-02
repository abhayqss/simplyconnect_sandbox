import { Actions } from 'redux/utils/List'

import service from 'services/DirectoryService'

import actionTypes from './appointmentTypeListActionTypes'

export default Actions({
    actionTypes,
    isMinimal: true,
    doLoad: () => service.findAppointmentTypes()
})