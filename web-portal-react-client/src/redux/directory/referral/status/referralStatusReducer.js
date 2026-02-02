import InitialState from './ReferralStatusInitialState'

import listReducer from './list/referralStatusListReducer'

const initialState = new InitialState()

export default function referralStatusReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
