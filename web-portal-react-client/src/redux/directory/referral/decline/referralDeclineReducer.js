import InitialState from './ReferralDeclineInitialState'

import reasonReducer from './reason/referralDeclineReasonReducer'

const initialState = new InitialState()

export default function referralDeclineReducer(state = initialState, action) {
    let nextState = state

    const reason = reasonReducer(state.reason, action)
    if (reason !== state.reason) nextState = nextState.setIn(['reason'], reason)

    return nextState
}
