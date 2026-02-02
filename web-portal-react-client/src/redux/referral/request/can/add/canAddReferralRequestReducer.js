import { Reducer } from 'redux/utils/Value'

import actionTypes from './canAddReferralRequestActionTypes'
import InitialState from './CanAddReferralRequestInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })