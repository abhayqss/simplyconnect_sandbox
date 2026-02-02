import InitialState from './ReferralRecipientInitialState'

import listReducer from './list/referralRecipientListReducer'

const initialState = new InitialState()

export default function referralRecipientReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}