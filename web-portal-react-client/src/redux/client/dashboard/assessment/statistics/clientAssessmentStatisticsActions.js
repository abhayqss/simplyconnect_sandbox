import { Actions } from 'redux/utils/List'

import service from 'services/ClientDashboardService'

import actionTypes from './clientAssessmentStatisticsActionTypes'

export default Actions({
    actionTypes,
    isMinimal: true,
    doLoad: params => service.findAssessmentStatistics(params)
})