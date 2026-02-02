import { Actions } from 'redux/utils/Value'

import actionTypes from './referralRequestAssignActionTypes'

import service from 'services/ReferralService'

export default Actions({
    actionTypes,
    doLoad: (...args) => service.assignToRequest(...args)
})