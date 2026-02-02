import { Reducer } from 'redux/utils/Value'

import actionTypes from './referralRequestUnassignActionTypes'
import InitialState from './ReferralRequestUnassignInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })