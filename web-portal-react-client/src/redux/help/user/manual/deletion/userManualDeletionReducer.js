import { Reducer } from 'redux/utils/Delete'

import actionTypes from './userManualDeletionActionTypes'
import InitialState from './UserManualDeletionInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})