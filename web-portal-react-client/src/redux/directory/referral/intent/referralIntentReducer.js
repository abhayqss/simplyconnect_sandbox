import InitialState from './ReferralIntentInitialState'

import listReducer from './list/referralIntentListReducer'

const initialState = new InitialState()

export default function referralIntentsReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
