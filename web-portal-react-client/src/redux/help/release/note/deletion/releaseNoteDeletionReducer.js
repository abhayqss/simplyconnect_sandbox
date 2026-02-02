import { Reducer } from 'redux/utils/Delete'

import actionTypes from './releaseNoteDeletionActionTypes'
import InitialState from './ReleaseNoteDeletionInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})