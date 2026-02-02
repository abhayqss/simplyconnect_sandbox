import InitialState from './ReferralRequestSenderInitialState'

import listReducer from './list/referralRequestSenderListReducer'

const initialState = new InitialState()

export default function referralRequestSharingMemberReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}