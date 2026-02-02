import { Actions } from 'redux/utils/List'

import actionTypes from './referralRecipientListActionTypes'

import service from 'services/ReferralService'

export default Actions({
    actionTypes,
    isMinimal: true,
    doLoad: params => service.findRecipients(params)
})