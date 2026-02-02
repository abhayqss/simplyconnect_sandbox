import { Actions } from 'redux/utils/Value'

import actionTypes from './canViewLabActionTypes'

import service from 'services/LabResearchOrderService'

export default Actions({
    actionTypes,
    doLoad: params => service.canView(params)
})