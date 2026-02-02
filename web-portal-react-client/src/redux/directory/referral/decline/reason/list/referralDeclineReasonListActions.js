import { Actions } from 'redux/utils/List'

import service from 'services/DirectoryService'

import actionTypes from './referralDeclineReasonListActionTypes'

export default Actions({
    actionTypes,
    doLoad: params => service.findReferralDeclineReasons(params)
})