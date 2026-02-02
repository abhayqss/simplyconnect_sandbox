import { Actions } from 'redux/utils/List'

import service from 'services/LabResearchOrderService'

import actionTypes from './labResearchIcdCodeListActionTypes'

export default Actions({
    actionTypes,
    isMinimal: true,
    doLoad: params => service.findIcdCodes(params)
})