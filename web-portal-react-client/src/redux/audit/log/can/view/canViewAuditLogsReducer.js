import { Reducer } from 'redux/utils/Value'

import actionTypes from './canViewAuditLogsActionTypes'
import InitialState from './CanViewAuditLogsInitialState'

export default Reducer({
    actionTypes,
    stateClass: InitialState
})