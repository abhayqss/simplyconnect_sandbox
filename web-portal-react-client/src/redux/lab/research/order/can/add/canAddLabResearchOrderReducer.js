import { Reducer } from 'redux/utils/Value'

import actionTypes from './canAddLabResearchOrderActionTypes'
import InitialState from './CanAddLabResearchOrderInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })