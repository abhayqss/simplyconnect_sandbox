import { Actions } from 'redux/utils/List'

import service from 'services/DirectoryService'

import actionTypes from './referralPriorityListActionTypes'

export default Actions({
    actionTypes,
    isMinimal: true,
    doLoad: params => service.findReferralPriorities(params)
})