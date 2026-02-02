import { Reducer } from 'redux/utils/Value'

import actionTypes from './referralRequestPreadmitActionTypes'
import InitialState from './ReferralRequestPreadmitInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })