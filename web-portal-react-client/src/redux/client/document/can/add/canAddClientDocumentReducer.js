import { Reducer } from 'redux/utils/Value'

import actionTypes from './canAddClientDocumentActionTypes'
import InitialState from './CanAddClientDocumentInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})