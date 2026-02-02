import InitialState from './GroupedEventInitialState'

import groupedEventTypeReducer from './type/groupedEventTypeReducer'

const initialState = new InitialState()

export default function groupedEventReducer(state = initialState, action) {
    let nextState = state

    const type = groupedEventTypeReducer(state.type, action)
    if (type !== state.type) nextState = nextState.setIn(['type'], type)

    return nextState
}