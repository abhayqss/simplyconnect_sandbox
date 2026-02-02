import { Actions } from 'redux/utils/Value'

import actionTypes from './referralRequestPreadmitActionTypes'

import service from 'services/ReferralService'

export default Actions({
    actionTypes,
    doLoad: requestId => service.preadmitRequest(requestId)
})