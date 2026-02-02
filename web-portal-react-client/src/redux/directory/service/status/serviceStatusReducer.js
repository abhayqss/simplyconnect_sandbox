import InitialState from './ServiceStatusInitialState'

import listReducer from './list/serviceStatusListReducer'

const initialState = new InitialState()

export default function serviceStatusReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
