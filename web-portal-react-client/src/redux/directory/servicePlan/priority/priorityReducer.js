import InitialState from './PriorityInitialState'

import listReducer from './list/priorityListReducer'

const initialState = new InitialState()

export default function priorityReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
