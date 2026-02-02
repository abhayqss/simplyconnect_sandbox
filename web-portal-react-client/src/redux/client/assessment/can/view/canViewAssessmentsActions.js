import { Actions } from 'redux/utils/Value'

import actionTypes from './canViewAssessmentsActionTypes'

import service from 'services/AssessmentService'

export default Actions({
    actionTypes,
    doLoad: params => service.canView(params)
})