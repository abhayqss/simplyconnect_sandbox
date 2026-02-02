import { Reducer } from 'redux/utils/List'

import actionTypes from './clientProblemListActionTypes'
import InitialState from './ClientProblemListInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})