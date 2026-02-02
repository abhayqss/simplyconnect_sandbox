import { Reducer } from 'redux/utils/List'

import actionTypes from './referralRequestListActionTypes'
import InitialState from './ReferralRequestListInitialState'

export default Reducer({
    actionTypes,
    isFilterable: false,
    stateClass: InitialState
})