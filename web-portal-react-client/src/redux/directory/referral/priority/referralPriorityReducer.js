import InitialState from './ReferralPriorityInitialState'

import listReducer from './list/referralPriorityListReducer'

const initialState = new InitialState()

export default function referralPriorityReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
