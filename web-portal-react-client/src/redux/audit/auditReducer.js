import InitialState from './AuditInitialState'

import logReducer from './log/auditLogReducer'

const initialState = new InitialState()

export default function reducer(state = initialState, action) {
    let nextState = state

    const log = logReducer(state.log, action)
    if (log !== state.log) nextState = nextState.setIn(['log'], log)

    return nextState
}