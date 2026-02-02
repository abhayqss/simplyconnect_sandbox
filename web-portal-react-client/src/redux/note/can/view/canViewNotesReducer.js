import { Reducer } from 'redux/utils/Value'

import actionTypes from './actionTypes'
import InitialState from './CanViewNotesInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})