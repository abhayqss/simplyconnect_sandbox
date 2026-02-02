import { Reducer } from 'redux/utils/List'

import actionTypes from './reportListActionTypes'
import InitialState from './ReportListInitialState'

const { VALIDATE_FILTER } = actionTypes

export default Reducer({
    actionTypes,
    isPageable: false,
    isSortable: false,
    stateClass: InitialState,
    extReducer: (state, action) => {
        if (action.type === VALIDATE_FILTER) {
            return state
                .set('isFilterValid', action.payload.success)
                .set('errors', action.payload.success ? null : action.payload.errors)
        }

        return state
    }
})