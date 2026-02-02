import { Actions } from 'redux/utils/Details'

import actionTypes from './referralRequestDefaultActionTypes'

import service from 'services/ReferralService'

export default Actions({
    actionTypes,
    doLoad: params => service.findDefault(params)
})