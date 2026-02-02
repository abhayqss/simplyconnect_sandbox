import { Reducer } from 'redux/utils/Details'

import actionTypes from './labOrderDetailsActionTypes'
import InitialState from './LabOrderDetailsInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})