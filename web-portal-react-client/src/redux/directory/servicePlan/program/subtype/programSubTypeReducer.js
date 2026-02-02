import InitialState from './ProgramSubTypeInitialState'

import listReducer from './list/programSubTypeListReducer'

const initialState = new InitialState()

export default function programSubTypeReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
