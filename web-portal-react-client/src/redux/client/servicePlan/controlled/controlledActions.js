import { Actions } from 'redux/utils/Details'

import actionTypes from './actionTypes'

import service from 'services/ServicePlanService'

export default Actions({
    actionTypes,
    doLoad: clientId => service.findControlled(clientId)
})