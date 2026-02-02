import { Actions } from 'redux/utils/List'

import service from 'services/DirectoryService'

import actionTypes from './referralNetworkDetailsListActionTypes'

export default Actions({
    actionTypes,
    doLoad: params => service.findReferralNetworkById(params)
})