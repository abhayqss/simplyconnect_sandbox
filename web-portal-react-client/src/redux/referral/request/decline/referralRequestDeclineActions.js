import { Actions } from 'redux/utils/Form'

import actionTypes from './referralRequestDeclineActionTypes'

import service from 'services/ReferralService'

export default Actions({
    actionTypes,
    doSubmit: (data, requestId) => service.declineRequest(data, requestId)
})