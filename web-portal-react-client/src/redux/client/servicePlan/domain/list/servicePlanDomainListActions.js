import { Actions } from 'redux/utils/List'

import service from 'services/ServicePlanService'

import actionTypes from './servicePlanDomainListActionTypes'

export default Actions({
    actionTypes,
    doLoad: ({ clientId, servicePlanId }) => service.findDomains(clientId, servicePlanId)
})