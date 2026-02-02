import { Reducer } from 'redux/utils/Details'

import actionTypes from './releaseNoteDetailsActionTypes'
import InitialState from './ReleaseNoteDetailsInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})