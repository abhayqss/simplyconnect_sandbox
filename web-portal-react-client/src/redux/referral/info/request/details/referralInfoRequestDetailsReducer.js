import { Reducer } from 'redux/utils/Details'

import actionTypes from './referralInfoRequestDetailsActionTypes'
import InitialState from './ReferralInfoRequestDetailsInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})