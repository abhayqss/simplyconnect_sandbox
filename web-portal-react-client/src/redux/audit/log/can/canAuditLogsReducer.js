import InitialState from './CanAuditLogsInitialState'

import viewReducer from './view/canViewAuditLogsReducer'

const initialState = new InitialState()

export default function reducer(state = initialState, action) {
    let nextState = state

    const view = viewReducer(state.view, action)
    if (view !== state.view) nextState = nextState.setIn(['view'], view)

    return nextState
}