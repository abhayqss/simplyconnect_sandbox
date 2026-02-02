import { Actions } from 'redux/utils/Form'

import actionTypes from './referralInfoRequestFormActionTypes'

import service from 'services/ReferralService'

export default Actions({
    actionTypes,
    doSubmit: (data, params) => service.sendInfoRequest(data, params)
})