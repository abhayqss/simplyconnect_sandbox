import InitialState from './OrganizationInitialState'

import listReducer from './list/organizationListReducer'

const initialState = new InitialState()

export default function organizationReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}