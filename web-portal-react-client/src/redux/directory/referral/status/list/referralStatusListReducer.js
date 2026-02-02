import { Reducer } from 'redux/utils/List'

import actionTypes from './referralStatusListActionTypes'
import InitialState from './ReferralStatusListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,    
    stateClass: InitialState
})