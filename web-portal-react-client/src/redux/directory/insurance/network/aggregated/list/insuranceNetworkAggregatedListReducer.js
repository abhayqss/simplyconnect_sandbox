import { Reducer } from 'redux/utils/List'

import actionTypes from './insuranceNetworkAggregatedListActionTypes'
import InitialState from './InsuranceNetworkAggregatedListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})