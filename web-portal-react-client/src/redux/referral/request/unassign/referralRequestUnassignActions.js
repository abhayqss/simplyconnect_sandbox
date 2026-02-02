import { Actions } from 'redux/utils/Value'

import actionTypes from './referralRequestUnassignActionTypes'

import service from 'services/ReferralService'

export default Actions({
    actionTypes,
    doLoad: (...args) => service.unassignFromRequest(...args)
})