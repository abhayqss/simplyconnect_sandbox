import InitialState from './ReferralReasonInitialState'

import listReducer from './list/referralReasonListReducer'

const initialState = new InitialState()

export default function referralReasonsReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
