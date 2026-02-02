import { Actions } from 'redux/utils/List'

import actionTypes from './actionTypes'

import service from 'services/ServicePlanService'

export default Actions({
    actionTypes,
    doLoad: clientId => service.findResourceNames(clientId)
})