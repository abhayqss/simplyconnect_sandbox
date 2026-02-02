import { Reducer } from 'redux/utils/List'

import actionTypes from './referralRequestSenderListActionTypes'
import InitialState from './ReferralRequestSenderListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})