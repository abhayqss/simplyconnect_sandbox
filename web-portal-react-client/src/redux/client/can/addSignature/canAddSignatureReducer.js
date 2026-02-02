import { Reducer } from 'redux/utils/Value'

import actionTypes from './actionTypes'
import InitialState from './CanAddSignatureInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})