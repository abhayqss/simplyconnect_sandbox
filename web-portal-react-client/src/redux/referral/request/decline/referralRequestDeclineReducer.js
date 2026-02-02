import { Reducer } from 'redux/utils/Value'

import actionTypes from './referralRequestDeclineActionTypes'
import InitialState from './ReferralRequestDeclineInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })