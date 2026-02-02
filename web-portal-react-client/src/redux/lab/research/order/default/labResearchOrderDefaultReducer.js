import { Reducer } from 'redux/utils/Details'

import actionTypes from './labResearchOrderDefaultActionTypes'
import InitialState from './LabResearchOrderDefaultInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})