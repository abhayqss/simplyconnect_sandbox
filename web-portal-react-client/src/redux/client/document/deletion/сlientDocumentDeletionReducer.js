import { Reducer } from 'redux/utils/Delete'

import actionTypes from './сlientDocumentDeletionActionTypes'
import InitialState from './сlientDocumentDeletionInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})