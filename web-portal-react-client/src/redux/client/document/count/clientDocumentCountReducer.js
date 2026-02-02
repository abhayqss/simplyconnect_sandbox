import { Reducer } from 'redux/utils/Value'

import actionTypes from './clientDocumentCountActionTypes'
import InitialState from './ClientDocumentCountInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})