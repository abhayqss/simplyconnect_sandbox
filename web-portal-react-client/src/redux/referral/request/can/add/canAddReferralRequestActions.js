import { Actions } from 'redux/utils/Details'

import actionTypes from './canAddReferralRequestActionTypes'

import service from 'services/ReferralService'

export default Actions({
    actionTypes,
    doLoad: params => service.canAdd(params)
})