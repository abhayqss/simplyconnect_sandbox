import { Reducer } from 'redux/utils/List'

import actionTypes from './referralNetworkListActionTypes'
import InitialState from './ReferralNetworkListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,    
    stateClass: InitialState
})