import { Reducer } from 'redux/utils/Details'

import actionTypes from './referralRequestDetailsActionTypes'
import InitialState from './ReferralRequestDetailsInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})