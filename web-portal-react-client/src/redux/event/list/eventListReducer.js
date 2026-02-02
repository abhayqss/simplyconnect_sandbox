import { Reducer } from 'redux/utils/List'

import actionTypes from './actionTypes'
import InitialState from './EventListInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})