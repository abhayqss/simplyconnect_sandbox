import { Reducer } from 'redux/utils/List'

import actionTypes from './clientListActionTypes'
import InitialState from './ClientListInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})