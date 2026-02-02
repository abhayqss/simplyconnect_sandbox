import InitialState from './InsuranceNetworkAggregatedInitialState'

import listReducer from './list/insuranceNetworkAggregatedListReducer'

const initialState = new InitialState()

export default function insuranceNetworkAggregatedReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)

    return nextState
}


