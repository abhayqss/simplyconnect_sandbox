import { Actions } from 'redux/utils/Details'

import actionTypes from './referralInfoRequestDetailsActionTypes'

import service from 'services/ReferralService'

export default Actions({
    actionTypes,
    doLoad: params => service.findInfoRequestById(params)
})