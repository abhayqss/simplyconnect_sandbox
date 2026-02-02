import { Reducer } from 'redux/utils/List'

import actionTypes from './auditLogListActionTypes'
import InitialState from './AuditLogListInitialState'

export default Reducer({
    actionTypes,
    isPageable: false,
    isSortable: false,
    isFilterable: true,
    stateClass: InitialState
})