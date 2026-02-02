import { Reducer } from 'redux/utils/List'

import actionTypes from './clientDocumentListActionTypes'
import InitialState from './ClientDocumentListInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})