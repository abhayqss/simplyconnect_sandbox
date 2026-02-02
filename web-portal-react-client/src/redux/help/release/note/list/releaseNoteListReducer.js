import { Reducer } from 'redux/utils/List'

import actionTypes from './releaseNoteListActionTypes'
import InitialState from './ReleaseNoteListInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })