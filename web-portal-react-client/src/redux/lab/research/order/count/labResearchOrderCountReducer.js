import { Reducer } from 'redux/utils/Value'

import actionTypes from './labResearchOrderCountActionTypes'
import InitialState from './LabResearchOrderCountInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })