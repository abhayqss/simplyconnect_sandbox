import { Actions } from 'redux/utils/Value'

import actionTypes from './canAddLabResearchOrderActionTypes'

import service from 'services/LabResearchOrderService'

export default Actions({
    actionTypes,
    doLoad: params => service.canAdd(params)
})