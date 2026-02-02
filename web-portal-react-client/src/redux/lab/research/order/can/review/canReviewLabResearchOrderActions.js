import { Actions } from 'redux/utils/Value'

import actionTypes from './canReviewLabResearchOrderActionTypes'

import service from 'services/LabResearchOrderService'

export default Actions({
    actionTypes,
    doLoad: params => service.canReview(params)
})