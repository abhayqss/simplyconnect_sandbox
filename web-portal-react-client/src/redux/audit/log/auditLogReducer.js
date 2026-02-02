import InitialState from './AuditLogInitialState'

import canReducer from './can/canAuditLogsReducer'
import listReducer from './list/auditLogListReducer'

const initialState = new InitialState()

export default function reducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)
    
    const can = canReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)

    return nextState
}