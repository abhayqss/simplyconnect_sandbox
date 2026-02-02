import { Reducer } from 'redux/utils/Form'

import actionTypes from './referralRequestAcceptActionTypes'
import InitialState from './ReferralRequestAcceptInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })