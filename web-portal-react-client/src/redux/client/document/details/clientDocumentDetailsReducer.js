import { Reducer } from 'redux/utils/Details'

import actionTypes from './clientDocumentDetailsActionTypes'
import InitialState from './ClientDocumentDetailsInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})