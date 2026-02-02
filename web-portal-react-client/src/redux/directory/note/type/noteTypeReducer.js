import InitialState from './NoteTypeInitialState'

import listReducer from './list/noteTypeListReducer'

const initialState = new InitialState()

export default function noteTypeReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}