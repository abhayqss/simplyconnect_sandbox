import { Actions } from 'redux/utils/List'

import actionTypes from './labOrderListActionTypes'

import service from 'services/LabResearchOrderService'

export default Actions({
    actionTypes,
    doLoad: (params, options) => service.find(params, options)
})