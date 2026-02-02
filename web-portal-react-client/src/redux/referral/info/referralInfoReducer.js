import InitialState from './ReferralInfoInitialState'

import requestReducer from './request/referralInfoRequestReducer'

const initialState = new InitialState()

export default function referralInfoReducer(state = initialState, action) {
    let nextState = state

    const request = requestReducer(state.request, action)
    if (request !== state.request) nextState = nextState.setIn(['request'], request)

    return nextState
}