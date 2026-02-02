import InitialState from './ServiceControlInitialState'

import requestReducer from './request/serviceControlRequestReducer'

const initialState = new InitialState()

export default function serviceControlReducer(state = initialState, action) {
    let nextState = state

    const request = requestReducer(state.request, action)
    if (request !== state.request) nextState = nextState.setIn(['request'], request)

    return nextState
}
