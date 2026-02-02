import { Actions } from 'redux/utils/Form'

import actionTypes from './referralInfoResponseFormActionTypes'

import service from 'services/ReferralService'

export default Actions({
    actionTypes,
    doSubmit: (data, params) => service.sendInfoResponse(data, params)
})