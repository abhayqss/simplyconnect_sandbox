import { Actions } from 'redux/utils/List'

import actionTypes from './labResearchOrderTestResultListActionTypes'

import service from 'services/LabResearchOrderService'

export default Actions({
    actionTypes,
    doLoad: params => service.findTestResults(params)
})