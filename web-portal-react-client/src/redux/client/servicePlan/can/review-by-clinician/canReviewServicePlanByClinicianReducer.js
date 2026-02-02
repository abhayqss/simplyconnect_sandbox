import { Reducer } from 'redux/utils/Value'

import actionTypes from './canReviewServicePlanByClinicianActionTypes'
import InitialState from './CanReviewServicePlanByClinicianInitialState'

export default Reducer({
    actionTypes, stateClass: InitialState
})