import { Reducer } from 'redux/utils/Value'

import actionTypes from './canUploadReleaseNoteActionTypes'
import InitialState from './CanUploadReleaseNoteInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })