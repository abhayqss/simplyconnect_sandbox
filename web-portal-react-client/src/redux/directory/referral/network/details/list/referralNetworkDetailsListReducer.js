import { Reducer } from 'redux/utils/List'

import actionTypes from './referralNetworkDetailsListActionTypes'
import InitialState from './ReferralNetworkDetailsListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,    
    stateClass: InitialState
})