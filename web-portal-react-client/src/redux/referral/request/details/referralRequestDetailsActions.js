import { Actions } from 'redux/utils/Details'

import actionTypes from './referralRequestDetailsActionTypes'

import service from 'services/ReferralService'

export default Actions({
    actionTypes,
    doLoad: ({ requestId, ...params }) => service.findRequestById(requestId, params)
})