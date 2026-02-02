import { Actions } from 'redux/utils/List'

import actionTypes from './referralRequestContactListActionTypes'

import service from 'services/ReferralService'

export default Actions({
    actionTypes,
    isMinimal: true,
    doLoad: params => service.findRequestContacts(params)
})