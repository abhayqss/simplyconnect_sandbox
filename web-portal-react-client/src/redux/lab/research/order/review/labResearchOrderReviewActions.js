import { Actions } from 'redux/utils/Value'

import service from 'services/LabResearchOrderService'

import actionTypes from './labResearchOrderReviewActionTypes'

export default Actions({
    actionTypes,
    doLoad: (params) => service.setReviewed(params)
})