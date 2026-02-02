import { Actions } from 'redux/utils/Form'

import actionTypes from './referralRequestAcceptActionTypes'

import service from 'services/ReferralService'

export default Actions({
    actionTypes,
    doSubmit: (requestId, data) => service.acceptRequest(requestId, data)
})