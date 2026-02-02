import InitialState from './ServiceControlRequestInitialState'

import statusReducer from './status/serviceControlRequestStatusReducer'

const initialState = new InitialState()

export default function serviceControlRequestReducer(state = initialState, action) {
    let nextState = state

    const status = statusReducer(state.status, action)
    if (status !== state.status) nextState = nextState.setIn(['status'], status)

    return nextState
}
