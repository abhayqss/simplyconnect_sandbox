import { Reducer } from 'redux/utils/Value'

import actionTypes from './actionTypes'
import InitialState from './CanAddEventNoteInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})