import { Reducer } from 'redux/utils/List'

import actionTypes from './referralIntentListActionTypes'
import InitialState from './ReferralIntentListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,    
    stateClass: InitialState
})