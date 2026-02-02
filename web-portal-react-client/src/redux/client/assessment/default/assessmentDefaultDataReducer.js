import { Reducer } from 'redux/utils/Data'

import actionTypes from './assessmentDefaultDataActionTypes'
import InitialState from './AssessmentDefaultDataInitialState'

export default Reducer({
    actionTypes,
    stateClass: InitialState
})