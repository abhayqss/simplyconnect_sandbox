import { Reducer } from 'redux/utils/List'

import actionTypes from './actionTypes'
import InitialState from './NoteListInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})