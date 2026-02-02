import { Reducer } from 'redux/utils/List'

import actionTypes from './referralPriorityListActionTypes'
import InitialState from './ReferralPriorityListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,    
    stateClass: InitialState
})