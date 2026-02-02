import { Actions } from 'redux/utils/Details'

import service from 'services/ClientDashboardService'

import actionTypes from './clientServicePlanDetailsActionTypes'

export default Actions({
    actionTypes,
    doLoad: params => service.findInDevelopmentServicePlan(params)
})