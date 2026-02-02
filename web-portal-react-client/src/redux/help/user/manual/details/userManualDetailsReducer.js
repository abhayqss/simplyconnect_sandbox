import { Reducer } from 'redux/utils/Details'

import actionTypes from './userManualDetailsActionTypes'
import InitialState from './UserManualDetailsInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})