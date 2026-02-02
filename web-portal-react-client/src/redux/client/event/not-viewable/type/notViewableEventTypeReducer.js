import InitialState from './NotViewableEventTypeInitialState'

import listReducer from './list/notViewableEventTypeListReducer'

const initialState = new InitialState()

export default function notViewableEventTypeReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}