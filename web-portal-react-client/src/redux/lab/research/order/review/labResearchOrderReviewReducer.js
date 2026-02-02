import { Reducer } from 'redux/utils/Value'

import actionTypes from './labResearchOrderReviewActionTypes'
import InitialState from './LabResearchOrderReviewInitialState'

export default Reducer({ actionTypes, stateClass: InitialState })