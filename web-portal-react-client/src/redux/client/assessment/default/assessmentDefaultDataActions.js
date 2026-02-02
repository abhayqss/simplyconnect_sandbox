import { Actions } from 'redux/utils/Data'

import service from 'services/AssessmentService'

import actionTypes from './assessmentDefaultDataActionTypes'

export default Actions({
    actionTypes,
    doLoad: (params) => service.findDefault(params)
})
