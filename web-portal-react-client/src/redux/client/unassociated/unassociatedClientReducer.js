import InitialState from './UnassociatedClientInitialState'

import listReducer from './list/unassociatedClientListReducer'

const initialState = new InitialState()

export default function servicePlanReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}