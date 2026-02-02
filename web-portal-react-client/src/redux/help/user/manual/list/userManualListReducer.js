import { Reducer } from 'redux/utils/List'

import actionTypes from './userManualListActionTypes'
import InitialState from './UserManualListInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})