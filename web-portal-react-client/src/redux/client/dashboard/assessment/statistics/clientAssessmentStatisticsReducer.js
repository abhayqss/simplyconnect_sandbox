import { Reducer } from 'redux/utils/List'

import actionTypes from './clientAssessmentStatisticsActionTypes'
import InitialState from './ClientAssessmentStatisticsInitialState'

export default Reducer({
    actionTypes,
    isMinimal: true,
    stateClass: InitialState
})