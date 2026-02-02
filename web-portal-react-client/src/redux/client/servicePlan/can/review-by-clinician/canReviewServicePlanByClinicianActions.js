import { Actions } from 'redux/utils/Value'

import actionTypes from './canReviewServicePlanByClinicianActionTypes'

import service from 'services/ServicePlanService'

export default Actions({
    actionTypes,
    doLoad: params => service.canReviewByClinician(params)
})