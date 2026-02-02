import InitialState from './ProgramTypeInitialState'

import listReducer from './list/programTypeListReducer'

const initialState = new InitialState()

export default function programTypeReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
