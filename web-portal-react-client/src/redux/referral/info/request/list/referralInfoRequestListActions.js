import { Actions } from 'redux/utils/List'

import actionTypes from './referralInfoRequestListActionTypes'

import service from 'services/ReferralService'

export default Actions({
    actionTypes,
    isFilterable: false,
    doLoad: params => service.findInfoRequests(params)
})