import { Reducer } from 'redux/utils/Details'

import actionTypes from './documentDetailsActionTypes'
import InitialState from './DocumentDetailsInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})