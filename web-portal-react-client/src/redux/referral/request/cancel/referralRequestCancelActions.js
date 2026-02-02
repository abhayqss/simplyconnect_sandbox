import { Actions } from 'redux/utils/Value'

import actionTypes from './referralRequestCancelActionTypes'

import service from 'services/ReferralService'

export default Actions({
    actionTypes,
    doLoad: requestId => service.cancelRequest(requestId)
})