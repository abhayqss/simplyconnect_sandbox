import { Reducer } from 'redux/utils/Form'

import actionTypes from './actionTypes'
import InitialState from './NoteFormInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})