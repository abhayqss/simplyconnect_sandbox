import InitialState from './ReferralDeclineReasonInitialState'

import listReducer from './list/referralDeclineReasonListReducer'

const initialState = new InitialState()

export default function referralDeclineReasonReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
