import { Reducer } from 'redux/utils/List'

import actionTypes from './clientAssessmentListActionTypes'
import stateClass from './ClientAssessmentListInitialState'

export default Reducer({
    actionTypes,
    stateClass,
    isSortable: false,
    isFilterable: false
})