import InitialState from './ReferralRequestContactInitialState'

import listReducer from './list/referralRequestContactListReducer'

const initialState = new InitialState()

export default function referralRequestContactReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}