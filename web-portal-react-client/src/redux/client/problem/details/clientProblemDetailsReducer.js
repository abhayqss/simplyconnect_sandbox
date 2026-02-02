import { Reducer } from 'redux/utils/Details'

import actionTypes from './clientProblemDetailsActionTypes'
import InitialState from './ClientProblemDetailsInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})