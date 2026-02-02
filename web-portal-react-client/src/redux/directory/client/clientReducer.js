import InitialState from './ClientInitialState'

import listReducer from './list/clientListReducer'
import statusReducer from './status/clientStatusReducer'

const initialState = new InitialState()

export default function clientStatusReducer (state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const status = statusReducer (state.status, action)
    if (status !== state.status) nextState = nextState.setIn(['status'], status)

    return nextState
}
