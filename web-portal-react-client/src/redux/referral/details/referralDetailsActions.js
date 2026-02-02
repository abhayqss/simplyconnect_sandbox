import { Actions } from 'redux/utils/Details'

import service from 'services/ReferralService'

import { isInteger } from 'lib/utils/Utils'

import actionTypes from './referralDetailsActionTypes'

export default Actions({
    actionTypes,
    doLoad: ({ referralId, requestId, ...params }) => {
        if (isInteger(requestId)) {
            return service.findRequestById(requestId, params)
        }

        return service.findById(referralId, params)
    }
})