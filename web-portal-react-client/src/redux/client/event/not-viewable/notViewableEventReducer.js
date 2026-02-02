import InitialState from './NotViewableEventInitialState'

import typeReducer from './type/notViewableEventTypeReducer'

const initialState = new InitialState()

export default function notViewableEventReducer(state = initialState, action) {
    let nextState = state

    const type = typeReducer(state.type, action)
    if (type !== state.type) nextState = nextState.setIn(['type'], type)

    return nextState
}