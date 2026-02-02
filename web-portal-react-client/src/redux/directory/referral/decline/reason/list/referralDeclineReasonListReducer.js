import { Reducer } from 'redux/utils/List'

import actionTypes from './referralDeclineReasonListActionTypes'
import InitialState from './ReferralDeclineReasonListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})