import InitialState from './DomainInitialState'

import listReducer from './list/domainListReducer'

const initialState = new InitialState()

export default function domainReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
