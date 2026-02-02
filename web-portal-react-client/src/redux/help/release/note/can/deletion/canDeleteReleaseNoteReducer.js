import { Reducer } from 'redux/utils/Value'

import actionTypes from './canDeleteReleaseNoteActionTypes'
import InitialState from './CanDeleteReleaseNoteInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })