import InitialState from './InsuranceNetworkInitialState'

import listReducer from './list/insuranceNetworkListReducer'
import aggregatedReducer from './aggregated/insuranceNetworkAggregatedReducer'

const initialState = new InitialState()

export default function insuranceNetworkReducer(state = initialState, action) {
    let nextState = state

    const list = listReducer(state.list, action)
    if (list !== state.list) nextState = nextState.setIn(['list'], list)
    
    const aggregated = aggregatedReducer(state.aggregated, action)
    if (aggregated !== state.aggregated) nextState = nextState.setIn(['aggregated'], aggregated)

    return nextState
}


