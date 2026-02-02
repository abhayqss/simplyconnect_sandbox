import OrganizationInitialState from './CategoryInitialState'

import canReducer from './can/categoryCanReducer'
import listReducer from './list/categoryListReducer'

const initialState = new OrganizationInitialState()

export default function loginReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const can = canReducer(state.can, action)
    if (can !== state.can) nextState = nextState.setIn(['can'], can)

    return nextState
}