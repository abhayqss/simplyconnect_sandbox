import InitialState from './ClientNoteInitialState'

import listReducer from './list/clientNoteListReducer'

const initialState = new InitialState()

export default function clientEventReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}