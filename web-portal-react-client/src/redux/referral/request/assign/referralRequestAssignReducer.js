import { Reducer } from 'redux/utils/Value'

import actionTypes from './referralRequestAssignActionTypes'
import InitialState from './ReferralRequestAssignInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })