import { Reducer } from 'redux/utils/List'

import actionTypes from './clientProblemStatisticsActionTypes'
import InitialState from './ClientProblemStatisticsInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})