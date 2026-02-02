import { Reducer } from 'redux/utils/Details'

import actionTypes from './referralRequestDefaultActionTypes'
import InitialState from './ReferralRequestDefaultInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})