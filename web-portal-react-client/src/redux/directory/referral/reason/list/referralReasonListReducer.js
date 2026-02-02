import { Reducer } from 'redux/utils/List'

import actionTypes from './referralReasonListActionTypes'
import InitialState from './ReferralReasonListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,    
    stateClass: InitialState
})