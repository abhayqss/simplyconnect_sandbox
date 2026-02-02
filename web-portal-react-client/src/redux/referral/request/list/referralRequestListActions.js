import { Actions } from 'redux/utils/List'

import actionTypes from './referralRequestListActionTypes'

import service from 'services/ReferralService'

export default Actions({
    actionTypes,
    isFilterable: false,
    doLoad: params => service.findRequests(params)
})