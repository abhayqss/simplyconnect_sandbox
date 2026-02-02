import InitialState from './ReferralInfoRequestInitialState'

import listReducer from './list/referralInfoRequestListReducer'
import detailsReducer from './details/referralInfoRequestDetailsReducer'

const initialState = new InitialState()

export default function referralInfoRequestReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    const details = detailsReducer(state.details, action)
    if (details !== state.details) nextState = nextState.setIn(['details'], details)

    return nextState
}