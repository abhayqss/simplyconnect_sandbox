import { Reducer } from 'redux/utils/List'

import actionTypes from './referralInfoRequestListActionTypes'
import InitialState from './ReferralInfoRequestListInitialState'

export default Reducer({
    actionTypes,
    isFilterable: false,
    stateClass: InitialState
})