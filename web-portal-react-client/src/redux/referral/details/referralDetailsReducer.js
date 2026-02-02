import { Reducer } from 'redux/utils/Details'

import actionTypes from './referralDetailsActionTypes'
import InitialState from './ReferralDetailsInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})