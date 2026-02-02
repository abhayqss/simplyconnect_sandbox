import { Reducer } from 'redux/utils/Value'

import actionTypes from './canReviewLabResearchOrderActionTypes'
import InitialState from './CanReviewLabResearchOrderInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })