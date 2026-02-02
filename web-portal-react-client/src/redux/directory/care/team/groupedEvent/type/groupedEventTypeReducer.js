import InitialState from './GroupedEventTypeInitialState'

import eventTypeListReducer from './list/groupedEventTypeListReducer'

const initialState = new InitialState()

export default function eventTypeReducer(state = initialState, action) {
    let nextState = state

    const list = eventTypeListReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}