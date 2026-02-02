import InitialState from './ReferralNetworkDetailsInitialState'

import listReducer from './list/referralNetworkDetailsListReducer'

const initialState = new InitialState()

export default function referralNetworkReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}
