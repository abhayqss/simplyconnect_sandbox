import InitialState from './ReferralNetworkInitialState'

import listReducer from './list/referralNetworkListReducer'
import detailsReducer from './details/referralNetworkDetailsReducer'

const initialState = new InitialState()

export default function referralNetworkReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    return nextState
}
