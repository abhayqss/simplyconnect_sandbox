import { Reducer } from 'redux/utils/Value'

import actionTypes from './actionTypes'
import InitialState from './CanAddClientInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})