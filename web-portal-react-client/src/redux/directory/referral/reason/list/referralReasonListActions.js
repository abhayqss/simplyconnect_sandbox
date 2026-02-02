import { Actions } from 'redux/utils/List'

import service from 'services/DirectoryService'

import actionTypes from './referralReasonListActionTypes'

export default Actions({
    actionTypes,
    doLoad: params => service.findReferralReasons(params)
})