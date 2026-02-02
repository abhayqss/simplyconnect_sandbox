import { Actions } from 'redux/utils/Value'

import service from 'services/ClientMedicationService'

import actionTypes from './clientMedicationCountActionTypes'

export default Actions({
    actionTypes,
    doLoad: (params) => service.count(params)
})