import { Reducer } from 'redux/utils/Value'

import actionTypes from './actionTypes'
import InitialState from './CanAddEventInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})