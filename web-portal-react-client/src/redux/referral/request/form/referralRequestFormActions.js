import { Actions } from 'redux/utils/Form'

import actionTypes from './referralRequestFormActionTypes'

import service from 'services/ReferralService'

export default Actions({
    actionTypes,
    doSubmit: data => service.save(data)
})