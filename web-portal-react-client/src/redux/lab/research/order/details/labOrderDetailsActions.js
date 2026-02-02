import { Actions } from 'redux/utils/Details'

import service from 'services/LabResearchOrderService'

import actionTypes from './labOrderDetailsActionTypes'

export default Actions({
    actionTypes,
    doLoad: (orderId, params) => service.findById(orderId, params)
})