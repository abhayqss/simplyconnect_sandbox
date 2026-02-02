import { Actions } from 'redux/utils/Value'

import actionTypes from './labResearchOrderCountActionTypes'

import service from 'services/LabResearchOrderService'

export default Actions({
    actionTypes,
    doLoad: params => service.count(params)
})