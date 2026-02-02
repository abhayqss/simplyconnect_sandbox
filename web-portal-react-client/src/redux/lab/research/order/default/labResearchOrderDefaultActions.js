import { Actions } from 'redux/utils/Details'

import service from 'services/LabResearchOrderService'

import actionTypes from './labResearchOrderDefaultActionTypes'

export default Actions({
    actionTypes,
    doLoad: (params) => service.findDefault(params)
})